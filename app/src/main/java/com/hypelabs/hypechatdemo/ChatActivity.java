//
// MIT License
//
// Copyright (C) 2018 HypeLabs Inc.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package com.hypelabs.hypechatdemo;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.Message;

import java.io.UnsupportedEncodingException;

public class ChatActivity extends Activity implements Store.Delegate {

    public static String INTENT_EXTRA_STORE = "com.hypelabs.store";

    protected Message sendMessage(String text, Instance instance) throws UnsupportedEncodingException {

        // When sending content there must be some sort of protocol that both parties
        // understand. In this case, we simply send the text encoded in UTF-8. The data
        // must be decoded when received, using the same encoding.
        byte[] data = text.getBytes("UTF-8");

        // Sends the data and returns the message that has been generated for it. Messages have
        // identifiers that are useful for keeping track of the message's deliverability state.
        // In order to track message delivery set the last parameter to true. Notice that this
        // is not recommend, as it incurs extra overhead on the network. Use this feature only
        // if progress tracking is really necessary.
        return Hype.send(data, instance, false);
    }

    @Override
    protected void onStart() {

        super.onStart();

        setContentView(R.layout.chat_view);

        final ListView listView = (ListView) findViewById(R.id.list_view);
        final Button sendButton = (Button) findViewById(R.id.send_button);
        final EditText messageField = (EditText) findViewById(R.id.message_field);

        final Store store = getStore();

        listView.setAdapter(new ChatViewAdapter(this, store));

        // Start listening to message queue events so we can update the UI accordingly
        store.setDelegate(this);

        // Flag all messages as read
        store.setLastReadIndex(store.getMessages().size());

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String text = messageField.getText().toString();

                // Avoids accidental presses
                if (text == null || text.length() == 0) {
                    return;
                }

                try {
                    Log.v(getClass().getSimpleName(), "send message");
                    Message message = sendMessage(text, store.getInstance());

                    if (message != null) {

                        // Clear message content
                        messageField.setText("");

                        // Add the message to the store so it shows in the UI
                        store.add(message);
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        if(getStore() != null){
            getStore().setLastReadIndex(getStore().getMessages().size());
        }
    }

    @Override
    public void onMessageAdded(Store store, Message message) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ListView listView = (ListView) findViewById(R.id.list_view);

                ((ChatViewAdapter)listView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    protected Store getStore() {

        ChatApplication chatApplication = (ChatApplication)getApplication();
        String storeIdentifier = getIntent().getStringExtra(INTENT_EXTRA_STORE);
        Store store = chatApplication.getStores().get(storeIdentifier);

        return store;
    }
}

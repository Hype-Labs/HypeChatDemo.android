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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.hypelabs.hype.Message;

import java.lang.ref.WeakReference;

public class ContactActivity extends Activity implements Store.Delegate {

    private static final int REQUEST_ACCESS_COARSE_LOCATION_ID = 0;
    private String displayName;
    private static WeakReference<ContactActivity> defaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ListView listView;

        final ChatApplication chatApplication = (ChatApplication)getApplication();
        final ContactActivity contactActivity = this;
        chatApplication.setActivity(this);

        setContentView(R.layout.contact_view);

        listView = (ListView) findViewById(R.id.contact_view);
        listView.setAdapter(new ContactViewAdapter(this, chatApplication.getStores(), new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {

                    Intent intent = new Intent(ContactActivity.this, ChatActivity.class);

                    TextView displayName = (TextView) view.findViewById(R.id.hype_id);
                    CharSequence charSequence = displayName.getText();

                    setDisplayName(charSequence.toString());

                    Store store = chatApplication.getStores().get(getDisplayName());
                    store.setDelegate(contactActivity);

                    intent.putExtra(ChatActivity.INTENT_EXTRA_STORE, store.getInstance().getStringIdentifier());

                    startActivity(intent);
                }

                return true;
            }
        }));

        // Gives access to ChatApplication for notifying when instances are found
        setContactActivity(this);

        TextView announcementView = findViewById(R.id.hype_announcement);
        announcementView.setText("Device: " + ChatApplication.announcement);
    }

    @Override
    protected void onResume() {

        super.onResume();

        // Updates the UI on the press of a back button
        updateInterface();
    }

    @Override
    public void onMessageAdded(Store store, Message message) {

        updateInterface();
    }

    public String getDisplayName() {

        return displayName;
    }

    public void setDisplayName(String displayName) {

        this.displayName = displayName;
    }

    public static ContactActivity getDefaultInstance() {

        return defaultInstance != null ? defaultInstance.get() : null;
    }

    private static void setContactActivity(ContactActivity instance) {

        defaultInstance = new WeakReference<>(instance);
    }

    protected void notifyContactsChanged() {

        updateInterface();
    }

    protected void notifyAddedMessage() {

        updateInterface();
    }

    protected void updateInterface() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView) findViewById(R.id.contact_view);
                updateHypeInstancesLabel(listView.getAdapter().getCount());

                ((ContactViewAdapter)listView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    private void updateHypeInstancesLabel(int nHypeInstances)
    {
        TextView hypeInstancesText = (TextView) findViewById(R.id.hype_instances_label);

        if(nHypeInstances == 0)
            hypeInstancesText.setText("No Hype Devices Found");
        else
            hypeInstancesText.setText("Hype Devices Found: " + nHypeInstances);
    }

    public void requestPermissions(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION_ID);
        }
        else {
            ChatApplication chatApplication = (ChatApplication) getApplication();
            chatApplication.requestHypeToStart();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION_ID:

                ChatApplication chatApplication = (ChatApplication) getApplication();
                chatApplication.requestHypeToStart();

                break;
        }
    }
}

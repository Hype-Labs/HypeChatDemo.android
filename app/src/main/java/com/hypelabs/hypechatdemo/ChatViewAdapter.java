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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hypelabs.hype.Message;

import java.io.UnsupportedEncodingException;

public class ChatViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater = null;
    private Store store;

    public ChatViewAdapter(Context context, Store store) {

        this.context = context;
        this.store = store;
    }

    protected LayoutInflater getInflater() {

        if (inflater == null) {
            inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return inflater;
    }

    protected Context getContext() {

        return context;
    }

    protected Store getStore() {

        return store;
    }

    @Override
    public int getCount() {

        return getStore().getMessages().size();
    }

    @Override
    public Object getItem(int position) {

        return getStore().getMessages().get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;

        if (vi == null)
            vi = getInflater().inflate(R.layout.chat_cell_view, null);

        final Message message = (Message) getItem(position);

        TextView text = (TextView)vi.findViewById(R.id.text);

        try {
            text.setText(new String(message.getData(), "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return vi;
    }
}

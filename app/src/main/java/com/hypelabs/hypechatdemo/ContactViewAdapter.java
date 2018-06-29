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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ContactViewAdapter extends BaseAdapter {

    private Context context;
    private Map<String, Store> stores;
    private LayoutInflater inflater = null;
    private View.OnTouchListener onTouchListener;

    public ContactViewAdapter(Context context, Map<String, Store> stores, View.OnTouchListener onTouchListener) {

        this.context = context;
        this.stores = stores;
        this.onTouchListener = onTouchListener;
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

    protected Map<String, Store> getStores() {

        return stores;
    }

    @Override
    public int getCount() {

        return stores.size();
    }

    @Override
    public Object getItem(int position) {

        // This way of getting ordered stores is an overkill, but that's not the point of this demo
        return getStores().values().toArray()[position];
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;

        if (vi == null)
            vi = getInflater().inflate(R.layout.contact_cell_view, null);

        Store store = (Store)getItem(position);

        TextView displayName = (TextView)vi.findViewById(R.id.hype_id);
        TextView announcement = (TextView)vi.findViewById(R.id.hype_announcement);

        ImageView contentIndicator = (ImageView)vi.findViewById(R.id.new_content);

        displayName.setText(store.getInstance().getStringIdentifier());

        try {
            announcement.setText(new String(store.getInstance().getAnnouncement(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            announcement.setText("");
        }

        contentIndicator.setVisibility(store.hasNewMessages() ? View.VISIBLE : View.INVISIBLE);

        vi.setOnTouchListener(getOnTouchListener());

        return vi;
    }

    protected View.OnTouchListener getOnTouchListener() {

        return onTouchListener;
    }
}

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

import com.hypelabs.hype.Instance;
import com.hypelabs.hype.Message;

import java.lang.ref.WeakReference;
import java.util.Vector;

public class Store {

    public interface Delegate {

        void onMessageAdded(Store store, Message message);
    }

    private Instance instance;
    private Vector<Message> messages;
    private int lastReadIndex;
    private WeakReference<Delegate> delegateWeakReference;

    public Store(Instance instance) {

        this.instance = instance;
        this.lastReadIndex = 0;
    }

    public Instance getInstance() {

        return instance;
    }

    public void add(Message message) {

        getMessages().add(message);

        Delegate delegate = getDelegate();

        if (delegate != null) {
            delegate.onMessageAdded(this, message);
        }
    }

    public Vector<Message> getMessages() {

        if (messages == null) {
            messages = new Vector<>();
        }

        return messages;
    }

    public int getLastReadIndex() {

        return lastReadIndex;
    }

    public void setLastReadIndex(int lastReadIndex) {

        this.lastReadIndex = lastReadIndex;
    }

    public boolean hasNewMessages() {

        return getLastReadIndex() < getMessages().size();
    }

    public Delegate getDelegate() {

        return delegateWeakReference != null ? delegateWeakReference.get() : null;
    }

    public void setDelegate(Delegate delegate) {

        this.delegateWeakReference = new WeakReference<>(delegate);
    }
}

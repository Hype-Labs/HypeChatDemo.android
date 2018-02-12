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
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

// This class keeps track of whether an activity is running in the foreground.
// The Android API does not provide a means to query that, which we need in
// order to know whether the app is actively running. This is used to stop the
// Hype framework when the app is sent to the background, something that may or
// may not be desirable. In this case, we are running the framework only when the
// app is on the foreground. This is motivated by the fact that background support,
// although already existent, is not yet officially supported by the framework.
// This code was written here to prevent distracting from the ChatApplication's
// main purpose, which is to demonstrate how to use the Hype framework.
public class BaseApplication extends Application {

    public interface LifecycleDelegate {

        void onApplicationStart(Application app);
        void onApplicationStop(Application app);
    }

    private boolean isRunningForeground = false;
    private WeakReference<LifecycleDelegate> lifecycleDelegate;

    @Override
    public void onCreate() {

        super.onCreate();

        final Application thisApp = this;

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityResumed(Activity activity) {

                boolean wasRunningForeground = getRunningForeground();

                setRunningForeground(true);

                if (!wasRunningForeground) {
                    if (getLifecycleDelegate() != null) {
                        getLifecycleDelegate().onApplicationStart(thisApp);
                    }
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

                boolean wasRunningForeground = getRunningForeground();

                setRunningForeground(false);

                if (wasRunningForeground) {
                    if (getLifecycleDelegate() != null) {
                        getLifecycleDelegate().onApplicationStop(thisApp);
                    }
                }
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    private void setRunningForeground(boolean isRunningForeground) {
        this.isRunningForeground = isRunningForeground;
    }

    private boolean getRunningForeground() {
        return this.isRunningForeground;
    }

    public synchronized void setLifecyleDelegate(LifecycleDelegate lifecycleDelegate) {
        this.lifecycleDelegate = new WeakReference<>(lifecycleDelegate);
    }

    private synchronized LifecycleDelegate getLifecycleDelegate() {
        return this.lifecycleDelegate != null ? this.lifecycleDelegate.get() : null;
    }
}

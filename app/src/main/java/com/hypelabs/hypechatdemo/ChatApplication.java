//
// The MIT License (MIT)
// Copyright (c) 2016 Hype Labs Ltd
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
// of the Software, and to permit persons to whom the Software is furnished to do
// so, subject to the following conditions:
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

import android.app.Application;
import android.util.Log;

import com.hypelabs.hype.Error;
import com.hypelabs.hype.Hype;
import com.hypelabs.hype.IOObserver;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.LifecycleObserver;
import com.hypelabs.hype.Message;
import com.hypelabs.hype.NetworkObserver;

import java.util.HashMap;
import java.util.Map;

public class ChatApplication extends BaseApplication implements LifecycleObserver, NetworkObserver, IOObserver, BaseApplication.LifecycleDelegate {

    private static final String TAG = ChatApplication.class.getName();

    // The stores object keeps track of message storage associated with each instance (peer)
    private Map<String, Store> stores;

    @Override
    public void onApplicationStart(Application app) {

        requestHypeToStart();
    }

    @Override
    public void onApplicationStop(Application app) {

        requestHypeToStop();
    }

    protected void requestHypeToStart() {

        // The application context is used to query the user for permissions, such as using
        // the Bluetooth adapter (not available yet) or enabling Wi-Fi. The context must be
        // set before anything else is attempted, otherwise resulting in an exception being
        // thrown.
        Hype.getInstance().setContext(getApplicationContext());

        // Adding itself as an Hype lifecycle observer makes sure that the application gets
        // notifications for lifecycle events being triggered by the Hype framework. These
        // events include starting and stopping, as well as some error handling.
        Hype.getInstance().addLifecycleObserver(this);

        // Network observer notifications include other devices entering and leaving the
        // network. When a device is found all observers get a onInstanceFound notification,
        // and when they leave onInstanceLost is triggered instead.
        Hype.getInstance().addNetworkObserver(this);

        // I/O notifications indicate when messages are sent (not available yet) or fail
        // to be sent. Notice that a message being sent does not imply that it has been
        // delivered, only that it has left the device. If considering mesh networking,
        // in which devices will be forwarding content for each other, a message being
        // means that its contents have been flushed out of the output stream, but not
        // that they have reached their destination. This, in turn, is what acknowledgements
        // are used for, but those have not yet available.
        Hype.getInstance().addIOObserver(this);

        // Requesting Hype to start is equivalent to requesting the device to publish
        // itself on the network and start browsing for other devices in proximity. If
        // everything goes well, the onStart(Hype) observer method gets called, indicating
        // that the device is actively participating on the network. The 00000000 realm is
        // reserved for test apps, so it's not recommended that apps are shipped with it.
        // For generating a realm go to https://hypelabs.io, login, access the dashboard
        // under the Apps section and click "Create New App". The resulting app should
        // display a realm number. Copy and paste that here.
        Hype.getInstance().start(new HashMap<String, Object>() {{

            put(Hype.OptionRealmKey, "00000000");
        }});
    }

    protected void requestHypeToStop() {

        // Stopping the Hype framework does not break existing connections. When the framework
        // stops, all active connections are kept and found devices are not lost. Stopping means
        // that no new devices will be found, as the framework won't be looking for them anymore
        // and that this device is not advertising itself either.
        Hype.getInstance().stop();
    }

    @Override
    public void onStart(Hype hype) {

        // At this point, the device is actively participating on the network. Other devices
        // (instances) can be found at any time and the domestic (this) device can be found
        // by others. When that happens, the two devices should be ready to communicate.
        Log.i(TAG, "Hype started!");
    }

    @Override
    public void onStop(Hype hype, Error error) {

        String description = "";

        if (error != null) {

            // The error parameter will usually be null if the framework stopped because
            // it was requested to stop. This might not always happen, as even if requested
            // to stop the framework might do so with an error.
            description = String.format("[%s]", error.getDescription());
        }

        // The framework has stopped working for some reason. If it was asked to do so (by
        // calling stop) the error parameter is null. If, on the other hand, it was forced
        // by some external means, the error parameter indicates the cause. Common causes
        // include the user turning the Bluetooth and/or Wi-Fi adapters off. When the later
        // happens, you shouldn't attempt to start the Hype services again. Instead, the
        // framework triggers a onReady delegate method call if recovery from the failure
        // becomes possible.
        Log.i(TAG, String.format("Hype stopped [%s]", description));
    }

    @Override
    public void onFailedStarting(Hype hype, Error error) {

        // Hype couldn't start its services. Usually this means that all adapters (Wi-Fi
        // and Bluetooth) are not on, and as such the device is incapable of participating
        // on the network. The error parameter indicates the cause for the failure. Attempting
        // to restart the services is futile at this point. Instead, the implementation should
        // wait for the framework to trigger a onReady notification, indicating that recovery
        // is possible, and start the services then.
        Log.i(TAG, String.format("Hype failed starting [%s]", error.getDescription()));
    }

    @Override
    public void onReady(Hype hype) {

        // This Hype delegate event indicates that the framework believes that it's capable
        // of recovering from a previous start failure. This event is only triggered once.
        // It's not guaranteed that starting the services will result in success, but it's
        // known to be highly likely. If the services are not needed at this point it's
        // possible to delay the execution for later, but it's not guaranteed that the
        // recovery conditions will still hold by then.
        requestHypeToStart();
    }

    @Override
    public void onStateChange(Hype hype) {

        // State change updates are triggered before their corresponding, specific, observer
        // call. For instance, when Hype starts, it transits to the State.Running state,
        // triggering a call to this method, and only then is onStart(Hype) called. Every
        // such event has a corresponding observer method, so state change notifications
        // are mostly for convenience. This method is often not used.
    }

    @Override
    public void onInstanceFound(Hype hype, Instance instance) {

        // Hype instances that are participating on the network are identified by a full
        // UUID, composed by the vendor's realm followed by a unique identifier generated
        // for each instance.
        Log.i(TAG, String.format("Found instance: %s", instance.getStringIdentifier()));

        // Instances should be strongly kept by some data structure. Their identifiers
        // are useful for keeping track of which instances are ready to communicate.
        getStores().put(instance.getStringIdentifier(), new Store(instance));

        // Notify the contact activity to refresh the UI
        ContactActivity contactActivity = ContactActivity.getDefaultInstance();

        if (contactActivity != null) {
            contactActivity.notifyAddedContact();
        }
    }

    @Override
    public void onInstanceLost(Hype hype, Instance instance, Error error) {

        // An instance being lost means that communicating with it is no longer possible.
        // This usually happens by the link being broken. This can happen if the connection
        // times out or the device goes out of range. Another possibility is the user turning
        // the adapters off, in which case not only are all instances lost but the framework
        // also stops with an error.
        Log.i(TAG, String.format("Lost instance: %s [%s]", instance.getStringIdentifier(), error.getDescription()));

        // Cleaning up is always a good idea. It's not possible to communicate with instances
        // that were previously lost.
        getStores().remove(instance.getStringIdentifier());
    }

    @Override
    public void onMessageReceived(Hype hype, Message message, Instance instance) {

        Log.i(TAG, String.format("Got a message from: %s", instance.getStringIdentifier()));

        Store store = getStores().get(instance.getStringIdentifier());

        // Storing the message triggers a reload update in the ChatActivity
        store.add(message);

        // Update the UI for the ContactActivity as well
        ContactActivity contactActivity = ContactActivity.getDefaultInstance();

        if (contactActivity != null) {
            contactActivity.notifyAddedMessage();
        }
    }

    @Override
    public void onMessageFailedSending(Hype hype, Message message, Error error) {

        // Sending messages can fail for a lot of reasons, such as the adapters
        // (Bluetooth and Wi-Fi) being turned off by the user while the process
        // of sending the data is still ongoing. The error parameter describes
        // the cause for the failure.
        Log.i(TAG, String.format("Failed to send message: %d [%s]", message.getIdentifier(), error.getDescription()));
    }

    @Override
    public void onCreate() {

        super.onCreate();

        // See BaseApplication.java
        setLifecyleDelegate(this);
    }

    public Map<String, Store> getStores() {

        if (stores == null) {
            stores = new HashMap<>();
        }

        return stores;
    }
}

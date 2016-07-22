
![alt tag](https://hypelabs.io/static/img/NQMAnSZ.jpg)
![alt tag](https://hypelabs.io/static/img/logo200x.png)

[Hype](http://hypelabs.io/?r=10) is an SDK for cross-platform peer-to-peer communication with mesh networking. Hype works even without Internet access, connecting devices via other communication channels such as Bluetooth, Wi-Fi direct, and Infrastructural Wi-Fi.

The Hype SDK has been designed by [Hype Labs](http://hypelabs.io/?r=10). It is currently in private Beta for Android and [iOS](https://github.com/Hype-Labs/HypeChatDemo.ios).

You can start using Hype today, [join the beta by subscribing on our website](http://hypelabs.io/?r=10).

## What does it do?

This project consists of a chat app sketch written to illustrate how to work with Hype. The app displays a list of devices in close proximity, which can be tapped for exchanging text content. The SDK itself allows sending other kinds of media, such as pictures, or video, but the demo is limited for simplicity purposes.

Most of the documentation is inline with the code, and further information can be found on the Hype Labs [official documentation site](https://hypelabs.io/docs/).

## Setup

To run the project you'll need the Hype SDK binary. To access it, subscribe on the Hype Labs [website](http://hypelabs.io/?r=10) to get early access to the SDK. After extracting the downloaded file, you should see an [AAR](http://tools.android.com/tech-docs/new-build-system/aar-format) file. Now open your app project folder and find a subfolder called _Hype_ inside. Drag the AAR file there. In this case, the project is already configured to link against the Hype binary. If you are writting a project from scratch or integrating Hype into an existing project, check our [Getting Started](https://hypelabs.io/docs/android/getting-started/) guide for Android to learn how to set it up yourself.

## Overview

The chat demo has two activities, one for listing contacts in proximity and one for chatting with specific devices. The former is called _ContactActivity_ and the later _ChatActivity_. Each of those has its own adapter to handle data queries, namelly, _ContactViewAdapter_ and _ChatViewAdapter_. For the most part, these classes handle the UI, a discussion that is not included here. Instead, our focus goes to the _ChatApplication_ class which handles Hype's lifecycle. Finally, messages are sent in the _ChatActivity_, so we'll analyse that as well. There's inline documentation with the code that gives further details. 

#### 1. Download the Hype SDK

The first thing you need is the Hype SDK binary. Subscribe for the Beta program at the Hype Labs [website](http://hypelabs.io/?r=10) and follow the instructions from your inbox. You'll need your subscription to be activated before proceeding.

#### 2. Add the SDK to your Android Studio project

Integrating the SDK is really simple. Here we provide simple instructions, and alternativelly you can read Android's [official documentation](https://developer.android.com/studio/projects/android-library.html#AddDependency) on that regard. These instructions apply if you are starting a project from scratch. If, however, you are just experimenting with this demo, just drag the _Hype.aar_ file to the _Hype_ folder in your project root directory.

Open your project in Android Studio, go to _File_, _New_, _New Module_, and select _Import .JAR/.AAR_. Click _Next_. The window that follows will prompt for a _File name_ and _Subproject name_. For the _File name_ enter the path to the Hype AAR file or click the ellipsis symbol at the right and browse there. After selecting the correct binary, the _Subproject name_ field should have been filled automatically for you. If not, enter _Hype_. Press _Finish_. By now Android Studio should have already configured your _settings.gradle_ file to build against the framework. Make sure by checking if that file contains a line such as `include ':app', ':Hype'`. Finally, add the SDK as a dependency by adding `compile project(':Hype')` to your _build.gradle_. Notice that your project should have at least three _build.gradle_ files by now: one for the project, one for the app, and one for Hype. This line should be added to the app's build settings.

#### 3. Request app permissions

In order for Hype to function properly, it needs some permissions to be asked by the app. To do this, add the following to your manifest:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

The permissions are required for the following reasons:

- INTERNET is needed for enabling socket I/O,
- ACCESS_NETWORK_STATE query information about the network.

All permissions are required at this moment, as the Hype SDK only implements Infrastructural Wi-Fi. When other transports are used (soon) different permissions will be required according to which transports are to be used.

#### 4. Register an app

Go to [the apps page](http://hypelabs.io/apps) and create a new app by pressing the _Create new app_ button on the top left. Enter a name for your app and press Submit. The app dialog that appears afterwards yields a 8-digit hexadecimal number, called a _realm_. Keep that number for step 5. Realms are a means of segregating the network, by making sure that different apps do not communicate with each other, even though they are capable of forwarding each other's contents. If your project requires a deeper understanding of how the technology works we recommend reading the [Overview](http://hypelabs.io/docs/ios/overview/) page. There you'll find a more detailed analysis of what realms are and what they do, as well as other topics about the Hype framework.

This step is optional if you have previously already setup an app. You should use the same realm for all platforms, iOS and Android, as that will enable the two apps to communicate.

#### 5. Setup the realm

The realm can be configured in your manifest file or when starting the Hype services. To set it up using the manifest, access your _AndroidManifest.xml_ file and add this line between the &lt;application&gt; tags:

```xml
        <meta-data
            android:name="com.hypelabs.hype.realm"
            android:value="\ 00000000"/>
```

Notice the initial slash followed by an empty space. As indicated by [this](http://stackoverflow.com/questions/2154945/how-to-force-a-meta-data-value-to-type-string) StackOverflow discussion, the slash prevents the realm from being interpreted as a number, and instead forces it to be read as a string. Alternatively, the realm could be given with a `\0` suffix, such as `ABCDEFGH\0`. Also, you can configure the realm when the Hype services are requested to [start](https://hypelabs.io/docs/android/api-reference/#startmapstring-object) by passing the _Hype.OptionRealmKey_ with a `String` object indicating the realm. Here's how:

```java
        Hype.getInstance().start(new HashMap<String, Object>() {{

            put(Hype.OptionRealmKey, "00000000");
        }});
```

The 00000000 realm is reserved for testing purposes and apps should not be deployed with this realm. Also, setting the realm with `start(Map<String, Object>)` takes precedence over the realm read from the manifest file.

#### 6. Start Hype services

After the project has been properly set up, it's time to write some code! In this case, we want Hype to run for the duration of the app, meaning that it will be active as long as the app is on the foreground. Hype already supports background execution, but this has not been officially deployed and is not documented yet, so we'll not be using it. For this reason, we manage Hype's lifecycle in a custom `Application` instance, called `ChatApplication`. To do that, have your `Application` class implement the [StateObserver](https://hypelabs.io/docs/android/api-reference/#stateobserver) interface and set the instance as an Hype observer. Lets jump to the code, with some details omitted for simplicity:

```java
public class ChatApplication extends BaseApplication implements LifecycleObserver, NetworkObserver, MessageObserver {

    private static final String TAG = ContactActivity.class.getName();

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

        // Adding itself as an Hype state observer makes sure that the application gets
        // notifications for lifecycle events being triggered by the Hype framework. These
        // events include starting and stopping, as well as some error handling.
        Hype.getInstance().addStateObserver(this);

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
}
```

This code demonstrates how to manage Hype's lifecycle. First, when the app starts it requests Hype to start as well. For this the app needs to give Hype it's application context and set itself as an observer. This means asking the device to activelly participate on the local network, making it self available to communicate. This could result in either success or failure, indicated by the `onStart(Hype)` and `onFailedStarting(Hype, Error)` observer notifications, respectivelly. In case of success, the device is activelly participating in the network and could find and be found by other devices at any time. If it fails, however, the device will not be participating in the network at all. Common causes include the Wi-Fi adapter being off. At this point it's useless trying to start the services again, as that operation should still fail. Instead, the `onReady(Hype)` method indicates when the framework has acknowledged that a recovery from a previous error has become possiblie, and that's the time to attempt restarting the services.

The next step would be to handle found instances. Here's how that can be accomplished, while expanding the previous example: 

```java
public class ChatApplication extends BaseApplication implements LifecycleObserver, NetworkObserver, MessageObserver {

    private static final String TAG = ContactActivity.class.getName();

    // The stores object keeps track of message storage associated with each instance (peer)
    private Map<String, Store> stores;

    @Override
    public void onInstanceFound(Hype hype, Instance instance) {

        // Hype instances that are participating on the network are identified by a full
        // UUID, composed by the vendor's realm followed by a unique identifier generated
        // for each instance.
        Log.i(TAG, String.format("Found instance: %s", instance.getStringIdentifier()));

        // Instances should be strongly kept by some data structure. Their identifiers
        // are useful for keeping track of which instances are ready to communicate.
        getStores().put(instance.getStringIdentifier(), new Store(instance));
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
}
```

Notice that the instance has previously been added as a network observer and that it complies with the `NetworkObserver` interface.

### 7. Sending messages

Sending messages is performed by the `ChatActivity` activity. For that, we need some content to send and instance to send it to. Here's how:

```java
    protected Message sendMessage(String text, Instance instance) throws UnsupportedEncodingException {

        // When sending content there must be some sort of protocol that both parties
        // understand. In this case, we simply send the text encoded in UTF-8. The data
        // must be decoded when received, using the same encoding.
        byte[] data = text.getBytes("UTF-8");

        // Sends the data and returns the message that has been generated for it. Messages have
        // identifiers that are useful for keeping track of the message's deliverability state
        // (not available yet).
        return Hype.getInstance().send(data, instance);
    }
```

Finally, messages are received by all Hype I/O observers actively listenning to framework events.

```java
    @Override
    public void onMessageReceived(Hype hype, Message message, Instance instance) {

        Log.i(TAG, String.format("Got a message from: %s", instance.getStringIdentifier()));

        Store store = getStores().get(instance.getStringIdentifier());

        // Storing the message triggers a reload update in the ChatActivity
        store.add(message);
    }

    @Override
    public void onMessageFailedSending(Hype hype, Message message, Instance instance, Error error) {

        // Sending messages can fail for a lot of reasons, such as the adapters
        // (Bluetooth and Wi-Fi) being turned off by the user while the process
        // of sending the data is still ongoing. The error parameter describes
        // the cause for the failure.
        Log.i(TAG, String.format("Failed to send message: %d [%s]", message.getIdentifier(), error.getDescription()));
    }
```

Notice the message being added to a store, which in turn has a delegate. When messages are added, the `ChatActivity` gets notified and triggers a UI update. This happens when that activity's view is active on screen. When that happens, the message is decoded to its original formated, using UTF-8. This detail is important, as the data must be read using the same protocol as it was sent.

## License

This project is MIT-licensed.

## Follow us

Follow us on [twitter](http://www.twitter.com/hypelabstech) and [facebook](http://www.facebook.com/hypelabs.io). We promise to keep you up to date!

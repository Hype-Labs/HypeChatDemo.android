
![alt tag](https://hypelabs.io/static/img/NQMAnSZ.jpg)
![alt tag](https://hypelabs.io/static/img/logo200x.png)

[Hype](https://hypelabs.io/?r=10) is an SDK for cross-platform peer-to-peer communication with mesh networking. Hype works even without Internet access, connecting devices via other communication channels such as Bluetooth, Wi-Fi direct, and Infrastructural Wi-Fi.

The Hype SDK has been designed by [Hype Labs](http://hypelabs.io/?r=10). It is currently available for multiple platforms.

You can [start using Hype](http://hypelabs.io/?r=10) today.

## What does it do?

This project consists of a chat app sketch using the Hype SDK. The app displays a list of devices in proximity, which can be tapped for exchanging text content. The SDK itself allows sending other kinds of media, such as pictures, or video, but the demo is limited for simplicity purposes.

This demo does not serve the purpose of documenting how to use the SDK. In case you're looking for documentation, check our quickstart guides and project templates on the [apps](https://hypelabs.io/apps/?r=10) page, or our [documentation](https://hypelabs.io/docs/?r=10) page.

## Setup

This demo does not work out of the box. The following are the necessary steps to configure it:

 1. [Download](https://hypelabs.io/downloads/?r=10) the SDK binary for Android.
 2. Extract it, and drag it to the project root folder
 3. Access the [apps](https://hypelabs.io/apps/?r=10) page and create a new app
 4. Name the app and press "Create new app"
 5. Go to the app settings
 6. Copy the identifier under `App ID`
 7. With the project open on Android Studio, in the file `ChatApplication.java`, find the line that reads `Hype.setAppIdentifier("{{app_identifier}}");`
 8. Replace `{{app_identifier}}` by the App ID you just copied
 9. Go back to the app settings
 10. This time, copy the `Access Token`, under the Authorization group
 11. Open the same file, `ChatApplication.java`, and find the method declaration for `public String onHypeRequestAccessToken`
 12. Where that method returns `{{access_token}}`, have it return the token you just copied instead

You should be ready to go! 

Please note that the app can **ONLY be run on physical hardware devices**. Running on *emulators will not work* due to APIs related to certain features being unsupported.

If you run into trouble, feel free to reach out to our [community](https://hypelabs.io/community/?r=10) or browse our other [support](https://hypelabs.io/support/?r=10) options. Also keep in mind our project templates on the [apps](https://hypelabs.io/apps/?r=10) page for demos working out of the box.

## Overview

Most of the action takes place in `ChatApplication` class. This class manages Hype's lifecycle and network activity. Another interesting class is `ChatActivity`, as this is where messages are sent. Other classes are mostly accessories.

## License

This project is MIT-licensed.

## Follow us

Follow us on [twitter](http://www.twitter.com/hypelabstech) and [facebook](http://www.facebook.com/hypelabs.io). We promise to keep you up to date!

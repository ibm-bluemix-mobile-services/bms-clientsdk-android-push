Bluemix Push Notifications Android SDK
====================================

[![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push.svg?branch=master)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push)
[![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push.svg?branch=development)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5c49c09a1f9f45c99c39623f8033d1eb)](https://www.codacy.com/app/ibm-bluemix-mobile-services/bms-clientsdk-android-push?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ibm-bluemix-mobile-services/bms-clientsdk-android-push&amp;utm_campaign=Badge_Grade)

The [Bluemix Push Notifications service](https://console.ng.bluemix.net/catalog/services/push-notifications) provides a unified push service to send real-time notifications to mobile and web applications. The SDK enables Android apps to receive push notifications sent from the service. Before starting to configure Android SDK follow the [Bluemix Push service setup guide](https://console.ng.bluemix.net/docs/services/mobilepush/index.html#gettingstartedtemplate)

## Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Setup Client Application](#setup-client-application)
  - [Include the client Push SDK with Gradle](#installing-the-client-push-sdk-with-gradle)
    - [Configure Gradle](#configure-gradle)
    - [Configure AndroidManifest](#configure-androidmanifest)
  - [Initialize](#initialize)
    - [Initializing the Core SDK](#initializing-the-core-sdk)
    - [Initializing the Push SDK](#initializing-the-push-sdk)
  - [Register to Push Service](#register-to-push-ervice)
    - [Register Without UserId](#register-without-userid)
    - [Register With UserId](#register-with-userid)
    - [Receiving push notifications on Android devices](#receiving-push-notifications-on-android-devices)
    - [Unregistering the Device from Push Notification](#unregistering-the-device-from-push-notification)
    - [Unregistering the Device from UserId](#unregistering-the-device-from-userid)
  - [Bluemix tags](#bluemix-tags)
    - [Retrieve Available tags](#retrieve-available-tags)
    - [Subscribe to Available tags](#subscribe-to-available-tags)
    - [Retrieve Subscribed tags](#retrieve-subscribed-tags)
    - [Unsubscribing from tags](#unsubscribing-from-tags)
  - [Notification Options](#notification-options)
   - [Enable Interactive push notifications](#enable-interactive-push-notifications)
   - [Handling Interactive push notifications](#handling-interactive-push-notifications)
   - [Adding custom DeviceId for registration](#ddding-custom-deviceid-for-registration)
- [Android Custom Notification Sound](#android-custom-notification-sound)
- [Hold Android notifications](#hold-android-notifications)
- [Enable Monitoring](#enable-monitoring)
  - [Setup Monitoring](#setup-monitoring)
  - [Listening to the DISMISSED status](#listening-to-the-dismissed-status)
- [Android Notification custom Options](#android-notification-custom-options)
  - [Collapse Key](#collapse-key)
  - [Sound](#sound)
  - [Icon](#icon)
  - [Priority](#priority)
  - [Visibility](#visibility)
  - [Time to live](#time-to-live)
  - [Delay when idle](#delay-when-idle)
  - [Sync](#sync)
  - [Additional payload](#additional-payload)
- [Samples and videos](#samples-and-videos)


## Requirements

The package is supported on Android API level 14 and up (Android 4.0 and up).

 * Android Studio


## Installation

There multiple way to integrate android Bluemix push notifications package,

- Download and import this package to your Android Studio project
- Get it via Gradle.

## Setup Client Application

Follow the steps to enable Android applications to receive push notifications

### Include the client Push SDK with Gradle

Ensure that you have gone through [Configuring credentials for a notification provider](https://console.ng.bluemix.net/docs/services/mobilepush/t__main_push_config_provider.html) to setup the FCM project and obtain your credentials.

This section describes how to install and use the client Push SDK to further develop your Android applications.After creating and opening your mobile application, complete the following steps using Android Studio,

#### Configure Gradle

 Configure the <strong>Module level build.gradle</strong> and <strong>Project level build.gradle</strong> files.

1. Add dependencies to your <strong>Module level build.gradle</strong> file.

```
com.ibm.mobilefirstplatform.clientsdk.android:push:3.+

```

2. Add the following dependency to your <strong>Module level build.gradle</strong> file at the end after the <strong>dependencies{.....}</strong>,

```
apply plugin: 'com.google.gms.google-services'

```
3. Add the following dependencies to your <strong>Project level build.gradle</strong> file.

```
dependencies {
 classpath 'com.android.tools.build:gradle:2.2.3'
 classpath 'com.google.gms:google-services:3.0.0'
}
```

#### Configure AndroidManifest

 Next step is to configure the <strong>AndroidManifest.xml</strong> file. Refer the [example here](https://github.com/ibm-bluemix-mobile-services/bms-samples-android-hellopush/blob/master/helloPush/app/src/main/AndroidManifest.xml). Please add the following code inside app <strong>AndroidManifest.xml</strong> file.

1. Add the following permissions ,

 ```
 <uses-permission android:name="android.permission.INTERNET"/>
 <uses-permission android:name="android.permission.GET_ACCOUNTS" />
 <uses-permission android:name="android.permission.USE_CREDENTIALS" />
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>

 ```

2. Add the notification intent settings for the activity. This setting starts the application when the user clicks the received notification from the notification area.

```
<intent-filter>
     <action android:name="Your_Android_Package_Name.IBMPushNotification"/>
     <category  android:name="android.intent.category.DEFAULT"/>
 </intent-filter>

```
>**Note**: Replace <strong>Your_Android_Package_Name</strong> in the previous action with the application package name used in your application.


3. Add the <strong>Firebase Cloud Messaging (FCM)</strong> or <strong>Google Cloud Messaging (GCM)</strong> intent service and intent filters for the <strong>RECEIVE</strong> and </strong>REGISTRATION</strong> event notifications

```
<service android:name="com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushIntentService"
     android:exported="true" >
     <intent-filter>
         <action android:name="com.google.firebase.MESSAGING_EVENT" />
 </intent-filter>
 </service>
 <service
 android:name="com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush"
 android:exported="true" >
 <intent-filter>
     <action android:name="com.google.firebase.INSTANCE_ID_EVENT" />
 </intent-filter>
 </service>

```

4. Push Notifications service supports retrieval of individual notifications from the notification tray. For notifications accessed from the notification tray, you are provided with a handle only to the notification that is being clicked. All notifications are displayed when the application is opened normally. Update your <strong>AndroidManifest.xml</strong> file with the following snippet to use this functionality,

```
<activity android:name="
   com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationHandler"
   android:theme="@android:style/Theme.NoDisplay"/>

```

5. Add the <strong>google-services.json</strong> in Android application module root directory.



### Initialize

A common place to put the initialization code is in the onCreate method of the main activity in your Android application.

#### Initializing the Core SDK

Initialize the `BMSCore` SDK following way,

```
// Initialize the SDK for Android
    BMSClient.getInstance().initialize(this, "bluemixRegionSuffix");

```

##### bluemixRegionSuffix

Specifies the location where the app is hosted. You can use one of the three values:

- <strong>BMSClient.REGION_US_SOUTH</strong>
- <strong>BMSClient.REGION_UK</strong>
- <strong>BMSClient.REGION_SYDNEY</strong>


#### Initializing the Push SDK

Initialize the `BMSPushClient`  following way,

```
//Initialize client Push SDK for Java
MFPPush push = MFPPush.getInstance();
push.initialize(getApplicationContext(), "appGUID", "clientSecret");

```
##### appGUID

- The Push app GUID value.

##### clientSecret

- The Push client secret value.


### Register to Push Service

Use the `MFPPush.register()` API to register the device with Push Notifications service.

#### Register Without UserId

To register without userId use the following pattern,

```
//Register Android devices

push.registerDevice(new MFPPushResponseListener<String>() {
    @Override
    public void onSuccess(String response) {
        //handle success here
    }
    @Override
    public void onFailure(MFPPushException ex) {
        //handle failure here
    }
  });
```

#### Register Without UserId

For `userId` based notification, the register method will accept one more parameter - `userId`

```
// Register the device to Push Notifications

push.registerDeviceWithUserId("userId",new MFPPushResponseListener<String>() {
  @Override
  public void onSuccess(String response) {
    Log.d("Device is registered with Push Service.");
  }
  @Override
  public void onFailure(MFPPushException ex) {
    Log.d("Error registering with Push Service...\n" + "Push notifications will not be received.");
  }
});

```
##### userId

- Pass the unique userId value for registering for Push Notifications.

>**Note**: If userId is provided the client secret value must be provided.


#### Receiving push notifications on Android devices

To register the notificationListener object with Push Notifications service, use the `MFPPush.listen()` method. This method is typically called from the `onResume()` and `onPause()` methods of the activity that is handling push notifications.

```
//Handles the notification when it arrives

MFPPushNotificationListener notificationListener = new MFPPushNotificationListener() {
  @Override
  public void onReceive (final MFPSimplePushNotification message){
      // Handle Push Notification
          }
};


@Override
 protected void onResume(){
    super.onResume();
    if(push != null) {
      push.listen(notificationListener);
    }
 }


 @Override
 protected void onPause() {
   super.onPause();
   if (push != null) {
       push.hold();
    }
 }
```

#### Unregistering the Device from Push Notification

Use the following code snippets to Unregister the device from Bluemix Push Notification

```
push.unregister(new MFPPushResponseListener<String>() {

    @Override
    public void onSuccess(String s) {
        // Handle success
    }

    @Override
    public void onFailure(MFPPushException e) {
        // Handle Failure
    }
});
```

#### Unregistering the Device from UserId

To unregister from the `UserId` based registration you have to call the registration method [without userId](#register-without-userid).


### Bluemix tags

#### Retrieve Available tags

The `getTags` API returns the list of available tags to which the device can subscribe. After the device is subscribed to a particular tag, the device can receive any push notifications that are sent for that tag.Call the push service to get subscriptions for a tag.

Use the following code snippets into your Swift mobile application to get a list of available tags to which the
device can subscribe.

```
// Get a list of available tags to which the device can subscribe

push.getTags(new MFPPushResponseListener<List<String>>(){  
  @Override
  public void onSuccess(List<String> tags){
      System.out.println("Available tags are: "+tags);
   }    
  @Override    
  public void onFailure(MFPPushException ex){
    System.out.println("Error getting available tags.. " + ex.getMessage());
  }
})
```

#### Subscribe to Available tags

The `subscribe` API will subscribe the iOS device for the list of given tags. After the device is subscribed to a particular tag, the device can receive any push notifications
that are sent for that tag.

Use the following code snippets into your Swift mobile application to subscribe a list of tags.

```
// Subscribe to the given tag

push.subscribe(allTags.get(0), new MFPPushResponseListener<String>() {
  @Override
  public void onSuccess(String arg) {
      System.out.println("Succesfully Subscribed to: "+ arg);
  }
  @Override
  public void onFailure(MFPPushException ex) {
      System.out.println("Error subscribing to Tag1.." + ex.getMessage());
  }
});
```

#### Retrieve Subscribed tags

The `getSubscriptions` API will return the list of tags to which the device is subscribed.

Use the following code snippets into your Swift mobile application to get the  subscription list.

```
// Get a list of tags that to which the device is subscribed.

push.getSubscriptions(new MFPPushResponseListener<List<String>>() {
  @Override
  public void onSuccess(List<String> tags) {
      System.out.println("Subscribed tags are: "+tags);
  }
  @Override
  public void onFailure(MFPPushException ex) {
       System.out.println("Error getting subscriptions.. " + ex.getMessage());
  }
})
```

#### Unsubscribing from tags

The `unsubscribeFromTags` API will remove the device subscription from the list tags.

Use the following code snippets to allow your devices to get unsubscribe from a tag.

```
// unsubscibe from the given tag ,that to which the device is subscribed.

push.unsubscribe(tag, new MFPPushResponseListener<String>() {
   @Override
   public void onSuccess(String s) {
      System.out.println("Successfully unsubscribed from tag . "+ tag);
    }
   @Override
   public void onFailure(MFPPushException e) {
      System.out.println("Error while unsubscribing from tags. "+ e.getMessage());
   }
 });
```

### Notification Options

#### Enable Interactive push notifications

To enable interactive push notifications, the notification action parameters must be passed in as part of the notification object.  The following is a sample code to enable interactive notifications.

```
MFPPushNotificationOptions options = new MFPPushNotificationOptions();
MFPPushNotificationButton firstButton = new MFPPushNotificationButton.Builder("Accept Button")
      .setIcon("check_circle_icon")
      .setLabel("Accept")
      .build();

MFPPushNotificationButton secondButton = new MFPPushNotificationButton.Builder("Decline Button")
      .setIcon("extension_circle_icon")
      .setLabel("Decline")
      .build();

MFPPushNotificationButton thirdButton = new MFPPushNotificationButton.Builder("View Button")
              .setIcon("extension_circle_icon")
              .setLabel("view")
              .build();

List<MFPPushNotificationButton> getButtons =  new ArrayList<MFPPushNotificationButton>();
getButtons.add(firstButton);
getButtons.add(secondButton);
getButtons.add(thirdButton);

List<MFPPushNotificationButton> getButtons1 =  new ArrayList<MFPPushNotificationButton>();
getButtons1.add(firstButton);
getButtons1.add(secondButton);

List<MFPPushNotificationButton> getButtons2 =  new ArrayList<MFPPushNotificationButton>();
getButtons2.add(firstButton);

MFPPushNotificationCategory category = new MFPPushNotificationCategory.Builder("First_Button_Group1").setButtons(getButtons).build();
MFPPushNotificationCategory category1 = new MFPPushNotificationCategory.Builder("First_Button_Group2").setButtons(getButtons1).build();
MFPPushNotificationCategory category2 = new MFPPushNotificationCategory.Builder("First_Button_Group3").setButtons(getButtons2).build();

List<MFPPushNotificationCategory> categoryList =  new ArrayList<MFPPushNotificationCategory>();
categoryList.add(category);
categoryList.add(category1);
categoryList.add(category2);
options.setInteractiveNotificationCategories(categoryList);
push = MFPPush.getInstance();
push.initialize(getApplicationContext(),appGuid,clientSecret,options);
```

#### Handling Interactive push notifications

To identify the action clicked follow the below method,

```
notificationListener = new MFPPushNotificationListener() {

    @Override
    public void onReceive(final MFPSimplePushNotification message) {


        if (message.actionName.equals("Accept Button")){

           System.out.print("Clicked Accept Action");

       }else if (message.actionName.equals("Decline Button")){

           System.out.print("Clicked Decline Action");

       }else if (message.actionName.equals("View Button")){

           System.out.print("Clicked View Action");
       }

    }
};

```
This new callback method is invoked when user clicks the action button.

#### Adding custom DeviceId for registration

To send `DeviceId` use the `setDeviceId` method of `MFPPushNotificationOptions` class.

```
MFPPushNotificationOptions options = new MFPPushNotificationOptions();
options.setDeviceid("YOUR_DEVICE_ID");

```
>**Note**: Remember to keep custom DeviceId <strong>unique</strong> for each device.



## Android Custom Notification Sound

1. Create a folder named `raw` in the `res` directory of your android application and add the ringtone files to that folder.
2. Specify the ringtone file name when you send notification from Bluemix Push dashboard.


## Hold Android notifications

When your application goes into background, you might want Push Notifications to hold back notifications sent to your application. To hold notifications, call the hold() method in the onPause() method of the activity that is handling Push Notifications.

```
@Override
protected void onPause() {
    super.onPause();
    if (push != null) {
        push.hold();
    }
}
```

## Enable Monitoring

### Setup Monitoring

To monitor the current status of the notification within the application, you can implement the <strong>com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationStatusListener</strong> interface and define the method `onStatusChange(String messageId, MFPPushNotificationStatus status)`.

The <strong>messageId</strong> is the identifier of the message sent from the server. <strong>MFPPushNotificationStatus</strong> defines the status of the notifications as values,

* <strong>RECEIVED</strong> - App has received the notification.

* <strong>QUEUED</strong> - App queues the notification for invoking the notification listener.

* <strong>OPENED</strong> - User opens the notification by clicking the notification in the tray or by launching it from app icon or when the app is in foreground.

* <strong>DISMISSED</strong> - User clears/dismisses the notification in the tray.

You need to register the <strong>com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationStatusListener</strong> class with </strong>MFPPush</strong>.

```
push.setNotificationStatusListener(new MFPPushNotificationStatusListener() {
   @Override
   public void onStatusChange(String messageId, MFPPushNotificationStatus status) {
       // Handle status change
   }
 });

```

### Listening to the DISMISSED status

You can choose to listen to the DISMISSED status on either of the following conditions,

* When the app is <strong>active (running in foreground or background)</strong>

   Add the snippet to your <strong>AndroidManifest.xml</strong> file,

    ```
    <receiver android:name="com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationDismissHandler">
        <intent-filter>
        <action android:name="Your_Android_Package_Name.Cancel_IBMPushNotification"/>
        </intent-filter>
        </receiver>
    ```
* When the app is both <strong>active (running in foreground or background)</strong> and <strong>not running (closed)</strong>. Extend the <strong>com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationDismissHandler</strong> broadcast receiver and override the method <strong>onReceive()</strong>, where the </strong>MFPPushNotificationStatusListener</strong> should be registered before calling method </strong>onReceive()</strong> of the base class,

```
public class MyDismissHandler extends MFPPushNotificationDismissHandler {
   @Override
   public void onReceive(Context context, Intent intent) {
     MFPPush.getInstance().setNotificationStatusListener(new MFPPushNotificationStatusListener() {
       @Override
       public void onStatusChange(String messageId, MFPPushNotificationStatus status) {
       // Handle status change
       }
     });
     super.onReceive(context, intent);
   }
 }
```

Add the following snippet to you </strong>AndroidManifest.xml</strong> file,

```
<receiver android:name="Your_Android_Package_Name.Your_Handler">
<intent-filter>
<action android:name="Your_Android_Package_Name.Cancel_IBMPushNotification"/>
</intent-filter>
</receiver>

```

## Android Notification custom Options

 You can further customize the Push Notifications settings for sending notifications to Android devices.

### Collapse Key

Collapse keys are attached to notifications. If multiple notifications arrive sequentially with the same collapse key when the device is offline, they are collapsed. When a device comes online, it receives notifications from the FCM/GCM server, and displays only the latest notification bearing the same collapse key. If the collapse key is not set, both the new and old messages are stored for the future delivery.

### Sound

Indicates a sound clip to be played on the receipt of a notification. Supports default or the name of a sound resource that is bundled in the app.

### Icon

Specify the name of the icon to display for the notification. Ensure that you have packaged the icon in the <strong>res/drawable</strong> folder, with the client application.

### Priority

Specifies the options for assigning delivery priority to messages. A priority of <strong>high</strong> or <strong>max</strong> will result in <strong>heads-up notification</strong>, while <strong>low</strong> or <strong>default</strong> priority messages would not open network connections on a sleeping device. For messages with the option set to <strong>min</strong>, it will be a silent notification.

### Visibility

You can choose to set the notification visibility option to either <strong>public</strong> or <strong>private</strong>. The <strong>private</strong> option restricts public viewing and you can choose to enable it if your device is secure with a pin or pattern, and the notification setting is set to <strong>Hide sensitive notification content</strong>. When the visibility is set as <strong>private</strong>, a <strong>redact</strong> field must be mentioned. Only the content specified in the <strong>redact</strong> field will show up on a secure locked screen on the device. Choosing <strong>public</strong> would render the notifications to be freely read.

### Time to live

This value is set in seconds. If this parameter is not specified, the FCM/GCM server stores the message for four weeks and will try to deliver. The validity expires after four weeks. The possible value range is from 0 to 2,419,200 seconds.

### Delay when idle

Setting this value to <strong>true</strong> instructs the FCM/GCM server not to deliver the notification if the device is idle. Set this value to <strong>false</strong>, to ensure delivery of notification even if the device is idle

### Sync

By setting this option to <strong>true</strong>, notifications across all your registered devices are in sync. If the user with a username has multiple devices with the same application installed, reading the notification on one device ensures deletion of notifications in the other devices. You need to ensure that you are registered with Push Notifications service with userId for this option to work.

### Additional payload

Specifies the custom payload values for your notifications.


## Samples and videos

* Please visit for samples - [Github Sample](https://github.com/ibm-bluemix-mobile-services/bms-samples-android-hellopush)

* Video Tutorials Available here - [Bluemix Push Notifications](https://www.youtube.com/channel/UCRr2Wou-z91fD6QOYtZiHGA)

### Learning More

* Visit the **[Bluemix Developers Community](https://developer.ibm.com/bluemix/)**.

* [Getting started with IBM MobileFirst Platform for iOS](https://www.ng.bluemix.net/docs/mobile/index.html)

### Connect with Bluemix

[Twitter](https://twitter.com/ibmbluemix) |
[YouTube](https://www.youtube.com/playlist?list=PLzpeuWUENMK2d3L5qCITo2GQEt-7r0oqm) |
[Blog](https://developer.ibm.com/bluemix/blog/) |
[Facebook](https://www.facebook.com/ibmbluemix) |
[Meetup](http://www.meetup.com/bluemix/)

=======================
Copyright 2015 IBM Corp.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

IBM Bluemix Push Notifications Android SDK
==========================================

[![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push.svg?branch=master)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push)
[![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push.svg?branch=development)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5c49c09a1f9f45c99c39623f8033d1eb)](https://www.codacy.com/app/ibm-bluemix-mobile-services/bms-clientsdk-android-push?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ibm-bluemix-mobile-services/bms-clientsdk-android-push&amp;utm_campaign=Badge_Grade)

The [Bluemix Push Notifications service](https://console.ng.bluemix.net/catalog/services/push-notifications) provides a unified push service to send real-time notifications to mobile devices. The Push Notifications Android SDK enables Android apps to receive the sent push notifications.

Ensure that you go through [Bluemix Push Notifications service documentation](https://console.ng.bluemix.net/docs/services/mobilepush/index.html#gettingstartedtemplate) before you start.

## Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Initialize SDK](#initialize-sdk)
	- [Include client Push SDK with Gradle](#include-client-push-sdk-with-gradle)
	- [Include core SDK and Push SDK](#include-core-sdk-and-push-sdk)	
- [Register for notifications](#register-for-notifications)
	- [Receiving notifications](#receiving-notifications)
	- [Unregistering from notifications](#unregistering-from-notifications)
- [Push Notifications service tags](#push-notifications-service-tags)
	- [Retrieve tags](#retrieve-tags)
	- [Subscribe to tags](#subscribe-to-tags)
	- [Retrieve subscribed tags](#retrieve-subscribed-tags)
	- [Unsubscribe from tags](#unsubscribe-from-tags)
- [Notification options](#notification-options)
	- [Interactive notifications](#interactive-notifications)
	- [Adding custom DeviceId for registration](#adding-custom-deviceid-for-registration)
	- [Advanced options](#advanced-options)
	- [Holding notifications](#holding-notifications)
- [Monitoring](#monitoring)
	- [Listening to the DISMISSED status](#listening-to-the-dismissed-status)
- [Samples and videos](#samples-and-videos)


## Prerequisites


 * [Push Notifications Android Client SDK package](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.ibm.mobilefirstplatform.clientsdk.android%22)
 * Android API level 14 or later
 * Android 4.0 or later
 * [Android Studio](https://developer.android.com/studio/index.html)
 * [Gradle](https://gradle.org/install)
 * [Android helloPush sample app](https://github.com/ibm-bluemix-mobile-services/bms-samples-android-hellopush)
 * [BMSCore](https://github.com/ibm-bluemix-mobile-services/bms-clientsdk-android-core)
 * [BMSPush](https://github.com/ibm-bluemix-mobile-services/bms-clientsdk-android-push) SDKs installed by using 
  either Android Studio or Gradle

## Installation

Choose to integrate the Push Notifications Android Client SDK package using either of the following options:

- Download and import the package to your Android Studio project
- Get the package through Gradle

## Initialize SDK


Ensure that you have gone through [Configuring credentials for a notification provider](https://console.ng.bluemix.net/docs/services/mobilepush/push_step_1.html#push_step_1) to setup the FCM project and obtain your credentials.


### Include client Push SDK with Gradle

Configure the Module level `build.gradle` and Project level `build.gradle` files.

1. Add Bluemix Push Notifications Android SDK dependency to your Project level `build.gradle` file.
	
	```
	dependencies {
    	........
		compile group: 'com.ibm.mobilefirstplatform.clientsdk.android',
         	name: 'push',
        	 version: '3.+',
        	 ext: 'aar',
         	transitive: true
		.......
	}
	```

2. Add the following dependencies to your Module level `build.gradle` file.
		
	 ```
	  dependencies {
	  	 classpath 'com.android.tools.build:gradle:2.2.3'
	   	classpath 'com.google.gms:google-services:3.0.0'
	 }
  	```

3. Add the `Google Play services` dependency to your Module level `build.gradle` file at the end, after the `dependencies{.....}`:

	```
	apply plugin: 'com.google.gms.google-services'
	```
4. Configure the `AndroidManifest.xml` file. Refer the [example here](https://github.com/ibm-bluemix-mobile-services/bms-samples-android-hellopush/blob/master/helloPush/app/src/main/AndroidManifest.xml). Add the following permissions inside application's `AndroidManifest.xml` file. 

	 ```
	 <uses-permission android:name="android.permission.INTERNET"/>
	 <uses-permission android:name="android.permission.GET_ACCOUNTS" />
	 <uses-permission android:name="android.permission.USE_CREDENTIALS" />
	 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	 ```
5. Add the notification intent settings for the activity. This setting starts the application when the user clicks the received notification from the notification area.

	```
	 <intent-filter>
     <action android:name="Your_Android_Package_Name.IBMPushNotification"/>
     <category  android:name="android.intent.category.DEFAULT"/>
	 	</intent-filter>
	```
	>**Note**: Replace `Your_Android_Package_Name` in the previous action with the application package name used in your application.

6. Update the `Firebase Cloud Messaging (FCM)` or `Google Cloud Messaging (GCM)` intent service and intent filters for the `RECEIVE` and `REGISTRATION` event notifications:

	```
	<service android:name="com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushIntentService"
     android:exported="true" >
     <intent-filter>
         <action android:name="com.google.firebase.MESSAGING_EVENT" />
	 </intent-filter>
	</service>
	<service
	android:name="com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush"android:exported="true" >
	<intent-filter>
       <action android:name="com.google.firebase.INSTANCE_ID_EVENT" />
	</intent-filter>
	</service>
	```

7. Push Notifications service supports retrieval of individual notifications from the notification tray. For notifications accessed from the notification tray, you are provided with a handle only to the notification that is being clicked. All notifications are displayed when the application is opened normally. Update your `AndroidManifest.xml` file with the following snippet to use this functionality:
	
	```
	<activity android:name="com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationHandler"
	   android:theme="@android:style/Theme.NoDisplay"/>
	```
8. Add the `google-services.json` in Android application module root directory.  For more information on how to add this file, see [Setup the Push client SDK on FCM](https://console.ng.bluemix.net/docs/services/mobilepush/push_step_3.html#push_step_3_Android).


### Include core SDK and Push SDK

A common place to put the initialization code is the `onCreate()` method of the `main activity` in your Android application: 

```
// Initialize the SDK
BMSClient.getInstance().initialize(this, "bluemixRegionSuffix");
//Initialize client Push SDK
MFPPush push = MFPPush.getInstance();
push.initialize(getApplicationContext(), "appGUID", "clientSecret");
```

Where `bluemixRegionSuffix` specifies the location where the app is hosted. You can use any of the following values:

- `BMSClient.REGION_US_SOUTH`
- `BMSClient.REGION_UK`
- `BMSClient.REGION_SYDNEY`

The `appGUID` is the push app GUID value, while `clientSecret` is the push client secret value.


## Register for notifications

Use the `MFPPush.register()` API to register the device with Push Notifications service. 

The following options are supported:

- Register without userId:
	
	```
	//Register Android devices
	push.registerDevice(new MFPPushResponseListener<String>() {
    @Override
    public void onSuccess(String response) {
    //handle successful device registration here
    }
    @Override
    public void onFailure(MFPPushException ex) {
    //handle failure in device registration here
    }
	  		});
	```

- Register with UserId. For `userId` based notification, the register method will accept one more parameter - `userId`.

	```
	// Register the device to Push Notifications
	push.registerDeviceWithUserId("userId",new MFPPushResponseListener<String>() {
	@Override	
	public void onSuccess(String response) {
    //handle successful device registration here
	}
	@Override	
	public void onFailure(MFPPushException ex) {
    //handle failure in device registration here
	}
	});
	```

The userId is used to pass the unique userId value for registering for Push Notifications. If the userId is provided, ensure that the the client secret value is also provided.


### Receiving notifications

To register the `notificationListener` object with Push Notifications service, use the `MFPPush.listen()` method. This method is typically called from the `onResume()` and `onPause()` methods of the activity that is handling push notifications.

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

### Unregistering from notifications

Use the following code snippets to un-register from Push Notifications.
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
>**Note**: To unregister from the `UserId` based registration, you have to call the registration method. See the `Register without userId option` in [Register for notifications](#register-for-notifications).


## Push Notifications service tags

### Retrieve tags

The `getTags` API returns the list of available tags to which the device can subscribe. After subscribing to a particular tag, the device can receive notifications that are sent for that tag. Call the push service to get subscriptions for a tag.

Add the following code snippet to your mobile application to get a list of available tags to which the device can subscribe.

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

### Subscribe to tags

The `subscribe` API will subscribe the device for the list of given tags. After the device is subscribed to a particular tag, the device can receive notifications that are sent for that tag. 

Add the following code snippet to your Swift mobile application to subscribe to a list of tags.

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

### Retrieve subscribed tags

The `getSubscriptions` API will return the list of tags to which the device is subscribed. Use the following code snippets in the mobile application to get the  subscription list.

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

### Unsubscribe from tags

The `unsubscribeFromTags` API will remove the device subscription from the list tags. Use the following code snippets to allow your devices to get unsubscribe from a tag.

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

## Notification options

The following notification options are supported.


### Interactive notifications

1. To enable interactive push notifications, the notification action parameters must be passed in as part of the notification object.  The following is a sample code to enable interactive notifications:

```
	MFPPushNotificationOptions options = new MFPPushNotificationOptions();
	MFPPushNotificationButton acceptButton = new MFPPushNotificationButton.Builder("Accept Button")
      .setIcon("check_circle_icon")
      .setLabel("Accept")
      .build();
	MFPPushNotificationButton declineButton = new MFPPushNotificationButton.Builder("Decline Button")
      .setIcon("extension_circle_icon")
      .setLabel("Decline")
      .build();
	MFPPushNotificationButton viewButton = new MFPPushNotificationButton.Builder("View Button")
              .setIcon("extension_circle_icon")
              .setLabel("view")
              .build();
	List<MFPPushNotificationButton> buttonGroup_1 =  new ArrayList<MFPPushNotificationButton>();
	getButtons.add(acceptButton);
	getButtons.add(declineButton);
	getButtons.add(viewButton);
	List<MFPPushNotificationButton> buttonGroup_2 =  new ArrayList<MFPPushNotificationButton>();
	getButtons1.add(acceptButton);
	getButtons1.add(declineButton);
	List<MFPPushNotificationButton> buttonGroup_3 =  new ArrayList<MFPPushNotificationButton>();
	getButtons2.add(acceptButton);
	MFPPushNotificationCategory category = new MFPPushNotificationCategory.Builder("First_Button_Group1").setButtons(buttonGroup_1).build();
	MFPPushNotificationCategory category1 = new MFPPushNotificationCategory.Builder("First_Button_Group2").setButtons(buttonGroup_2).build();
	MFPPushNotificationCategory category2 = new MFPPushNotificationCategory.Builder("First_Button_Group3").setButtons(buttonGroup_3).build();
	List<MFPPushNotificationCategory> categoryList =  new ArrayList<MFPPushNotificationCategory>();
	categoryList.add(category);
	categoryList.add(category1);
	categoryList.add(category2);
	options.setInteractiveNotificationCategories(categoryList);
	push = MFPPush.getInstance();
	push.initialize(getApplicationContext(),"appGUID", "clientSecret",options);
```

2. To handle the interactive notifications by identifying which action is clicked, follow the method:

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
	
This callback method is invoked when user clicks the action button.

### Adding custom DeviceId for registration

To send `DeviceId` use the `setDeviceId` method of `MFPPushNotificationOptions` class.

```
	MFPPushNotificationOptions options = new MFPPushNotificationOptions();
	options.setDeviceid("YOUR_DEVICE_ID");
```

>**Note**: Remember to keep custom DeviceId `unique` for each device.


### Advanced options

You can choose to specify a ring-tone for your notifications. To specify a ring-tone, complete the following steps:

1. Create a folder named `raw` in the `res` directory of your android application and add the ring-tone files to that folder.
2. Specify the ring-tone file name when you send notification from Bluemix Push  Notifications dashboard.


### Holding notifications

When your application goes into background, you might want Push Notifications to hold back notifications sent to your application. To hold notifications, call the `hold()` method in the `onPause()` method of the activity that is handling Push Notifications.

```
	@Override
	protected void onPause() {
    	super.onPause();
	    if (push != null) {
    	    push.hold();
    	}
	}
```

## Monitoring

To monitor the current status of the notification within the application, you can implement the `com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationStatusListener` interface and define the method `onStatusChange(String messageId, MFPPushNotificationStatus status)`.

The `messageId` is the identifier of the message sent from the server. `MFPPushNotificationStatus` defines the status of the notifications as values,

* `RECEIVED` - App has received the notification.

* `QUEUED` - App queues the notification for invoking the notification listener.

* `OPENED` - User opens the notification by clicking the notification in the tray or by launching it from app icon or when the app is in foreground.

* `DISMISSED` - User clears/dismisses the notification in the tray.

You need to register the `com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationStatusListener` class with `MFPPush`.

```
	push.setNotificationStatusListener(new MFPPushNotificationStatusListener() {
	@Override
	   public void onStatusChange(String messageId, MFPPushNotificationStatus status) {
       // Handle status change
	}
	});
```

### Listening to the DISMISSED status

You can choose to listen to the DISMISSED status on either of the following conditions:

* When the app is `active (running in foreground or background)`. Add the snippet to your `AndroidManifest.xml` file:

```
		<receiver android:name="com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationDismissHandler">
    	  <intent-filter>
        <action android:name="Your_Android_Package_Name.Cancel_IBMPushNotification"/>
    	  </intent-filter>
   	</receiver>
```
* When the app is both `active (running in foreground or background)` and `not running (closed)`. Extend the `com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationDismissHandler` broadcast receiver and override the method `onReceive()`, where the `MFPPushNotificationStatusListener` should be registered before calling method `onReceive()` of the base class:

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

Add the following snippet to you `AndroidManifest.xml` file:

```
	<receiver android:name="Your_Android_Package_Name.Your_Handler">
	<intent-filter>
	    <action android:name="Your_Android_Package_Name.Cancel_IBMPushNotification"/>
	</intent-filter>
	</receiver>
```

## Samples and videos

* For samples, visit - [Github Sample](https://github.com/ibm-bluemix-mobile-services/bms-samples-android-hellopush)

* For video tutorials, visit - [Bluemix Push Notifications](https://www.youtube.com/channel/UCRr2Wou-z91fD6QOYtZiHGA)

### Learning More

* Visit the **[Bluemix Developers Community](https://developer.ibm.com/bluemix/)**.

* [Getting started with IBM MobileFirst Platform for iOS](https://www.ng.bluemix.net/docs/mobile/index.html)

### Connect with Bluemix

[Twitter](https://twitter.com/ibmbluemix)|
[YouTube](https://www.youtube.com/watch?v=dQ1WcY_Ill4) |
[Blog](https://developer.ibm.com/bluemix/blog/) |
[Facebook](https://www.facebook.com/ibmbluemix) |
[Meetup](http://www.meetup.com/bluemix/)

=======================
Copyright 2016-17 IBM Corp.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

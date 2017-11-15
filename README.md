IBM Bluemix Mobile Services - Client SDK Android Push
===

[![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push.svg?branch=master)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push)
[![Build Status](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push.svg?branch=development)](https://travis-ci.org/ibm-bluemix-mobile-services/bms-clientsdk-android-push)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5c49c09a1f9f45c99c39623f8033d1eb)](https://www.codacy.com/app/ibm-bluemix-mobile-services/bms-clientsdk-android-push?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ibm-bluemix-mobile-services/bms-clientsdk-android-push&amp;utm_campaign=Badge_Grade)

This is the push component of Android SDK for IBM Bluemix Mobile Services. https://console.ng.bluemix.net/solutions/mobilefirst

### Installation
You can either download and import this package to your Android Studio project or get it via Gradle.

### Contents
This package contains the push components of Android SDK.  This package has to be used in combination with core SDK.

### Supported Levels
The package is supported on Android API level 14 and up (Android 4.0 and up).
#### Multidex support prior to Android 5.0
Versions of the platform prior to Android 5.0 (API level 21) use the Dalvik runtime for executing app code.
1. Add the following in your gradle file,

```

android {
...
   defaultConfig{
    ....
      multiDexEnabled true
    ....
   }
...
}
...
dependencies {
  .....
  compile 'com.android.support:multidex:1.0.1'
  ....
}
```
2. In the `manifest.xml` file add teh following,

```
<application 
    android:name="android.support.multidex.MultiDexApplication"
```

### Sound Configuration
1. Create a folder named `raw` in the `res` directory of your android application and add the ringtone files to that folder.
2. Specify the ringtone file name when you send notification from Bluemix Push dashboard.

### Bluemix Region - Core SDK
 Bluemix Region specifies the location where the app hosted. You can either use one of following values or specify the region manually. 
   - `BMSClient.REGION_US_SOUTH`
   - `BMSClient.REGION_UK`
   - `BMSClient.REGION_SYDNEY` 
   - `BMSClient.REGION_GERMANY`
   
>**Note**:  If you are using dedicated service, use `overrideServerHost` and add any of the `bluemixRegion` (bluemix region) value.

#### 3.6.3

* removed unused code snippets
* Added new icon for push

#### 3.6.2

* New way to set message icon. 
  - Add an image named `push_notification_icon.png` in the app @drawable
* Fixed Device Unregistration issue.

#### 3.6.1

* removed duplicate code
* Updated the Core dependency to the working version
* Updated the doc with Bluemix region specification


#### 3.6

 * Added custom DeviceId for registration To send `DeviceId` please use `BMSPushClientOptions` class method,

```
MFPPushNotificationOptions options = new MFPPushNotificationOptions();
options.setDeviceid("your_device_id");
```

>**Note**: Remember to keep custom DeviceId <strong>unique</strong> for each device.

#### 3.5

* Added Multiple category in actionable push notification.
* Introduced new `init` method.

To add categories you have to follow the below steps,

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

  MFPPushNotificationButton secondButton1 = new MFPPushNotificationButton.Builder("Decline Button2")
                .setIcon("extension_circle_icon")
                .setLabel("Decline2")
                .build();

  List<MFPPushNotificationButton> getButtons =  new ArrayList<MFPPushNotificationButton>();
  getButtons.add(firstButton);
  getButtons.add(secondButton);
  getButtons.add(secondButton1);

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
#### 3.4.1

* Fixed actionable push notification bugs.

#### 3.4

* Fixed bug that causes crash in certain apps due to unavailability of certain android manifest permissions.

#### 3.3

* Bug fixes. Support expandable notifications starting Android version 4.1 instead of 5.0.
* Fix registration happening when deviceToken returned from FCM is null.


#### 3.2

* Added support to listen to the notification status by registering the MFPPushNotificationStatusListener with MFPPush as shown below

````
push.setNotificationStatusListener(new MFPPushNotificationStatusListener() {
    @Override
        public void onStatusChange(String messageId, MFPPushNotificationStatus status) {
        // Handle status change
        }
    });

````

Add the below snippet in AndroidManifest.xml to capture the notification dismissal status.

````
<receiver android:name="com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationDismissHandler">
        <intent-filter>
            <action android:name="Your_Android_Package_Name.Cancel_IBMPushNotification"/>
        </intent-filter>
    </receiver>
````

#### 3.1
* Bug fix: Fix support for interactive push notification.  Interactive notifications can be used by using the below code.

````
 MFPPushNotificationOptions options = new MFPPushNotificationOptions();
        MFPPushNotificationButton firstButton = new MFPPushNotificationButton.Builder("Accept Button")
                .setIcon("check_circle")
                .setLabel("Accept")
                .setPerformsInForeground(true)
                .build();

        MFPPushNotificationButton secondButton = new MFPPushNotificationButton.Builder("Decline Button")
                .setIcon("extension")
                .setLabel("Decline")
                .setPerformsInForeground(true)
                .build();

        options.setInteractiveNotificationButtonGroup("First_Button_Group", firstButton, secondButton);
        MFPPush.getInstance().setNotificationOptions(getApplicationContext(),options);

       // Action Handler
        @Override
         public void onReceive(final MFPSimplePushNotification message) {

             if (message.actionName.equals("Accept Button")){

                 System.out.print("Clicked Accept Action");

             }else if (message.actionName.equals("Decline Button")){
                 System.out.print("Clicked Decline Action");
             }
         }
 ````

#### 3.0

* Update push service to use FCM instead of GCM. All existing applications will work as-is and moving forward all new applications will use FCM.  The client application set up is different in FCM in comparison to the old GCM model. Please refer to [documentation](https://console.ng.bluemix.net/docs/services/mobilepush/c_android_enable.html) for details.
* To compile the SDK, create a FCM project in google console -> Add firebase to android application -> Add the package names `com.ibm.mobilefirstplatform.clientsdk.android.app` , `com.ibm.mobilefirstplatform.clientsdk.android.push` and `com.ibm.mobilefirst.clientsdk.android.app` -> Copy the generated google-services.json file inside the `app` directory.


#### 2.0.9
* Support to specify multiple custom icons for notification.
* Enhanced error checking and bug fixes.

#### 2.0.8
* Included support to retrieve individual notifications from the notification tray. If the notification is tapped on from the notification tray, the developer is provided with a handle only to the notification that is being clicked.  When the application is openend normally, then all the notifications are displayed.

Include the below in your AndroidManifest.xml to use this functionality

```
<activity android:name="com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationHandler"
          android:theme="@android:style/Theme.NoDisplay"/>
```

#### 2.0.7
* Fixed bugs.

#### 2.0.6
* Updated SDK to include custom android notification options - expandable notifications, priority, visibility, sync notifications across devices, timeToLive etc
* Set default options for these parameters on the SDK, that can be overridden from the server during send notification call.

#### 2.0.5
* Updated the userId based API.

#### 2.0.4
* Updated with userId based notifications support.
* To register UserId based notifications follow the below steps,

```
    1. MFPPush push = MFPPush.getInstance();
    2. push.initialize(getApplicationContext(),"your appGuid of push", "clientSecret");
    3. registerDeviceWithUserId("your userId"",new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String response) {
                updateTextView("Device is registered with Push Service.");
                displayTags();
            }

            @Override
            public void onFailure(MFPPushException ex) {
                updateTextView("Error registering with Push Service...\n" + ex.toString()
                + "Push notifications will not be received.");
            }
        });
```

#### 2.0.2
* Update references of core to 2.0.1
* Update google play services to version 9.0.1

#### 2.0.1
* Bug Fix - Including support for custom ringtone on receiving push notifications.

#### 2.0.0
* Update references of core to 2.0.0
* Several BMS core APIs have been updated, and those would not be compatible with previous versions.

#### 1.0.2
* Update references of core to 1.1.0.
* Fixed send for Logger in MFPPushInvoker class.
* Replace deprecated method logger.getPackageName(), replaced by logger.getName().

#### 1.0.1
* Use unprotected public API to get GCM senderId information from server, during registration.

#### 1.0.0
* Initial release


### Samples & videos

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

IBM Bluemix Mobile Services - Client SDK Android Push
===

This is the push component of Android SDK for IBM Bluemix Mobile Services. https://console.ng.bluemix.net/solutions/mobilefirst

###Installation
You can either download and import this package to your Android Studio project or get it via Gradle.

###Contents
This package contains the push components of Android SDK.  This package has to be used in combination with core SDK.

###Supported Levels
The package is supported on Android API level 14 and up (Android 4.0 and up).

####2.0.0
* Update references of core to 2.0.0
* Several BMS core APIs have been updated, and those would not be compatible with previous versions.

####1.0.2
* Update references of core to 1.1.0.
* Fixed send for Logger in MFPPushInvoker class.
* Replace deprecated method logger.getPackageName(), replaced by logger.getName().

####1.0.1
* Use unprotected public API to get GCM senderId information from server, during registration.

####1.0.0
* Initial release
 

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

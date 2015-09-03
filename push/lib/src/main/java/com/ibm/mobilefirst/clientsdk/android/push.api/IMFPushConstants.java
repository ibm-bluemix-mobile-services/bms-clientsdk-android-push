/*
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
*/

package com.ibm.mobilefirst.clientsdk.android.push.api;

/**
 * Created by jialfred on 9/2/15.
 */
public interface IMFPushConstants {
	public static final String FROM_NOTIFICATION_BAR = "notificationBar";

	public static final String NAME = "name";
	public static final String TOKEN = "token";
	public static final String PLATFORM = "platform";
	public static final String DEVICE_ID = "deviceId";
	public static final String CONSUMER_ID = "consumerId";
	public static final String TAG_NAME = "tagName";
	public static final String TAGS = "tags";
	public static final String SUBSCRIPTIONS = "subscriptions";
	public static final String REGISTRATION_ID = "registrationId";

	public static final String ENVIRONMENT = "environment";
	public static final String SANDBOX_ENVIRONMENT = "sandbox";
	public static final String PRODUCTION_ENVIRONMENT = "production";

	public static final String SANDBOX_CREDENTIALS = "gcmSandboxCredentials";
	public static final String PRODUCTION_CREDENTIALS = "gcmProductionCredentials";
	public static final String SENDER_ID = "senderId";

    public static final double MIN_SUPPORTED_ANDRIOD_VERSION = 2.2;
}

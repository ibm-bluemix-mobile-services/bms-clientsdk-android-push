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

package com.ibm.mobilefirstplatform.clientsdk.android.push.internal;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushIntentService;

/**
 * MFPPushBroadcastReceiver receives GCM messages and delivers them to
 * <class>com.ibm.mobile.services.push.MFPPushIntentService</class>.
 *
 * The class should be configured as the broadcast receiver in
 * AndroidManifest.xml of the android application.
 *
 * <pre>
 * <p> For example, </p>
 * {@code
 * <application>
 * ....
 *  	<receiver android:name="com.ibm.mobile.services.push.MFPPushBroadcastReceiver"
 *  		android:permission="com.google.android.c2dm.permission.SEND">
 * 			<intent-filter>
 * 				<action android:name="com.google.android.c2dm.intent.RECEIVE" />
 * 				<category android:name="<package name of android application>" />
 * 			</intent-filter>
 * 			<intent-filter>
 *				<action android:name="android.intent.action.BOOT_COMPLETED" />
 *				<category android:name="<package name of android application>" />
 *			</intent-filter>
 * 		</receiver>
 * ...
 * </application>
 * }
 */

public class MFPPushBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
			ComponentName component = new ComponentName(
					context.getPackageName(),
					MFPPushIntentService.class.getName());
			startWakefulService(context, (intent.setComponent(component)));
			setResultCode(Activity.RESULT_OK);
	}
}

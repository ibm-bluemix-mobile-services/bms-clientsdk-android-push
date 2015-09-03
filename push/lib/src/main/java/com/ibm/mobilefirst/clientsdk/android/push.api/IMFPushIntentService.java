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

//import static com.ibm.mobile.services.push.internal.IMFPushUtils.debug;
//import static com.ibm.mobile.services.push.internal.IMFPushUtils.entering;
//import static com.ibm.mobile.services.push.internal.IMFPushUtils.error;
//import static com.ibm.mobile.services.push.internal.IMFPushUtils.exiting;
//import static com.ibm.mobile.services.push.internal.IMFPushUtils.getIntentPrefix;
//import static com.ibm.mobile.services.push.internal.IMFPushUtils.getResourceId;
//import static com.ibm.mobile.services.push.internal.IMFPushUtils.warning;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.LinkedList;
import java.util.Random;

//import com.ibm.mobile.services.core.internal.IBMLogger;
//import com.ibm.mobile.services.push.internal.IMFPushConstants;
//import com.ibm.mobile.services.push.internal.IMFPushUtils;
//import com.ibm.mobile.services.push.internal.IMFInternalPushMessage;
//import com.ibm.mobile.services.push.internal.metrics.Manager;
//import com.ibm.mobile.services.push.internal.metrics.MetricAction;
//import com.ibm.mobile.services.push.internal.wakeful.AlarmReceiver;

/**
 * IMFPushIntentService responsible for handling communication from GCM (Google
 * Cloud Messaging). This class should be configured as the GCM intent service
 * in AndroidManifest.xml of the android application as follows:
 *
 * <pre>
 * <p></p>
 * {@code
 * <application>
 * ...
 * 	<service android:name="com.ibm.mobile.services.push.IMFPushIntentService" />
 * ...
 * </application>
 * }
 * </pre>
 */

/**
 * Created by jialfred on 9/2/15.
 */
public class IMFPushIntentService extends IntentService {

	public static final String IBM_PUSH_NOTIFICATION = ".IBMPushNotification";
	public static final String GCM_REGISTERED = ".C2DM_REGISTERED";
	public static final String GCM_UNREGISTERED = ".C2DM_UNREGISTERED";
	public static final String GCM_MESSAGE = ".C2DM_MESSAGE";
	public static final String GCM_ERROR = ".C2DM_ERROR";

	public static final String GCM_EXTRA_MESSAGE = "message";
	public static final String GCM_EXTRA_REGISTRATION_ID = "registrationId";
	public static final String GCM_EXTRA_ERROR_ID = "errorId";

	private static boolean isAppForeground = true;

	private static Random randomObj = new Random();

	private LinkedList<Intent> intentsQueue = new LinkedList<Intent>();

	public IMFPushIntentService() {
		super("IMFPushIntentService");
	}

	public static boolean isAppForeground() {
		return isAppForeground;
	}

	public static void setAppForeground(boolean isAppForeground) {
		IMFPushIntentService.isAppForeground = isAppForeground;
	}

	private BroadcastReceiver resultReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (getResultCode() == Activity.RESULT_FIRST_USER
					|| !isAppForeground()) {
				//IMFPushIntentService: App is not running in foreground. Create a notification
				onUnhandled(context, intent);
			}
		}
	};

	private void saveInSharedPreferences(IMFInternalPushMessage message) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				IMFPush.PREFS_NAME, Context.MODE_PRIVATE);
		String msgString = message.toJson().toString();
		//PREFS_NOTIFICATION_COUNT value provides the count of number of undelivered notifications stored in the sharedpreferences
		int count = sharedPreferences.getInt(IMFPush.PREFS_NOTIFICATION_COUNT, 0);
		//Increment the count and use it for the next notification
		count++;
		IMFPushUtils.storeContentInSharedPreferences(sharedPreferences, IMFPush.PREFS_NOTIFICATION_MSG + count, msgString);

		IMFPushUtils.storeContentInSharedPreferences(sharedPreferences, IMFPush.PREFS_NOTIFICATION_COUNT, count);
	}

	private void onUnhandled(Context context, Intent intent) {
		String action = intent.getAction();
		if ((IMFPushUtils.getIntentPrefix(context) + GCM_MESSAGE).equals(action)) {
			IMFInternalPushMessage message = intent
					.getParcelableExtra(GCM_EXTRA_MESSAGE);
			saveInSharedPreferences(message);

			intent = new Intent(IMFPushUtils.getIntentPrefix(context)
					+ IBM_PUSH_NOTIFICATION);
			intent.putExtra(GCM_EXTRA_MESSAGE, message);
			generateNotification(context, message.getAlert(),
					getNotificationTitle(context), message.getAlert(),
					getNotificationIcon(), intent);
		}
	}

	private String getNotificationTitle(Context context) {
		// Check if push_notification_title is defined, if not get the
		// application name
		int notificationTitle = -1;
		try {
			notificationTitle = IMFPushUtils.getResourceId(getApplicationContext(),
					"string", "push_notification_title");
			return context.getString(notificationTitle);
		} catch (Exception e) {
			// ignore the exception
		}

		if (notificationTitle == -1) {
			ApplicationInfo appInfo = null;
			PackageManager packManager = context.getPackageManager();
			try {
				appInfo = packManager.getApplicationInfo(
						context.getPackageName(), 0);
			} catch (Exception e) {
				//warning - Notification will not have a title because application name is not available.
			}

			if (appInfo != null) {
				return (String) packManager.getApplicationLabel(appInfo);
			}
		}

		return "";
	}

	private int getNotificationIcon() {
		int notificationIcon;
		try {
			notificationIcon = IMFPushUtils.getResourceId(getApplicationContext(),
					"drawable", "push_notification_icon");
		} catch (Exception e) {
			//Failed to find the icon resource.  Add the icon file(push_notification_icon.png) under /res/drawable foler.
			//Notification will be showin with a default star icon from Android.
			notificationIcon = android.R.drawable.btn_star;
		}
		return notificationIcon;
	}

	@SuppressWarnings("deprecation")
	private void generateNotification(Context context, String ticker,
			String title, String msg, int icon, Intent intent) {
		long when = System.currentTimeMillis();

        //TODO: jialfred - setLatestEventInfo deprecated. Will this work on older devices?
//		Notification notification = new Notification(icon, ticker, when);
//		notification.setLatestEventInfo(context, title, msg, PendingIntent
//				.getActivity(context, 0, intent,
//						PendingIntent.FLAG_UPDATE_CURRENT));
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//		NotificationManager notificationManager = (NotificationManager) context
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(randomObj.nextInt(), notification);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        Notification notification = builder.setContentIntent(PendingIntent
				.getActivity(context, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(icon).setTicker(ticker).setWhen(when)
                .setAutoCancel(true).setContentTitle(title)
                .setContentText(msg).build();
        NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(randomObj.nextInt(), notification);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		intent = handleMessageIntent(intent, extras);
		IMFPushBroadcastReceiver.completeWakefulIntent(intent);
	}

	private Intent handleMessageIntent(Intent intent, Bundle extras) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging
				.getInstance(getApplicationContext());
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				//received a message from GCM Server.
				IMFInternalPushMessage message = new IMFInternalPushMessage(intent);
				intent = new Intent(IMFPushUtils.getIntentPrefix(getApplicationContext())
						+ GCM_MESSAGE);
				intent.putExtra(GCM_EXTRA_MESSAGE, message);

				if (!isAppForeground()) {
					//App is not on foreground. Queue the intent for later re-sending when app is back on foreground.
					intentsQueue.add(intent);
				}
				getApplicationContext().sendOrderedBroadcast(intent, null,
						resultReceiver, null, Activity.RESULT_FIRST_USER, null,
						null);
			}
		}
		return intent;
	}
}

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

package com.ibm.mobilefirstplatform.clientsdk.android.push.api;

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

import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPInternalPushMessage;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushBroadcastReceiver;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushUtils;

import java.util.LinkedList;
import java.util.Random;

import android.media.RingtoneManager;
import android.net.Uri;


/**
 * MFPPushIntentService responsible for handling communication from GCM (Google
 * Cloud Messaging). This class should be configured as the GCM intent service
 * in AndroidManifest.xml of the android application as follows:
 *
 * <pre>
 * <p></p>
 * {@code
 * <application>
 * ...
 * 	<service android:name="com.ibm.mobile.services.push.MFPPushIntentService" />
 * ...
 * </application>
 * }
 * </pre>
 */

public class MFPPushIntentService extends IntentService {

	public static final String IBM_PUSH_NOTIFICATION = ".IBMPushNotification";
	public static final String GCM_MESSAGE = ".C2DM_MESSAGE";
	public static final String GCM_EXTRA_MESSAGE = "message";

	public static boolean isAppForeground = true;

	private static Random randomObj = new Random();

	private LinkedList<Intent> intentsQueue = new LinkedList<Intent>();

	private static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + MFPPushIntentService.class.getSimpleName());

	public MFPPushIntentService() {
		super("MFPPushIntentService");
	}

	public static boolean isAppForeground() {
		return isAppForeground;
	}

	public static void setAppForeground(boolean isAppForeground) {
		MFPPushIntentService.isAppForeground = isAppForeground;
	}

	private BroadcastReceiver resultReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (getResultCode() == Activity.RESULT_FIRST_USER
					|| !isAppForeground()) {
				logger.debug("MFPPushIntentService: App is not running in foreground. Create a notification.");
				onUnhandled(context, intent);
			}
		}
	};

	private void saveInSharedPreferences(MFPInternalPushMessage message) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				MFPPush.PREFS_NAME, Context.MODE_PRIVATE);
		String msgString = message.toJson().toString();
		//PREFS_NOTIFICATION_COUNT value provides the count of number of undelivered notifications stored in the sharedpreferences
		int count = sharedPreferences.getInt(MFPPush.PREFS_NOTIFICATION_COUNT, 0);
		//Increment the count and use it for the next notification
		count++;
		MFPPushUtils.storeContentInSharedPreferences(sharedPreferences, MFPPush.PREFS_NOTIFICATION_MSG + count, msgString);

		MFPPushUtils.storeContentInSharedPreferences(sharedPreferences, MFPPush.PREFS_NOTIFICATION_COUNT, count);
	}

	private void onUnhandled(Context context, Intent intent) {
		String action = intent.getAction();
		if ((MFPPushUtils.getIntentPrefix(context) + GCM_MESSAGE).equals(action)) {
			MFPInternalPushMessage message = intent
					.getParcelableExtra(GCM_EXTRA_MESSAGE);

			saveInSharedPreferences(message);

			intent = new Intent(MFPPushUtils.getIntentPrefix(context)
					+ IBM_PUSH_NOTIFICATION);
			intent.putExtra(GCM_EXTRA_MESSAGE, message);
			generateNotification(context, message.getAlert(),
					getNotificationTitle(context), message.getAlert(),
					getNotificationIcon(), intent, message.getSound());
		}
	}

	private String getNotificationTitle(Context context) {
		// Check if push_notification_title is defined, if not get the
		// application name
		int notificationTitle = -1;
		try {
			notificationTitle = MFPPushUtils.getResourceId(getApplicationContext(),
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
				logger.warn("MFPPushIntentService:getNotificationTitle() - Notification will not have a title because application name is not available.");
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
			notificationIcon = MFPPushUtils.getResourceId(getApplicationContext(),
					"drawable", "push_notification_icon");
		} catch (Exception e) {
			//Failed to find the icon resource.  Add the icon file(push_notification_icon.png) under /res/drawable foler.
			//Notification will be showin with a default star icon from Android.
			notificationIcon = android.R.drawable.btn_star;
		}
		return notificationIcon;
	}

	private void generateNotification(Context context, String ticker,
			String title, String msg, int icon, Intent intent, String sound) {
		long when = System.currentTimeMillis();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        Notification notification = builder.setContentIntent(PendingIntent
				.getActivity(context, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(icon).setTicker(ticker).setWhen(when)
                .setAutoCancel(true).setContentTitle(title)
                .setContentText(msg).setSound(getNotificationSoundUri(context, sound)).build();
        NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		//builder.setSound(getNotificationSoundUri(context,sound));

		notificationManager.notify(randomObj.nextInt(), notification);
	}


    private Uri getNotificationSoundUri(Context context, String sound) {
        Uri uri = null;

        if (sound == null) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else if (!(sound.trim().isEmpty())) {
            String soundResourceString = sound;
            try {
                if (soundResourceString.contains(".")) {
                soundResourceString = soundResourceString.substring(0, soundResourceString.indexOf("."));
                }
                int resourceId = getResourceId (context, "raw", soundResourceString);
                if(resourceId == -1) {
                logger.error("Specified sound file is not found in res/raw");
                }
                uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);
            } catch (Exception e) {
                logger.error("Exception while parsing sound file");
            }
        }

        return uri;
    }



    public static int getResourceId(Context context, String resourceCategory, String resourceName)  {
        int resourceId = -1;
        try
        {
            resourceId = context.getResources().getIdentifier(resourceName, "raw", context.getPackageName());

        } catch (Exception e) {
            logger.error("Failed to find resource R." + resourceCategory + "." + resourceName, e);
        }
        return resourceId;
    }

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		intent = handleMessageIntent(intent, extras);
		MFPPushBroadcastReceiver.completeWakefulIntent(intent);
	}

	private Intent handleMessageIntent(Intent intent, Bundle extras) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging
				.getInstance(getApplicationContext());
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				logger.debug("MFPPushIntentService:handleMessageIntent() - Received a message from GCM Server." +intent.getExtras());
				MFPInternalPushMessage message = new MFPInternalPushMessage(intent);
				intent = new Intent(MFPPushUtils.getIntentPrefix(getApplicationContext())
						+ GCM_MESSAGE);
				intent.putExtra(GCM_EXTRA_MESSAGE, message);

				if (!isAppForeground()) {
					logger.debug("MFPPushIntentService:handleMessageIntent() - App is not on foreground. Queue the intent for later re-sending when app is on foreground");
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

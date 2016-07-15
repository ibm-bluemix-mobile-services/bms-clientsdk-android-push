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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import com.ibm.mobilefirstplatform.clientsdk.android.analytics.api.*;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushIntentService;

public class MFPPushUtils extends Activity {

	private static final String LOG_CAT = MFPPush.class.getName();

	public static String getIntentPrefix(Context context) {
		return context.getPackageName();
	}

	public static int getResourceId(Context context, String resourceCategory,
			String resourceName) {
		int resourceId = -1;
		try {
			@SuppressWarnings("rawtypes")
			Class[] classes = Class.forName(context.getPackageName() + ".R")
					.getDeclaredClasses();
			for (int i = 0; i < classes.length; i++) {
				if (classes[i].getSimpleName().equals(resourceCategory)) {
					resourceId = classes[i].getField(resourceName).getInt(null);
					break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to find resource R."
					+ resourceCategory + "." + resourceName, e);
		}

		return resourceId;
	}

	/**
	 * I see that android fails on some occasions when need to log and the msg
	 * is null
	 *
	 * @param msg
	 *            original message
	 * @return "null" if null, else returns original message
	 */
	private static String getMsgNotNull(String msg) {
		if (msg == null) {
			return "null";
		}

		return msg;
	}

	public static String getContentFromSharedPreferences(Context appContext,
			String applicationId, String valueType) {
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(
				MFPPush.PREFS_NAME, 0);
		return sharedPreferences.getString(applicationId + valueType, null);
	}

	public static void storeContentInSharedPreferences(Context appContext,
			String applicationId, String valueType, String value) {
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(
				MFPPush.PREFS_NAME, 0);
		Editor editor = sharedPreferences.edit();
		editor.putString(applicationId + valueType, value);
		editor.commit();
	}

	// Remove the key from SharedPreferences
	public static void removeContentFromSharedPreferences(SharedPreferences sharedPreferences, String key ) {

		Editor editor = sharedPreferences.edit();
		String msg = sharedPreferences.getString(key, null);
		editor.remove(key);
		editor.commit();
	}

	// Store the key, value in SharedPreferences
	public static void storeContentInSharedPreferences(SharedPreferences sharedPreferences, String key, String value ) {
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	// Store the key, value in SharedPreferences
	public static void storeContentInSharedPreferences(SharedPreferences sharedPreferences, String key, int value ) {
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void dumpIntent(Intent i) {

		Bundle bundle = i.getExtras();
		if (bundle != null) {
			Set<String> keys = bundle.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
			}
		}
	}

	public static final String APPLICATION_ID = "APPLICATION_ID";
}

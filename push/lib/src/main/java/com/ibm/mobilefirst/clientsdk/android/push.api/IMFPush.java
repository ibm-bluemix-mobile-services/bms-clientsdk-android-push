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

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.FailResponse;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.MFPRequest;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushConstants.CONSUMER_ID;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushConstants.DEVICE_ID;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushConstants.FROM_NOTIFICATION_BAR;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushConstants.PLATFORM;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushConstants.SUBSCRIPTIONS;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushConstants.TAG_NAME;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushConstants.TOKEN;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushConstants.SENDER_ID;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushConstants.TAGS;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushIntentService.GCM_EXTRA_MESSAGE;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushIntentService.GCM_MESSAGE;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushConstants.NAME;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushIntentService.setAppForeground;
import static com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushUtils.getIntentPrefix;

/**
 * <class>IMFPush</class> provides methods required by an android application to
 * be able to receive push notifications.
 * <p/>
 * <br>
 * </br>
 * <p/>
 * Follow the below steps to enable android application for push notifications:
 * <p/>
 * <br>
 * </br>
 * <p/>
 * <pre>
 * 1. The below permissions have to be set in the AndroidManifest.xml of the android application
 *
 *  &lt;permission android:name="&lt;android application package name&gt;.permission.C2D_MESSAGE"
 *      android:protectionLevel="signature" /&gt;
 *
 *  &lt;uses-permission android:name="android.permission.INTERNET" /&gt;
 *  &lt;uses-permission android:name="&lt;android application package name&gt;.permission.C2D_MESSAGE" /&gt;
 *  &lt;uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" /&gt;
 *  &lt;uses-permission android:name="android.permission.WAKE_LOCK" /&gt;
 *  &lt;uses-permission android:name="android.permission.GET_ACCOUNTS" /&gt;
 *  &lt;uses-permission android:name="android.permission.USE_CREDENTIALS" /&gt;
 *  &lt;uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /&gt;
 *  &lt;uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/&gt;
 *
 * 2. The activity that is the receiver of push notifications should declare a notification intent in the AndroidManifest.xml as follows:
 *
 *  &lt;intent-filter&gt;
 *    &lt;action android:name="&lt;android application package name&gt;.IBMPushNotification" /&gt;
 *    &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *  &lt;/intent-filter&gt;
 *
 * 3. Refer to {@link IBMPushBroadcastReceiver},  {@link IBMPushIntentService} to declare the receiver and intent service in AndroidManifest.xml
 *
 * 4. To present the notification on status bar, an icon is required. This icon - push.png should be copied into /res/drawable folder of the
 * android application. If the icon given is an invalid resource, the notification will not be shown
 *
 * 5. Sample usage of IMFPush in the android application:
 *
 *  IMFPush push = null;
 *  IMFPushNotificationListener notificationListener = null;
 *
 *  {@literal @}Override
 *  protected void onCreate(Bundle savedInstanceState) {
 *    // Initialize IBM BaaS
 *    IBMBaaS.initializeSDK(this, "applicationID@1234");
 *
 *    // Obtain Push Service
 *    push = IMFPush.initializeService();
 *
 *    // Use Push Service APIs
 *    push.register("DemoDevice", "DemoUser", new IMFPushResponseListener&lt;String&gt;() {
 *      {@literal @}Override
 *      public void onSuccess(String deviceId) {
 *        ...
 *      }
 *      {@literal @}Override
 *      public void onFailure(IMFPushException ex) {
 *        ...
 *      }
 *    });
 *
 *    // Create an object of IMFPushNotificationListener and implement its onReceive method
 *    notificationListener = new IMFPushNotificationListener() {
 *      {@literal @}Override
 *      public void onReceive(final IMFPushMessage message) {
 *        ...
 *      }
 *    };
 *
 *    ...
 *  }
 *
 *  {@literal @}Override
 *  protected void onResume() {
 *    super.onResume();
 *    if (push != null) {
 *      // Request IMFPush to deliver incoming push messages to notificationListener.onReceive() method
 *      push.listen(notificationListener);
 *    }
 *  }
 *
 *  {@literal @}Override
 *  protected void onPause() {
 *    super.onPause();
 *    if (push != null) {
 *      // Request IMFPush to stop delivering incoming push messages to notificationListener.onReceive() method.
 *      // After hold(), IMFPush will store the latest push message in private shared preference
 *      // and deliver that message during the next listen().
 *      push.hold();
 *    }
 *  }
 *
 * </pre>
 */

/**
 * Created by jialfred on 9/2/15.
 */
public class IMFPush {
    public static final String PREFS_NAME = "com.ibm.mobile.services.push";
    static final String PREFS_NOTIFICATION_MSG = "LatestNotificationMsg";
    static final String PREFS_DEVICE_ID = "DeviceId";
    static final String PREFS_DEVICE_TOKEN = "DeviceToken";
    static final String PREFS_CONSUMER_ID = "ConsumerId";
    static final String PREFS_NOTIFICATION_COUNT = "NotificationCount";

    private static IMFPush instance;
    private static Context appContext = null;

    private String userId = null;
    private String gcmSenderId = null;
    private String deviceId = null;
    private String xtifyAppId = null;
    private String deviceToken = null;
    private String regId = null;
    private String applicationId = null;

    private boolean isTokenUpdatedOnServer = false;

    private List<IMFInternalPushMessage> pending = new ArrayList<IMFInternalPushMessage>();
    private GoogleCloudMessaging gcm;

    private IMFPushUrlBuilder urlBuilder = null;
    private IMFPushNotificationListener notificationListener = null;
    private IMFPushResponseListener<String> registerResponseListener = null;

    private boolean onMessageReceiverRegistered = false;
    private boolean isNewRegistration = false;
    private boolean hasRegisterParametersChanged = false;

    private IMFPush() {
        try {
            // Get the applicationId from core
            applicationId = BMSClient.getInstance().getBackendGUID();

            // Get the application context from core?
            //appContext =

            //TODO: jialfred - temporarily commented out.
            //validateAndroidContext();
        } catch (Exception e) {
            // an error occured while initializing.
            throw new RuntimeException(e);
        }
    }

    public synchronized static IMFPush getInstance() {
        if (instance == null) {
            instance = new IMFPush();
        }
        return instance;
    }

    public synchronized static IMFPush getService() {
        if (instance == null) {
            throw new RuntimeException(
                    "IMFPush was never initialized.  First call IMFPush.initializeService().");
        }
        return instance;
    }

    // Ask core to set up this appContext?
    static public void setContext(final Context context){
        IMFPush.appContext = context;
    }

    /**
     * Request IMFPush to deliver incoming push messages to listener.onReceive()
     * method.
     * <p/>
     * This method is typically called from the onResume() method of the
     * activity that is handling push notifications.
     *
     * @param notificationListener IMFPushNotificationListener object whose onReceive() method
     *                             will be called upon receipt of a push message.
     */
    public void listen(IMFPushNotificationListener notificationListener) {
        if (!onMessageReceiverRegistered) {
            appContext.registerReceiver(onMessage, new IntentFilter(
                    getIntentPrefix(appContext) + GCM_MESSAGE));
            onMessageReceiverRegistered = true;

            this.notificationListener = notificationListener;
            setAppForeground(true);

            boolean gotSavedMessages = getMessagesFromSharedPreferences();
            if (gotSavedMessages) {
                dispatchPending();
            }
            cancelAllNotification();
        } else {
            //onMessage broadcast listener has already been registered.
        }
    }

    /**
     * Request IMFPush to stop delivering incoming push messages to
     * notificationListener.onReceive() method. After hold(), IMFPush will store
     * the latest push message in private shared preference and deliver that
     * message during the next {@link #listen(IMFPushNotificationListener)}.
     * <p/>
     * This method is typically called from the onPause() method of the activity
     * that is handling push notifications.
     */
    public void hold() {
        notificationListener = null;
        setAppForeground(false);
        if (onMessageReceiverRegistered) {
            try {
                appContext.unregisterReceiver(onMessage);
            } catch (Exception e) {
                //warning: e.getMessage();
            }
            onMessageReceiverRegistered = false;
        }
    }

    /**
     * Checks whether push notification is supported.
     *
     * @return true if push is supported, false otherwise.
     */
    public boolean isPushSupported() {
        String version = android.os.Build.VERSION.RELEASE.substring(0, 3);
        return (Double.valueOf(version) >= IMFPushConstants.MIN_SUPPORTED_ANDRIOD_VERSION);
    }

    /**
     * Registers the device for Push notifications with the given alias and
     * consumerId
     *
     * @param userId   - consumerId that will be used for device registration
     * @param listener - Mandatory listener class. When the device is successfully
     *                 registered with Push service the
     *                 {@link IMFPushResponseListener}.onSuccess method is called
     *                 with the deviceId. {@link IMFPushResponseListener}.onFailure
     *                 method is called otherwise
     */
    public void register(final String userId,
                         IMFPushResponseListener<String> listener) {
        this.userId = userId;
        this.registerResponseListener = listener;

        //retrieve senderId from push server.
        getSenderIdFromServerAndRegisterInBackground();
    }

	/**
	 * Subscribes to the given tag
	 *
	 * @param tagName
	 *            name of the tag
	 * @param listener
	 *            Mandatory listener class. When the subscription is created
	 *            successfully the {@link IMFPushResponseListener}.onSuccess
	 *            method is called with the tagName for which subscription is
	 *            created. {@link IMFPushResponseListener}.onFailure method is
	 *            called otherwise
	 */
    public void subscribe(final String tagName,
            final IMFPushResponseListener<String> listener) {
		if (isAbleToSubscribe()) {
            //TODO - jialfred - hardcoding until server urls are sorted out.
            String path = "http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/subscriptions";
            IMFPushInvoker invoker = IMFPushInvoker.newInstance(path, MFPRequest.POST, 10);
            invoker.addHeaders();
            invoker.setJSONRequestBody(buildSubscription(tagName));
            invoker.setCoreResponseListener(new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    //Subscription successfully created.
			        listener.onSuccess(tagName);
                }

                @Override
                public void onFailure(FailResponse failResponse, Throwable throwable) {
                    //Error while subscribing to tags.
                    listener.onFailure(new IMFPushException(failResponse.toString()));
                }
            });
            invoker.execute();
		}
	}

	/**
	 * Unsubscribes to the given tag
	 *
	 * @param tagName
	 *            name of the tag
	 * @param listener
	 *            Mandatory listener class. When the subscription is deleted
	 *            successfully the {@link IMFPushResponseListener}.onSuccess
	 *            method is called with the tagName for which subscription is
	 *            deleted. {@link IMFPushResponseListener}.onFailure method is
	 *            called otherwise
	 */
    public void unsubscribe(final String tagName,
            final IMFPushResponseListener<String> listener) {
		if (isAbleToSubscribe()) {
            //TODO - jialfred - hardcoding until server urls are sorted out.
            String path = "http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/subscriptions?deviceId="+deviceId+"&tagName="+tagName;
            IMFPushInvoker invoker = IMFPushInvoker.newInstance(path, MFPRequest.DELETE, 10);
            invoker.addHeaders();
            invoker.setCoreResponseListener(new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    listener.onSuccess(tagName);
                }

                @Override
                public void onFailure(FailResponse failResponse, Throwable throwable) {
                    listener.onFailure(new IMFPushException(failResponse.toString()));
                }
            });
            invoker.execute();
		}
	}

    /**
     * Unregister the device from Push Server
     *
     * @param listener
     *            Mandatory listener class. When the subscription is deleted
     *            successfully the {@link IMFPushResponseListener}.onSuccess
     *            method is called with the tagName for which subscription is
     *            deleted. {@link IMFPushResponseListener}.onFailure method is
     *            called otherwise
     */
    public void unregisterDevice(final IMFPushResponseListener<String> listener) {
        if (isAbleToSubscribe()) {
            //TODO - jialfred - hardcoding until server urls are sorted out.
            String path = "http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/devices/"+deviceId;
            IMFPushInvoker invoker = IMFPushInvoker.newInstance(path, MFPRequest.DELETE, 10);
            invoker.addHeaders();
            invoker.setCoreResponseListener(new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    listener.onSuccess("Device Successfully unregistered from receiving push notifications.");
                }

                @Override
                public void onFailure(FailResponse failResponse, Throwable throwable) {
                    listener.onFailure(new IMFPushException(failResponse.toString()));
                }
            });
            invoker.execute();
        }
    }

	/**
	 * Get the list of tags
	 *
	 * @param listener
	 *            Mandatory listener class. When the list of tags are
	 *            successfully retrieved the {@link IMFPushResponseListener}
	 *            .onSuccess method is called with the list of tagNames
	 *            {@link IMFPushResponseListener}.onFailure method is called
	 *            otherwise
	 */
    public void getTags(final IMFPushResponseListener<List<String>> listener) {
        //TODO -jialfred - hardcoding path until server urls are resolved.
        String path = "http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/tags";
        IMFPushInvoker invoker = IMFPushInvoker.newInstance(path, MFPRequest.GET, 10);
        invoker.addHeaders();
        invoker.setCoreResponseListener(new ResponseListener() {

            @Override
            public void onSuccess(Response response) {
                List<String> tagNames = new ArrayList<String>();
                try {
                    JSONArray tags = (JSONArray) response.getResponseJSON().get(TAGS);
                    int tagsCnt = tags.length();
                    for (int tagsIdx = 0; tagsIdx < tagsCnt; tagsIdx++) {
                        tagNames.add(tags.getJSONObject(tagsIdx)
                                .getString(NAME));
                    }
                } catch (JSONException e) {
                    listener.onFailure(new IMFPushException(e));
                }
                listener.onSuccess(tagNames);
            }

            @Override
            public void onFailure(FailResponse failResponse, Throwable throwable) {
                listener.onFailure(new IMFPushException(failResponse.toString()));
            }
        });
		invoker.execute();
	}

	/**
	 * Get the list of tags subscribed to
	 *
	 * @param listener
	 *            Mandatory listener class. When the list of tags subscribed to
	 *            are successfully retrieved the {@link IMFPushResponseListener}
	 *            .onSuccess method is called with the list of tagNames
	 *            {@link IMFPushResponseListener}.onFailure method is called
	 *            otherwise
	 */
	public void getSubscriptions(
			final IMFPushResponseListener<List<String>> listener) {

        //TODO - jialfred - hardcoding until server urls are sorted out.
        String path = "http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/subscriptions?deviceId="+deviceId;
        IMFPushInvoker invoker = IMFPushInvoker.newInstance(path, MFPRequest.GET, 10);
        invoker.addHeaders();
        invoker.setCoreResponseListener(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                List<String> tagNames = new ArrayList<String>();
                try {
                    JSONArray tags = (JSONArray) response.getResponseJSON()
                            .get(SUBSCRIPTIONS);
                    int tagsCnt = tags.length();
                    for (int tagsIdx = 0; tagsIdx < tagsCnt; tagsIdx++) {
                        tagNames.add(tags.getJSONObject(tagsIdx)
                                .getString(TAG_NAME));
                    }
                } catch (JSONException e) {
                    listener.onFailure(new IMFPushException(e));
                }
                listener.onSuccess(tagNames);
            }

            @Override
            public void onFailure(FailResponse failResponse, Throwable throwable) {
                listener.onFailure(new IMFPushException(failResponse.toString()));
            }
        });
		invoker.execute();
	}

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(appContext);
                    }
                    deviceToken = gcm.register(gcmSenderId);
                    gcm.close();
                    //Successfully registered with GCM. Returned deviceToken:
                    computeRegId();
                    verifyDeviceRegistration();
                } catch (IOException ex) {
                    msg = ex.getMessage();
                    //Failed to register at GCM Server.
                    registerResponseListener
                            .onFailure(new IMFPushException(msg));
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    // Generate a Device UUID based on ANDROID_ID and Device MAC.
    // The addition of MAC address is to ensure uniqueness.
    // There are reported cases of non-unique ANDROID_ID in Froyo devices are of
    // no concern.
    private void computeRegId() {
        //debug("IMFPush: Computing device's registrationId");
        String macAddr = null;
        PackageManager packageManager = appContext.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            WifiManager wfManager = (WifiManager) appContext
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiinfo = wfManager.getConnectionInfo();
            macAddr = wifiinfo.getMacAddress();
        }

        String uuid = Settings.Secure.getString(
                appContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String packageName = appContext.getPackageName();

        if (macAddr != null)
            uuid += macAddr;
        if (packageName != null)
            uuid += packageName;
        // Use a hashed UUID not exposing the device ANDROID_ID/Mac Address
        regId = UUID.nameUUIDFromBytes(uuid.getBytes()).toString();
        //debug("Generated RegId is: " + regId);
    }

    private boolean verifyDeviceRegistration() {
//        String path = urlBuilder.getDevicesUrl() + "?filter="
//                + "registrationId == " + regId + "&expand=true";

        //TODO-jialfred - hardcoding until server urls are sorted out.
        String path = "http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/devices/" + regId;

        IMFPushInvoker invoker = IMFPushInvoker.newInstance(path, MFPRequest.GET, 10);
        invoker.setJSONRequestBody(null);
        invoker.addHeaders();
        invoker.setCoreResponseListener(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                try {
                    String retDeviceId = (response.getResponseJSON()).getString("deviceId");
                    String retToken = (response.getResponseJSON()).getString("token");
                    String retUserId = (response.getResponseJSON().getString("userId"));

                    if (!(retDeviceId.equals(regId))
                            || !(retToken.equals(deviceToken))
                            || !(retUserId.equals(userId))) {
                        deviceId = retDeviceId;
                        IMFPushUtils
                                .storeContentInSharedPreferences(
                                        appContext, applicationId,
                                        DEVICE_ID, deviceId);

                        hasRegisterParametersChanged = true;
                        updateTokenCallback(deviceToken);
                    } else {
                        deviceId = retDeviceId;
                        isTokenUpdatedOnServer = true;
                        IMFPushUtils
                                .storeContentInSharedPreferences(
                                        appContext, applicationId,
                                        DEVICE_ID, deviceId);
                        registerResponseListener
                                .onSuccess(deviceId);
                    }
                } catch (JSONException e1) {
                    //Exception caught while parsing JSON.
                    e1.printStackTrace();
                }
            }

            @Override
            public void onFailure(FailResponse failResponse, Throwable throwable) {
                // Device is not registered.
                isNewRegistration = true;
                updateTokenCallback(deviceToken);
            }
        });
        invoker.execute();

        return true;
    }

    private void updateTokenCallback(String deviceToken) {
        if (isNewRegistration) {
            //Device is registering with push for the first time.
            //TODO-jialfred - hardcoding until server urls are sorted out.
            String path = "http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/devices";
            IMFPushInvoker invoker = IMFPushInvoker
                    .newInstance(path, MFPRequest.POST, 10);
            invoker.setJSONRequestBody(buildDevice());
            invoker.addHeaders();
            invoker.setCoreResponseListener(new ResponseListener() {

                @Override
                public void onSuccess(Response response) {
                    isNewRegistration = false;
                    registerResponseListener.onSuccess(response.toString());
                }

                @Override
                public void onFailure(FailResponse failResponse, Throwable throwable) {
                    //failure while device registration.
                    registerResponseListener.onFailure(new IMFPushException(failResponse.toString()));
                }
            });
            invoker.execute();
        } else if (hasRegisterParametersChanged) {
            //device is already registered. Registration parameters have changed, so updating params.
            //TODO-jialfred - hardcoding until server urls are sorted out.
            String path = "http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/devices/" + deviceId;
            IMFPushInvoker invoker = IMFPushInvoker
                    .newInstance(path, MFPRequest.PUT, 10);
            invoker.setJSONRequestBody(buildDevice());
            invoker.addHeaders();
            invoker.setCoreResponseListener(new ResponseListener() {

                @Override
                public void onSuccess(Response response) {
                    isNewRegistration = false;
                    registerResponseListener.onSuccess(response.toString());
                }

                @Override
                public void onFailure(FailResponse failResponse, Throwable throwable) {
                    //failure while device registration.
                    registerResponseListener.onFailure(new IMFPushException(failResponse.toString()));
                }
            });
            invoker.execute();

            hasRegisterParametersChanged = false;
        } else {
            isTokenUpdatedOnServer = true;
            registerResponseListener.onSuccess(deviceId);
        }
    }

    private IMFPushResponseListener<JSONObject> getDeviceRegistrationListener() {
        return new IMFPushResponseListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject deviceDetail) {
                try {
                    deviceId = deviceDetail.getString(DEVICE_ID);
                    IMFPushUtils.storeContentInSharedPreferences(appContext,
                            applicationId, DEVICE_ID, deviceId);
                } catch (JSONException e) {
                    //missing deviceId in response.
                }
                isTokenUpdatedOnServer = true;
                registerResponseListener.onSuccess(deviceId);
            }

            @Override
            public void onFailure(IMFPushException exception) {
                isTokenUpdatedOnServer = false;
                //Failed to register device on IBM Push Server.
                registerResponseListener.onFailure(exception);
            }
        };
    }

    private IMFPushResponseListener<JSONObject> getDeviceUpdateListener() {
        return new IMFPushResponseListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject deviceDetail) {
                isTokenUpdatedOnServer = true;
                registerResponseListener.onSuccess(deviceId);
            }

            @Override
            public void onFailure(IMFPushException exception) {
                isTokenUpdatedOnServer = false;
                //Failed to update token on IBM Push server.
                registerResponseListener.onFailure(exception);
            }
        };
    }

    private JSONObject buildDevice() {
        JSONObject device = new JSONObject();
        try {
            device.put(DEVICE_ID, regId);
            device.put(CONSUMER_ID, userId);
            device.put(TOKEN, deviceToken);
            device.put(PLATFORM, "G");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return device;
    }

    private JSONObject buildSubscription(String tagName) {
        JSONObject subscriptionObject = new JSONObject();

        try {
            subscriptionObject.put(DEVICE_ID, deviceId);
            subscriptionObject.put(TAG_NAME, tagName);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return subscriptionObject;
    }

    private boolean isAbleToSubscribe() {
        if (!isTokenUpdatedOnServer) {
            //cannot subscribe to tag while token is not updated on the server.
            return false;
        }

        return true;
    }

    private void cancelAllNotification() {
        NotificationManager notificationManager = (NotificationManager) appContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void dispatchPending() {
        while (true) {
            IMFInternalPushMessage message = null;
            synchronized (pending) {
                if (pending.size() > 0) {
                    message = pending.remove(0);
                }
            }

            if (message == null) {
                break;
            }

            if (notificationListener != null) {
                IMFSimplePushNotification simpleNotification = new IMFSimplePushNotification(
                        message);
                notificationListener.onReceive(simpleNotification);
            }
        }
    }

    private void validateAndroidContext() {
        if (appContext == null) {
            throw new RuntimeException(
                    "Android context cannot be null. Pass a valid android context.");
        }

        if (android.os.Build.VERSION.SDK_INT < 8) {
            String errorMsg = "The current Android version "
                    + android.os.Build.VERSION.RELEASE
                    + " not allowed to work with push.";
            throw new RuntimeException(errorMsg);
        }

        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(appContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil
                    .isUserRecoverableError(ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED)) {
                //warning - "Newer version of Google play services is available."
            } else if (GooglePlayServicesUtil
                    .isUserRecoverableError(ConnectionResult.SERVICE_MISSING)) {
                throw new RuntimeException(
                        "Google Play Services is not installed on your device.");
            } else if (GooglePlayServicesUtil
                    .isUserRecoverableError(ConnectionResult.SERVICE_DISABLED)) {
                throw new RuntimeException(
                        "Google Play Services is disabled on your device.");
            }
        }
    }

    private BroadcastReceiver onMessage = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Successfully received message for dispatching.
            synchronized (pending) {
                pending.add((IMFInternalPushMessage) intent
                        .getParcelableExtra(GCM_EXTRA_MESSAGE));
            }

            dispatchPending();

            boolean isFromNotificationBar = intent.getBooleanExtra(
                    FROM_NOTIFICATION_BAR, false);
            if (!isFromNotificationBar) {
                setResultCode(Activity.RESULT_OK);
            }
        }
    };

    /**
     * Based on the PREFS_NOTIFICATION_COUNT value, fetch all the stored notifications in the same order from
     * the shared preferences and save it onto the list.
     * This method will ensure that the notifications are sent to the Application in the same order in which they arrived.
     */
    private boolean getMessagesFromSharedPreferences() {
        boolean gotMessages = false;
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(
                PREFS_NAME, Context.MODE_PRIVATE);

        int countOfStoredMessages = sharedPreferences.getInt(IMFPush.PREFS_NOTIFICATION_COUNT, 0);

        if (countOfStoredMessages > 0) {
            for (int index = 1; index <= countOfStoredMessages; index++) {

                String key = PREFS_NOTIFICATION_MSG + index;
                try {
                    String msg = sharedPreferences.getString(key, null);

                    if (msg != null) {
                        gotMessages = true;
                        //messages retrieved from sharedpreferences.
                        IMFInternalPushMessage pushMessage = new IMFInternalPushMessage(
                                new JSONObject(msg));

                        synchronized (pending) {
                            pending.add(pushMessage);
                        }

                        IMFPushUtils.removeContentFromSharedPreferences(sharedPreferences, key);
                    }
                } catch (JSONException e) {
                    IMFPushUtils.removeContentFromSharedPreferences(sharedPreferences, key);
                }
            }
            IMFPushUtils.storeContentInSharedPreferences(sharedPreferences, IMFPush.PREFS_NOTIFICATION_COUNT, 0);
        }

        return gotMessages;
    }

    private void getSenderIdFromServerAndRegisterInBackground() {
        IMFPushInvoker invoker = IMFPushInvoker.newInstance("http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/settings/gcmConf", MFPRequest.GET, 10);
        invoker.setJSONRequestBody(null);
        invoker.addHeaders();
        invoker.setCoreResponseListener(new ResponseListener() {

            @Override
            public void onSuccess(Response response) {
                // success retrieving senderId from server.
                String senderId = null;
                try {
                    senderId = (String) response.getResponseJSON().get("senderId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (senderId == null) {
                    String errString = "SenderId is not configured in the backend application.";
                    registerResponseListener.onFailure(new IMFPushException(errString));
                } else {
                    gcmSenderId = senderId;
                    IMFPushUtils.storeContentInSharedPreferences(appContext, applicationId, SENDER_ID, gcmSenderId);
                    registerInBackground();
                }

                registerResponseListener.onSuccess(response.toString());
            }

            @Override
            public void onFailure(FailResponse failResponse, Throwable throwable) {
                registerResponseListener.onFailure(new IMFPushException(failResponse.toString()));
            }
        });

        invoker.execute();
    }
}


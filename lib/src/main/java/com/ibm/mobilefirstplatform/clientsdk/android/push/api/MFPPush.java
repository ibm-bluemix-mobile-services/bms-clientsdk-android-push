/*
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
*/

package com.ibm.mobilefirstplatform.clientsdk.android.push.api;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPInternalPushMessage;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushInvoker;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushUrlBuilder;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushUtils;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;
import com.ibm.mobilefirstplatform.clientsdk.android.security.api.AuthorizationManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.ACTION;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.DEVICE_ID;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.NID;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.OPEN;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.PLATFORM;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.SUBSCRIPTIONS;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.TAG_NAME;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.TOKEN;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.SENDER_ID;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.TAGS;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.NAME;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushIntentService.GCM_EXTRA_MESSAGE;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushIntentService.GCM_MESSAGE;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.MIN_SUPPORTED_ANDRIOD_VERSION;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushIntentService.setAppForeground;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.USER_ID;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushUtils.getIntentPrefix;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.DISMISS_NOTIFICATION;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.STATUS;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.PREFS_BMS_REGION;


/**
 * <class>MFPPush</class> provides methods required by an android application to
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
 *
 * 3. To present the notification on status bar, an icon is required. This icon - push.png should be copied into /res/drawable folder of the
 * android application. If the icon given is an invalid resource, the notification will not be shown
 *
 * 4. Sample usage of MFPPush in the android application:
 *
 *  MFPPush push = null;
 *  MFPPushNotificationListener notificationListener = null;
 *
 *  {@literal @}Override
 *  protected void onCreate(Bundle savedInstanceState) {
 *    // Initialize IBM BaaS
 *    BMSClient.getInstance().initialize(getApplicationContext(), BMSClient.REGION_US_SOUTH);
 *
 *    // Obtain Push Service
 *    MFPPush.getInstance().initialize(getApplicationContext(),appGuid,clientSecret);
 *
 *    // Use Push Service APIs
 *    push.registerDevice(new MFPPushResponseListener&lt;String&gt;() {
 *      {@literal @}Override
 *      public void onSuccess(String deviceId) {
 *        ...
 *      }
 *      {@literal @}Override
 *      public void onFailure(MFPPushException ex) {
 *        ...
 *      }
 *    });
 *
 *    // Create an object of MFPPushNotificationListener and implement its onReceive method
 *    notificationListener = new MFPPushNotificationListener() {
 *      {@literal @}Override
 *      public void onReceive(final MFPPushMessage message) {
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
 *      // Request MFPPush to deliver incoming push messages to notificationListener.onReceive() method
 *      push.listen(notificationListener);
 *    }
 *  }
 *
 *  {@literal @}Override
 *  protected void onPause() {
 *    super.onPause();
 *    if (push != null) {
 *      // Request MFPPush to stop delivering incoming push messages to notificationListener.onReceive() method.
 *      // After hold(), MFPPush will store the latest push message in private shared preference
 *      // and deliver that message during the next listen().
 *      push.hold();
 *    }
 *  }
 *
 * </pre>
 */

public class MFPPush extends FirebaseInstanceIdService {
    public static final String PREFS_NAME = "com.ibm.mobile.services.push";
    static final String PREFS_NOTIFICATION_MSG = "LatestNotificationMsg";
    static final String PREFS_NOTIFICATION_COUNT = "NotificationCount";
    static final String PREFS_MESSAGES_URL = "MessagesURL";
    static final String PREFS_MESSAGES_URL_CLIENT_SECRET = "MessagesURLClientSecret";
    static final int INITIALISATION_ERROR = 403;


    private static MFPPush instance;
    private static Context appContext = null;

    private String gcmSenderId = null;
    private String deviceId = null;
    private String deviceToken = null;
    private String regId = null;
    private String applicationId = null;
    private String errorString = null;
    private int statusCode = 0;


    private String clientSecret;
    private boolean isInitialized = false;

    private boolean isTokenUpdatedOnServer = false;

    private List<MFPInternalPushMessage> pending = new ArrayList<MFPInternalPushMessage>();
    private Map<String, MFPPushNotificationStatus> pendingStatus = new HashMap<String, MFPPushNotificationStatus>();

    private MFPPushNotificationListener notificationListener = null;
    private MFPPushNotificationStatusListener statusListener = null;
    private MFPPushResponseListener<String> registerResponseListener = null;

    private boolean onMessageReceiverRegistered = false;
    private boolean isNewRegistration = false;
    private boolean hasRegisterParametersChanged = false;
    public static boolean isRegisteredForPush = false;
    public static MFPPushNotificationOptions options = null;
    private boolean isFromNotificationBar = false;
    private MFPInternalPushMessage messageFromBar = null;
    private Intent pushNotificationIntent = null;
    private boolean sendDeliveryStatus = true;
    private final Object sendDeliveryStatusLock = new Object();

    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + MFPPush.class.getSimpleName());
    public static String overrideServerHost = null;

    public MFPPush() {
    }

    public synchronized static MFPPush getInstance() {
        if (instance == null) {
            instance = new MFPPush();
        }
        return instance;
    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        logger.debug("MFPPush:onTokenRefresh - Received token: "+ refreshedToken);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MFPFirebaseToken", refreshedToken);
        editor.apply();
    }

    /**
     * @deprecated As of release 2.+, replaced by {@link #initialize(Context, String, String)}
     * MFPPush Intitialization method with clientSecret and Push App GUID.
     * <p/>
     *
     * @param context This is the Context of the application from getApplicationContext()
     * @param appGUID The unique ID of the Push service instance that the application must connect to.
     */
    public void initialize(Context context, String appGUID) {
        try {

            if (MFPPushUtils.validateString(appGUID)) {
                // Get the applicationId and backend route from core
                applicationId = appGUID;
                appContext = context.getApplicationContext();
                isInitialized = true;
                validateAndroidContext();
            } else {
                logger.error("MFPPush:initialize() - An error occured while initializing MFPPush service. Add a valid push service instance ID Value");
                throw new MFPPushException("MFPPush:initialize() - An error occured while initializing MFPPush service. Add a valid push service instance ID Value", INITIALISATION_ERROR);
            }
        } catch (Exception e) {
            logger.error("MFPPush:initialize() - An error occured while initializing MFPPush service.");
            throw new RuntimeException(e);
        }
    }

    /**
     * MFPPush Intitialization method with clientSecret and Push App GUID.
     * <p/>
     *
     * @param context          this is the Context of the application from getApplicationContext()
     * @param appGUID          The unique ID of the Push service instance that the application must connect to.
     * @param pushClientSecret ClientSecret from the push service.
     */
    public void initialize(Context context, String appGUID, String pushClientSecret) {
        try {
            if (MFPPushUtils.validateString(pushClientSecret) && MFPPushUtils.validateString(appGUID)) {
                // Get the applicationId and backend route from core
                clientSecret = pushClientSecret;
                applicationId = appGUID;
                appContext = context.getApplicationContext();
                isInitialized = true;
                validateAndroidContext();

                //Storing the messages url and the client secret.
                //This is because when the app is not running and notification is dismissed/cleared,
                //there wont be applicationId and clientSecret details available to
                //MFPPushNotificationDismissHandler.
                SharedPreferences sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                MFPPushUtils.storeContentInSharedPreferences(sharedPreferences, MFPPush.PREFS_MESSAGES_URL, buildMessagesURL());
                MFPPushUtils.storeContentInSharedPreferences(sharedPreferences, MFPPush.PREFS_MESSAGES_URL_CLIENT_SECRET, clientSecret);
                MFPPushUtils.storeContentInSharedPreferences(sharedPreferences, PREFS_BMS_REGION, BMSClient.getInstance().getBluemixRegionSuffix());
            } else {
                logger.error("MFPPush:initialize() - An error occured while initializing MFPPush service. Add a valid ClientSecret and push service instance ID Value");
                System.out.print("MFPPush:initialize() - An error occured while initializing MFPPush service. Add a valid ClientSecret and push service instance ID Value");
                throw new MFPPushException("MFPPush:initialize() - An error occured while initializing MFPPush service. Add a valid ClientSecret and push service instance ID Value", INITIALISATION_ERROR);
            }

        } catch (Exception e) {
            logger.error("MFPPush:initialize() - An error occured while initializing MFPPush service.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Request MFPPush to deliver incoming push messages to listener.onReceive()
     * method.
     * <p/>
     * This method is typically called from the onResume() method of the
     * activity that is handling push notifications.
     *
     * @param notificationListener MFPPushNotificationListener object whose onReceive() method
     *                             will be called upon receipt of a push message.
     */
    public void listen(MFPPushNotificationListener notificationListener) {

        if (!onMessageReceiverRegistered) {
            appContext.registerReceiver(onMessage, new IntentFilter(
                    getIntentPrefix(appContext) + GCM_MESSAGE));
            onMessageReceiverRegistered = true;

            this.notificationListener = notificationListener;
            setAppForeground(true);

            boolean gotSavedMessages;

            if (pushNotificationIntent != null) {
                gotSavedMessages = getMessagesFromSharedPreferences(pushNotificationIntent.getIntExtra("notificationId", 0));
                pushNotificationIntent = null;
            } else {
                gotSavedMessages = getMessagesFromSharedPreferences(0);
            }

            if (!isFromNotificationBar) {
                if (gotSavedMessages) {
                    dispatchPending();
                }
                cancelAllNotification();
            } else {
                if (messageFromBar != null) {
                    isFromNotificationBar = false;
                    sendNotificationToListener(messageFromBar);
                    messageFromBar = null;
                }
            }
        } else {
            logger.info("MFPPush:listen() - onMessage broadcast listener has already been registered.");
        }
    }

    /**
     * Request MFPPush to stop delivering incoming push messages to
     * notificationListener.onReceive() method. After hold(), MFPPush will store
     * the latest push message in private shared preference and deliver that
     * message during the next {@link #listen(MFPPushNotificationListener)}.
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
                logger.warn("MFPPush:hold() - Exception while unregistering receiver. " + e.getMessage());
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
        return (Double.valueOf(version) >= MIN_SUPPORTED_ANDRIOD_VERSION);
    }

    /**
     * Registers the device for Push notifications with the given alias and
     * consumerId
     *
     * @param listener - Mandatory listener class. When the device is successfully
     *                 registered with Push service the
     *                 {@link MFPPushResponseListener}.onSuccess method is called
     *                 with the deviceId. {@link MFPPushResponseListener}.onFailure
     *                 method is called otherwise
     * @param userId   -  The UserId for registration.
     */
    public void registerDeviceWithUserId(String userId, MFPPushResponseListener<String> listener) {

        if (isInitialized) {
            this.registerResponseListener = listener;
            if (MFPPushUtils.validateString(userId)) {
                setAppForeground(true);
                logger.info("MFPPush:register() - Retrieving senderId from MFPPush server.");
                getSenderIdFromServerAndRegisterInBackground(userId);
            } else {
                logger.error("MFPPush:register() - An error occured while registering for MFPPush service. Add a valid userId Value");
                System.out.print("MFPPush:register() - An error occured while registering for MFPPush service. Add a valid userId Value");
                registerResponseListener.onFailure(new MFPPushException("MFPPush:register() - An error occured while registering for MFPPush service. Add a valid userId Value", INITIALISATION_ERROR));
            }
        } else {
            logger.error("MFPPush:register() - An error occured while registering for MFPPush service. Push not initialized with call to initialize()");
        }

    }

    /**
     * Registers the device for Push notifications with the given alias and
     * consumerId
     *
     * @param listener - Mandatory listener class. When the device is successfully
     *                 registered with Push service the
     *                 {@link MFPPushResponseListener}.onSuccess method is called
     *                 with the deviceId. {@link MFPPushResponseListener}.onFailure
     *                 method is called otherwise
     */
    public void registerDevice(MFPPushResponseListener<String> listener) {

        if (isInitialized) {
            this.registerResponseListener = listener;
            logger.info("MFPPush:register() - Registering for MFPPush service.");
            setAppForeground(true);
            getSenderIdFromServerAndRegisterInBackground(null);
        } else {
            logger.error("MFPPush:register() - An error occured while registering for MFPPush service. Push not initialized with call to initialize()");
        }
    }

    /**
     * Subscribes to the given tag
     *
     * @param tagName  name of the tag
     * @param listener Mandatory listener class. When the subscription is created
     *                 successfully the {@link MFPPushResponseListener}.onSuccess
     *                 method is called with the tagName for which subscription is
     *                 created. {@link MFPPushResponseListener}.onFailure method is
     *                 called otherwise
     */
    public void subscribe(final String tagName,
                          final MFPPushResponseListener<String> listener) {
        if (isAbleToSubscribe()) {
            MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
            String path = builder.getSubscriptionsUrl();
            logger.debug("MFPPush:subscribe() - The tag subscription path is: " + path);
            MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.POST, clientSecret);
            invoker.setJSONRequestBody(buildSubscription(tagName));
            invoker.setResponseListener(new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    //Subscription successfully created.
                    logger.info("MFPPush:subscribe() - Tag subscription successfully created.  The response is: " + response.toString());
                    listener.onSuccess(tagName);
                }

                @Override
                public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                    //Error while subscribing to tags.
                    errorString = null;
                    statusCode = 0;
                    if (response != null) {
                        errorString = response.getResponseText();
                        statusCode = response.getStatus();
                    } else if (errorString == null && throwable != null) {
                        errorString = throwable.toString();
                    } else if (errorString == null && jsonObject != null) {
                        errorString = jsonObject.toString();
                    }
                    listener.onFailure(new MFPPushException(errorString, statusCode));
                }

            });
            invoker.execute();
        }
    }

    /**
     * Unsubscribes to the given tag
     *
     * @param tagName  name of the tag
     * @param listener Mandatory listener class. When the subscription is deleted
     *                 successfully the {@link MFPPushResponseListener}.onSuccess
     *                 method is called with the tagName for which subscription is
     *                 deleted. {@link MFPPushResponseListener}.onFailure method is
     *                 called otherwise
     */
    public void unsubscribe(final String tagName,
                            final MFPPushResponseListener<String> listener) {
        if (isAbleToSubscribe()) {
            MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
            String path = builder.getSubscriptionsUrl(deviceId, tagName);
            logger.debug("MFPPush:unsubscribe() - The tag unsubscription path is: " + path);
            MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.DELETE, clientSecret);

            invoker.setResponseListener(new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    logger.info("MFPPush:unsubscribe() - Tag unsubscription successful.  The response is: " + response.toString());
                    listener.onSuccess(tagName);
                }

                @Override
                public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                    //Error while subscribing to tags.
                    errorString = null;
                    statusCode = 0;
                    if (response != null) {
                        errorString = response.getResponseText();
                        statusCode = response.getStatus();
                    } else if (errorString == null && throwable != null) {
                        errorString = throwable.toString();
                    } else if (errorString == null && jsonObject != null) {
                        errorString = jsonObject.toString();
                    }
                    listener.onFailure(new MFPPushException(errorString, statusCode));
                }
            });
            invoker.execute();
        }
    }

    /**
     * Unregister the device from Push Server
     *
     * @param listener Mandatory listener class. When the subscription is deleted
     *                 successfully the {@link MFPPushResponseListener}.onSuccess
     *                 method is called with the tagName for which subscription is
     *                 deleted. {@link MFPPushResponseListener}.onFailure method is
     *                 called otherwise
     */
    public void unregister(final MFPPushResponseListener<String> listener) {
        MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
        String path = builder.getUnregisterUrl(deviceId);
        logger.debug("MFPPush:unregister() - The device unregister url is: " + path);
        MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.DELETE, clientSecret);

        invoker.setResponseListener(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                logger.info("MFPPush:unregister() - Successfully unregistered device. Response is: " + response.toString());
                isTokenUpdatedOnServer = false;
                isRegisteredForPush = false;
                listener.onSuccess("Device Successfully unregistered from receiving push notifications.");
            }

            @Override
            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                errorString = null;
                statusCode = 0;
                if (response != null) {
                    errorString = response.getResponseText();
                    statusCode = response.getStatus();
                } else if (errorString == null && throwable != null) {
                    errorString = throwable.toString();
                } else if (errorString == null && jsonObject != null) {
                    errorString = jsonObject.toString();
                }
                listener.onFailure(new MFPPushException(errorString, statusCode));
            }
        });
        invoker.execute();
    }

    /**
     * Get the list of tags
     *
     * @param listener Mandatory listener class. When the list of tags are
     *                 successfully retrieved the {@link MFPPushResponseListener}
     *                 .onSuccess method is called with the list of tagNames
     *                 {@link MFPPushResponseListener}.onFailure method is called
     *                 otherwise
     */
    public void getTags(final MFPPushResponseListener<List<String>> listener) {
        MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
        String path = builder.getTagsUrl();
        MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.GET, clientSecret);

        invoker.setResponseListener(new ResponseListener() {

            @Override
            public void onSuccess(Response response) {
                logger.info("MFPPush:getTags() - Successfully retreived tags.  The response is: " + response.toString());
                List<String> tagNames = new ArrayList<String>();
                try {
                    String responseText = response.getResponseText();
                    JSONArray tags = (JSONArray) (new JSONObject(responseText)).get(TAGS);
                    Log.d("JSONArray of tags is: ", tags.toString());
                    int tagsCnt = tags.length();
                    for (int tagsIdx = 0; tagsIdx < tagsCnt; tagsIdx++) {
                        Log.d("Adding tag: ", tags.getJSONObject(tagsIdx).toString());
                        tagNames.add(tags.getJSONObject(tagsIdx)
                                .getString(NAME));
                    }
                    listener.onSuccess(tagNames);
                } catch (JSONException e) {
                    logger.error("MFPPush: getTags() - Error while retrieving tags.  Error is: " + e.getMessage());
                    listener.onFailure(new MFPPushException(e));
                }
            }

            @Override
            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                //Error while subscribing to tags.
                errorString = null;
                statusCode = 0;
                if (response != null) {
                    errorString = response.getResponseText();
                    statusCode = response.getStatus();
                } else if (errorString == null && throwable != null) {
                    errorString = throwable.toString();
                } else if (errorString == null && jsonObject != null) {
                    errorString = jsonObject.toString();
                }
                listener.onFailure(new MFPPushException(errorString, statusCode));
            }
        });
        invoker.execute();
    }

    /**
     * Get the list of tags subscribed to
     *
     * @param listener Mandatory listener class. When the list of tags subscribed to
     *                 are successfully retrieved the {@link MFPPushResponseListener}
     *                 .onSuccess method is called with the list of tagNames
     *                 {@link MFPPushResponseListener}.onFailure method is called
     *                 otherwise
     */
    public void getSubscriptions(
            final MFPPushResponseListener<List<String>> listener) {

        MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
        String path = builder.getSubscriptionsUrl(deviceId, null);
        MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.GET, clientSecret);

        invoker.setResponseListener(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                List<String> tagNames = new ArrayList<String>();
                try {
                    JSONArray tags = (JSONArray) (new JSONObject(response.getResponseText()))
                            .get(SUBSCRIPTIONS);
                    int tagsCnt = tags.length();
                    for (int tagsIdx = 0; tagsIdx < tagsCnt; tagsIdx++) {
                        tagNames.add(tags.getJSONObject(tagsIdx)
                                .getString(TAG_NAME));
                    }
                    listener.onSuccess(tagNames);

                } catch (JSONException e) {
                    logger.error("MFPPush: getSubscriptions() - Failure while getting subscriptions.  Failure response is: " + e.getMessage());
                    listener.onFailure(new MFPPushException(e));
                }
            }

            @Override
            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                //Error while subscribing to tags.
                errorString = null;
                statusCode = 0;
                if (response != null) {
                    errorString = response.getResponseText();
                    statusCode = response.getStatus();
                } else if (errorString == null && throwable != null) {
                    errorString = throwable.toString();
                } else if (errorString == null && jsonObject != null) {
                    errorString = jsonObject.toString();
                }
                listener.onFailure(new MFPPushException(errorString, statusCode));
            }
        });
        invoker.execute();
    }

    /**
     * Get the Push Application GUID
     */
    public String getApplicationId() {
        if (!applicationId.isEmpty()) {
            return applicationId;
        } else {
            return null;
        }
    }

    /**
     * Set the default push notification options for notifications.
     *
     * @param options - The MFPPushNotificationOptions with the default parameters
     */
    public void setNotificationOptions(MFPPushNotificationOptions options) {
        this.options = options;
    }

    /**
     * Set the listener class to receive the notification status changes.
     *
     * @param statusListener - Mandatory listener class. When the notification status changes
     *                 {@link MFPPushNotificationStatusListener}.onStatusChange method is called
     */
    public void setNotificationStatusListener(MFPPushNotificationStatusListener statusListener) {
        this.statusListener = statusListener;
        synchronized (pendingStatus) {
            if(!pendingStatus.isEmpty()) {
                for(Map.Entry<String, MFPPushNotificationStatus> entry : pendingStatus.entrySet()) {
                    changeStatus(entry.getKey(), entry.getValue());
                }
                pendingStatus.clear();
            }
        }
    }

    public void changeStatus(String messageId, MFPPushNotificationStatus status) {
        if(statusListener != null) {
            statusListener.onStatusChange(messageId, status);
        } else {
            synchronized (pendingStatus) {
                pendingStatus.put(messageId, status);
            }
        }
    }

    public MFPPushNotificationOptions getNotificationOptions() {
        return options;
    }

    public void sendMessageDeliveryStatus(Context context, String messageId, String status) {
        while(true) {
            synchronized (sendDeliveryStatusLock) {
                if(!sendDeliveryStatus) {
                    try {
                        sendDeliveryStatusLock.wait();
                    } catch (InterruptedException e) {
                        logger.error("Delivery status interrupted", e);
                    }
                } else {
                    sendDeliveryStatus = false;
                    if (this.appContext == null) {
                        this.appContext = context.getApplicationContext();
                    }

                    MFPPushUrlBuilder builder = new MFPPushUrlBuilder();
                    String path = MFPPushUtils.getContentFromSharedPreferences(appContext, PREFS_MESSAGES_URL);
                    path = builder.getMessageUrl(path, messageId);
                    logger.debug("MFPPush:sendMessageDeliveryStatus() - The message status update path is: " + path);
                    if (this.applicationId == null) {
                        this.applicationId = path.split("/")[6];
                    }
                    if (this.deviceId == null) {
                        this.deviceId = MFPPushUtils.getContentFromSharedPreferences(appContext, applicationId + DEVICE_ID);
                    }
                    if (this.clientSecret == null) {
                        this.clientSecret = MFPPushUtils.getContentFromSharedPreferences(appContext, PREFS_MESSAGES_URL_CLIENT_SECRET);
                    }
                    MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.PUT, clientSecret);
                    invoker.setJSONRequestBody(buildMessage(status));
                    invoker.setResponseListener(new ResponseListener() {
                        @Override
                        public void onSuccess(Response response) {
                            synchronized (sendDeliveryStatusLock) {
                                sendDeliveryStatus = true;
                                sendDeliveryStatusLock.notify();
                            }
                            logger.info("MFPPush:sendMessageDeliveryStatus() - Successfully updated the message status.  The response is: " + response.toString());
                        }

                        @Override
                        public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                            synchronized (sendDeliveryStatusLock) {
                                sendDeliveryStatus = true;
                                sendDeliveryStatusLock.notify();
                            }
                            logger.info("MFPPush:sendMessageDeliveryStatus() - Failed to update the message status.  The response is: " + response.toString());
                        }
                    });
                    invoker.execute();
                    break;
                }
            }
        }
    }

    private String buildMessagesURL() {
        MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
        return builder.getMessagesUrl();
    }

    private void registerInBackground(final String userId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    deviceToken = FirebaseInstanceId.getInstance().getToken();
                    logger.info("MFPPush:registerInBackground() - Successfully registered with FCM. Returned deviceToken is: " + deviceToken);
                    computeRegId();
                    if (MFPPushUtils.validateString(userId)) {
                        registerWithUserId(userId);
                    } else {
                        register();
                    }
                } catch (Exception ex) {
                    msg = ex.getMessage();
                    //Failed to register at GCM Server.
                    logger.error("MFPPush:registerInBackground() - Failed to register at GCM Server. Exception is: " + ex.getMessage());
                    registerResponseListener
                            .onFailure(new MFPPushException(msg));
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    private void computeRegId() {
        logger.debug("MFPPush:computeRegId() Computing device's registrationId");

        if (regId == null) {
            AuthorizationManager authorizationManager = BMSClient.getInstance().getAuthorizationManager();
            regId = authorizationManager.getDeviceIdentity().getId();
            logger.debug("MFPPush:computeRegId() - DeviceId obtained from AuthorizationManager is : " + regId);
        }
    }

    private boolean registerWithUserId(final String userId) {

        if (isInitialized == true) {
            if (MFPPushUtils.validateString(userId) && MFPPushUtils.validateString(clientSecret)) {

                MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
                String path = builder.getDeviceIdUrl(regId);
                MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.GET, clientSecret);
                invoker.setJSONRequestBody(null);

                invoker.setResponseListener(new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        try {
                            String retDeviceId = (new JSONObject(response.getResponseText())).getString(DEVICE_ID);
                            String retToken = (new JSONObject(response.getResponseText())).getString(TOKEN);
                            String userIdFromResponse = (new JSONObject(response.getResponseText())).getString(USER_ID);

                            if (!(retDeviceId.equals(regId))
                                    || !(retToken.equals(deviceToken)) || !(userId.equals(userIdFromResponse))) {
                                deviceId = retDeviceId;
                                MFPPushUtils
                                        .storeContentInSharedPreferences(
                                                appContext, applicationId,
                                                DEVICE_ID, deviceId);

                                hasRegisterParametersChanged = true;
                                updateTokenCallback(deviceToken, userId);
                            } else {
                                deviceId = retDeviceId;
                                isTokenUpdatedOnServer = true;
                                MFPPushUtils
                                        .storeContentInSharedPreferences(
                                                appContext, applicationId,
                                                DEVICE_ID, deviceId);
                                registerResponseListener
                                        .onSuccess(response.toString());
                            }
                        } catch (JSONException e1) {
                            logger.error("MFPPush:verifyDeviceRegistrationWithUserId() - Exception caught while parsing JSON response.");
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                        // Device is not registered.
                        isNewRegistration = true;
                        updateTokenCallback(deviceToken, userId);
                    }
                });
                invoker.execute();
            } else {

                String error = "Error while registration - Please verify your UserId and ClientSecret value";
                logger.error("MFPPush:verifyDeviceRegistrationWithUserId() - Please verify your UserId and ClientSecret value");
                System.out.print("MFPPush:verifyDeviceRegistrationWithUserId() - " + error);
            }
        } else {
            String error = "Error while registration -. Not initialized MFPPush";
            logger.error("MFPPush:verifyDeviceRegistrationWithUserId() - Error while registration -. Not initialized MFPPush");
            System.out.print("MFPPush:verifyDeviceRegistrationWithUserId() - " + error);
        }

        return true;
    }

    private boolean register() {

        if (isInitialized) {
            MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
            String path = builder.getDeviceIdUrl(regId);
            MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.GET, clientSecret);
            invoker.setJSONRequestBody(null);

            invoker.setResponseListener(new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    try {
                        String retDeviceId = (new JSONObject(response.getResponseText())).getString(DEVICE_ID);
                        String retToken = (new JSONObject(response.getResponseText())).getString(TOKEN);
                        String userIdFromResponse = (new JSONObject(response.getResponseText())).getString(USER_ID);

                        if (!(retDeviceId.equals(regId))
                                || !(retToken.equals(deviceToken)) || !(userIdFromResponse.equals("anonymous"))) {
                            deviceId = retDeviceId;
                            MFPPushUtils
                                    .storeContentInSharedPreferences(
                                            appContext, applicationId,
                                            DEVICE_ID, deviceId);

                            hasRegisterParametersChanged = true;
                            updateTokenCallback(deviceToken, null);
                        } else {
                            deviceId = retDeviceId;
                            isTokenUpdatedOnServer = true;
                            MFPPushUtils
                                    .storeContentInSharedPreferences(
                                            appContext, applicationId,
                                            DEVICE_ID, deviceId);
                            registerResponseListener
                                    .onSuccess(response.toString());
                        }
                    } catch (JSONException e1) {
                        logger.error("MFPPush:VerifyDeviceRegistration() - Exception caught while parsing JSON response.");
                        e1.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                    // Device is not registered.
                    isNewRegistration = true;
                    updateTokenCallback(deviceToken, null);
                }
            });
            invoker.execute();

        } else {
            String error = "Error while registration -. Not initialized MFPPush";
            logger.error("MFPPush:verifyDeviceRegistrationWithUserId() - Error while registration -. Not initialized MFPPush");
        }
        return true;
    }

    private void updateTokenCallback(String deviceToken, String userId) {
        if (isNewRegistration) {
            logger.debug("MFPPush:updateTokenCallback() - Device is registering with push server for the first time.");
            MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
            String path = builder.getDevicesUrl();
            MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.POST, clientSecret);

            //Add header for xtify deviceId for migration
            final SharedPreferences sharedPreferences = appContext.getSharedPreferences("com.ibm.mobile.services.push", 0);
            if (MFPPushUtils.validateString(userId)) {
                invoker.setJSONRequestBody(buildDevice(userId));
            } else {
                invoker.setJSONRequestBody(buildDevice(""));
            }
            invoker.setResponseListener(new ResponseListener() {

                @Override
                public void onSuccess(Response response) {
                    try {
                        String retDeviceId = (new JSONObject(response.getResponseText())).getString(DEVICE_ID);
                        deviceId = retDeviceId;
                        MFPPushUtils
                                .storeContentInSharedPreferences(
                                        appContext, applicationId,
                                        DEVICE_ID, deviceId);

                        isNewRegistration = false;
                        isTokenUpdatedOnServer = true;
                        isRegisteredForPush = true;
                        logger.info("MFPPush:updateTokenCallback() - Successfully registered device.");
                        registerResponseListener.onSuccess(response.toString());
                    } catch (JSONException e1) {
                        logger.error("MFPPush:updateTokenCallback() - Exception caught while parsing JSON response.");
                        e1.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                    logger.error("MFPPush:updateTokenCallback() - Failure during device registration.");
                    errorString = null;
                    statusCode = 0;
                    if (response != null) {
                        errorString = response.getResponseText();
                        statusCode = response.getStatus();
                    } else if (errorString == null && throwable != null) {
                        errorString = throwable.toString();
                    } else if (errorString == null && jsonObject != null) {
                        errorString = jsonObject.toString();
                    }
                    registerResponseListener.onFailure(new MFPPushException(errorString, statusCode));
                }
            });
            invoker.execute();
        } else if (hasRegisterParametersChanged) {
            logger.debug("MFPPush:updateTokenCallback() - Device is already registered. Registration parameters have changed.");
            MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
            String path = builder.getDeviceIdUrl(deviceId);
            MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.PUT, clientSecret);

            if (MFPPushUtils.validateString(userId)) {
                invoker.setJSONRequestBody(buildDevice(userId));
            } else {
                invoker.setJSONRequestBody(buildDevice(""));
            }
            invoker.setResponseListener(new ResponseListener() {

                @Override
                public void onSuccess(Response response) {
                    logger.debug("MFPPush:updateTokenCallback() - Device registration successfully updated.");
                    isTokenUpdatedOnServer = true;
                    isNewRegistration = false;
                    isRegisteredForPush = true;
                    registerResponseListener.onSuccess(response.toString());
                }

                @Override
                public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
                    logger.debug("MFPPush:updateTokenCallback() - Failure while updating device registration details.");
                    errorString = null;
                    statusCode = 0;
                    if (response != null) {
                        errorString = response.getResponseText();
                        statusCode = response.getStatus();
                    } else if (errorString == null && throwable != null) {
                        errorString = throwable.toString();
                    } else if (errorString == null && jsonObject != null) {
                        errorString = jsonObject.toString();
                    }
                    registerResponseListener.onFailure(new MFPPushException(errorString, statusCode));
                }
            });
            invoker.execute();

            hasRegisterParametersChanged = false;
        } else {
            isTokenUpdatedOnServer = true;
            registerResponseListener.onSuccess(deviceId);
        }
    }

    private MFPPushResponseListener<JSONObject> getDeviceRegistrationListener() {
        return new MFPPushResponseListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject deviceDetail) {
                try {
                    deviceId = deviceDetail.getString(DEVICE_ID);
                    MFPPushUtils.storeContentInSharedPreferences(appContext,
                            applicationId, DEVICE_ID, deviceId);
                } catch (JSONException e) {
                    logger.debug("MFPPush:getDeviceRegistrationListener() - Missing deviceId in response.");
                }
                isTokenUpdatedOnServer = true;
                registerResponseListener.onSuccess(deviceId);
            }

            @Override
            public void onFailure(MFPPushException exception) {
                isTokenUpdatedOnServer = false;
                logger.error("MFPPush:getDeviceRegistrationListener() - Failed to register device on IMF Push server.");
                registerResponseListener.onFailure(exception);
            }
        };
    }

    private MFPPushResponseListener<JSONObject> getDeviceUpdateListener() {
        return new MFPPushResponseListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject deviceDetail) {
                isTokenUpdatedOnServer = true;
                registerResponseListener.onSuccess(deviceId);
            }

            @Override
            public void onFailure(MFPPushException exception) {
                isTokenUpdatedOnServer = false;
                logger.error("MFPPush:getDeviceUpdateListener() - Failed to update token on IMF Push server.");
                registerResponseListener.onFailure(exception);
            }
        };
    }

    private JSONObject buildDevice(String userId) {
        JSONObject device = new JSONObject();
        try {
            if (MFPPushUtils.validateString(userId)) {
                device.put(DEVICE_ID, regId);
                device.put(TOKEN, deviceToken);
                device.put(PLATFORM, "G");
                device.put(USER_ID, userId);
            } else {
                device.put(DEVICE_ID, regId);
                device.put(TOKEN, deviceToken);
                device.put(PLATFORM, "G");
            }

        } catch (JSONException e) {
            logger.error("MFPPush: buildDevice() - Error while building device JSON object.");
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
            logger.error("MFPPush: buildSubscription() - Error while building device subscription JSON object.");
            throw new RuntimeException(e);
        }

        return subscriptionObject;
    }

    private JSONObject buildMessage(String status) {
        JSONObject messageObject = new JSONObject();

        try {
            messageObject.put(DEVICE_ID, deviceId);
            messageObject.put(STATUS, status);
        } catch (JSONException e) {
            logger.error("MFPPush: buildMessage() - Error while building message JSON object.");
            throw new RuntimeException(e);
        }

        return messageObject;
    }

    private boolean isAbleToSubscribe() {
        if (!isTokenUpdatedOnServer) {
            logger.debug("MFPPush:isAbleToSubscribe() - Cannot subscribe to tag while token is not updated on the server.");
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
            MFPInternalPushMessage message = null;
            synchronized (pending) {
                if (pending.size() > 0) {
                    message = pending.remove(0);
                }
            }

            if (message == null) {
                break;
            }

            if (notificationListener != null) {
                sendNotificationToListener(message);
            }
        }
    }

    private void sendNotificationToListener(MFPInternalPushMessage message) {
        MFPSimplePushNotification simpleNotification = new MFPSimplePushNotification(
                message);
        notificationListener.onReceive(simpleNotification);
        sendMessageDeliveryStatus(appContext, message.getId(), OPEN);
        relayNotificationSync(message.getKey(), message.getId());
        MFPPush.getInstance().changeStatus(message.getId(), MFPPushNotificationStatus.OPENED);
    }

    private void relayNotificationSync(String key, String nid) {
        if (key != null) {
            try {
                Thread t = new Thread(new UpstreamSyncMessage(key, nid));
                t.start();
            } catch (Exception e) {
                logger.error("MFPPush: UpstreamSyncMessage() - Error sending upstream message.");
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

        try {
            MFPPushUtils.checkManifest (appContext);
        } catch (Exception e) {
            throw new RuntimeException("Your manifest is not allowed to work with push. " + e.getMessage());
        }
    }

    private BroadcastReceiver onMessage = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            logger.debug("MFPPush:onMessage() - Successfully received message for dispatching.");
            MFPInternalPushMessage message = (MFPInternalPushMessage) intent.getParcelableExtra(GCM_EXTRA_MESSAGE);
            changeStatus(message.getId(), MFPPushNotificationStatus.QUEUED);
            synchronized (pending) {
                pending.add(message);
            }

            dispatchPending();
        }
    };

    /**
     * Based on the PREFS_NOTIFICATION_COUNT value, fetch all the stored notifications in the same order from
     * the shared preferences and save it onto the list.
     * This method will ensure that the notifications are sent to the Application in the same order in which they arrived.
     */
    public boolean getMessagesFromSharedPreferences(int notificationId) {
        boolean gotMessages = false;
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(
                PREFS_NAME, Context.MODE_PRIVATE);

        int countOfStoredMessages = sharedPreferences.getInt(MFPPush.PREFS_NOTIFICATION_COUNT, 0);

        if (countOfStoredMessages > 0) {
            String key = null;
            try {
                Map<String, ?> allEntriesFromSharedPreferences = sharedPreferences.getAll();
                Map<String, String> notificationEntries = new HashMap<String, String>();

                for (Map.Entry<String, ?> entry : allEntriesFromSharedPreferences.entrySet()) {
                    String rKey = entry.getKey();
                    if (entry.getKey().startsWith(PREFS_NOTIFICATION_MSG)) {
                        notificationEntries.put(rKey, entry.getValue().toString());
                    }
                }

                for (Map.Entry<String, String> entry : notificationEntries.entrySet()) {
                    String nKey = entry.getKey();
                    key = nKey;
                    String msg = sharedPreferences.getString(nKey, null);

                    if (msg != null) {
                        gotMessages = true;
                        logger.debug("MFPPush:getMessagesFromSharedPreferences() - Messages retrieved from shared preferences.");
                        MFPInternalPushMessage pushMessage = new MFPInternalPushMessage(
                                new JSONObject(msg));

                        if (notificationId != 0) {
                            if (notificationId == pushMessage.getNotificationId()) {
                                isFromNotificationBar = true;
                                messageFromBar = pushMessage;
                                MFPPushUtils.removeContentFromSharedPreferences(sharedPreferences, nKey);
                                MFPPushUtils.storeContentInSharedPreferences(sharedPreferences, MFPPush.PREFS_NOTIFICATION_COUNT, countOfStoredMessages - 1);
                                break;
                            }
                        } else {
                            synchronized (pending) {
                                pending.add(pushMessage);
                            }
                            MFPPushUtils.removeContentFromSharedPreferences(sharedPreferences, nKey);
                        }
                    }
                }

            } catch (JSONException e) {
                MFPPushUtils.removeContentFromSharedPreferences(sharedPreferences, key);
            }
            if (notificationId == 0) {
                MFPPushUtils.storeContentInSharedPreferences(sharedPreferences, MFPPush.PREFS_NOTIFICATION_COUNT, 0);
            }
        }

        return gotMessages;
    }

    public void setIntent(Intent pushNotificationIntent) {
        this.pushNotificationIntent = pushNotificationIntent;
    }

    public MFPPushNotificationListener getNotificationListener() {
        return notificationListener;
    }

    public static void openMainActivityOnNotificationClick(Context ctx) {
        Intent intentToLaunch = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());

        if (intentToLaunch != null) {
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intentToLaunch);
        }
    }

    private void getSenderIdFromServerAndRegisterInBackground(final String userId) {
        MFPPushUrlBuilder builder = new MFPPushUrlBuilder(applicationId);
        String path = builder.getSettingsUrl();
        MFPPushInvoker invoker = MFPPushInvoker.newInstance(appContext, path, Request.GET, clientSecret);
        logger.debug("MFPPush: getSenderIdFromServerAndRegisterInBackground() - The url for getting gcm configuration is: " + path);
        invoker.setJSONRequestBody(null);

        invoker.setResponseListener(new ResponseListener() {

            @Override
            public void onSuccess(Response response) {
                logger.debug("MFPPush: getSenderIdFromServerAndRegisterInBackground() - success retrieving senderId from server");
                String senderId = null;
                try {
                    senderId = (String) (new JSONObject(response.getResponseText())).get(SENDER_ID);
                } catch (JSONException e) {
                    logger.error("MFPPush: getSenderIdFromServerAndRegisterInBackground() - Failure while parsing JSON object for " +
                            "retrieving senderId");
                    e.printStackTrace();
                }

                if (senderId == null) {
                    errorString = "MFPPush: getSenderIdFromServerAndRegisterInBackgound() - SenderId is not configured in the backend application.";
                    registerResponseListener.onFailure(new MFPPushException(errorString));
                } else {
                    gcmSenderId = senderId;
                    MFPPushUtils.storeContentInSharedPreferences(appContext, applicationId, SENDER_ID, gcmSenderId);
                    registerInBackground(userId);
                }
            }

            @Override
            public void onFailure(Response response, Throwable throwable, JSONObject object) {
                logger.error("MFPPush: getSenderIdFromServerAndRegisterInBackground() - Error while getting senderId from push server.");
                errorString = null;
                statusCode = 0;
                if (response != null) {
                    errorString = response.getResponseText();
                    statusCode = response.getStatus();
                } else if (errorString == null && throwable != null) {
                    errorString = throwable.toString();
                } else if (errorString == null && object != null) {
                    errorString = object.toString();
                }
                registerResponseListener.onFailure(new MFPPushException(errorString, statusCode));
            }
        });

        invoker.execute();
    }


    class UpstreamSyncMessage implements Runnable {

        String key;
        String nid;

        public UpstreamSyncMessage(String key, String nid) {
            this.key = key;
            this.nid = nid;
        }

        @Override
        public void run() {
            FirebaseMessaging fcm = FirebaseMessaging.getInstance();
            AtomicInteger msgId = new AtomicInteger();
            String id = Integer.toString(msgId.incrementAndGet());

            fcm.send(new RemoteMessage.Builder(gcmSenderId+"@gcm.googleapis.com")
                    .setMessageId(id)
                    .addData(ACTION, DISMISS_NOTIFICATION)
                    .addData(NID, nid)
                    .build());

        }
    }

}




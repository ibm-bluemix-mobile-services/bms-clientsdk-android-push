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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
//import android.support.v4.app.NotificationCompat;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.ACTION;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.DEFAULT_CHANNEL_ID;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.DISMISS_NOTIFICATION;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.DRAWABLE;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.ID;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.LEDARGB;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.LEDOFFMS;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.LEDONMS;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.LINES;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.MESSAGE_TYPE;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.NID;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.NOTIFICATIONID;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.PREFS_BMS_REGION;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.RAW;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.TEXT;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.URL;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.TYPE;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.TITLE;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.PICTURE_NOTIFICATION;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.BIGTEXT_NOTIFICATION;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.INBOX_NOTIFICATION;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPInternalPushMessage;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushUtils;


import java.util.List;
import java.util.Map;
import java.util.Random;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;
import org.json.JSONException;


/**
 * MFPPushIntentService responsible for handling communication from GCM (Google
 * Cloud Messaging). This class should be configured as the GCM intent service
 * in AndroidManifest.xml of the android application as follows:
 * <p/>
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

public class MFPPushIntentService extends FirebaseMessagingService {

    public static final String IBM_PUSH_NOTIFICATION = ".IBMPushNotification";
    public static final String CANCEL_IBM_PUSH_NOTIFICATION = ".Cancel_IBMPushNotification";
    public static final String GCM_MESSAGE = ".C2DM_MESSAGE";
    public static final String GCM_EXTRA_MESSAGE = "message";

    public static boolean isAppForeground = false;

    private static Random randomObj = new Random();

    private static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + MFPPushIntentService.class.getSimpleName());

    public static boolean isAppForeground() {
        return isAppForeground;
    }

    public static void setAppForeground(boolean isAppForeground) {
        MFPPushIntentService.isAppForeground = isAppForeground;
    }

    private String getMessageId(JSONObject dataPayload) {
        try {
            String payload = dataPayload.getString("payload");
            JSONObject payloadObject = new JSONObject(payload);
            return payloadObject.getString(NID);
        } catch (JSONException e) {
            logger.error("MFPPushIntentService:getMessageId() - Exception while parsing JSON, get payload  "+ e.toString());
        }
        return null;
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();

        Map<String, String> data = message.getData();
        int notificationId = randomObj.nextInt();
        onNotificationReceived(data, notificationId);
    }

    public void onNotificationReceived(Map <String, String> data, int notificationId) {

        JSONObject dataPayload = new JSONObject(data);
        String messageId = getMessageId(dataPayload);
        String action = data.get(ACTION);

        logger.info("MFPPushIntentService:onMessageReceived() - New notification received. Payload is: " + dataPayload.toString());

        if (action != null && action.equals(DISMISS_NOTIFICATION)) {
            logger.debug("MFPPushIntentService:handleMessageIntent() - Dismissal message from GCM Server");
            dismissNotification(data.get(NID).toString());
        } else {
            Context context = getApplicationContext();
            String regionSuffix = BMSClient.getInstance().getBluemixRegionSuffix();
            if(regionSuffix == null) {
                String region = MFPPushUtils.getContentFromSharedPreferences(context, PREFS_BMS_REGION);
                BMSClient.getInstance().initialize(context, region);
            }
            MFPPush.getInstance().changeStatus(messageId, MFPPushNotificationStatus.RECEIVED);
            MFPInternalPushMessage recMessage = new MFPInternalPushMessage(dataPayload);

            if(recMessage.getHastemplate() == 1) {

                MFPPushNotificationOptions options = MFPPush.getInstance().getNotificationOptions(this.getApplicationContext());
                if (options != null && options.getTemplateValues() != null
                        && options.getTemplateValues().length() > 0) {

                        String messageVlue = recMessage.getAlert();
                        Pattern p = Pattern.compile("\\{\\{.*?\\}\\}");
                        Matcher m = p.matcher(messageVlue);
                        while (m.find()) {
                            String res = m.toMatchResult().group(0);
                            String temp = res;
                            temp = temp.replace("{{","");
                            temp = temp.replace("}}","");
                            if(options.getTemplateValues().has(temp)) {
                                try {
                                    String k = options.getTemplateValues().getString(temp);
                                    messageVlue = messageVlue.replace(res,k);
                                } catch (JSONException e) {
                                    logger.info("MFPPushIntentService:onMessageReceived() - Received template based push notification. Empty options values or Facing error to fetch options" + e.toString());
                                }
                            }
                        }
                        recMessage.setAlert(messageVlue);
                } else {
                    logger.info("MFPPushIntentService:onMessageReceived() - Received template based push notification. Empty options values or Facing error to fetch options");
                }
            }


            if(isAppForeground()) {
                Intent intent = new Intent(MFPPushUtils.getIntentPrefix(context)
                                           + GCM_MESSAGE);
                intent.putExtra(GCM_EXTRA_MESSAGE, recMessage);
                getApplicationContext().sendBroadcast(intent);
            } else {
                MFPPush.getInstance().sendMessageDeliveryStatus(context, messageId, MFPPushConstants.SEEN);
                onUnhandled(context, recMessage, notificationId);
            }

        }
    }

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

    private void onUnhandled(Context context, MFPInternalPushMessage notification, int notificationId) {

        MFPInternalPushMessage message = notification;


        message.setNotificationId(notificationId);
        saveInSharedPreferences(message);

        if (message.getMessageType() != null && message.getMessageType().equalsIgnoreCase(MESSAGE_TYPE) ) {
            logger.info("MFPPushIntentService:onUnhandled() - Received silent push notification");
        } else {
            Intent intent = new Intent(MFPPushUtils.getIntentPrefix(context)
                    + IBM_PUSH_NOTIFICATION);
            intent.setClass(context, MFPPushNotificationHandler.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.putExtra(NOTIFICATIONID, message.getNotificationId());

            generateNotification(context, message.getAlert(),
                    getNotificationTitle(context, message.getAndroidTitle()), message.getAlert(),
                    getCustomNotificationIcon(context, message.getIcon()), intent, getNotificationSound(message), notificationId, message);
        }
    }

    private String getNotificationTitle(Context context, String title) {
        // Check if push_notification_title is defined, if not get the
        // application name

        if (title.equals(null) && title.equals("")) {
            return  title;
        }
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


    private void generateNotification(Context context, String ticker,
                                      String title, String msg, int icon, Intent intent, String sound, int notificationId, MFPInternalPushMessage message) {

        int androidSDKVersion = Build.VERSION.SDK_INT;
        long when = System.currentTimeMillis();
        Notification notification = null;
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String id = context.getPackageName();
            NotificationChannel channel = message.getChannel(context,mNotificationManager);
            if(channel != null) {
                // ArrayList<MFPInternalPushChannelGroup> channelGroups = channel.getChannelGroups();
                mNotificationManager.createNotificationChannel(channel);
            } else {

                int importance = NotificationManager.IMPORTANCE_LOW;
                channel = new NotificationChannel(id, getNotificationDefaultTitle(context),importance);
                channel.enableLights(true);
                mNotificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(this, id);
            builder.setChannelId(channel.getId());


        } else {
            builder = new NotificationCompat.Builder(this);
        }

        Intent deleteIntent = new Intent(MFPPushUtils.getIntentPrefix(context)
                                         + CANCEL_IBM_PUSH_NOTIFICATION);
        deleteIntent.putExtra(ID, message.getId());
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, notificationId, deleteIntent, 0);

        if (message.getGcmStyle() != null && androidSDKVersion > 15) {
            NotificationManager notificationManager = (NotificationManager) context
            .getSystemService(Context.NOTIFICATION_SERVICE);

            try {
                JSONObject gcmStyleObject = new JSONObject(message.getGcmStyle());
                String type = gcmStyleObject.getString(TYPE);

                if (type != null && type.equalsIgnoreCase(PICTURE_NOTIFICATION)) {
                    Bitmap remote_picture = null;
                    NotificationCompat.BigPictureStyle notificationStyle = new NotificationCompat.BigPictureStyle();
                    notificationStyle.setBigContentTitle(ticker);
                    notificationStyle.setSummaryText(gcmStyleObject.getString(TITLE));

                    try {
                        remote_picture = new getBitMapBigPictureNotification().execute(gcmStyleObject.getString(URL)).get();
                    } catch (Exception e) {
                        logger.error("MFPPushIntentService:generateNotification() - Error while fetching image file.");
                    }
                    if (remote_picture != null) {
                        notificationStyle.bigPicture(remote_picture);
                    }

                    builder.setSmallIcon(icon)
                    .setLargeIcon(remote_picture)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(PendingIntent
                                      .getActivity(context, notificationId, intent,
                                                   PendingIntent.FLAG_UPDATE_CURRENT))
                    .setDeleteIntent(deletePendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentText(msg)
                    .setStyle(notificationStyle);
                } else if (type != null && type.equalsIgnoreCase(BIGTEXT_NOTIFICATION)) {
                    NotificationCompat.BigTextStyle notificationStyle = new NotificationCompat.BigTextStyle();
                    notificationStyle.setBigContentTitle(ticker);
                    notificationStyle.setSummaryText(gcmStyleObject.getString(TITLE));
                    notificationStyle.bigText(gcmStyleObject.getString(TEXT));

                    builder.setSmallIcon(icon)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(PendingIntent
                                      .getActivity(context, notificationId, intent,
                                                   PendingIntent.FLAG_UPDATE_CURRENT))
                    .setDeleteIntent(deletePendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentText(msg)
                    .setStyle(notificationStyle);

                } else if (type != null && type.equalsIgnoreCase(INBOX_NOTIFICATION)) {
                    NotificationCompat.InboxStyle notificationStyle = new NotificationCompat.InboxStyle();
                    notificationStyle.setBigContentTitle(ticker);
                    notificationStyle.setSummaryText(gcmStyleObject.getString(TITLE));

                    String lines = gcmStyleObject.getString(LINES).replaceAll("\\[", "").replaceAll("\\]", "");
                    String[] lineArray = lines.split(",");

                    for (String line : lineArray) {
                        notificationStyle.addLine(line);
                    }

                    builder.setSmallIcon(icon)

                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(PendingIntent
                                      .getActivity(context, notificationId, intent,
                                                   PendingIntent.FLAG_UPDATE_CURRENT))
                    .setDeleteIntent(deletePendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentText(msg)
                    .setStyle(notificationStyle);
                }

              this.setNotificationActions(context,intent,notificationId,message.getCategory(),builder);
                notification = builder.build();

                if (message.getLights() != null) {
                    try {
                        JSONObject lightsObject = new JSONObject(message.getLights());
                        String ledARGB = lightsObject.getString(LEDARGB);
                        if (ledARGB!=null && ledARGB.equalsIgnoreCase("black")) {
                            notification.ledARGB = Color.BLACK;
                        } else if (ledARGB.equalsIgnoreCase("darkgray")) {
                            notification.ledARGB = Color.DKGRAY;
                        } else if (ledARGB.equalsIgnoreCase("gray")) {
                            notification.ledARGB = Color.GRAY;
                        } else if (ledARGB.equalsIgnoreCase("lightgray")) {
                            notification.ledARGB = Color.LTGRAY;
                        } else if (ledARGB.equalsIgnoreCase("white")) {

                            notification.ledARGB = Color.WHITE;
                        } else if (ledARGB.equalsIgnoreCase("red")) {
                            notification.ledARGB = Color.RED;
                        } else if (ledARGB.equalsIgnoreCase("green")) {
                            notification.ledARGB = Color.GREEN;
                        } else if (ledARGB.equalsIgnoreCase("blue")) {
                            notification.ledARGB = Color.BLUE;
                        } else if (ledARGB.equalsIgnoreCase("yellow")) {
                            notification.ledARGB = Color.YELLOW;
                        } else if (ledARGB.equalsIgnoreCase("cyan")) {
                            notification.ledARGB = Color.CYAN;
                        } else if (ledARGB.equalsIgnoreCase("magenta")) {
                            notification.ledARGB = Color.MAGENTA;
                        } else if (ledARGB.equalsIgnoreCase("transparent")) {
                            notification.ledARGB = Color.TRANSPARENT;
                        }
                        int ledOnMS = lightsObject.getInt(LEDONMS);
                        int ledOffMS = lightsObject.getInt(LEDOFFMS);

                        if (ledOnMS != 0 && ledOffMS != 0) {
                            notification.ledOnMS = ledOnMS;
                            notification.ledOffMS = ledOffMS;
                            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
                        }
                    } catch (Exception e) {
                        logger.error("MFPPushIntentService:generateNotification() - Error while parsing JSON");
                    }
                } else {
                    notification.defaults |= Notification.DEFAULT_LIGHTS;
                }

                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(notificationId, notification);
            } catch (JSONException e) {
                logger.error("MFPPushIntentService:generateNotification() - Error while parsing JSON.");
            }

        } else {
            if (androidSDKVersion > 10) {
                builder.setContentIntent(PendingIntent
                                         .getActivity(context, notificationId, intent,
                                                      PendingIntent.FLAG_UPDATE_CURRENT))
                .setDeleteIntent(deletePendingIntent)
                .setSmallIcon(icon).setTicker(ticker).setWhen(when)
                .setAutoCancel(true).setContentTitle(title)
                .setContentText(msg).setSound(getNotificationSoundUri(context, sound));

                if (androidSDKVersion > 15) {
                    int priority = getPriorityOfMessage(message);
                    builder.setPriority(priority);

                    this.setNotificationActions(context,intent,notificationId,message.getCategory(),builder);
                    notification = builder.build();
                }

                if (androidSDKVersion > 19) {
                    //As new material theme is very light, the icon is not shown clearly
                    //hence setting the background of icon to black
                    builder.setColor(Color.BLACK);
                    Boolean isBridgeSet = message.getBridge();
                    if (!isBridgeSet) {
                        // show notification only on current device.
                        builder.setLocalOnly(true);
                    }

                    notification = builder.build();
                    int receivedVisibility = 1;
                    String visibility = message.getVisibility();
                    if (visibility != null && visibility.equalsIgnoreCase(MFPPushConstants.VISIBILITY_PRIVATE)) {
                        receivedVisibility = 0;
                    }
                    if (receivedVisibility == Notification.VISIBILITY_PRIVATE && message.getRedact() != null) {
                        builder.setContentIntent(PendingIntent
                                                 .getActivity(context, notificationId, intent,
                                                              PendingIntent.FLAG_UPDATE_CURRENT))
                        .setSmallIcon(icon).setTicker(ticker).setWhen(when)
                        .setAutoCancel(true).setContentTitle(title)
                        .setContentText(message.getRedact()).setSound(getNotificationSoundUri(context, sound));

                        notification.publicVersion = builder.build();
                    }
                }

                if (androidSDKVersion > 21) {
                    String setPriority = message.getPriority();
                    if (setPriority != null && setPriority.equalsIgnoreCase(MFPPushConstants.PRIORITY_MAX)) {
                        //heads-up notification
                        builder.setContentText(msg)
                        .setFullScreenIntent(PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT), true);
                        notification = builder.build();
                    }
                }

            } else {
                notification = builder.setContentIntent(PendingIntent
                                                        .getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setDeleteIntent(deletePendingIntent)
                .setSmallIcon(icon).setTicker(ticker).setWhen(when)
                .setAutoCancel(true).setContentTitle(title)
                .setContentText(msg).setSound(getNotificationSoundUri(context, sound))
                .build();
            }

            if (message.getLights() != null) {
                try {
                    JSONObject lightsObject = new JSONObject(message.getLights());
                    String ledARGB = lightsObject.getString(LEDARGB);
                    if (ledARGB!=null && ledARGB.equalsIgnoreCase("black")) {
                        notification.ledARGB = Color.BLACK;
                    } else if (ledARGB.equalsIgnoreCase("darkgray")) {
                        notification.ledARGB = Color.DKGRAY;
                    } else if (ledARGB.equalsIgnoreCase("gray")) {
                        notification.ledARGB = Color.GRAY;
                    } else if (ledARGB.equalsIgnoreCase("lightgray")) {
                        notification.ledARGB = Color.LTGRAY;
                    } else if (ledARGB.equalsIgnoreCase("white")) {
                        notification.ledARGB = Color.WHITE;
                    } else if (ledARGB.equalsIgnoreCase("red")) {
                        notification.ledARGB = Color.RED;
                    } else if (ledARGB.equalsIgnoreCase("green")) {
                        notification.ledARGB = Color.GREEN;
                    } else if (ledARGB.equalsIgnoreCase("blue")) {
                        notification.ledARGB = Color.BLUE;
                    } else if (ledARGB.equalsIgnoreCase("yellow")) {
                        notification.ledARGB = Color.YELLOW;
                    } else if (ledARGB.equalsIgnoreCase("cyan")) {
                        notification.ledARGB = Color.CYAN;
                    } else if (ledARGB.equalsIgnoreCase("magenta")) {
                        notification.ledARGB = Color.MAGENTA;
                    } else if (ledARGB.equalsIgnoreCase("transparent")) {
                        notification.ledARGB = Color.TRANSPARENT;
                    }
                    int ledOnMS = lightsObject.getInt(LEDONMS);
                    int ledOffMS = lightsObject.getInt(LEDOFFMS);

                    if (ledOnMS != 0 && ledOffMS != 0) {
                        notification.ledOnMS = ledOnMS;
                        notification.ledOffMS = ledOffMS;
                        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
                    }
                } catch (Exception e) {
                    logger.error("MFPPushIntentService:generateNotification() - Error while parsing JSON");
                }
            } else {
                notification.defaults |= Notification.DEFAULT_LIGHTS;
            }

            NotificationManager notificationManager = (NotificationManager) context
            .getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId, notification);
        }
    }

    private void setNotificationActions(Context context, Intent intent, int notificationId, String messageCategory, NotificationCompat.Builder mBuilder ){
        MFPPushNotificationOptions options = MFPPush.getInstance().getNotificationOptions(context);
        if (options != null && options.getInteractiveNotificationCategories() != null){

            List<MFPPushNotificationCategory> categorylist = options.getInteractiveNotificationCategories();

            for(MFPPushNotificationCategory category:categorylist) {
                if(category.getCategoryName().equals( messageCategory)) {
                    for (MFPPushNotificationButton newButton:category.getButtons()) {
                        intent.setAction(newButton.getButtonName());
                        mBuilder.addAction(getResourceIdForCustomIcon(context, DRAWABLE, newButton.getIcon()), newButton.getLabel(),
                                PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                    }

                }

            }
        }
    }

    private int getPriorityOfMessage (MFPInternalPushMessage message) {
        String priorityFromServer = message.getPriority();
        MFPPushNotificationOptions options = MFPPush.getInstance().getNotificationOptions(this.getApplicationContext());
        int priorityPreSetValue = 0;

        if (options != null && options.getPriority()!=null) {
            priorityPreSetValue = options.getPriority().getValue();
        }

        if (priorityFromServer != null) {
            if (priorityFromServer.equalsIgnoreCase(MFPPushConstants.PRIORITY_MAX)) {
                return Notification.PRIORITY_MAX;
            } else if (priorityFromServer.equalsIgnoreCase(MFPPushConstants.PRIORITY_MIN)) {
                return Notification.PRIORITY_MIN;
            } else if (priorityFromServer.equalsIgnoreCase(MFPPushConstants.PRIORITY_HIGH)) {
                return Notification.PRIORITY_HIGH;
            } else if (priorityFromServer.equalsIgnoreCase(MFPPushConstants.PRIORITY_LOW)) {
                return Notification.PRIORITY_LOW;
            }
        } else if (priorityPreSetValue != 0){
            return priorityPreSetValue;
        }
        return Notification.PRIORITY_DEFAULT;
    }

    private String getNotificationSound(MFPInternalPushMessage message) {
        String soundFromServer = message.getSound();
        String soundPreSet = null;
        MFPPushNotificationOptions options = MFPPush.getInstance().getNotificationOptions(this.getApplicationContext());

        if (options != null && options.getSound()!=null){
            soundPreSet = options.getSound();
        }
        if (soundFromServer != null) {
            return soundFromServer;
        } else if (soundPreSet != null) {
            return soundPreSet;
        }
        return null;
    }

    private String getNotificationDefaultTitle(Context context) {
        int notificationTitle = -1;
        try {
            notificationTitle = MFPPushUtils.getResourceId(getApplicationContext(),
                    "string", DEFAULT_CHANNEL_ID);
            return context.getString(notificationTitle);
        } catch (Exception e) {
            // ignore the exception
        }
        return  DEFAULT_CHANNEL_ID;
    }

    public Uri getNotificationSoundUri(Context context, String sound) {
        Uri uri = null;

        if (sound == null) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else if (!(sound.trim().isEmpty())) {
            String soundResourceString = sound;
            try {
                if (soundResourceString.contains(".")) {
                    soundResourceString = soundResourceString.substring(0, soundResourceString.indexOf("."));
                }
                int resourceId = getResourceId(context, RAW, soundResourceString);
                if (resourceId == -1) {
                    logger.error("MFPPushIntentService:getNotificationSoundUri() - Specified sound file is not found in res/raw");
                }
                uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);
            } catch (Exception e) {
                logger.error("MFPPushIntentService:getNotificationSoundUri() - Exception while parsing sound file");
            }
        }

        return uri;
    }

    public int getCustomNotificationIcon(Context context, String resourceName) {
        int resourceId = -1;

        try {
            if (resourceName == null){
                resourceName = "push_notification_icon";
            }
            resourceId = getResourceIdForCustomIcon(context, DRAWABLE, resourceName);
        } catch (Exception e) {
            logger.error("MFPPushIntentService: getCustomNotification() - Exception while parsing icon file.");
            resourceId = android.R.drawable.btn_star;
        }

        if (resourceId == 0) {
            resourceId = android.R.drawable.btn_star;
        }
        return resourceId;
    }

    public static int getResourceId(Context context, String resourceCategory, String resourceName) {
        int resourceId = -1;
        try {
            resourceId = context.getResources().getIdentifier(resourceName, RAW, context.getPackageName());
        } catch (Exception e) {
            logger.error("MFPPushIntentService: getResourceId() - Failed to find resource R." + resourceCategory + "." + resourceName, e);
        }
        return resourceId;
    }

    public static int getResourceIdForCustomIcon(Context context, String resourceCategory, String resourceName) {
        int resourceId = -1;

        try {
            resourceId = context.getResources().getIdentifier(resourceName, DRAWABLE, context.getPackageName());
        } catch (Exception e) {
            logger.error("MFPPushIntentService: Failed to find resource R." + resourceCategory + "." + resourceName, e);
        }
        return resourceId;
    }

    protected void dismissNotification(String nid) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(MFPPush.PREFS_NAME, Context.MODE_PRIVATE);
        int countOfStoredMessages = sharedPreferences.getInt(MFPPush.PREFS_NOTIFICATION_COUNT, 0);

        if (countOfStoredMessages > 0) {
            for (int index = 1; index <= countOfStoredMessages; index++) {

                String key = MFPPush.PREFS_NOTIFICATION_MSG + index;
                try {
                    String msg = sharedPreferences.getString(key, null);
                    if (msg != null) {
                        JSONObject messageObject = new JSONObject(msg);
                        if (messageObject != null && !messageObject.isNull(NID)) {
                            String id = messageObject.getString(NID);
                            if (id != null && id.equals(nid)) {
                                MFPPushUtils.removeContentFromSharedPreferences(sharedPreferences, key);
                                MFPPushUtils.storeContentInSharedPreferences(sharedPreferences, MFPPush.PREFS_NOTIFICATION_COUNT, countOfStoredMessages - 1);
                                NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.cancel(messageObject.getInt(NOTIFICATIONID));
                            }
                        }
                    }
                } catch (JSONException e) {
                    logger.error("MFPPushIntentService: dismissNotification() - Failed to dismiss notification.");
                }
            }
        }
    }

    class getBitMapBigPictureNotification extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                return null;
            }
        }

    }

}

package com.ibm.mobilefirstplatform.clientsdk.android.push.internal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushIntentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MFPInternalPushChannel {

    private static final String GROUP_CHANNEL_ID = "channelId";
    private static final String GROUP_CHANNEL_NAME = "channelName";
    private static final String GROUP_CHANNEL_IMPORTANCE = "importance";
    private static final String GROUP_CHANNEL_ENABLE_LIGHTS = "enableLights";
    private static final String GROUP_CHANNEL_ENABLE_VIBRATION = "enableVibration";
    private static final String GROUP_CHANNEL_LIGHT_COLOR = "lightColor";
    private static final String GROUP_CHANNEL_LOCKSCREEN_VISIBILITY = "lockScreenVisibility";
    private static final String GROUP_CHANNEL_GROUP = "groupJson";
    private static final String GROUP_CHANNEL_BYPASS_DND = "bypassDND";
    private static final String GROUP_CHANNEL_DESCRIPTION = "description";
    private static final String GROUP_CHANNEL_SHOWBADGE = "showBadge";
    private static final String GROUP_CHANNEL_SOUND = "sound";
    private static final String GROUP_CHANNEL_VIBRATIONPATTERN = "vibrationPattern";


    private String channelId = null;
    private String channelName = null;
    private int importance = NotificationManager.IMPORTANCE_DEFAULT;
    private boolean enableLights = false;
    private boolean enableVibration = false;
    private String lightColor = null;
    private int lockScreenVisibility = Notification.VISIBILITY_PUBLIC;
    private JSONObject groupJson = null;
    private boolean bypassDND = false;
    private String description = null;
    private boolean showBadge = true;
    private String sound = null;
    private JSONArray vibrationPattern = new JSONArray();
    //private MFPInternalPushChannelGroup groupsArray[] = {};

    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + MFPInternalPushChannel.class.getSimpleName());

    @Override
    public String toString() {
        return "MFPInternalPushChannel [channelId=" + channelId + ", channelName=" + channelName +
                ",importance="+ importance +", enableLights=" +enableLights+", enableVibration ="+enableVibration+"" +
                " lightColor="+lightColor+",lockScreenVisibility="+lockScreenVisibility+", " +
                "groupJson="+groupJson+", bypassDND="+bypassDND+", description="+description+", showBadge="+showBadge+", " +
                "vibrationPattern="+vibrationPattern+" ]";
    }
    public String getChannelId() {
        return channelId;
    }
    public String getChannelName() {
        return channelName;
    }
    public int getImportance() {
        return importance;
    }
    public boolean getEnableLights() {return enableLights;}
    public boolean getEnableVibration() {return enableVibration;}
    public String getLightColor() {return lightColor;}
    public int getLockScreenVisibility() {
        return lockScreenVisibility;
    }
    public JSONObject getGroupJson() { return groupJson;}
    public boolean getBypassDND() { return  bypassDND;}
    public String getDescription() { return description;}
    public boolean getShowBadge() {return showBadge;}
    public String getSound() { return sound;}
    public JSONArray getVibrationPattern() {return  vibrationPattern;}


    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    public void setChannelName(String channelName) { this.channelName = channelName;}
    public void setImportance(int importance) { this.importance = importance;}
    public void setEnableLights(boolean enableLights) { this.enableLights = enableLights;}
    public void setEnableVibration(boolean enableVibration) { this.enableVibration = enableVibration;}
    public void setLightColor(String lightColor) { this.lightColor = lightColor;}
    public void setLockScreenVisibility(int lockScreenVisibility) { this.lockScreenVisibility = lockScreenVisibility;}
    public void setGroupsArray(JSONObject groupJson) { this.groupJson = groupJson;}
    public void setBypassDND(boolean bypassDND) { this.bypassDND = bypassDND;}
    public void setShowBadge(boolean showBadge) { this.showBadge = showBadge;}
    public void setDescription(String description) { this.description = description;}
    public void setSound(String sound) { this.sound = sound;}
    public void setVibrationPattern(JSONArray vibrationPattern) { this.vibrationPattern = vibrationPattern;}



    public MFPInternalPushChannel(JSONObject json) {

        try {
            channelId = json.getString(GROUP_CHANNEL_ID);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            channelName = json.getString(GROUP_CHANNEL_NAME);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            importance = json.getInt(GROUP_CHANNEL_IMPORTANCE);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            enableLights = json.getBoolean(GROUP_CHANNEL_ENABLE_LIGHTS);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            enableVibration = json.getBoolean(GROUP_CHANNEL_ENABLE_VIBRATION);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            lightColor = json.getString(GROUP_CHANNEL_LIGHT_COLOR);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            lockScreenVisibility = json.getInt(GROUP_CHANNEL_LOCKSCREEN_VISIBILITY);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            groupJson = json.getJSONObject(GROUP_CHANNEL_GROUP);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            bypassDND = json.getBoolean(GROUP_CHANNEL_BYPASS_DND);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            description = json.getString(GROUP_CHANNEL_DESCRIPTION);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            showBadge = json.getBoolean(GROUP_CHANNEL_SHOWBADGE);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            sound = json.getString(GROUP_CHANNEL_SOUND);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }

        try {
            vibrationPattern = json.getJSONArray(GROUP_CHANNEL_VIBRATIONPATTERN);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationChannelGroup getChannelGroup() {

        if (groupJson != null) {

            MFPInternalPushChannelGroup group = new MFPInternalPushChannelGroup(this.groupJson);
            NotificationChannelGroup channelGroup = new NotificationChannelGroup(group.getGroupId(),group.getChannelGroupName());

            return  channelGroup;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationChannel getChannel(Context context, NotificationManager mNotificationManager) {

        NotificationChannel mChannel = new NotificationChannel(this.channelId, this.channelName,this.importance);
        mChannel.enableLights(this.enableLights);
        mChannel.enableVibration(this.enableVibration);
        mChannel.setLightColor(getLightColor(this.lightColor));
        mChannel.setLockscreenVisibility(this.lockScreenVisibility);
        mChannel.setBypassDnd(this.bypassDND);
        mChannel.setShowBadge(this.showBadge);
        if (this.description != null) {
            mChannel.setDescription(this.description);
        }
        if (this.sound != null) {
            MFPPushIntentService mfpPushIntentService = new MFPPushIntentService();
            mChannel.setSound(mfpPushIntentService.getNotificationSoundUri(context,this.sound),null);
        }

        if(this.vibrationPattern != null) {
            int len = this.vibrationPattern.length();
            long[] list = new long[len];
            for (int i=0;i<len;i++){
                try {
                    list[i] = this.vibrationPattern.getLong(i);
                } catch (JSONException e) {
                    logger.error("MFPInternalPushChannel: MFPInternalPushChannel() - Exception while parsing JSON, get alert.  "+ e.toString());
                }
            }
            if(list.length > 0) {
                mChannel.setVibrationPattern(list);
            }
        }

        if(this.groupJson != null ) {
            NotificationChannelGroup channelGroup = getChannelGroup();
            mNotificationManager.createNotificationChannelGroup(channelGroup);
            mChannel.setGroup(channelGroup.getId());
        }
        return  mChannel;
    }

    private int getLightColor(String ledARGB) {
        if (ledARGB!=null && ledARGB.equalsIgnoreCase("black")) {
            return Color.BLACK;
        } else if (ledARGB.equalsIgnoreCase("darkgray")) {
            return Color.DKGRAY;
        } else if (ledARGB.equalsIgnoreCase("gray")) {
            return Color.GRAY;
        } else if (ledARGB.equalsIgnoreCase("lightgray")) {
            return Color.LTGRAY;
        } else if (ledARGB.equalsIgnoreCase("white")) {
            return Color.WHITE;
        } else if (ledARGB.equalsIgnoreCase("red")) {
            return Color.RED;
        } else if (ledARGB.equalsIgnoreCase("green")) {
            return Color.GREEN;
        } else if (ledARGB.equalsIgnoreCase("blue")) {
            return Color.BLUE;
        } else if (ledARGB.equalsIgnoreCase("yellow")) {
            return Color.YELLOW;
        } else if (ledARGB.equalsIgnoreCase("cyan")) {
            return Color.CYAN;
        } else if (ledARGB.equalsIgnoreCase("magenta")) {
            return Color.MAGENTA;
        } else if (ledARGB.equalsIgnoreCase("transparent")) {
            return Color.TRANSPARENT;
        } else {
            return Color.BLACK;
        }
    }
}
package com.ibm.mobilefirstplatform.clientsdk.android.push.internal;

import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class MFPInternalPushChannelGroup {

    private static final String CHANNEL_GROUP_ID = "groupId";
    private static final String CHANNEL_GROUP_NAME = "groupName";

    private String groupId = null;
    private String groupName = null;

    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + MFPInternalPushChannelGroup.class.getSimpleName());

    public MFPInternalPushChannelGroup() {
    }

    public MFPInternalPushChannelGroup(JSONObject json) {

        try {
            groupId = json.getString(CHANNEL_GROUP_ID);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannelGroup: MFPInternalPushChannelGroup() - Exception while parsing JSON, get alert.  "+ e.toString());
        }
        try {
            groupName = json.getString(CHANNEL_GROUP_NAME);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannelGroup: MFPInternalPushChannelGroup() - Exception while parsing JSON, get androidTitle.  "+ e.toString());
        }
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put(CHANNEL_GROUP_ID, groupId);
            json.put(CHANNEL_GROUP_NAME, groupName);
        } catch (JSONException e) {
            logger.error("MFPInternalPushChannelGroup: MFPInternalPushChannelGroup() - Exception while parsing JSON.  "+ e.toString());
        }
        return json;
    }

    @Override
    public String toString() {
        return "MFPInternalPushChannelGroup [groupId=" + groupId + ", groupName=" + groupName + "]";
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getChannelGroupName() {
        return groupName;
    }

    public void setChannelGroupName(String groupName) {
        this.groupName = groupName;
    }
}

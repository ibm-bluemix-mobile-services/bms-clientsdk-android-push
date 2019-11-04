package com.ibm.mobilefirstplatform.clientsdk.android.app;

import com.google.firebase.messaging.RemoteMessage;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushIntentService;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPInternalPushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MessageIntentService extends MFPPushIntentService {


    @Override
    public void onMessageReceived(RemoteMessage message) {

        String from = message.getFrom();

        Map<String, String> data = message.getData();
        JSONObject dataPayload = new JSONObject(data);

        MFPInternalPushMessage recMessage = new MFPInternalPushMessage(dataPayload);
        int collapseid = -1;
        JSONObject payload = null;
        try {
            payload = new JSONObject(recMessage.getPayload());
            if(payload != null && payload.has("collapseId")) {
                collapseid = payload.getInt("collapseId");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(collapseid != -1 ) {
            onNotificationReceived(data, collapseid);
        } else {
            super.onMessageReceived(message);
        }
    }
}

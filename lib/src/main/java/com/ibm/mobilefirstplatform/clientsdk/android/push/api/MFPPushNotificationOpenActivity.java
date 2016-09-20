package com.ibm.mobilefirstplatform.clientsdk.android.push.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPInternalPushMessage;

/**
 * Created by jialfred on 9/20/16.
 */

public class MFPPushNotificationOpenActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

      Intent in = getIntent();
      MFPInternalPushMessage messageFromParcel = in.getParcelableExtra("message");
      int notificationId = messageFromParcel.getNotificationId();

      MFPPush.getInstance().updateSharePreferenceAndDispatchNotification(notificationId);
      finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

      Intent in = getIntent();
      MFPInternalPushMessage messageFromParcel = in.getParcelableExtra("message");
      int notificationId = messageFromParcel.getNotificationId();

      MFPPush.getInstance().updateSharePreferenceAndDispatchNotification(notificationId);
      finish();
    }

}

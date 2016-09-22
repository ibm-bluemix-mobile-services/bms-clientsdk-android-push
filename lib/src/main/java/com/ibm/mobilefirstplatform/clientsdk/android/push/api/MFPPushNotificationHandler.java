package com.ibm.mobilefirstplatform.clientsdk.android.push.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.NOTIFICATIONID;

/**
 * Created by jialfred on 9/20/16.
 */

public class MFPPushNotificationHandler extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        MFPPush.getInstance().setIntent(getIntent());
        MFPPush.getInstance().openMainActivityOnNotificationClick(this);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        MFPPush.getInstance().setIntent(getIntent());
        MFPPush.getInstance().openMainActivityOnNotificationClick(this);
        finish();
    }

}

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.ID;

/**
 * Broadcast receiver to handle the dismissal or clear notifications from the notification tray.
 */
public class MFPPushNotificationDismissHandler extends BroadcastReceiver {
    /**
     * This method is called when a notification is dimissed or cleared from the
     * notification tray.
     *
     * @param context
     *            This is the context of the application
     * @param intent
     *            Intent which invoked this receiver
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String messageId = intent.getStringExtra(ID);
        MFPPush.getInstance().changeStatus(messageId, MFPPushNotificationStatus.DISMISSED);
    }
}

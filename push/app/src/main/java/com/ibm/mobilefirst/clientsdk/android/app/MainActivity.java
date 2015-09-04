package com.ibm.mobilefirst.clientsdk.android.app;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.ibm.mobilefirst.clientsdk.android.push.api.IMFPush;
import com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushException;
import com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushNotificationListener;
import com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushResponseListener;
import com.ibm.mobilefirst.clientsdk.android.push.api.IMFSimplePushNotification;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.FailResponse;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.MFPRequest;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResourceRequest;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;

import java.net.MalformedURLException;
import java.util.List;

public class MainActivity extends Activity {

    private TextView txtVResult = null;

    private IMFPush push = null;
    private IMFPushNotificationListener notificationListener = null;

    private List<String> allTags;
    private List<String> subscribedTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtVResult = (TextView) findViewById(R.id.display);

        updateTextView("Starting Push Android Sample..");

        try {
                BMSClient.getInstance().initialize(getApplicationContext(), "http://imfpush.stage1-dev.ng.bluemix.net", "android-sdk-test2");
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        push = IMFPush.getInstance(getApplicationContext());
        push.register("DemoDevice",
                new IMFPushResponseListener<String>() {
                    @Override
                    public void onSuccess(String deviceId) {
                        updateTextView("Device is registered with Push Service.");
                        displayTags();
                    }

                    @Override
                    public void onFailure(IMFPushException ex) {
                        updateTextView("Error registering with Push Service...\n"
                                + "Push notifications will not be received.");
                    }
                });


        final Activity activity = this;

        notificationListener = new IMFPushNotificationListener() {

            @Override
            public void onReceive(final IMFSimplePushNotification message) {
                showNotification(activity, message);

            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (push != null) {
            push.listen(notificationListener);
        }
    }

    void showNotification(Activity activity, IMFSimplePushNotification message) {
        Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Notification Received : " + message.toString());
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void displayTags() {
        push.getTags(new IMFPushResponseListener<List<String>>() {
            @Override
            public void onSuccess(List<String> tags) {
                updateTextView("Retrieved Tags : " + tags);
                allTags = tags;
                displayTagSubscriptions();
            }

            @Override
            public void onFailure(IMFPushException ex) {
                updateTextView("Error getting tags..." + ex.getMessage());
            }
        });
    }

    void displayTagSubscriptions() {
        push.getSubscriptions(new IMFPushResponseListener<List<String>>() {
            @Override
            public void onSuccess(List<String> tags) {
                updateTextView("Retrieved subscriptions : " + tags);
                System.out.println("Subscribed tags are: "+tags);
                subscribedTags = tags;
                subscribeToTag();
            }

            @Override
            public void onFailure(IMFPushException ex) {
                updateTextView("Error getting subscriptions.. "
                        + ex.getMessage());
            }
        });
    }

    void subscribeToTag() {
        System.out.println("subscribedTags: "+ subscribedTags+ "size is: "+subscribedTags.size());
        System.out.println("allTags: " + allTags +"Size is: "+allTags.size());

        if ((allTags.size() != 0)) {
            push.subscribe(allTags.get(0),
                    new IMFPushResponseListener<String>() {
                        @Override
                        public void onFailure(IMFPushException ex) {
                            updateTextView("Error subscribing to Tag1.."
                                    + ex.getMessage());
                        }

                        @Override
                        public void onSuccess(String arg0) {
                            updateTextView("Succesfully Subscribed to Tag1...");
                        }
                    });
        } else {
            updateTextView("Not subscribing to any more tags.");
        }

    }

    public void updateTextView(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtVResult.append(str);
                txtVResult.append("\n");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (push != null) {
            push.hold();
        }


    }
}

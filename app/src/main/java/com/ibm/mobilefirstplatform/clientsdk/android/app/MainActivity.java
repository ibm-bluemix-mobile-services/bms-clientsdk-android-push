package com.ibm.mobilefirstplatform.clientsdk.android.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;

import java.net.MalformedURLException;
import java.util.List;

public class MainActivity extends Activity {

    private TextView txtVResult = null;

    private MFPPush push = null;
    private MFPPushNotificationListener notificationListener = null;

    private List<String> allTags;
    private List<String> subscribedTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtVResult = (TextView) findViewById(R.id.display);

        updateTextView("Starting Push Android Sample..");

        try {
                BMSClient.getInstance().initialize(getApplicationContext(), "http://simpletestappnew.stage1.bluemix.net", "d88c328d-631f-49ca-a80d-f4efb32de716");
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        MFPPush.getInstance().initialize(getApplicationContext());
        push = MFPPush.getInstance();
        push.register(new MFPPushResponseListener<String>() {
                    @Override
                    public void onSuccess(String deviceId) {
                        updateTextView("Device is registered with Push Service.");
                        displayTags();
                    }

                    @Override
                    public void onFailure(MFPPushException ex) {
                        updateTextView("Error registering with Push Service...\n"
                                + "Push notifications will not be received.");
                    }
                });


        final Activity activity = this;


            notificationListener = new MFPPushNotificationListener() {

                @Override
                public void onReceive(final MFPSimplePushNotification message) {
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

    void showNotification(Activity activity, MFPSimplePushNotification message) {
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
        push.getTags(new MFPPushResponseListener<List<String>>() {
            @Override
            public void onSuccess(List<String> tags) {
                updateTextView("Retrieved Tags : " + tags);
                allTags = tags;
                displayTagSubscriptions();
            }

            @Override
            public void onFailure(MFPPushException ex) {
                updateTextView("Error getting tags..." + ex.getMessage());
            }
        });
    }

    void unregisterDevice(){
        push.unregister(new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String s) {
                updateTextView("Device is successfully unregistered. Success response is: " + s);
            }

            @Override
            public void onFailure(MFPPushException e) {
                updateTextView("Device unregistration failure. Failure response is: "+ e);
            }
        });
    }

    void unsubscribeFromTags(final String tag) {
        push.unsubscribe(tag, new MFPPushResponseListener<String>() {

            @Override
            public void onSuccess(String s) {
                updateTextView("Unsubscribing from tag");
                updateTextView("Successfully unsubscribed from tag . " + tag);
            }

            @Override
            public void onFailure(MFPPushException e) {
                updateTextView("Error while unsubscribing from tags. " + e.getMessage());
            }

        });
    }

    void displayTagSubscriptions() {
        push.getSubscriptions(new MFPPushResponseListener<List<String>>() {
            @Override
            public void onSuccess(List<String> tags) {
                updateTextView("Retrieved subscriptions : " + tags);
                System.out.println("Subscribed tags are: " + tags);
                subscribedTags = tags;
                subscribeToTag();
            }

            @Override
            public void onFailure(MFPPushException ex) {
                updateTextView("Error getting subscriptions.. "
                        + ex.getMessage());
            }
        });
    }

    void subscribeToTag() {
        System.out.println("subscribedTags: "+ subscribedTags+ "size is: "+subscribedTags.size());
        System.out.println("allTags: " + allTags + "Size is: " + allTags.size());

        if ((allTags.size() != 0)) {
            push.subscribe(allTags.get(0),
                    new MFPPushResponseListener<String>() {
                        @Override
                        public void onFailure(MFPPushException ex) {
                            updateTextView("Error subscribing to Tag1.."
                                    + ex.getMessage());
                        }

                        @Override
                        public void onSuccess(String arg0) {
                            updateTextView("Succesfully Subscribed to: "+ arg0);
                            unsubscribeFromTags(arg0);
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

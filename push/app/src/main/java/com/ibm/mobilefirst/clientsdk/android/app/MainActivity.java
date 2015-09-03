package com.ibm.mobilefirst.clientsdk.android.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ibm.mobilefirst.clientsdk.android.push.api.IMFPush;
import com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushException;
import com.ibm.mobilefirst.clientsdk.android.push.api.IMFPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.FailResponse;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.MFPRequest;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResourceRequest;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;

import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity implements ResponseListener{

    private IMFPush push = null;
    private TextView txtVResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            BMSClient.getInstance().initialize(getApplicationContext(), "http://imfpush.stage1-dev.ng.bluemix.net", "aa6c4d5c-1650-41cf-804c-6d73ecd087da");
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
//        //txtVResult = (TextView) findViewById(R.id.display);
//        push = IMFPush.getInstance();
//        push.register("sampleConsumerId", new IMFPushResponseListener<String>() {
//
//            @Override
//            public void onSuccess(String o) {
//                System.out.println("Success response in testApp is: "+ o);
//            }
//
//            @Override
//            public void onFailure(IMFPushException e) {
//                System.out.println("Failure response in testApp is: "+ e.getMessage());
//
//            }
//        });
//        ResourceRequest request = new ResourceRequest(this, "http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/devices", MFPRequest.GET);
        ResourceRequest request = new ResourceRequest("http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/devices", MFPRequest.GET, 10);

        request.send(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(Response response) {
        System.out.println("Success response in MainActivity is: "+ response.toString());
    }

    @Override
    public void onFailure(FailResponse failResponse, Throwable throwable) {
        System.out.println("Failure response in MainActivity is: "+ failResponse.toString());
    }

//    public void updateTextView(final String str) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                txtVResult.append(str);
//                txtVResult.append("\n");
//            }
//        });
//    }
}

package com.ibm.mobilefirst.clientsdk.android.app;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.ibm.mobilefirst.clientsdk.android.push.api.IBMPush;
import com.ibm.mobilefirst.clientsdk.android.push.api.IBMPushException;
import com.ibm.mobilefirst.clientsdk.android.push.api.IBMPushResponseListener;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public static void main(String[] args){
        IBMPush push = IBMPush.initializeService();

        push.register("sampleAlias", "sampleConsumerId", new IBMPushResponseListener<String>() {

            @Override
            public void onSuccess(String o) {
                System.out.println("Success response in testApp is: " + o);
            }

            @Override
            public void onFailure(IBMPushException e) {
                System.out.println("Failure response in testApp is: " + e.getMessage());

            }
        });
    }
}

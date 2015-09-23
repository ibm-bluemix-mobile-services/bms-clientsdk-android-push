/*
    Copyright 2015 IBM Corp.
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

package com.ibm.mobilefirstplatform.clientsdk.android.push.internal;

import android.content.Context;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;

import org.json.JSONObject;

import java.net.MalformedURLException;

public class MFPPushInvoker implements ResponseListener{

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static Context appContext = null;

    private Request requestBuilder = null;
    private MFPPushResponseListener<JSONObject> listener = null;
    private JSONObject requestBody = null;
    private MFPPushNotificationListener notificationListener = null;
    private ResponseListener responseListener = null;

    protected static Logger logger = Logger.getInstance("com.ibm.mobilefirstplatform.clientsdk.android.push.internal");

    private MFPPushInvoker(String url, String method) {
        requestBuilder = new Request(url, method);
    }

    public static MFPPushInvoker newInstance(Context ctx, String url, String method) {
        appContext = ctx;
        return new MFPPushInvoker(url, method);
    }

    public MFPPushInvoker setResponseListener(
            MFPPushResponseListener<JSONObject> listener) {
        this.listener = listener;
        return this;
    }

    public void setJSONRequestBody(JSONObject object){
        this.requestBody = object;
    }

    public void setResponseListener(ResponseListener listener){
        this.responseListener = listener;
    }

    public MFPPushInvoker addHeaders(String headerName, String headerValue) {

        if (headerName == null && headerValue == null) {
            requestBuilder.addHeader(CONTENT_TYPE, APPLICATION_JSON);
        } else {
            requestBuilder.addHeader(CONTENT_TYPE, APPLICATION_JSON);
            requestBuilder.addHeader(headerName, headerValue);
        }
        return this;
    }

    public void execute() {
        try {
            logger.info("MFPPushInvoker: execute().  Sending request to push server, with url = " + requestBuilder.getUrl().toString()
                            + "with http method = "+ requestBuilder.getMethod());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (requestBody != null && requestBody.length() != 0) {
            requestBuilder.send(requestBody, this);
        } else {
            requestBuilder.send(appContext,this);
        }
    }

    @Override
    public void onSuccess(Response response) {
        logger.debug("MFPPushInvoker.onSuccess() - Success response in invoker is: "+ response.toString());
        responseListener.onSuccess(response);
    }

    @Override
    public void onFailure(Response response, Throwable throwable, JSONObject object) {
        responseListener.onFailure(response, throwable, object);
    }
}

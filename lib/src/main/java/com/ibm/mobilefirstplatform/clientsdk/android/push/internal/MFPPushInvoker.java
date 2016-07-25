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

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;

import org.json.JSONObject;

import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.IMFPUSH_CLIENT_SECRET;
import static com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushConstants.IMFPUSH_USER_ID;

public class MFPPushInvoker implements ResponseListener{

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCEPT = "Accept";
    private static final String APPLICATION_JSON = "application/json";
     private static final String X_REWRITE_DOMAIN = "X-REWRITE-DOMAIN";
    private static Context appContext = null;

    private Request request = null;
    private MFPPushResponseListener<JSONObject> listener = null;
    private JSONObject requestBody = null;
    private MFPPushNotificationListener notificationListener = null;
    private ResponseListener responseListener = null;

    protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + MFPPushInvoker.class.getSimpleName());

    private MFPPushInvoker(String url, String method) {
        request = new Request(url, method);
        request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.addHeader(ACCEPT, APPLICATION_JSON);
        MFPPushUrlBuilder builder = new MFPPushUrlBuilder(BMSClient.getInstance().getBluemixAppGUID());
        request.addHeader(X_REWRITE_DOMAIN, builder.getRewriteDomain());
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

        if (headerName != null && headerValue != null) {
            request.addHeader(headerName, headerValue);
        }
        return this;
    }

    public void execute() {
        logger.info("MFPPushInvoker: execute().  Sending request to push server, with url = " + request.getUrl().toString()
                            + " with http method = "+ request.getMethod());
        if (requestBody != null && requestBody.length() != 0) {
            request.send(appContext, requestBody.toString(), this);
        } else {
            request.send(appContext,this);
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

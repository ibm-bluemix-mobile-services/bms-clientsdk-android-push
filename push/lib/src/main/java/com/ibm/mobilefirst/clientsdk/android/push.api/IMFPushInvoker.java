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

package com.ibm.mobilefirst.clientsdk.android.push.api;

import android.annotation.SuppressLint;
import android.util.Log;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.FailResponse;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.MFPRequest;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResourceRequest;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;

import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Created by jialfred on 9/2/15.
 */

//TODO: jialfred - This class is tentative, subject to change.
public class IMFPushInvoker implements ResponseListener{

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String CODE = "code";
    private static final String DOCURL = "docUrl";
    private static final String MESSAGE = "message";
    private static final String SLASH = "/";

    private ResourceRequest requestBuilder = null;
    private IMFPushResponseListener<JSONObject> listener = null;
    private JSONObject requestBody = null;
    private IMFPushNotificationListener notificationListener = null;
    private ResponseListener rspListener = null;

    private IMFPushInvoker(String url, String method, int timeout) {
        requestBuilder = new ResourceRequest(url, method, timeout);
    }

    public static IMFPushInvoker newInstance(String url, String method, int timeout) {
        return new IMFPushInvoker(url, method, timeout);
    }

    public IMFPushInvoker setResponseListener(
            IMFPushResponseListener<JSONObject> listener) {
        this.listener = listener;
        return this;
    }

    public void setJSONRequestBody(JSONObject object){
        this.requestBody = object;
    }

    public void setCoreResponseListener(ResponseListener listener){
        this.rspListener = listener;
    }

    public void setPushNotificationListener(IMFPushNotificationListener listener){
        this.notificationListener = listener;
    }


    //TODO - jialfred - to be fixed after the server urls are resolved.
//    public IMFPushInvoker setMethod(String method) {
//        requestBuilder.
//        return this;
//    }

//    public IMFPushInvoker setResourcePath(String resourcePath) {
//        if (resourcePath.startsWith(SLASH)) {
//            resourcePath = resourcePath.substring(1);
//        }
//
//        if (resourcePath.endsWith(SLASH)) {
//            resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
//        }
//
//        StringBuilder uriBuilder = new StringBuilder(IBMBaaSConfig.getInstance().getBaaSUrl()).append(SLASH).append(resourcePath);
//        try {
//            requestBuilder.setUrl(new URL(uriBuilder.toString()));
//        } catch (MalformedURLException e) {
//            IMFPushUtils.error(e.getLocalizedMessage(), e);
//            throw new RuntimeException(e);
//        }
//        return this;
//    }

    public IMFPushInvoker addHeaders() {
        requestBuilder.addHeader(CONTENT_TYPE, APPLICATION_JSON);
        return this;
    }

    //TODO - jialfred, clean up required.
    public void execute() {
        Log.d("execute", "Sending request to push server.");
        if (requestBody != null && requestBody.length() != 0) {
            requestBuilder.send(requestBody, rspListener);
        } else {
            Log.d("MFPRequestBuilder is: ", requestBuilder.toString());
            Log.d("Request Method is: ", requestBuilder.getMethod());
            try {
                Log.d("Request URL is: ", requestBuilder.getUrl());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //requestBuilder.send(rspListener);
            requestBuilder.send(this);

        }

//        ResourceRequest request = new ResourceRequest(this, "http://imfpush.stage1-dev.ng.bluemix.net/imfpush/v1/apps/aa6c4d5c-1650-41cf-804c-6d73ecd087da/devices", MFPRequest.GET);
//        request.send(this);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onSuccess(Response response) {
        Log.d("Success Response in invoker is: ", response.toString());
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onFailure(FailResponse failResponse, Throwable throwable) {
        Log.d("Failure Response in invoker is: ", failResponse.toString());
    }
}

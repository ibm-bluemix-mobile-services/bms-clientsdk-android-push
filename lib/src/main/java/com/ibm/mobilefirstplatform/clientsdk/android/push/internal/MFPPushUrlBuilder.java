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

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;

public class MFPPushUrlBuilder {

	private static final String FORWARDSLASH = "/";
	private static final String IMFPUSH = "imfpush";
	private static final String V1 = "v1";
	private static final String APPS = "apps";
	private static final String AMPERSAND = "&";
	private static final String QUESTIONMARK = "?";
	private static final String EQUALTO = "=";
	private static final String SUBSCRIPTIONS = "subscriptions";
	private static final String TAGS = "tags";
	private static final String DEVICES = "devices";
	private static final String TAGNAME = "tagName";
	private static final String DEVICEID = "deviceId";
	private static final String SETTINGS = "settings" + FORWARDSLASH
			+ "gcmConfPublic";

    String reWriteDomain = "";
	private final StringBuilder pwUrl_ = new StringBuilder();

	public MFPPushUrlBuilder(String applicationId) {
		if (MFPPush.overrideServerHost != null){
			pwUrl_.append(MFPPush.overrideServerHost);
		} else {
			pwUrl_.append(BMSClient.getInstance().getDefaultProtocol());
			pwUrl_.append("://");
			pwUrl_.append(IMFPUSH);
            if ((BMSClient.getInstance().getBluemixRegionSuffix().contains("stage1-test") == true) ||
                (BMSClient.getInstance().getBluemixRegionSuffix().contains("stage1-dev") == true)){
                
                pwUrl_.append(".stage1.ng.bluemix.net");
                if (BMSClient.getInstance().getBluemixRegionSuffix().contains("stage1-test") == true) {
                    reWriteDomain = "stage1-test.ng.bluemix.net";
                }
                else {
                    reWriteDomain = "stage1-dev.ng.bluemix.net";
                }
                
            }else {
                pwUrl_.append(BMSClient.getInstance().getBluemixRegionSuffix());
                reWriteDomain = "";
            }
		}

		pwUrl_.append(FORWARDSLASH);
		pwUrl_.append(IMFPUSH);
		pwUrl_.append(FORWARDSLASH);
		pwUrl_.append(V1);
		pwUrl_.append(FORWARDSLASH);
		pwUrl_.append(APPS);
		pwUrl_.append(FORWARDSLASH);
		pwUrl_.append(applicationId);
		pwUrl_.append(FORWARDSLASH);

	}

	public String getDevicesUrl() {
		return getCollectionUrl(DEVICES).toString();
	}

	public String getTagsUrl() {
		return getCollectionUrl(TAGS).toString();
	}

	public String getSubscriptionsUrl() {
		return getCollectionUrl(SUBSCRIPTIONS).toString();
	}

	public String getSettingsUrl() {
		return getCollectionUrl(SETTINGS).toString();
	}

	public String getSubscriptionsUrl(String deviceId, String tagName) {
		StringBuilder suscriptionsOfTagUrl = new StringBuilder(
				getSubscriptionsUrl());
		suscriptionsOfTagUrl.append(QUESTIONMARK);
		boolean isFirstParam = true;
		if (deviceId != null) {
			suscriptionsOfTagUrl.append(DEVICEID).append(EQUALTO)
					.append(deviceId);
			isFirstParam = false;
		}

		if (tagName != null) {
			if (!isFirstParam) {
				suscriptionsOfTagUrl.append(AMPERSAND);
			}

			suscriptionsOfTagUrl.append(TAGNAME).append(EQUALTO)
					.append(tagName);
		}

		return suscriptionsOfTagUrl.toString();
	}

	public String getDeviceIdUrl(String deviceId) {
		StringBuilder deviceIdUrl = new StringBuilder(getDevicesUrl());
		deviceIdUrl.append(FORWARDSLASH).append(deviceId);

		return deviceIdUrl.toString();
	}

	public String getUnregisterUrl(String deviceId){
		StringBuilder deviceUnregisterUrl = new StringBuilder(
				getDevicesUrl());
		deviceUnregisterUrl.append(FORWARDSLASH);
		deviceUnregisterUrl.append(deviceId);

		return deviceUnregisterUrl.toString();
	}
    public String getRewriteDomain(){
        
        return reWriteDomain;
    }


	private StringBuilder getCollectionUrl(String collectionName) {
		StringBuilder collectionUrl = new StringBuilder(pwUrl_);
		collectionUrl.append(collectionName);

		return collectionUrl;
	}
}

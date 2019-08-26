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
	private static final String MESSAGES = "messages";
	private static final String TAGNAME = "tagName";
	private static final String DEVICEID = "deviceId";
	private static final String SETTINGS = "settings" + FORWARDSLASH
			+ "gcmConfPublic";
	private static final String STAGE_TEST = "stage1-test";
	private static final String STAGE_DEV = "stage1-dev";
	private static final String STAGE_TEST_URL = "stage1-test.ng.bluemix.net";
	private static final String STAGE_DEV_URL = "stage1-dev.ng.bluemix.net";
	private static final String STAGE_PROD_URL = ".stage1.ng.bluemix.net";
	public static final String DEVICE_ID_NULL = "nullDeviceId";

    String reWriteDomain = null;
	private final StringBuilder pwUrl_ = new StringBuilder();

	public MFPPushUrlBuilder() {
	}

	public MFPPushUrlBuilder(String applicationId) {
		if (MFPPush.overrideServerHost != null){
			pwUrl_.append(MFPPush.overrideServerHost);
			reWriteDomain = "";
		} else {
			pwUrl_.append(BMSClient.getInstance().getDefaultProtocol());
			pwUrl_.append("://");
			pwUrl_.append(IMFPUSH);
			String regionSuffix = BMSClient.getInstance().getBluemixRegionSuffix();
            if (regionSuffix != null && regionSuffix.contains(STAGE_TEST) || regionSuffix.contains(STAGE_DEV)){
                pwUrl_.append(STAGE_PROD_URL);
                if (regionSuffix.contains(STAGE_TEST)) {
                    reWriteDomain = STAGE_TEST_URL;
                }
                else {
                    reWriteDomain = STAGE_DEV_URL;
                }
            } else {
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

	public String getMessagesUrl() {
		return getCollectionUrl(MESSAGES).toString();
	}

	public String getSubscriptionsUrl(String deviceId, String tagName) {
		StringBuilder subscriptionsOfTagUrl = new StringBuilder(
				getSubscriptionsUrl());
		subscriptionsOfTagUrl.append(QUESTIONMARK);
		boolean isFirstParam = true;
		if (deviceId != null) {
			subscriptionsOfTagUrl.append(DEVICEID).append(EQUALTO)
					.append(deviceId);
			isFirstParam = false;
		} else {
			return DEVICE_ID_NULL;
		}

		if (tagName != null) {
			if (!isFirstParam) {
				subscriptionsOfTagUrl.append(AMPERSAND);
			}

			subscriptionsOfTagUrl.append(TAGNAME).append(EQUALTO)
					.append(tagName);
		}

		return subscriptionsOfTagUrl.toString();
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

	public String getMessageUrl(String messagesPath, String messageId) {
		StringBuilder messageIdUrl = new StringBuilder(messagesPath);
		messageIdUrl.append(FORWARDSLASH).append(messageId);

		return messageIdUrl.toString();
	}


	private StringBuilder getCollectionUrl(String collectionName) {
		StringBuilder collectionUrl = new StringBuilder(pwUrl_);
		collectionUrl.append(collectionName);

		return collectionUrl;
	}
}

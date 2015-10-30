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


package com.ibm.mobilefirstplatform.clientsdk.android.push.api;

import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPAbstractPushMessage;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPInternalPushMessage;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPPushMessage;

public class MFPSimplePushNotification extends MFPAbstractPushMessage implements
		MFPPushMessage {
	private String url;
	private String payload;

	MFPSimplePushNotification(String alert, String id,
							  String url, String payload) {
		super(alert, id);
		this.url = url;
		this.payload = payload;
	}

	MFPSimplePushNotification(MFPInternalPushMessage message) {
		super(message);
		this.url = message.getUrl();
		this.payload = message.getPayload();
	}

	/**
	 * Get the url property specified in the notification
	 *
	 * @return the url property
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Get the additional payload defined in the notification message
	 *
	 * @return the payload
	 */
	public String getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return "MFPSimplePushNotification :{url:" + url + ", payload:"
				+ payload + ", alert:" + alert + "}";
	}

}

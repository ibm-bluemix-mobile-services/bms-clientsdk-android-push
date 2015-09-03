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

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

//import com.ibm.mobile.services.push.IMFPushMessage;

/**
 * Created by jialfred on 9/2/15.

 * Represents message received from Push Server via GCM
 * Its visibility is restricted to package level since it will be used by the sdk internally
 * to store the push received before it can be passed to Jane.
 */
public class IMFInternalPushMessage implements Parcelable, IMFPushMessage {

	private static final String GCM_EXTRA_ID = "id";
	private static final String GCM_EXTRA_ALERT = "alert";
	private static final String GCM_EXTRA_PAYLOAD = "payload";
	private static final String GCM_EXTRA_URL = "url";
	private static final String GCM_EXTRA_MID = "mid";
	private static final String GCM_EXTRA_TYPE = "type";

	public static final String LOG_TAG = "PushMessage";

	private String id = null;
	private String url = null;
	private String alert = null;
	private String payload = null;
	private String mid = null;
	//private PushType type = null; //Uncomment for Rich

	private String htmlTitle = null;
	private String htmlContent = null;

	public IMFInternalPushMessage(Intent intent) {

		Bundle info = intent.getExtras();
		IMFPushUtils.dumpIntent(intent);
	}

	private IMFInternalPushMessage(Parcel source) {
		id = source.readString();
		alert = source.readString();
		url = source.readString();
		payload = source.readString();
		mid = source.readString();
	}

	public IMFInternalPushMessage(JSONObject json) {
		try {
			id = json.getString(GCM_EXTRA_ID);
		} catch (JSONException e) {
		}
		try {
			alert = json.getString(GCM_EXTRA_ALERT);
		} catch (JSONException e) {
		}
		try {
			url = json.getString(GCM_EXTRA_URL);
		} catch (JSONException e) {
		}
		try {
			payload = json.getString(GCM_EXTRA_PAYLOAD);
		} catch (JSONException e) {
		}
		try {
			mid = json.getString(GCM_EXTRA_MID);
		} catch (JSONException e) {
		}
	}

	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		try {
			json.put(GCM_EXTRA_ID, id);
			json.put(GCM_EXTRA_ALERT, alert);
			json.put(GCM_EXTRA_URL, url);
			json.put(GCM_EXTRA_PAYLOAD, payload);
			json.put(GCM_EXTRA_MID, mid);
		} catch (JSONException e) {
		}
		return json;
	}

	/* (non-Javadoc)
	 * @see com.ibm.mobile.services.push.IBMMessage#getAlert()
	 */
	@Override
	public	String getAlert() {
		return alert;
	}

	/**
	 * Returns the payload as string
	 *
	 * @return payload as string
	 */
	public String getPayload() {
		return payload;
	}

	/**
	 * Gets the URL that is part of the notification
	 *
	 * @return url as String
	 */
	public String getUrl() {
		return url;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getHtmlTitle() {
		return htmlTitle;
	}

	public void setHtmlTitle(String htmlTitle) {
		this.htmlTitle = htmlTitle;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	@Override
	public String toString() {
		return "IMFPushMessage [url=" + url + ", alert=" + alert + ", payload="
				+ payload + ", mid=" + mid + "]";
	}

	/* (non-Javadoc)
	 * @see com.ibm.mobile.services.push.IBMMessage#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.ibm.mobile.services.push.IBMMessage#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(alert);
		dest.writeString(url);
		dest.writeString(payload);
		dest.writeString(mid);
	}

	public static final Creator<IMFInternalPushMessage> CREATOR = new Creator<IMFInternalPushMessage>() {

		@Override
		public IMFInternalPushMessage[] newArray(int size) {
			return new IMFInternalPushMessage[size];
		}

		@Override
		public IMFInternalPushMessage createFromParcel(Parcel source) {
			return new IMFInternalPushMessage(source);
		}
	};

	/* (non-Javadoc)
	 * @see com.ibm.mobile.services.push.IBMMessage#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}

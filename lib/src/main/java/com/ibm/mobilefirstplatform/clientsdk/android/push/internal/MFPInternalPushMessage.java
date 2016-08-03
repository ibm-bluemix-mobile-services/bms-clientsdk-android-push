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

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Represents message received from Push Server via GCM
 * Its visibility is restricted to package level since it will be used by the sdk internally
 * to store the push received before it can be passed to Jane.
 */
public class MFPInternalPushMessage implements Parcelable, MFPPushMessage {

	private static final String GCM_EXTRA_ID = "nid";
	private static final String GCM_EXTRA_ALERT = "alert";
	private static final String GCM_EXTRA_PAYLOAD = "payload";
	private static final String GCM_EXTRA_URL = "url";
	private static final String GCM_EXTRA_MID = "mid";
	private static final String GCM_EXTRA_TYPE = "type";
	private static final String GCM_EXTRA_SOUND = "sound";
	private static final String GCM_EXTRA_BRIDGE = "bridge";
	private static final String GCM_EXTRA_VISIBILITY = "visibility";
	private static final String GCM_EXTRA_PRIORITY = "priority";
	private static final String GCM_EXTRA_REDACT = "redact";
	private static final String GCM_EXTRA_CATEGORY = "category";
	private static final String GCM_EXTRA_KEY = "key";
	private static final String PRIORITY_HIGH = "high";
	private static final String PRIORITY_LOW = "low";
	private static final String PRIORITY_MAX = "max";
	private static final String PRIORITY_MIN = "min";
	private static final String VISIBILITY_PUBLIC = "public";
	private static final String VISIBILITY_PRIVATE = "private";
	private static final String VISIBILITY_SECRET = "secret";

	public static final String LOG_TAG = "PushMessage";

	private String id = null;
	private String url = null;
	private String alert = null;
	private String payload = null;
	private String mid = null;
	private String sound = null;
	private boolean bridge = true;
	private int priority = 0;
	private int visibility = 1;
	private String redact = null;
	private String key = null;
	private String category = null;

	private String htmlTitle = null;
	private String htmlContent = null;

	protected static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + MFPInternalPushMessage.class.getSimpleName());

	public MFPInternalPushMessage(Intent intent) {

		Bundle info = intent.getExtras();
		MFPPushUtils.dumpIntent(intent);
		String tPriority = null;
		String tVisibility = null;
		alert = info.getString(GCM_EXTRA_ALERT);
		url = info.getString(GCM_EXTRA_URL);
		payload = info.getString(GCM_EXTRA_PAYLOAD);
		sound = info.getString(GCM_EXTRA_SOUND);
		bridge = info.getBoolean(GCM_EXTRA_BRIDGE);
		tPriority = info.getString(GCM_EXTRA_PRIORITY);
		if (tPriority != null && !(tPriority.isEmpty())) {
			priority = Integer.parseInt(tPriority);
		}
		tVisibility = info.getString(GCM_EXTRA_VISIBILITY);
		if (tVisibility != null && !(tVisibility.isEmpty())) {
			visibility = Integer.parseInt(tVisibility);
		}
		redact = info.getString(GCM_EXTRA_REDACT);
		key = info.getString(GCM_EXTRA_KEY);
		category = info.getString(GCM_EXTRA_CATEGORY);

		try {
			JSONObject towers = new JSONObject(payload);
			id = towers.getString(GCM_EXTRA_ID);
		}
		catch (JSONException e){
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get id.  "+ e.toString());
		}
	}

	private MFPInternalPushMessage(Parcel source) {
		String tPriority = null;
		String tVisibility = null;
		id = source.readString();
		alert = source.readString();
		url = source.readString();
		payload = source.readString();
		mid = source.readString();
		sound = source.readString();
		bridge = Boolean.valueOf(source.readString());
		tPriority = source.readString();
		if (tPriority != null && !(tPriority.isEmpty())) {
			if(tPriority.equalsIgnoreCase(PRIORITY_HIGH)) {
				priority = 1;
			} else if (tPriority.equalsIgnoreCase(PRIORITY_LOW)) {
				priority = -1;
			} else if (tPriority.equalsIgnoreCase(PRIORITY_MAX)) {
				priority = 2;
			} else if (tPriority.equalsIgnoreCase(PRIORITY_MIN)) {
				priority = -2;
			} else  {
				priority = 0;
			}
		}
		tVisibility = source.readString();
		if (tVisibility != null && !(tVisibility.isEmpty())) {
			if (tVisibility.equalsIgnoreCase(VISIBILITY_PUBLIC)) {
				visibility = 1;
			} else if (tVisibility.equalsIgnoreCase(VISIBILITY_PRIVATE)) {
				visibility = 0;
			} else if (tVisibility.equalsIgnoreCase(VISIBILITY_SECRET)) {
				visibility = -1;
			} else {
				visibility = 1;
			}
		}
		redact = source.readString();
		key = source.readString();
		category = source.readString();

	}

	public MFPInternalPushMessage(JSONObject json) {
		try {
			id = json.getString(GCM_EXTRA_ID);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get id.  "+ e.toString());
		}
		try {
			alert = json.getString(GCM_EXTRA_ALERT);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get alert.  "+ e.toString());
		}
		try {
			url = json.getString(GCM_EXTRA_URL);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get url .  "+ e.toString());
		}
		try {
			payload = json.getString(GCM_EXTRA_PAYLOAD);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get payload  "+ e.toString());
		}
		try {
			mid = json.getString(GCM_EXTRA_MID);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get mid.  "+ e.toString());
		}
		try {
			sound = json.getString(GCM_EXTRA_SOUND);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get sound.  "+ e.toString());
		}
		try {
			bridge = json.getBoolean(GCM_EXTRA_BRIDGE);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get bridge.  "+ e.toString());
		}
		try {
			priority = json.getInt(GCM_EXTRA_PRIORITY);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get priority.  "+ e.toString());
		}
		try {
			visibility = json.getInt(GCM_EXTRA_VISIBILITY);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get visibility.  "+ e.toString());
		}
		try {
			redact = json.getString(GCM_EXTRA_REDACT);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get redact.  "+ e.toString());
		}
		try {
			category = json.getString(GCM_EXTRA_CATEGORY);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get category.  "+ e.toString());
		}
		try {
			key = json.getString(GCM_EXTRA_KEY);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON, get key.  "+ e.toString());
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
			json.put(GCM_EXTRA_SOUND,sound);
			json.put(GCM_EXTRA_BRIDGE, bridge);
			json.put(GCM_EXTRA_PRIORITY, priority);
			json.put(GCM_EXTRA_VISIBILITY, visibility);
			json.put(GCM_EXTRA_REDACT, redact);
			json.put(GCM_EXTRA_CATEGORY, category);
			json.put(GCM_EXTRA_KEY, key);
		} catch (JSONException e) {
			logger.error("MFPInternalPushMessage: MFPInternalPushMessage() - Exception while parsing JSON.  "+ e.toString());
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

	public void setPriority(int priority) { this.priority = priority; }

	public void setVisibility (int visibility) { this.visibility = visibility; }

	public void setRedact(String redact) {this.redact = redact; }

	public void setCategory (String category) {this.category = category; }

	public void setKey (String key) { this.key = key; }

	@Override
	public String toString() {
		return "MFPPushMessage [url=" + url + ", alert=" + alert + ", payload="
				+ payload + ", mid=" + mid + ",sound="+ sound+ ",priority="+ priority + ",visibility="+ visibility + ",redact=" + redact + ",category="+category + ",key="+key +"]";
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
		dest.writeString(sound);
		dest.writeString(String.valueOf(bridge));
		dest.writeInt(priority);
		dest.writeInt(visibility);
		dest.writeString(redact);
		dest.writeString(category);
		dest.writeString(key);
	}

	public static final Creator<MFPInternalPushMessage> CREATOR = new Creator<MFPInternalPushMessage>() {

		@Override
		public MFPInternalPushMessage[] newArray(int size) {
			return new MFPInternalPushMessage[size];
		}

		@Override
		public MFPInternalPushMessage createFromParcel(Parcel source) {
			return new MFPInternalPushMessage(source);
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

	public String getSound() {
		return sound;
	}

	public boolean getBridge() { return bridge; }

	public int getPriority() { return priority; }

	public int getVisibility() { return visibility; }

	public String getRedact() { return redact; }

	public String getCategory() { return category; }

	public String getKey() { return key; }
}

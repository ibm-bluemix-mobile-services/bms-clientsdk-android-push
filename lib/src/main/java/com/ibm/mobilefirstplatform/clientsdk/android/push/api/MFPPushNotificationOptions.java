package com.ibm.mobilefirstplatform.clientsdk.android.push.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jialfred on 8/29/16.
 */
public class MFPPushNotificationOptions {

    private Visibility visibility;
    private String redact;
    private Priority priority;
    private String sound;
    private String icon;
    private static MFPPushNotificationOptions instance = null;
    private List<MFPPushNotificationCategory> categories = new ArrayList<MFPPushNotificationCategory>();
    private String deviceId;

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getRedact() {
        return redact;
    }

    public void setRedact(String redact) {
        this.redact = redact;
    }

    public Priority getPriority() { return priority; }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public MFPPushNotificationOptions() {}

    public static enum Priority {
        MAX(2), HIGH(1), DEFAULT(0), LOW(-1), MIN(-2);

        private final int value;

        Priority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static enum Visibility {
        PUBLIC(1), PRIVATE(0), SECRET(-1);

        private final int value;

        Visibility(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    public void  setInteractiveNotificationCategory(MFPPushNotificationCategory category) {
        categories.add(category);
    }

    public void setInteractiveNotificationCategories(List<MFPPushNotificationCategory> categories) {
        this.setInteractiveNotificationCategories(categories,"");
    }

    public void setInteractiveNotificationCategories(List<MFPPushNotificationCategory> categories, String withDeviceId) {
        this.categories = categories;
        this.deviceId = withDeviceId;
    }

    public List<MFPPushNotificationCategory> getInteractiveNotificationCategories() {
        return categories;
    }

    public String getapplicationsDeviceId() {
        return deviceId;
    }

    }

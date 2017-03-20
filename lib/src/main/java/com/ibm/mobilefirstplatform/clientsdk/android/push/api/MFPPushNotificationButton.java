package com.ibm.mobilefirstplatform.clientsdk.android.push.api;

public class MFPPushNotificationButton {
    private String buttonName;
    private String label;
    private String icon;

    public String getButtonName() {
        return buttonName;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }


    public static class Builder {
        private String buttonName;
        private String label;
        private String icon;
        private boolean performsInForeground;

        public Builder(String buttonName) {
            this.buttonName = buttonName;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder setPerformsInForeground(boolean performsInForeground) {
            this.performsInForeground = performsInForeground;
            return this;
        }

        public MFPPushNotificationButton build() {
            return new MFPPushNotificationButton(this);
        }

    }

    private MFPPushNotificationButton(Builder builder) {
        buttonName = builder.buttonName;
        label = builder.label;
        icon = builder.icon;
    }

}

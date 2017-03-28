package com.ibm.mobilefirstplatform.clientsdk.android.push.api;

import java.util.List;

/**
 * Created by myGirl on 20/03/17.
 */

public class MFPPushNotificationCategory {

    private String categoryName;
    private List<MFPPushNotificationButton> buttons;

    public List<MFPPushNotificationButton> getButtons() {
        return buttons;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public static class Builder {

        private String categoryName;
        private List<MFPPushNotificationButton> buttons;

        public Builder(String categoryName) {
            this.categoryName = categoryName;
        }

        public Builder setButtons(List<MFPPushNotificationButton> buttons) {
            this.buttons = buttons;
            return this;
        }

        public MFPPushNotificationCategory build() {
            return new MFPPushNotificationCategory(this);
        }

    }

    private MFPPushNotificationCategory(Builder builder) {
        categoryName = builder.categoryName;
        buttons = builder.buttons;
    }

}

package org.example;

import java.util.Locale;
import java.util.ResourceBundle;

public class UserInfo {
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("User", Locale.getDefault());

    public String getUserName() {
        return RESOURCE_BUNDLE.getString("username");
    }

    public String getPassword() {
        return RESOURCE_BUNDLE.getString("password");
    }
}

package com.example.taskmanagementsystem.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.taskmanagementsystem.model.User;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "bookstoresharedpref";

    private static final String KEY_ID = "keyid";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_TOKEN = "keytoken";
    private static final String KEY_ROLE = "keyrole";

    private final Context mCtx;

    public SharedPrefManager(Context context) {
        mCtx = context;
    }

    // Store user after successful login
    public void storeUser(User user) {
        SharedPreferences sp = mCtx.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_ROLE, user.getRole());
        editor.apply();
    }

    // Check login session
    public boolean isLoggedIn() {
        SharedPreferences sp = mCtx.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_TOKEN, null) != null;
    }

    // Get logged-in user
    public User getUser() {
        SharedPreferences sp = mCtx.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE);

        User user = new User();
        user.setId(sp.getInt(KEY_ID, -1));
        user.setUsername(sp.getString(KEY_USERNAME, null));
        user.setEmail(sp.getString(KEY_EMAIL, null));
        user.setToken(sp.getString(KEY_TOKEN, null));
        user.setRole(sp.getString(KEY_ROLE, null));

        return user;
    }

    // Logout user
    public void logout() {
        SharedPreferences sp = mCtx.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}

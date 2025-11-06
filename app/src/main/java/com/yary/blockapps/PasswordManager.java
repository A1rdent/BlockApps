package com.yary.blockapps;

import android.content.Context;
import android.content.SharedPreferences;

public class PasswordManager {
    
    private static final String PREFS_NAME = "block_apps_prefs";
    private static final String KEY_PASSWORD = "parent_password";
    private static final String DEFAULT_PASSWORD = "1234";
    
    public static void savePassword(Context context, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }
    
    public static String getPassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PASSWORD, DEFAULT_PASSWORD);
    }
    
    public static boolean isPasswordSet(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_PASSWORD);
    }
    
    public static boolean checkPassword(Context context, String inputPassword) {
        String savedPassword = getPassword(context);
        return savedPassword.equals(inputPassword);
    }
}
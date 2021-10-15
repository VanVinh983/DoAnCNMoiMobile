package com.example.chatappcongnghemoi.retrofit;

import android.content.Context;
import android.content.SharedPreferences;

public class DataLoggedIn {
//    public static String userIdLoggedIn = "615dab37be309ec0c5edcd48";
//   public static String userIdLoggedIn = "614ddf15fe79c83cac2a7423";
    private String userIdLoggedIn;
    private Context context;

    public DataLoggedIn(Context context) {
        this.context = context;
    }

    public String getUserIdLoggedIn() {
        String SHARE_PREFERENCES = "saveID";
        SharedPreferences sharedPreferences =  context.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId","");
    }


}

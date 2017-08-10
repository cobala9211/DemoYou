package com.songskids.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionUtil {

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }
    public static boolean isConnected(Context context) {

        if (context == null) {
            return false;
        }

        NetworkInfo info = ConnectionUtil.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

}
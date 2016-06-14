package com.kesa.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author hongil@
 */
public class NetworkManager {
    public static boolean isNetworkAvailable(Context context) {
        int[] networkTypes =
            new int[]{ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};

        try {
            ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo netInfo = connectivityManager.getNetworkInfo(networkType);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}

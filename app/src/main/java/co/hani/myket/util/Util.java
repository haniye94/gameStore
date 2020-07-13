package co.hani.myket.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public final class Util {
    private static Util util = new Util();
    private Context context = null;

    private Util() {
    }

    public static Util getInstance(Context context) {
        if (util.context == null) {
            util.context = context;
        }
        return util;
    }


    public boolean isNetWorkConnect(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting() && networkInfo.isAvailable();
    }


    public void openNetworkSetting(Context context) {
        Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

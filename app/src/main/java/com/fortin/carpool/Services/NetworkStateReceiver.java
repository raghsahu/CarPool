package com.fortin.carpool.Services;

/**
 * Created by reeva on 23/1/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by shaobin on 3/9/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        Log.d("Tala", "NetworkStateReceiver, intent: " + intent);
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            HeartbeatFixerUtils.scheduleHeartbeatRequest(context, true);
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            HeartbeatFixerUtils.scheduleHeartbeatRequest(context, false);
        }
    }
}
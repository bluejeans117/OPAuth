package com.oxygen.opauth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Broadcast Listened", "Service tried to stop");
        Log.d("Broadcast Listened", "Service started");

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) | Intent.CATEGORY_HOME.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, OPAuth.class);
            context.startService(serviceIntent);
            PackageManager pm = context.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage("com.oxygen.opauth");
            context.startActivity(launchIntent);
        }
    }
}

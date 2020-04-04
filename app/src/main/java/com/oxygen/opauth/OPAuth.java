package com.oxygen.opauth;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.spec.OAEPParameterSpec;

public class OPAuth extends Service {

    private static final String TAG = "OPAuthService";

    public int counter = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public void onCreate() {
        Log.d(TAG, "Service has been started");
        if (MainActivity.mUser != null) {
            stopSelf();
        } else {
            Intent intent = new Intent(OPAuth.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (MainActivity.mUser != null) {
            return;
        } else {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }
    }
}


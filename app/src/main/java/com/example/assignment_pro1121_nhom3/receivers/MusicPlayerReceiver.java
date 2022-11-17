package com.example.assignment_pro1121_nhom3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MusicPlayerReceiver extends BroadcastReceiver {

    public static final String TAG = MusicPlayerReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
    }
}

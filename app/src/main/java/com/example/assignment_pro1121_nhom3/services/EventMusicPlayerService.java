package com.example.assignment_pro1121_nhom3.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EventMusicPlayerService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            int action = intent.getIntExtra("action", -1);
            Intent receiverIntent = new Intent(context, MusicPlayerService.class);
            receiverIntent.putExtra("action", action);
            context.startService(receiverIntent);
        }
    }
}

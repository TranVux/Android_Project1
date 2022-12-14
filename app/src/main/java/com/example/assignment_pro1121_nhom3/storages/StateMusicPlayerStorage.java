package com.example.assignment_pro1121_nhom3.storages;

import android.content.Context;
import android.content.SharedPreferences;

public class StateMusicPlayerStorage {
    public static String STATE_MUSIC_PLAYER_STORAGE = "STATE_MUSIC_PLAYER_STORAGE";
    private static SharedPreferences stateMusicPlayerSharePreferences;

    public static SharedPreferences getInstance(Context context) {
        if (stateMusicPlayerSharePreferences == null) {
            return setUpSharedPreferences(context);
        }
        return stateMusicPlayerSharePreferences;
    }

    private static SharedPreferences setUpSharedPreferences(Context context) {
        return context.getSharedPreferences(STATE_MUSIC_PLAYER_STORAGE, Context.MODE_PRIVATE);
    }
}

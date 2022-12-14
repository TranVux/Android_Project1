package com.example.assignment_pro1121_nhom3.storages;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.assignment_pro1121_nhom3.utils.Constants;

public class MusicPlayerStorage {

    public static String MUSIC_PLAYER_STORAGE = "MUSIC_PLAYER_STORAGE";
    private static SharedPreferences musicPlayerSharePreferences;

    public static SharedPreferences getInstance(Context context) {
        if (musicPlayerSharePreferences == null) {
            return setUpSharedPreferences(context);
        }
        return musicPlayerSharePreferences;
    }

    private static SharedPreferences setUpSharedPreferences(Context context) {
        return context.getSharedPreferences(MUSIC_PLAYER_STORAGE, Context.MODE_PRIVATE);
    }

}

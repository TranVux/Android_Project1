package com.example.assignment_pro1121_nhom3.storages;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.assignment_pro1121_nhom3.models.Music;

@Database(entities = {Music.class}, version = 1)
public abstract class SongRecentDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "MUSIC_RECENT";
    private static SongRecentDatabase instance;

    public static synchronized SongRecentDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), SongRecentDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract MusicRecentDAO musicRecentDAO();
}

package com.example.assignment_pro1121_nhom3.storages;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.assignment_pro1121_nhom3.models.Music;

import java.util.List;

@Dao
public interface MusicRecentDAO {
    @Insert
    void insertSong(Music... music);

    @Query("SELECT * FROM recent_songs_tb GROUP BY ID LIMIT 50")
    List<Music> getListSongRecent();

    @Query("SELECT * FROM recent_songs_tb WHERE NAME LIKE :songName")
    List<Music> checkSong(String songName);
}

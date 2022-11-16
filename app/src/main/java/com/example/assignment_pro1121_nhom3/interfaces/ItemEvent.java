package com.example.assignment_pro1121_nhom3.interfaces;

import com.example.assignment_pro1121_nhom3.models.Music;
import com.example.assignment_pro1121_nhom3.models.Playlist;
import com.example.assignment_pro1121_nhom3.models.Singer;

public interface ItemEvent {
    interface MusicItemEvent {
        void onItemClick(Music music);

        void onSingerNameClick(String singerID);
    }

    interface PlaylistItemEvent {
        void onItemClick(Playlist playlist);
    }

    interface MusicItemInPlayListEvent {
        void onItemClick(int index);

        void onMoreClick(Music music);
    }

    interface SingerItemEvent {
        void onItemClick(Singer singer);
    }
}

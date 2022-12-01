package com.example.assignment_pro1121_nhom3.models;

import java.io.Serializable;

public class TopMusics implements Serializable {
    private Music music;
    private float rank;

    public TopMusics() {
    }

    public TopMusics(Music music, float rank) {
        this.music = music;
        this.rank = rank;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }
}

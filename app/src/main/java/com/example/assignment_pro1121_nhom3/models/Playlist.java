package com.example.assignment_pro1121_nhom3.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    private String id;
    private String name;
    private ArrayList<String> musics;
    private Long modifyDate;
    private Long creationDate;

    public Playlist() {
    }

    public Playlist(String id, String name, ArrayList<String> musics, Long modifyDate, Long creationDate) {
        this.id = id;
        this.name = name;
        this.musics = musics;
        this.modifyDate = modifyDate;
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<String> musics) {
        this.musics = musics;
    }

    public Long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }
}


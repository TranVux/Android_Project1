package com.example.assignment_pro1121_nhom3.views.model;

import java.io.Serializable;
import java.util.ArrayList;

public class playlist implements Serializable {
    String id;
    String name;
    ArrayList<String> musics;
    Long modyfiDate;
    Long createtionDate;

    public playlist() {
    }

    public playlist(String id, String name, ArrayList<String> musics, Long modyfiDate, Long createtionDate) {
        this.id = id;
        this.name = name;
        this.musics = musics;
        this.modyfiDate = modyfiDate;
        this.createtionDate = createtionDate;
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

    public Long getModyfiDate() {
        return modyfiDate;
    }

    public void setModyfiDate(Long modyfiDate) {
        this.modyfiDate = modyfiDate;
    }

    public Long getCreatetionDate() {
        return createtionDate;
    }

    public void setCreatetionDate(Long createtionDate) {
        this.createtionDate = createtionDate;
    }
}

package com.example.assignment_pro1121_nhom3.views.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    String id;
    String name;
    String email;
    String token;
    Long createtionDate;
    ArrayList<String> playlistsID;
    boolean isDelete;
    String bio;

    public User(String id, String name, String email, String token, Long createtionDate, ArrayList<String> playlistsID, String bio) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.token = token;
        this.createtionDate = createtionDate;
        this.playlistsID = playlistsID;
        this.bio = bio;
    }

    public User(String id, String name, String email, String token, Long createtionDate, ArrayList<String> playlistsID, boolean isDelete, String bio) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.token = token;
        this.createtionDate = createtionDate;
        this.playlistsID = playlistsID;
        this.isDelete = isDelete;
        this.bio = bio;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getCreatetionDate() {
        return createtionDate;
    }

    public void setCreatetionDate(Long createtionDate) {
        this.createtionDate = createtionDate;
    }

    public ArrayList<String> getPlaylistsID() {
        return playlistsID;
    }

    public void setPlaylistsID(ArrayList<String> playlistsID) {
        this.playlistsID = playlistsID;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}

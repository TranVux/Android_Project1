package com.example.assignment_pro1121_nhom3.views.models;

import java.io.Serializable;

public class Genre implements Serializable {
    String id;
    String name;
    Long modyfiDate;
    Long createtionDate;
    String urlThumbnail;

    public Genre() {
    }

    public Genre(String id, String name, Long modyfiDate, Long createtionDate, String urlThumbnail) {
        this.id = id;
        this.name = name;
        this.modyfiDate = modyfiDate;
        this.createtionDate = createtionDate;
        this.urlThumbnail = urlThumbnail;
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

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
    }
}

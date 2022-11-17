package com.example.assignment_pro1121_nhom3.models;

import java.io.Serializable;

public class Genres implements Serializable {
    private String id;
    private String name;
    private Long modifyDate;
    private Long creationDate;
    private String urlThumbnail;

    public Genres() {
    }

    public Genres(String id, String name, Long modifyDate, Long creationDate, String urlThumbnail) {
        this.id = id;
        this.name = name;
        this.modifyDate = modifyDate;
        this.creationDate = creationDate;
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

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
    }
}

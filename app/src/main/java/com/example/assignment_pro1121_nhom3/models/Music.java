package com.example.assignment_pro1121_nhom3.models;

public class Music {
    private String id;
    private String name;
    private String url;
    private String thumbnailUrl;
    private long creationDate;
    private long modifyDate;
    private String singerName;
    private String singerId;
    private long views;
    private String genresId;

    public Music(String id, String name, String url, String thumbnailUrl, long creationDate, long modifyDate, String singerName, String singerId, long views, String genresId) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.creationDate = creationDate;
        this.modifyDate = modifyDate;
        this.singerName = singerName;
        this.singerId = singerId;
        this.views = views;
        this.genresId = genresId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSingerId() {
        return singerId;
    }

    public void setSingerId(String singerId) {
        this.singerId = singerId;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public String getGenresId() {
        return genresId;
    }

    public void setGenresId(String genresId) {
        this.genresId = genresId;
    }
}

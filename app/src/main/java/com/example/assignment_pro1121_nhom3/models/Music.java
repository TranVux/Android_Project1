package com.example.assignment_pro1121_nhom3.models;

import java.io.Serializable;
import java.util.Objects;

public class Music implements Serializable {
    private String id;
    private String name;
    private String url;
    private String thumbnailUrl;
    private Long creationDate;
    private Long updateDate;
    private String singerName;
    private String singerId;
    private Long views;
    private String genresId;

    public Music() {
    }

    public Music(String id, String name, String url, String thumbnailUrl, long creationDate, long updateDate, String singerName, String singerId, long views, String genresId) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
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

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
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

    public Long getViews() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Music)) return false;
        Music music = (Music) o;
        return id.equals(music.id) && name.equals(music.name) && url.equals(music.url) && thumbnailUrl.equals(music.thumbnailUrl) && Objects.equals(creationDate, music.creationDate) && Objects.equals(updateDate, music.updateDate) && singerName.equals(music.singerName) && singerId.equals(music.singerId) && views.equals(music.views) && genresId.equals(music.genresId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, url, thumbnailUrl, creationDate, updateDate, singerName, singerId, views, genresId);
    }
}

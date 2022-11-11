package com.example.assignment_pro1121_nhom3.models;

import java.io.Serializable;

public class Singer implements Serializable {
    private String id;
    private String name;
    private String avtUrl;
    private String desc;

    public Singer() {
    }

    public Singer(String id, String name, String avtUrl, String desc) {
        this.id = id;
        this.name = name;
        this.avtUrl = avtUrl;
        this.desc = desc;
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

    public String getAvtUrl() {
        return avtUrl;
    }

    public void setAvtUrl(String avtUrl) {
        this.avtUrl = avtUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

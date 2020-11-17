package com.rapples.arafat.toolbox2.model;

import android.graphics.drawable.Icon;

public class Masterdata {
    private String title;
    private String subtitle;
    private int imgId;

    public Masterdata(String title, String subtitle, int imgId) {
        this.title = title;
        this.subtitle = subtitle;
        this.imgId = imgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getImage() {
        return imgId;
    }

    public void setImage(int imgId) {
        this.imgId = imgId;
    }
}

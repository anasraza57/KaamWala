package com.example.kaamwala;

public class ServiceModel {
    private String name;
    private String imageUrl;
    private int thumbnail;

    public ServiceModel() {
    }

    public ServiceModel(String name, String imageUrl) {
        if (name.trim().equals("")){
            name = "Service";
        }
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public ServiceModel(String name, String imageUrl, int thumbnail) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.thumbnail = thumbnail;
    }

    public ServiceModel(String name, int thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

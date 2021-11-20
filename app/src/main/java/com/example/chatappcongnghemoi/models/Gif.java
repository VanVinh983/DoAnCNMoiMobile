package com.example.chatappcongnghemoi.models;

public class Gif {
    private int id;
    private String url;
    private String type;

    public Gif(int id, String url, String type) {
        this.id = id;
        this.url = url;
        this.type = type;
    }

    public Gif() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Gif{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

package com.example.chatappcongnghemoi.models;

public class StartItem {
    private String name;
    private int img;

    public StartItem() {
    }

    public StartItem(String name, int img) {
        this.name = name;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "StartItem{" +
                "name='" + name + '\'' +
                ", img=" + img +
                '}';
    }
}

package com.example.chatappcongnghemoi.models;

public class Contact {
    private String _id;
    private String senderId;
    private String receiverId;
    private boolean status;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Contact(String _id, String senderId, String receiverId, boolean status) {
        this._id = _id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }
}

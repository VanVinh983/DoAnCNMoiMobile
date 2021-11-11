package com.example.chatappcongnghemoi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CallingDTO implements Serializable {
    @SerializedName("callerId")
    @Expose
    private String callerId;
    @SerializedName("receiverId")
    @Expose
    private String receiverId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private String status;

    public CallingDTO(String callerId, String receiverId, String type, String status) {
        this.callerId = callerId;
        this.receiverId = receiverId;
        this.type = type;
        this.status = status;
    }

    public CallingDTO() {
    }

    @Override
    public String toString() {
        return "CallingDTO{" +
                "callerId='" + callerId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

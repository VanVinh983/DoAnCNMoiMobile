package com.example.chatappcongnghemoi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contact {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("senderId")
    @Expose
    private String senderId;
    @SerializedName("receiverId")
    @Expose
    private String receiverId;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;
    @SerializedName("deletedAt")
    @Expose
    private Long deletedAt;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;
    @SerializedName("__v")
    @Expose
    private Integer v;

    /**
     * No args constructor for use in serialization
     *
     */
    public Contact() {
    }

    /**
     *
     * @param createdAt
     * @param senderId
     * @param deletedAt
     * @param receiverId
     * @param v
     * @param id
     * @param status
     * @param updatedAt
     */
    public Contact(String id, String senderId, String receiverId, Boolean status, Long updatedAt, Long deletedAt, Long createdAt, Integer v) {
        super();
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.v = v;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Long deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", status=" + status +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", createdAt=" + createdAt +
                ", v=" + v +
                '}';
    }
}
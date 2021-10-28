package com.example.chatappcongnghemoi.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ChatGroup implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("updatedAt")
    @Expose
    private String userId;
    @SerializedName("members")
    @Expose
    private ArrayList<String> members;
    @SerializedName("messageAmount")
    @Expose
    private Long messageAmount;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;

    @SerializedName("updatedAt")
    @Expose
    private Object updatedAt;

    @SerializedName("deletedAt")
    @Expose
    private Object deletedAt;

    @SerializedName("__v")
    @Expose
    private Integer v;

    public ChatGroup(String id, String name, String userId, ArrayList<String> members, Long messageAmount, Long createdAt, Object updatedAt, Object deletedAt, Integer v) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.members = members;
        this.messageAmount = messageAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.v = v;
    }

    public ChatGroup(String name, String userId, ArrayList<String> members, Long messageAmount) {
        this.name = name;
        this.userId = userId;
        this.members = members;
        this.messageAmount = messageAmount;
    }

    public ChatGroup() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public Long getMessageAmount() {
        return messageAmount;
    }

    public void setMessageAmount(Long messageAmount) {
        this.messageAmount = messageAmount;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public static Creator<ChatGroup> getCREATOR() {
        return CREATOR;
    }

    protected ChatGroup(Parcel in) {
    }

    @Override
    public String toString() {
        return "ChatGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", members=" + members +
                ", messageAmount=" + messageAmount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", v=" + v +
                '}';
    }

    public static final Creator<ChatGroup> CREATOR = new Creator<ChatGroup>() {
        @Override
        public ChatGroup createFromParcel(Parcel in) {
            return new ChatGroup(in);
        }

        @Override
        public ChatGroup[] newArray(int size) {
            return new ChatGroup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}

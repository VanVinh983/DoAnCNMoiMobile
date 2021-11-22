package com.example.chatappcongnghemoi.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatGroup implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("userAmount")
    @Expose
    private Long userAmount;
    @SerializedName("members")
    @Expose
    private ArrayList<Map<String,String>> members;

//    @SerializedName("members")
//    @Expose
//    private ArrayList<Map<String,String>> members;

    @SerializedName("messageAmount")
    @Expose
    private Long messageAmount;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("background")
    @Expose
    private String  background;
    @SerializedName("pin")
    @Expose
    private List<Map<String,String>> pins;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;

    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;

    @SerializedName("deletedAt")
    @Expose
    private Long deletedAt;

    @SerializedName("__v")
    @Expose
    private Integer v;

    public ChatGroup(String name, String userId, Long userAmount, ArrayList<Map<String,String>> members, Long messageAmount, Long createdAt, Long updatedAt, Long deletedAt, Integer v) {
        this.name = name;
        this.userId = userId;
        this.userAmount = userAmount;
        this.members = members;
        this.messageAmount = messageAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.v = v;
    }

    public ChatGroup(String id, String name, String userId, Long userAmount, ArrayList<Map<String, String>> members, Long messageAmount, String avatar, String background, Long createdAt, Long updatedAt, Long deletedAt, Integer v) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.userAmount = userAmount;
        this.members = members;
        this.messageAmount = messageAmount;
        this.avatar = avatar;
        this.background = background;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.v = v;
    }

    public ChatGroup(String name, String userId, Long userAmount) {
        this.name = name;
        this.userId = userId;
        this.userAmount = userAmount;
    }

    public ChatGroup() {
    }

    public List<Map<String, String>> getPins() {
        return pins;
    }

    public void setPins(List<Map<String, String>> pins) {
        this.pins = pins;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
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

    public ArrayList<Map<String,String>> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Map<String,String>> members) {
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

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public static Creator<ChatGroup> getCREATOR() {
        return CREATOR;
    }

    public Long getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(Long userAmount) {
        this.userAmount = userAmount;
    }

    protected ChatGroup(Parcel in) {
    }

    public ChatGroup(String name, String userId, Long userAmount, ArrayList<Map<String,String>> members) {
        this.name = name;
        this.userId = userId;
        this.userAmount = userAmount;
        this.members = members;
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

    @Override
    public String toString() {
        return "ChatGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", userAmount=" + userAmount +
                ", members=" + members +
                ", messageAmount=" + messageAmount +
                ", avatar='" + avatar + '\'' +
                ", background='" + background + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", v=" + v +
                '}';
    }
}

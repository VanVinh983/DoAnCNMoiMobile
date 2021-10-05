package com.example.chatappcongnghemoi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("local")
    @Expose
    private Local local;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("role")
    @Expose
    private String role;
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
    public User() {
    }

    /**
     *
     * @param birthday
     * @param createdAt
     * @param deletedAt
     * @param address
     * @param role
     * @param gender
     * @param v
     * @param id
     * @param avatar
     * @param userName
     * @param local
     * @param updatedAt
     */
    public User(Local local, String id, String userName, String gender, String birthday, String address, String avatar, String role, Long updatedAt, Long deletedAt, Long createdAt, Integer v) {
        super();
        this.local = local;
        this.id = id;
        this.userName = userName;
        this.gender = gender;
        this.birthday = birthday;
        this.address = address;
        this.avatar = avatar;
        this.role = role;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.v = v;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
        return "User{" +
                "local=" + local +
                ", id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", avatar='" + avatar + '\'' +
                ", role='" + role + '\'' +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", createdAt=" + createdAt +
                ", v=" + v +
                '}';
    }
}
package com.example.chatappcongnghemoi.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Parcelable {

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

    @SerializedName("isOnline")
    @Expose
    private boolean isOnline;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("background")
    @Expose
    private String background;


    /**
     * No args constructor for use in serialization
     */
    public User() {
    }

    public User(Local local, String id, String userName, String gender, String birthday, String address, String avatar, String role, Long updatedAt, Long deletedAt, Long createdAt, Integer v, boolean isOnline, String description, String background) {
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
        this.isOnline = isOnline;
        this.description = description;
        this.background = background;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User(Local local, String userName) {
        this.local = local;
        this.userName = userName;
    }

    protected User(Parcel in) {
        local = in.readParcelable(Local.class.getClassLoader());
        id = in.readString();
        userName = in.readString();
        gender = in.readString();
        birthday = in.readString();
        address = in.readString();
        avatar = in.readString();
        role = in.readString();
        description = in.readString();
        background = in.readString();
        if (in.readByte() == 0) {
            updatedAt = null;
        } else {
            updatedAt = in.readLong();
        }
        if (in.readByte() == 0) {
            deletedAt = null;
        } else {
            deletedAt = in.readLong();
        }
        if (in.readByte() == 0) {
            createdAt = null;
        } else {
            createdAt = in.readLong();
        }
        if (in.readByte() == 0) {
            v = null;
        } else {
            v = in.readInt();
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
        String preUrl = "https://stores3appchatmobile152130-dev.s3.ap-southeast-1.amazonaws.com/public/";
        return preUrl + avatar;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.local, flags);
        dest.writeString(this.id);
        dest.writeString(this.userName);
        dest.writeString(this.gender);
        dest.writeString(this.birthday);
        dest.writeString(this.address);
        dest.writeString(this.avatar);
        dest.writeString(this.role);
        if (this.updatedAt != null)
            dest.writeLong(this.updatedAt);
        if (this.deletedAt != null)
            dest.writeLong(this.deletedAt);
        if(this.createdAt != null)
        dest.writeLong(this.createdAt);
        if(this.v != null)
        dest.writeInt(this.v);
    }
}
package com.example.chatappcongnghemoi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserList {

    @SerializedName("users")
    @Expose
    private List<User> users = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public UserList() {
    }

    /**
     *
     * @param users
     */
    public UserList(List<User> users) {
        super();
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}

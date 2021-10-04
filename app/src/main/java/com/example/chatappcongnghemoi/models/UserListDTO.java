package com.example.chatappcongnghemoi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserListDTO {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private Integer result;
    @SerializedName("userList")
    @Expose
    private UserList userList;

    /**
     * No args constructor for use in serialization
     *
     */
    public UserListDTO() {
    }

    /**
     *
     * @param result
     * @param userList
     * @param status
     */
    public UserListDTO(String status, Integer result, UserList userList) {
        super();
        this.status = status;
        this.result = result;
        this.userList = userList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public UserList getData() {
        return userList;
    }

    public void setData(UserList userList) {
        this.userList = userList;
    }

}

package com.example.chatappcongnghemoi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatGroupDTO {
    @SerializedName("chat-group")
    @Expose
    private  ChatGroup chatGroup;

    public ChatGroupDTO(ChatGroup chatGroup) {
        super();
        this.chatGroup = chatGroup;
    }

    public ChatGroupDTO() {
    }

    public ChatGroup getChatGroup() {
        return chatGroup;
    }

    public void setChatGroup(ChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }
}

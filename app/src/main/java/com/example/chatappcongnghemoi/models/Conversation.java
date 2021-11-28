package com.example.chatappcongnghemoi.models;

public class Conversation {
    private User friend;
    private Message newMessage;
    private ChatGroup chatGroup;

    public Conversation() {
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public Message getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(Message newMessage) {
        this.newMessage = newMessage;
    }

    public ChatGroup getChatGroup() {
        return chatGroup;
    }

    public void setChatGroup(ChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "friend=" + friend +
                ", newMessage=" + newMessage +
                ", chatGroup=" + chatGroup +
                '}';
    }
}

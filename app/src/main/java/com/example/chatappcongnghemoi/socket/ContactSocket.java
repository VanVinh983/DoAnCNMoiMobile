package com.example.chatappcongnghemoi.socket;

import android.os.Handler;

import com.example.chatappcongnghemoi.models.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class ContactSocket {
    private static Socket socket = MySocket.getInstance().getSocket();

    public void addNewContact(User receiver) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()) {
                    try {
                        JSONObject jsonSender = new JSONObject();
                        jsonSender.put("receiverId", receiver.getId());
                        System.out.println("==> add-new-contact: " + jsonSender.toString());
                        socket.emit("add-new-contact", jsonSender);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    handler.removeCallbacks(this);
                } else {
                    System.out.println("==== Add New Contact on ContactSoket Failed ====");
                    socket.connect();
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

    }

    public void removeRequestContact(User receiver) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()) {
                    try {
                        JSONObject jsonSender = new JSONObject();
                        jsonSender.put("receiverId", receiver.getId());
                        System.out.println("==> remove-request-contact: " + jsonSender.toString());
                        socket.emit("remove-request-contact", jsonSender);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    handler.removeCallbacks(this);
                } else {
                    System.out.println("==== Add New Contact on ContactSoket Failed ====");
                    socket.connect();
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

    }

    public void deleteFriend(User receiver) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()) {
                    try {
                        JSONObject jsonSender = new JSONObject();
                        jsonSender.put("receiverId", receiver.getId());
                        socket.emit("delete-friend", jsonSender);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    handler.removeCallbacks(this);
                } else {
                    System.out.println("==== Add New Contact on ContactSoket Failed ====");
                    socket.connect();
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

    }

    public void removeRequestContactReceiver(User sender) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()) {
                    try {
                        JSONObject jsonSender = new JSONObject();
                        jsonSender.put("senderId", sender.getId());
                        System.out.println("==> remove-request-contact-receiver: " + jsonSender.toString());
//                        socket.emit("remove-request-contact-receiver", jsonSender);
                        socket.emit("deny-friend-request", jsonSender);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    handler.removeCallbacks(this);
                } else {
                    System.out.println("==== Add New Contact on ContactSoket Failed ====");
                    socket.connect();
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

    }


    public void acceptFriendRequest(User receiver) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()) {
                    try {
                        JSONObject jsonSender = new JSONObject();
                        jsonSender.put("senderId", receiver.getId());
                        System.out.println("==> accept-Friend-Request: " + jsonSender.toString());
                        socket.emit("accept-Friend-Request", jsonSender);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    handler.removeCallbacks(this);
                } else {
                    System.out.println("==== Add New Contact on ContactSoket Failed ====");
                    socket.connect();
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

    }


}

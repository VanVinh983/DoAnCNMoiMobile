package com.example.chatappcongnghemoi.socket;

import android.os.Handler;

import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;
import retrofit2.Call;

public class MessageSocket {
    private static Socket socket = MySocket.getInstance().getSocket();
    private List<ChatGroup> chatGroups;
    private User userCurrent;

    public MessageSocket(List<ChatGroup> chatGroups, User userCurrent) {
        this.chatGroups = chatGroups;
        this.userCurrent = userCurrent;
        String sender = new Gson().toJson(userCurrent);
        JSONObject senderJson = null;
        JSONArray jsonArray = new JSONArray(chatGroups);
        try {
            senderJson = new JSONObject(sender);
            senderJson.put("chatGroupIds", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        JSONObject finalSenderJson = senderJson;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()==true){
                    System.out.println("user json: "+ finalSenderJson);
                    socket.emit("send-user", finalSenderJson);
                }else {
                    socket.connect();
                    System.out.println("socket connect fail and again");
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }

    public void sendMessage(Message message,String isChatGroup){
        String mess = new Gson().toJson(message);
        JSONObject messJson = null;
        JSONObject jsonObjectGeneral = new JSONObject();

        try {
            messJson = new JSONObject(mess);
            jsonObjectGeneral.put("message", messJson);
            jsonObjectGeneral.put("isChatGroup", isChatGroup);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()==true){
                    System.out.println("json object general: "+ jsonObjectGeneral);
                    socket.emit("add-new-text", jsonObjectGeneral);
                    handler.removeCallbacks(this);
                }else {
                    socket.connect();
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }
    public void sendFile(List<Message> message,String isChatGroup){
        String mess = new Gson().toJson(message);
        JSONArray messJson = null;
        JSONObject jsonObjectGeneral = new JSONObject();

        try {
            messJson = new JSONArray(mess);
            jsonObjectGeneral.put("messages", messJson);
            jsonObjectGeneral.put("isChatGroup", isChatGroup);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()==true){
                    System.out.println("json object general: "+ jsonObjectGeneral);
                    socket.emit("add-new-file", jsonObjectGeneral);
                    handler.removeCallbacks(this);
                }else {
                    socket.connect();
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }
    public void deleteMessage(Message message){
        String mess = new Gson().toJson(message);
        JSONObject messJson = null;
        JSONObject jsonObjectGeneral = new JSONObject();

        try {
            messJson = new JSONObject(mess);
            jsonObjectGeneral.put("message", messJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()==true){
                    System.out.println("json object general: "+ jsonObjectGeneral);
                    socket.emit("delete-text", jsonObjectGeneral);
                    handler.removeCallbacks(this);
                }else {
                    socket.connect();
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }
    public void sendReaction(Message message){
        String mess = new Gson().toJson(message);
        JSONObject messJson = null;
        JSONObject jsonObjectGeneral = new JSONObject();

        try {
            messJson = new JSONObject(mess);
            jsonObjectGeneral.put("message", messJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()==true){
                    System.out.println("json object general: "+ jsonObjectGeneral);
                    socket.emit("reaction", jsonObjectGeneral);
                    handler.removeCallbacks(this);
                }else {
                    socket.connect();
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }
}

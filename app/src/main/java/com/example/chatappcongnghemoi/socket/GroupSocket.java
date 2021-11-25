package com.example.chatappcongnghemoi.socket;

import android.os.Handler;

import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

public class GroupSocket {
    private static Socket socket = MySocket.getInstance().getSocket();
    private List<ChatGroup> chatGroups;
    private User userCurrent;
    private Gson gson = new Gson();
    public GroupSocket(List<ChatGroup> chatGroups, User userCurrent) {
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
    public void createGroup(ChatGroup chatGroup){
        String jsonChatGroup = gson.toJson(chatGroup);
        JSONObject jsonObjectGeneral = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject(jsonChatGroup);
            jsonObjectGeneral.put("group",jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()==true){
                    System.out.println("json object general create-group: "+ jsonObjectGeneral);
                    socket.emit("create-group", jsonObjectGeneral);
                    handler.removeCallbacks(this);
                }else {
                    socket.connect();
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }
    public void leaveGroup(ChatGroup chatGroup){
        String jsonChatGroup = gson.toJson(chatGroup);
        JSONObject jsonObjectGeneral = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject(jsonChatGroup);
            jsonObjectGeneral.put("group",jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()==true){
                    System.out.println("json object general leave-group: "+ jsonObjectGeneral);
                    socket.emit("leave-group", jsonObjectGeneral);
                    handler.removeCallbacks(this);
                }else {
                    socket.connect();
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }
    public void addUserToGroup(ChatGroup chatGroup, List<User> members){
        String jsonGroup = gson.toJson(chatGroup);
        String jsonMembers = gson.toJson(members);
        JSONObject jsonObjectGroup = null;
        JSONArray jsonObjectMembers = null;
        JSONObject jsonObjectGeneral = new JSONObject();
        try {
            jsonObjectGroup = new JSONObject(jsonGroup);
            jsonObjectMembers = new JSONArray(jsonMembers);
            jsonObjectGeneral.put("group",jsonObjectGroup);
            jsonObjectGeneral.put("membersPre",jsonObjectMembers);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()==true){
                    System.out.println("json object general add-user-to-group: "+ jsonObjectGeneral);
                    socket.emit("add-user-to-group", jsonObjectGeneral);
                    handler.removeCallbacks(this);
                }else {
                    socket.connect();
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }
    public void deleteGroup(ChatGroup chatGroup){
        JSONObject jsonObjectGeneral = new JSONObject();
        try {
            jsonObjectGeneral.put("groupId",chatGroup.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()==true){
                    System.out.println("json object general delete-group: "+ jsonObjectGeneral);
                    socket.emit("delete-group", jsonObjectGeneral);
                    handler.removeCallbacks(this);
                }else {
                    socket.connect();
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }
}

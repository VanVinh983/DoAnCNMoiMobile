package com.example.chatappcongnghemoi.socket;

import android.os.Handler;

import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                    System.out.println("json object general: "+ jsonObjectGeneral);
                    socket.emit("create-group", jsonObjectGeneral);
                    handler.removeCallbacks(this);
                }else {
                    socket.connect();
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }
}

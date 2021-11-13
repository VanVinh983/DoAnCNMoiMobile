package com.example.chatappcongnghemoi.socket;

import android.os.Handler;

import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.gson.Gson;

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

    public MessageSocket(List<ChatGroup> chatGroups) {
        this.chatGroups = chatGroups;
    }

    public void sendMessage(User userCurrent, Message message){
        String sender = new Gson().toJson(userCurrent);
        String mess = new Gson().toJson(message);
        JSONObject senderJson = null;
        JSONObject messJson = null;
        JSONObject jsonObjectGeneral = new JSONObject();
        JSONArray jsonArray = new JSONArray(chatGroups);
        try {
            senderJson = new JSONObject(sender);
            senderJson.put("chatGroupIds", jsonArray);
            messJson = new JSONObject(mess);
            jsonObjectGeneral.put("message", messJson);
            jsonObjectGeneral.put("isChatGroup", "false");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Handler handler = new Handler();
        JSONObject finalSenderJson = senderJson;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()==true){
                    System.out.println("sender json: "+ finalSenderJson.toString());
                    socket.emit("send-user", finalSenderJson);
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
}

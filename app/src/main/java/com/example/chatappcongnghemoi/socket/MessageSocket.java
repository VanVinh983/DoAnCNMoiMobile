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

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageSocket {
    private static Socket socket = MySocket.getInstance().getSocket();
    private List<ChatGroup> chatGroups;
    private User userCurrent;
    private Gson gson = new Gson();
    public MessageSocket() {
    }

    public MessageSocket(User userCurrent, DataService dataService) {
        Call<List<ChatGroup>> chatGroupCall = dataService.getChatGroupByUserId(userCurrent.getId());
        chatGroupCall.enqueue(new Callback<List<ChatGroup>>() {
            @Override
            public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                List<String> groupIds = new ArrayList<>();
                response.body().forEach(group -> {
                    groupIds.add(group.getId());
                });
                String sender = new Gson().toJson(userCurrent);
                JSONObject senderJson = null;
                JSONArray jsonArray = new JSONArray(groupIds);
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

            @Override
            public void onFailure(Call<List<ChatGroup>> call, Throwable t) {

            }
        });
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
    public void removeReaction(Message message){
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
                    socket.emit("remove-reaction", jsonObjectGeneral);
                    handler.removeCallbacks(this);
                }else {
                    socket.connect();
                    handler.postDelayed(this, 500);
                }
            }
        },500);
    }
}

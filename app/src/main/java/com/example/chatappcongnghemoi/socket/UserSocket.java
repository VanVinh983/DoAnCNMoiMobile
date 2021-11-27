package com.example.chatappcongnghemoi.socket;

import android.content.SharedPreferences;
import android.os.Handler;

import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSocket {

    private static Socket socket = MySocket.getInstance().getSocket();
    private static DataService dataService = ApiService.getService();

    public void sendUserToSocket(String userId) {
        Call<UserDTO> callback = dataService.getUserById(userId);
        callback.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
               if(response.body().getUser() != null){
//                   System.out.println("==> Redirect Success");
                   redirectMessageSocket(response.body().getUser());
               }
//                System.out.println("==> Redirect Fail");
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
//                System.out.println("==> Redirect Fail");
            }
        });
    }

    public void redirectMessageSocket(User user){
        Call<List<ChatGroup>> listCall = dataService.getChatGroupByUserId(user.getId());
        listCall.enqueue(new Callback<List<ChatGroup>>() {
            @Override
            public void onResponse(Call<List<ChatGroup>> call, Response<List<ChatGroup>> response) {
                new MessageSocket(user,dataService);
            }

            @Override
            public void onFailure(Call<List<ChatGroup>> call, Throwable t) {
                System.err.println("fail get list group by user" + t.getMessage());
            }
        });
    }
}

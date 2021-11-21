package com.example.chatappcongnghemoi.socket;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatappcongnghemoi.activities.Home;
import com.example.chatappcongnghemoi.activities.IncomingCallActivity;
import com.example.chatappcongnghemoi.activities.OutgoingCallActivity;
import com.example.chatappcongnghemoi.activities.PhoneBookActivity;
import com.example.chatappcongnghemoi.activities.StartApp;
import com.example.chatappcongnghemoi.models.CallingDTO;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;
import com.google.gson.Gson;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListenSocket extends AppCompatActivity {

    private static Socket socket = MySocket.getInstance().getSocket();
    private static DataService dataService = ApiService.getService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testConnect();

        socket.on("receiver_room", onNewReceiverCall);

        // Only Run In start app
        startActivity(new Intent(ListenSocket.this, StartApp.class));

        new UserSocket().sendUserToSocket(getUserId());
    }


    private String getUserId() {
        final String SHARED_PREFERENCES = "saveID";
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

    /**
     * Đảm bảo socket luôn connect
     */
    public void testConnect() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!socket.connected()) {
                    socket.connect();
                    new UserSocket().sendUserToSocket(getUserId());
                }
                System.out.println("Listen socket still running: " + socket.connected());
                handler.postDelayed(this, 5000);
            }
        }, 1000);
    }

    public Emitter.Listener onNewReceiverCall = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message = data.optString("data");
                    CallingDTO callingDTO = new Gson().fromJson(message, CallingDTO.class);

                    /**
                     * Kiểm tra xem tài khoản cuộc gọi phải tài khoản đang đăng nhập không
                     */
                    if (callingDTO.getReceiverId().equals(getUserId()) && callingDTO.getStatus().equals("none")) {
                        Intent intent = new Intent(ListenSocket.this, IncomingCallActivity.class);
                        intent.putExtra("callingDTO", callingDTO);
                        startActivity(intent);
                    }

                    /**
                     * Kiếm tra xem người nhận chấp nhận cuộc gọi không
                     */
                    else if (callingDTO.getStatus().equals("accept") &&
                            (callingDTO.getCallerId().equals(getUserId()) || callingDTO.getReceiverId().equals(getUserId()))) {
                        if (callingDTO.getType().equals("video")) {
                            startVideoCall(callingDTO.getCallerId());
                        } else if (callingDTO.getType().equals("audio")) {
                            startAudioCall(callingDTO.getCallerId());
                        }

                    }

                    /**
                     * Kiếm tra xem người nhận chấp nhận cuộc gọi không
                     */
                    else if (callingDTO.getStatus().equals("cancel") &&
                            (callingDTO.getCallerId().equals(getUserId()) || callingDTO.getReceiverId().equals(getUserId()))) {
                        // Start Video Call When User is calling or receiver calling

                        // Get Current Activity is visible
                        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

                        if (cn.getClassName().equals(OutgoingCallActivity.class.getName()) || cn.getClassName().equals(IncomingCallActivity.class.getName())) {
                            startActivity(new Intent(ListenSocket.this, PhoneBookActivity.class));
                            Toast.makeText(ListenSocket.this, "Kết thúc cuộc gọi", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    };


    /**
     * @param room: callerId
     */
    private void startVideoCall(String room) {
        System.out.println("====> ON START VIDEO CALL: ");
        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setWelcomePageEnabled(false)
                    .setRoom(room)
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setAudioOnly(false)
                    .setWelcomePageEnabled(false)
                    .build();
            JitsiMeetActivity.launch(ListenSocket.this, options);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param room: callerId
     */
    private void startAudioCall(String room) {
        System.out.println("====> ON START AUDIO CALL: ");
        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setWelcomePageEnabled(false)
                    .setRoom(room)
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setAudioOnly(true)
                    .setWelcomePageEnabled(false)
                    .build();
            JitsiMeetActivity.launch(ListenSocket.this, options);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        socket.disconnect();
    }

}
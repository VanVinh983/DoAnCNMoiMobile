package com.example.chatappcongnghemoi.socket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatappcongnghemoi.activities.Home;
import com.example.chatappcongnghemoi.activities.IncomingCallActivity;
import com.example.chatappcongnghemoi.activities.StartApp;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ListenSocket extends AppCompatActivity {

    private static Socket socket = MySocket.getInstance().getSocket();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testConnect();

        socket.on("receiver_room", onNewReceiverCall);

        // Only Run In start app
        startActivity(new Intent(ListenSocket.this, StartApp.class));
    }

    private String getUserId() {
        final String SHARED_PREFERENCES = "saveID";
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

    public void testConnect() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!socket.connected())
                    socket.connect();
                System.out.println("Listen socket still running: " + socket.connected());
                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }

    public Emitter.Listener onNewReceiverCall = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message = data.optString("data");
                    String[] splits = message.split("/"); // Array [callerId] [receiverId] [type] [status]

//                    System.out.println("=====> MESSAGE: " + room);

                    // receiver call id equal user id
                    if (splits[1].equals(getUserId()) && splits.length < 4) {
                        Intent intent = new Intent(ListenSocket.this, IncomingCallActivity.class);
                        intent.putExtra("callerId", splits[0]);
                        intent.putExtra("message", message);
                        startActivity(intent);
                    }

                   if(splits.length > 4){
                       if(splits[3].equals("accept")){
                           // Start Video Call When User is calling or receiver calling
                           if(userId.equals(splits[0]) || userId.equals(splits[1])){
                               startVideoCall(splits[0]);
                           }
                       }
                   }
                }
            });
        }
    };

    private void startVideoCall(String room) {
        try {
            System.out.println("====> ON START VIDEO CALL: ");
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

    @Override
    protected void onStop() {
        super.onStop();
        socket.disconnect();
    }

}
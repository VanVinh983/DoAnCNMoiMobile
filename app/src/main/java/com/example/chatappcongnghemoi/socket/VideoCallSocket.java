package com.example.chatappcongnghemoi.socket;

import android.os.Handler;

import io.socket.client.Socket;

public class VideoCallSocket {
    private static Socket socket = MySocket.getInstance().getSocket();

    // message format callerId/receiverId/type/status
    public void sendStatusCallingSocket(String message) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket.connected()) {
                    socket.emit("call", message);
                    handler.removeCallbacks(this);
                } else {
                    System.out.println("==== OutgoingCall on VideoCallSocket Failed ====");
                    socket.connect();
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

    }
}

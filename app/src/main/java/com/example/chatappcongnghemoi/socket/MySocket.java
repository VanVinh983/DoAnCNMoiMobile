package com.example.chatappcongnghemoi.socket;

import android.os.Handler;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MySocket {
    private static MySocket instance = null;
    private static final String URL_SERVER = "http://192.168.1.23:4002";
    private Socket socket;
    public static MySocket getInstance() {
        if (instance == null) {
            instance = new MySocket();
        }

        return instance;
    }

    public Socket getSocket() {
        return socket;
    }

    public MySocket() {

        try {
            socket = IO.socket(URL_SERVER);
            if (!socket.connected()) {
                connect();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    // reconnect socket until connected
    private void connect() {
        socket.connect();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("tag", "Socket connected: " + socket.connected());
                if (socket.connected())
                    handler.removeCallbacks(this);
                else {
                    socket.connect();
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }
}

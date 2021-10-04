package com.example.chatappcongnghemoi.retrofit;

public class ApiService {
    private static final String  BASE_URL = "http://192.168.56.1:4000/";

    public static DataService getService(){
        return ApiClient.getClient(BASE_URL).create(DataService.class);
    }
}

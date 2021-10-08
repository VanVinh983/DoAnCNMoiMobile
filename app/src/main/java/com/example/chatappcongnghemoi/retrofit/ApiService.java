package com.example.chatappcongnghemoi.retrofit;

public class ApiService {
    private static final String  BASE_URL_LIEM = "http://192.168.56.1:5000/";
    private static final String  BASE_URL_VINH = "http://192.168.1.7:5000/";

    public static DataService getService(){
        return ApiClient.getClient(BASE_URL_LIEM).create(DataService.class);
    }
}

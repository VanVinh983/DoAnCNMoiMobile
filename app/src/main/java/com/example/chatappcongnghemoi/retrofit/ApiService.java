package com.example.chatappcongnghemoi.retrofit;

public class ApiService {
    private static final String  BASE_URL_LIEM = "http://192.168.1.10:4000/";
    private static final String  BASE_URL_VINH = "http://192.168.1.5:4000/";
    private static final String BASE_URL_THONG = "http://192.168.1.11:4000/";

    public static DataService getService(){
        return ApiClient.getClient(BASE_URL_LIEM).create(DataService.class);
    }
}

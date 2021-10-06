package com.example.chatappcongnghemoi.retrofit;

public class ApiService {
    private static final String  BASE_URL = "http://192.168.56.1:4000/";
    private static final String  BASE_URL_VINH = "http://192.168.15.100:4000/";

    public static DataService getService(){
        return ApiClient.getClient(BASE_URL_VINH).create(DataService.class);
    }
}

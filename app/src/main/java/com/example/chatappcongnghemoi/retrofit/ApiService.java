package com.example.chatappcongnghemoi.retrofit;

public class ApiService {
    private static final String  BASE_URL_LIEM = "http://192.168.1.11:4000/";
    private static final String  BASE_URL_VINH = "http://ec2-54-251-168-170.ap-southeast-1.compute.amazonaws.com:4000/";
    private static final String BASE_URL_THONG = "http://192.168.1.8:4000/";

    public static DataService getService(){
        return ApiClient.getClient(BASE_URL_VINH).create(DataService.class);
    }
}

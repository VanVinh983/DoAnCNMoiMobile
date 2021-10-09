package com.example.chatappcongnghemoi.retrofit;


import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactDTO;
import com.example.chatappcongnghemoi.models.ContactList;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DataService {

    @GET("contacts")
    Call<ContactList> getContactList();

    @GET("users/{id}")
    Call<UserDTO> getUserById(@Path("id") String id);

    @PUT("contacts/{id}")
    Call<PUT> updateContact(@Path("id") String id, @Body Contact contact);

    @POST("contacts")
    Call<POST> postContact(@Body Contact contact);

    @DELETE("contacts/{id}")
    Call<DELETE> deteleContactById(@Path("id") String id);

    @PUT("users/{id}")
    Call<UserDTO> updateUser(@Path("id") String id, @Body User user);

    @GET("users/searchPhone/{phone}")
    Call<UserDTO> getUserByPhone(@Path("phone") String phone);

    @GET("contacts/search/{userid}/{contactid}")
    Call<ContactDTO> checkContact(@Path("userid") String userid, @Path("contactid") String contactid);

    @POST("users/")
    Call<UserDTO> createUser(@Body User user);

}

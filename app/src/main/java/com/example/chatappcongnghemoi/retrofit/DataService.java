package com.example.chatappcongnghemoi.retrofit;


import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.ChatGroupDTO;
import com.example.chatappcongnghemoi.models.Contact;
import com.example.chatappcongnghemoi.models.ContactDTO;
import com.example.chatappcongnghemoi.models.ContactList;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DataService {

    @GET("contacts")
    Call<ContactList> getContactList();

    @GET("users/{id}")
    Call<UserDTO> getUserById(@Path("id") String id);

    @PUT("contacts/{id}")
    Call<String> updateContact(@Path("id") String id, @Body Contact contact);

    @POST("contacts")
    Call<ContactDTO> postContact(@Body Contact contact);

    @DELETE("contacts/{id}")
    Call<String> deteleContactById(@Path("id") String id);

    @PUT("users/{id}")
    Call<UserDTO> updateUser(@Path("id") String id, @Body User user);

    @GET("users/searchPhone/{phone}")
    Call<UserDTO> getUserByPhone(@Path("phone") String phone);

    @GET("contacts/search/{userid}/{contactid}")
    Call<ContactDTO> checkContact(@Path("userid") String userid, @Path("contactid") String contactid);

    @POST("users/")
    Call<UserDTO> createUser(@Body User user);

    @GET("messages/SearchBySenderIdAndReceiverId/{senderid}/{receiverid}")
    Call<List<Message>> getMessageBySIdAndRId(@Path("senderid") String senderid, @Path("receiverid") String receiverid);

    @POST("messages/")
    Call<Message> postMessage(@Body Message message);

    @GET("contacts/searchContact/{userid}")
    Call<List<Contact>> searchContactsByUserId(@Path("userid") String id);

    @POST("chatGroups")
    Call<ChatGroupDTO> createChatGroup(@Body ChatGroup chatGroup);
    @GET("chatGroups/searchUserIdToArray/{userId}")
    Call<List<ChatGroup>> getChatGroupByUserId(@Path("userId") String id);

    @GET("chatGroups/{chatgroupid}")
    Call<ChatGroup> getGroupById(@Path("chatgroupid") String id);

    @GET("messages/SearchByReceiverId/{id}")
    Call<List<Message>> getMessagesGroupByGroupId(@Path("id") String id);

    @PUT("chatGroups/{id}")
    Call<ChatGroup> updateGroup(@Path("id") String id,@Body ChatGroup chatGroup);

    @DELETE("chatGroups/{id}")
    Call<ChatGroup> deleteGroup(@Path("id") String id);

    @DELETE("messages/{id}")
    Call<Message> deleteMessage(@Path("id") String id);

    @GET("messages/SearchByReceiverId/{id}?")
    Call<List<Message>> getMessagePaging(@Path("id") String id, @Query("startFrom") int startFrom);

    @PUT("messages/{id}")
    Call<Message> updateMessage(@Path("id") String id, @Body Message message);
}

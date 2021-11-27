package com.example.chatappcongnghemoi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappcongnghemoi.R;
import com.example.chatappcongnghemoi.adapters.ChatBoxGroupRecyclerAdapter;
import com.example.chatappcongnghemoi.adapters.SearchMessageRecyclerAdapter;
import com.example.chatappcongnghemoi.models.ChatGroup;
import com.example.chatappcongnghemoi.models.Message;
import com.example.chatappcongnghemoi.models.User;
import com.example.chatappcongnghemoi.models.UserDTO;
import com.example.chatappcongnghemoi.retrofit.ApiService;
import com.example.chatappcongnghemoi.retrofit.DataService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMessage extends AppCompatActivity {
    ImageView btnBack,btnUp,btnDown,btnGoToBottom;
    EditText txtSearch;
    TextView tvSize;
    DataService dataService;
    RecyclerView recyclerView;
    String groupId;
    User userCurrent;
    ChatGroup chatGroup;
    List<Message> allMessages;
    SearchMessageRecyclerAdapter adapter;
    Spinner spinner;
    List<String> listItem = new ArrayList<>();
    boolean isSearchUser = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);
        getSupportActionBar().hide();
        mapping();
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        userCurrent = intent.getParcelableExtra("userCurrent");
        Call<ChatGroup> chatGroupCall =  dataService.getGroupById(groupId);
        chatGroupCall.enqueue(new Callback<ChatGroup>() {
            @Override
            public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                chatGroup = response.body();
                ArrayList<Map<String,String>> mapMembers = chatGroup.getMembers();
                List<String> listId = new ArrayList<>();
                List<User> membersSearch = new ArrayList<>();
                mapMembers.forEach(mem -> {
                    listId.add(mem.get("userId"));
                });
                listItem.add("Tìm theo thành viên");
                listId.forEach(id -> {
                    Call<UserDTO> userDTOCall = dataService.getUserById(id);
                    userDTOCall.enqueue(new Callback<UserDTO>() {
                        @Override
                        public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                            membersSearch.add(response.body().getUser());
                            listItem.add(response.body().getUser().getUserName());
                            if(listItem.size() == mapMembers.size()+1){
                                ArrayAdapter adapter = new ArrayAdapter(SearchMessage.this, android.R.layout.simple_spinner_item,listItem);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
                                spinner.setAutofillHints("***");
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        for(int i = 1 ; i< listItem.size();i++){
                                            if(position == i){
                                                getMessageByText("",membersSearch.get(position-1).getId());
                                                isSearchUser = true;
                                                txtSearch.setText("");
                                                txtSearch.setEnabled(false);
                                            }
                                        }
                                        if(position==0){
                                            isSearchUser = false;
                                            txtSearch.setEnabled(true);
//                                            getMessageByText("","");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDTO> call, Throwable t) {

                        }
                    });
                });
            }

            @Override
            public void onFailure(Call<ChatGroup> call, Throwable t) {

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(SearchMessage.this,ChatBoxGroup.class);
                intentBack.putExtra("groupId",groupId);
                startActivity(intentBack);
                finish();
            }
        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = txtSearch.getText().toString();
                if(isSearchUser == false) {
                    if (search.equals("")) {
                        btnDown.setVisibility(View.INVISIBLE);
                        btnUp.setVisibility(View.INVISIBLE);
                        getMessageByText("", "");
                    } else {
                        btnDown.setVisibility(View.INVISIBLE);
                        btnUp.setVisibility(View.INVISIBLE);
                        getMessageByText(search, "");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void mapping(){
        dataService = ApiService.getService();
        btnBack = findViewById(R.id.btnBackSearchMessage);
        btnUp = findViewById(R.id.btnUpSearchMessage);
        btnDown = findViewById(R.id.btnDownSearchMessage);
        recyclerView = findViewById(R.id.recyclerview_searchMessage);
        txtSearch = findViewById(R.id.txtSearchMessage);
        tvSize = findViewById(R.id.tvSizeSearch);
        btnDown.setVisibility(View.INVISIBLE);
        btnUp.setVisibility(View.INVISIBLE);
        spinner = findViewById(R.id.btnChooseUserSearchMessage);
    }
    public void getMessageByText(String search,String userId) {
        Call<ChatGroup> chatGroupCall = dataService.getGroupById(groupId);
        chatGroupCall.enqueue(new Callback<ChatGroup>() {
            @Override
            public void onResponse(Call<ChatGroup> call, Response<ChatGroup> response) {
                chatGroup = response.body();
                if(userId.equals("")) {
                    if (search.equals("")) {
                        ArrayList<Map<String, String>> mapMembers = chatGroup.getMembers();
                        List<String> listId = new ArrayList<>();
                        allMessages = new ArrayList<>();
                        mapMembers.forEach(map -> {
                            listId.add(map.get("userId"));
                        });
                        for (int i = 0; i < listId.size(); i++) {
                            Call<List<Message>> callMess = dataService.getAllMessages(listId.get(i), chatGroup.getId());
                            int finalI = i;
                            callMess.enqueue(new Callback<List<Message>>() {
                                @Override
                                public void onResponse(Call<List<Message>> callMess, Response<List<Message>> response) {
                                    List<Message> messages = response.body();
                                    messages.forEach(mess -> {
                                        if (mess.getMessageType().equals("file") || mess.getMessageType().equals("text"))
                                            allMessages.add(mess);
                                    });
                                    if (finalI >= listId.size() - 1) {
                                        Collections.sort(allMessages, new Comparator<Message>() {
                                            @Override
                                            public int compare(Message o1, Message o2) {
                                                return Integer.valueOf(o1.getCreatedAt().compareTo(o2.getCreatedAt()));
                                            }
                                        });
                                        adapter = new SearchMessageRecyclerAdapter(allMessages, userCurrent, chatGroup, SearchMessage.this, search,"text");
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchMessage.this, LinearLayoutManager.VERTICAL, false);
                                        linearLayoutManager.setStackFromEnd(true);
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.scrollToPosition(allMessages.size()-1);
                                        tvSize.setText("");
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Message>> callMess, Throwable t) {

                                }
                            });
                        }
                    } else {
                        ArrayList<Map<String, String>> mapMembers = chatGroup.getMembers();
                        List<String> listId = new ArrayList<>();
                        allMessages = new ArrayList<>();
                        mapMembers.forEach(map -> {
                            listId.add(map.get("userId"));
                        });
                        for (int i = 0; i < listId.size(); i++) {
                            Call<List<Message>> callMess = dataService.getAllMessages(listId.get(i), chatGroup.getId());
                            int finalI = i;
                            callMess.enqueue(new Callback<List<Message>>() {
                                @Override
                                public void onResponse(Call<List<Message>> callMess, Response<List<Message>> response) {
                                    List<Message> messages = response.body();
                                    messages.forEach(mess -> {
                                        if (mess.getMessageType().equals("file") || mess.getMessageType().equals("text")) {
                                            if (mess.getMessageType().equals("file")) {
                                                if (mess.getFileName().substring(37).contains(search)){
                                                    allMessages.add(mess);
                                                }
                                            } else {
                                                if (mess.getText().contains(search)) {
                                                    allMessages.add(mess);
                                                }
                                            }
                                        }
                                    });
                                    tvSize.setText("Có "+allMessages.size()+" kết quả");
                                    if (finalI >= listId.size() - 1) {
                                        Collections.sort(allMessages, new Comparator<Message>() {
                                            @Override
                                            public int compare(Message o1, Message o2) {
                                                return Integer.valueOf(o1.getCreatedAt().compareTo(o2.getCreatedAt()));
                                            }
                                        });
                                        adapter = new SearchMessageRecyclerAdapter(allMessages, userCurrent, chatGroup, SearchMessage.this, search,"text");
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchMessage.this, LinearLayoutManager.VERTICAL, false);
                                        linearLayoutManager.setStackFromEnd(true);
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        recyclerView.setAdapter(adapter);
//                                        btnDown.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                if (count == allMessages.size() - 1) {
//                                                    count = 0;
//                                                    recyclerView.scrollToPosition(count);
//                                                    tvSize.setText("Kết quả thứ " + (count + 1) + "/" + allMessages.size());
//                                                } else {
//                                                    count++;
//                                                    recyclerView.scrollToPosition(count);
//                                                    tvSize.setText("Kết quả thứ " + (count + 1) + "/" + allMessages.size());
//                                                }
//                                            }
//                                        });
//                                        btnUp.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                if (count == 0) {
//                                                    count = allMessages.size() - 1;
//                                                    recyclerView.scrollToPosition(count);
//                                                    tvSize.setText("Kết quả thứ " + (count + 1) + "/" + allMessages.size());
//                                                } else {
//                                                    count--;
//                                                    recyclerView.scrollToPosition(count);
//                                                    tvSize.setText("Kết quả thứ " + (count + 1) + "/" + allMessages.size());
//                                                }
//                                            }
//                                        });
//                                        int position = 0;
//                                    if (adapter.getItemCount()>0){
//                                        recyclerView.scrollToPosition(allMessages.size()-1);
//                                    }
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Message>> callMess, Throwable t) {

                                }
                            });
                        }
                    }
                }else{
                    Call<List<Message>> callMess = dataService.getAllMessages(userId, chatGroup.getId());
                    allMessages = new ArrayList<>();
                    callMess.enqueue(new Callback<List<Message>>() {
                        @Override
                        public void onResponse(Call<List<Message>> callMess, Response<List<Message>> response) {
//                            Toast.makeText(SearchMessage.this, ""+response.body().size(), Toast.LENGTH_SHORT).show();
                            List<Message> messages = response.body();
                            messages.forEach(mess -> {
                                if (!mess.getMessageType().equals("note")){
                                    allMessages.add(mess);
                                }
                            });
                            Collections.sort(allMessages, new Comparator<Message>() {
                                @Override
                                public int compare(Message o1, Message o2) {
                                    return Integer.valueOf(o1.getCreatedAt().compareTo(o2.getCreatedAt()));
                                }
                            });
                            adapter = new SearchMessageRecyclerAdapter(allMessages, userCurrent, chatGroup, SearchMessage.this, search,"user");
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchMessage.this, LinearLayoutManager.VERTICAL, false);
                            linearLayoutManager.setStackFromEnd(true);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                            if (adapter.getItemCount() > 0) {
                                recyclerView.scrollToPosition(allMessages.size() - 1);
                            }
                            tvSize.setText("");
                        }

                        @Override
                        public void onFailure(Call<List<Message>> callMess, Throwable t) {
                            }
                    });
                }
            }

            @Override
            public void onFailure(Call<ChatGroup> call, Throwable t) {

            }
        });
    }
}
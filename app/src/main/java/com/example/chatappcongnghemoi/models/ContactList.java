package com.example.chatappcongnghemoi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ContactList {

    @SerializedName("contacts")
    @Expose
    private ArrayList<Contact> contacts = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public ContactList() {
    }

    /**
     *
     * @param contacts
     */
    public ContactList(ArrayList<Contact> contacts) {
        super();
        this.contacts = contacts;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

}

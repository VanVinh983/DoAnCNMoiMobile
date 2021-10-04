package com.example.chatappcongnghemoi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactDTO {

    @SerializedName("contact")
    @Expose
    private Contact contact;

    /**
     * No args constructor for use in serialization
     *
     */
    public ContactDTO() {
    }

    /**
     *
     * @param contact
     */
    public ContactDTO(Contact contact) {
        super();
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

}

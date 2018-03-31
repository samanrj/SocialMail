package com.example.vanient.mycontacts.domain.entity;

import java.io.Serializable;

public class Contact implements Serializable{

    private String email;
    private String emailType;
    private String id;
    private String name;
    private String number;
    private String numberType;

    public Contact() {
    }

    public Contact(Contact contact) {
        this.name = contact.getName();
        this.number = contact.getNumber();
        this.numberType = contact.getNumberType();
        this.email = contact.getEmail();
        this.emailType = contact.getEmailType();
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }
}
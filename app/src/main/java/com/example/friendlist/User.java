package com.example.friendlist;

public class User {
    private String name;
    private String email;
    private String address;
    public boolean isFriend;

    public User(String name, String email, String address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }



}

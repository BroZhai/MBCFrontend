package com.example.friendlist;

public class User {
    private String name;
    private String email;
    private String uid;
    public boolean isFriend;

    public User(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    // 空的构造函数，准备让UserRequest继承后重写
    public User(String name, String email, String uid, String fid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}

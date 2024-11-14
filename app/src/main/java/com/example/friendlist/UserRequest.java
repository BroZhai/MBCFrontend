package com.example.friendlist;

public class UserRequest extends User{

    /*    原父类User的属性
    private String name;
    private String email;
    private String uid;
    public boolean isFriend;
    */

    // tips: uid是自己, fid是对方
    private String fid;

    private boolean pending=true;

    // 原User的构造函数
    public UserRequest(String name, String email, String uid, String fid) {
        super(name, email, uid, fid);
        this.fid = fid;
    }

    public void acceptRequest(){
        this.pending = false;
        this.isFriend = true;
    }

    public void rejectRequest(){
        this.pending = false;
        this.isFriend = false;
    }

    public boolean isPending(){
        return this.pending;
    }

}

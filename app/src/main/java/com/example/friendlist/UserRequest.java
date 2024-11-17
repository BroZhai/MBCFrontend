package com.example.friendlist;

public class UserRequest{

    /*    原父类User的属性
    private String name;
    private String email;
    private String uid;
    public boolean isFriend;
    */

    private String Femail;
    private String Fuid;
    private String Fname;
    public boolean isFriend;

    private boolean pending=true;

    public UserRequest(String fname, String femail, String fuid){
        this.Fname = fname;
        this.Femail = femail;
        this.Fuid = fuid;
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

    public String getFName() {
        return this.Fname;
    }

    public String getFEmail() {
        return this.Femail;
    }

    public String getFUid() {
        return this.Fuid;
    }

}

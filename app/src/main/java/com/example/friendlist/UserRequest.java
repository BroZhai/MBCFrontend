package com.example.friendlist;

public class UserRequest extends User{

    private boolean pending=true;

    public UserRequest(String name, String email, String address) {
        super(name, email, address);
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

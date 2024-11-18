package com.example.friendlist;

public class Message {
    private String senderUID;
    private String receiverUID;
    private String content;
    private boolean isFriendMsg = false;
    private String time; // 用于区分'不同的消息'

    public Message(String senderUID, String receiverUID, String content) {
        this.senderUID = senderUID;
        this.receiverUID = receiverUID;
        this.content = content;
   }

    public String getSender() {
        return senderUID;
    }

    public String getReceiver() {
        return receiverUID;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public boolean isFriendMsg() {
        return isFriendMsg;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFriendMsg(boolean friendMsg) {
        isFriendMsg = friendMsg;
    }
}

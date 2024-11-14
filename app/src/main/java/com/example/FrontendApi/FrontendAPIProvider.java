package com.example.FrontendApi;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class FrontendAPIProvider extends WebSocketClient {
    //Execute result
    public boolean success;
    public JSONObject latest_message;
    public JSONArray all_message;
    public JSONArray friend_list;

    //User info
    public String action;
    public String uid;
    public String fid;
    public String content;
    public String uname;
    public String email;
    public String cid; //目前对话的id

    // 修改了前端这里的API，我需要用到这个response json文件，就把他单独摘出来了
    public JSONObject response = null;

    // 构造函数，初始化 WebSocket 客户端
    public FrontendAPIProvider(URI serverURI) {
        super(serverURI);
        this.setConnectionLostTimeout(120);
    }
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("[→][Client] Connected to WebSocket server.");

    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[→×][Client] Disconnected from WebSocket server: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }
    // 接收并处理来自服务器的消息
    @Override
    public void onMessage(String message) {

        try {
            response = new JSONObject(message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        System.out.println("[←][Server] Received response: " + response);

        // 根据 action 字段判断响应类型
        String action = response.optString("action", "unknown");
        switch (action) {
            case "login":
                handleLoginResponse(response);
                break;

            case "getAllMessages":
                handleGetAllMessagesResponse(response);
                break;

            case "sendNewMessage":
                handleSendNewMessageResponse(response);
                break;

            case "getLatestMessage":
                handleGetLatestMessageResponse(response);
                break;

            case "getConversationID":
                handleGetConversationIDResponse(response);
                break;

            case "addNewFriend":
                handleAddNewFriendResponse(response);
                break;
            case "isFriendRequestAccept":
                handleIsFriendRequestAcceptResponse(response);
                break;

            case "deleteFriend":
                handleDeleteFriendResponse(response);
                break;

            case "changePassword":
                handleChangePasswordResponse(response);
                break;
            case "changeName":
                handleChangeNameResponse(response);
                break;

            case "getUserInfoByUID":
                handleGetUserInfoByUIDResponse(response);
                break;

            case "getUserInfoByEmail":
                handleGetUserInfoByEmailResponse(response);
                break;
            case "getUserFriendList":
                handleGetUserFriendListResponse(response);
                break;

            case "isUserOnline":
                handleIsUserOnlineResponse(response);
                break;

            case "isFriend":
                handleIsFriendResponse(response);
                break;

            case "register":
                handleRegisterResponse(response);
            case "startConversation":
                handleGetConversationID(response);
                break;



            //服务器推送给所有客户端
            case "serverPush":
                handleServerPush(response);
                break;
            default:
                System.out.println("[←][Server] Unhandled action: " + action);
                break;
        }
    }

    private void handleIsFriendResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        System.out.println("[←][Server & Client] Is friend result: " + success);

    }

    private void handleIsUserOnlineResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        System.out.println("[←][Server & Client] Is user online result: " + success);
    }

    private void handleGetUserFriendListResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        friend_list = response.optJSONArray("friendList");

        System.out.println("[←][Server & Client] Get user friend list result: " + success);

    }

    private void handleGetUserInfoByEmailResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        uname = response.optString("uname");
        email = response.optString("email");
        uid = response.optString("uid");
        System.out.println("[←][Server & Client] Get user info by email result: " + success);
    }

    private void handleGetUserInfoByUIDResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        System.out.println("Response: " + response.toString());
        action = response.optString("action");
        uname = response.optString("uname");
        email = response.optString("email");
        System.out.println("[←][Server & Client] Get user info by UID result: " + success);
    }

    private void handleChangeNameResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        System.out.println("[←][Server & Client] Change name result: " + success);
    }

    private void handleChangePasswordResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        System.out.println("[←][Server & Client] Change password result: " + success);
    }

    private void handleDeleteFriendResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        System.out.println("[←][Server & Client] Delete friend result: " + success);
    }

    private void handleIsFriendRequestAcceptResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        System.out.println("[←][Server & Client] Is friend request accept result: " + success);
    }

    private void handleGetConversationIDResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        cid = response.optString("cid");
        System.out.println("[←][Server & Client] Get conversation ID result: " + success);
    }

    private void handleGetLatestMessageResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        latest_message = response.optJSONObject("latestMessage");
        System.out.println("[←][Server & Client] Get latest message result: " + success);
    }

    private void handleSendNewMessageResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        System.out.println("[←][Server & Client] Send new message result: " + success);
    }

    private void handleGetAllMessagesResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        all_message = response.optJSONArray("uid");
        System.out.println("[←][Server & Client] Get all messages result: " + success);
    }

    private void handleAddNewFriendResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        System.out.println("[←][Server & Client] Add new friend result: " + success);
    }

    private void handleGetConversationID(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        cid = response.optString("cid");
        System.out.println("[←][Server & Client] Start conversation result: " + success);
    }



    // 处理登录响应
    private void handleLoginResponse(JSONObject response) {
        success = response.optBoolean("success", false);
        action = response.optString("action");
        uid = response.optString("uid", "unknown");
        System.out.println("[←][Server] Login Status: " + (success ? "successful" : "failed"));
        if (success) {
            System.out.println("[-][Client] Login successful. User ID: " + uid);
        } else {
            System.out.println("[-][Client] Login failed.");
        }
    }

    // 处理注册响应
    private void handleRegisterResponse(JSONObject response) {
        boolean success = response.optBoolean("success");
        System.out.println("[←][Server] Registration Status: " + (success ? "successful" : "failed"));
        if (success) {
            System.out.println("[-][Client] Registration successful.");
        } else {
            System.out.println("[-][Client] Registration failed.");
        }
    }

    //服务器推送，新的消息是否与自己有关
    public void handleServerPush(JSONObject response) {
        String client_action = response.optString("client_action", "client_action");
        System.out.println("[←][Server] Server push: " + client_action);

        //更新查询结果
        switch (client_action) {
            case "sendNewMessage":
                getLatestMessage();
                break;
            case "connectedNotify":
                break;
            case "addNewFriend":
                break;

            default:
                System.out.println("Unhandled action: " + client_action);
                break;
        }
        //To-do: 发起这个Provider的uid/fid是否跟上面两个一样
    }

    private void getLatestMessage() {


    }

    //---------------------------------以上是处理服务器回应的方法------------------------------
//---------------------------------以下是客户端发起的请求---------------------------------
// 发送注册请求示例方法
    public void sendRegisterRequest(String uname, String email, String password) throws JSONException {
        JSONObject registerRequest = new JSONObject();
        registerRequest.put("action", "register");
        registerRequest.put("uname", uname);
        registerRequest.put("email", email);
        registerRequest.put("password", password);

        send(registerRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent register request: " + registerRequest);
    }

    //    public void sendStartConversationRequest(String uid, String fid, String content) {
//        JSONObject ConversationRequest = new JSONObject();
//        ConversationRequest.put("action", "startConversation");
//        ConversationRequest.put("uid", uid);
//        ConversationRequest.put("fid", fid);
//        ConversationRequest.put("content", content);
//
//        send(ConversationRequest.toString());  // 发送 JSON 请求
//        System.out.println("[→][Client] Sent register request: " + ConversationRequest);
//    }
    public void register(String uname, String email, String password) throws JSONException {
        JSONObject registerRequest = new JSONObject();
        registerRequest.put("action", "register");
        registerRequest.put("uname", uname);
        registerRequest.put("email", email);
        registerRequest.put("password", password);

        send(registerRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent register request: " + registerRequest);
    }

    public void getAllMessage(String uid, String fid) throws JSONException {
        JSONObject getAllMessageRequest = new JSONObject();
        getAllMessageRequest.put("action", "getAllMessage");
        getAllMessageRequest.put("uid", uid);
        getAllMessageRequest.put("fid", fid);

        send(getAllMessageRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent get all message request: " + getAllMessageRequest);
    }

    public void sendNewMessage(String uid, String fid, String content) throws JSONException {
        JSONObject sendNewMessageRequest = new JSONObject();
        sendNewMessageRequest.put("action", "sendNewMessage");
        sendNewMessageRequest.put("uid", uid);
        sendNewMessageRequest.put("fid", fid);
        sendNewMessageRequest.put("content", content);

        send(sendNewMessageRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent send new message request: " + sendNewMessageRequest);
    }

    public void getLatestMessage(String uid, String fid) throws JSONException {
        JSONObject getLatestMessageRequest = new JSONObject();
        getLatestMessageRequest.put("action", "getLatestMessage");
        getLatestMessageRequest.put("uid", uid);
        getLatestMessageRequest.put("fid", fid);

        send(getLatestMessageRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent get latest message request: " + getLatestMessageRequest);
    }

    public void getConversationID(String uid, String fid) throws JSONException {
        JSONObject getConversationIDRequest = new JSONObject();
        getConversationIDRequest.put("action", "getConversationID");
        getConversationIDRequest.put("uid", uid);
        getConversationIDRequest.put("fid", fid);

        send(getConversationIDRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent get conversation ID request: " + getConversationIDRequest);
    }


    public void addNewFriend(String uid, String email) throws JSONException {
        JSONObject addNewFriendRequest = new JSONObject();
        addNewFriendRequest.put("action", "addNewFriend");
        addNewFriendRequest.put("uid", uid);
        addNewFriendRequest.put("email", email);

        send(addNewFriendRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent add new friend request: " + addNewFriendRequest);
    }

    public void isFriendRequestAccept(String uid, String fid,String status) throws JSONException {
        JSONObject isFriendRequestAcceptRequest = new JSONObject();
        isFriendRequestAcceptRequest.put("action", "isFriendRequestAccept");
        isFriendRequestAcceptRequest.put("uid", uid);
        isFriendRequestAcceptRequest.put("fid", fid);
        isFriendRequestAcceptRequest.put("status", status);

        send(isFriendRequestAcceptRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent is friend request accept request: " + isFriendRequestAcceptRequest);
    }

    public void deleteFriend(String uid, String fid) throws JSONException {
        JSONObject deleteFriendRequest = new JSONObject();
        deleteFriendRequest.put("action", "deleteFriend");
        deleteFriendRequest.put("uid", uid);
        deleteFriendRequest.put("fid", fid);

        send(deleteFriendRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent delete friend request: " + deleteFriendRequest);
    }

    public void changePassword(String uid, String password) throws JSONException {
        JSONObject changePasswordRequest = new JSONObject();
        changePasswordRequest.put("action", "changePassword");
        changePasswordRequest.put("uid", uid);
        changePasswordRequest.put("password", password);

        send(changePasswordRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent change password request: " + changePasswordRequest);
    }

    public void changeName(String uid, String uname) throws JSONException {
        JSONObject changeNameRequest = new JSONObject();
        changeNameRequest.put("action", "changeName");
        changeNameRequest.put("uid", uid);
        changeNameRequest.put("uname", uname);

        send(changeNameRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent change name request: " + changeNameRequest);
    }

    public void getUserInfoByUID(String uid) throws JSONException {
        JSONObject getUserInfoByUIDRequest = new JSONObject();
        getUserInfoByUIDRequest.put("action", "getUserInfoByUID");
        getUserInfoByUIDRequest.put("uid", uid);

        send(getUserInfoByUIDRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent get user info by UID request: " + getUserInfoByUIDRequest);
    }

    public void getUserInfoByEmail(String email) throws JSONException {
        JSONObject getUserInfoByEmailRequest = new JSONObject();
        getUserInfoByEmailRequest.put("action", "getUserInfoByEmail");
        getUserInfoByEmailRequest.put("email", email);

        send(getUserInfoByEmailRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent get user info by email request: " + getUserInfoByEmailRequest);
    }

    public void getUserFriendList(String uid) throws JSONException {
        JSONObject getUserFriendListRequest = new JSONObject();
        getUserFriendListRequest.put("action", "getUserFriendList");
        getUserFriendListRequest.put("uid", uid);

        send(getUserFriendListRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent get user friend list request: " + getUserFriendListRequest);
    }

    public void isUserOnline(String uid) throws JSONException {
        JSONObject isUserOnlineRequest = new JSONObject();
        isUserOnlineRequest.put("action", "isUserOnline");
        isUserOnlineRequest.put("uid", uid);

        send(isUserOnlineRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent is user online request: " + isUserOnlineRequest);
    }

    public void isFriend(String uid, String fid) throws JSONException {
        JSONObject isFriendRequest = new JSONObject();
        isFriendRequest.put("action", "isFriend");
        isFriendRequest.put("uid", uid);
        isFriendRequest.put("fid", fid);

        send(isFriendRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent is friend request: " + isFriendRequest);
    }

    public void login(String email, String password) throws JSONException {
        JSONObject loginRequest = new JSONObject();
        loginRequest.put("action", "login");
        loginRequest.put("email", email);
        loginRequest.put("password", password);

        send(loginRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent login request: " + loginRequest);
    }

    //测试发起会话请求
    public static void main(String[] args) {
        try {
            // 创建 WebSocket 客户端并连接到 WebSocket 服务器
            URI serverURI = new URI("ws://localhost:8080/backend-api");
            FrontendAPIProvider client = new FrontendAPIProvider(serverURI);//onOpen会被执行
            client.connectBlocking();  // 阻塞，直到连接建立

            // 示例：发送注册请求
            client.sendRegisterRequest("rokidna2", "rokidna2@gnetwork.com", "rokiroki");
            Thread.sleep(1000);
////            client.sendStartConversationRequest("d96f962d-f8c0-4c8f-b986-b87e9c877462", "1ac162a4-5a24-4058-a3de-5eb0d639a3fb", "hello");
//            client.sendNewMessage("d96f962d-f8c0-4c8f-b986-b87e9c877462", "1ac162a4-5a24-4058-a3de-5eb0d639a3fb", "add");

        } catch (URISyntaxException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
}

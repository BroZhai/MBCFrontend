package com.example.FrontendApi;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class FrontendAPIProvider extends WebSocketClient {
    private String cid; //目前对话的id
    // 构造函数，初始化 WebSocket 客户端
    public FrontendAPIProvider(URI serverURI) {
        super(serverURI);
    }
    public boolean isSuccessful = false;
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server.");

//        // 示例：发送登录请求
//        JSONObject loginRequest = new JSONObject();
//        loginRequest.put("action", "login");
//        loginRequest.put("email", "tester@gmail.com");
//        loginRequest.put("password", "123456");
//
//        send(loginRequest.toString());  // 发送 JSON 请求
//        System.out.println("Sent login request: " + loginRequest);
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }
    // 接收并处理来自服务器的消息
    @Override
    public void onMessage(String message) {
        JSONObject response = null;
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
                try {
                    handleLoginResponse(response);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "register":
                handleRegisterResponse(response);
                break;
            case "startConversation":
                handleStartConversationResponse(response);
                break;
            case "sendNewMessage":
                handleSendNewMessage(response);
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

    private void handleSendNewMessage(JSONObject response) {
        boolean isSuccessful = response.optBoolean("isSuccessful", false);
        System.out.println("[←][Server] Send new message result: " + isSuccessful);
    }

    private void handleStartConversationResponse(JSONObject response) {
        boolean isSuccessful = response.optBoolean("isSuccessful", false);
        System.out.println("[-][Client] Start conversation result: " + isSuccessful);
    }



    // 处理登录响应
    private void handleLoginResponse(JSONObject response) throws JSONException {
        boolean isSuccessful = response.optBoolean("isLogonSucessful", false);
        if (isSuccessful) {
            System.out.println("[←][Server] Login successful. User ID: " + response.getString("uid"));
        } else {
            System.out.println("[←][Server] Login failed.");
        }
    }

    // 处理注册响应
    public void handleRegisterResponse(JSONObject response) {
        isSuccessful = response.optBoolean("success", false);

    }

    //服务器推送，新的消息是否与自己有关
    public void handleServerPush(JSONObject response) {
        String client_action = response.optString("client_action", "client_action");
        System.out.println("[←][Server] Server push: " + client_action);
        //更新查询结果
        switch (client_action) {
            case "sendNewMessage":
                //To-do: 从服务器更新信息列表
                break;
            default:
                System.out.println("[←][Server] Unhandled action: " + client_action);
                break;
        }
        //To-do: 发起这个Provider的uid/fid是否跟上面两个一样
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

    public void sendStartConversationRequest(String uid, String fid, String content) throws JSONException {
        JSONObject ConversationRequest = new JSONObject();
        ConversationRequest.put("action", "startConversation");
        ConversationRequest.put("uid", uid);
        ConversationRequest.put("fid", fid);
        ConversationRequest.put("content", content);

        send(ConversationRequest.toString());  // 发送 JSON 请求
        System.out.println("[→][Client] Sent register request: " + ConversationRequest);
    }

    public void sendNewMessage(String uid,String fid,String content) throws JSONException {
        JSONObject newMessageRequest = new JSONObject();
        newMessageRequest.put("action", "sendNewMessage");
        newMessageRequest.put("uid", uid);
        newMessageRequest.put("fid", fid);
        newMessageRequest.put("content", content);

        send(newMessageRequest.toString());
        System.out.println("[→][Client] Sent new message request: " + newMessageRequest);
    }

    //测试发起会话请求
    public static void main(String[] args) {
        try {
            // 创建 WebSocket 客户端并连接到 WebSocket 服务器
            URI serverURI = new URI("ws://localhost:8080/backend-api");
            FrontendAPIProvider client = new FrontendAPIProvider(serverURI);//onOpen会被执行
            client.connectBlocking();  // 阻塞，直到连接建立

            // 示例：发送注册请求
            client.sendRegisterRequest("ABC", "ABC@exaample.com", "ABCABC");
            Thread.sleep(1000);
////            client.sendStartConversationRequest("d96f962d-f8c0-4c8f-b986-b87e9c877462", "1ac162a4-5a24-4058-a3de-5eb0d639a3fb", "hello");
//            client.sendNewMessage("d96f962d-f8c0-4c8f-b986-b87e9c877462", "1ac162a4-5a24-4058-a3de-5eb0d639a3fb", "add");

        } catch (URISyntaxException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
}

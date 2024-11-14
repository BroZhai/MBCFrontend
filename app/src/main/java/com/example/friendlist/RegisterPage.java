package com.example.friendlist;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.FrontendApi.FrontendAPIProvider;

import java.net.URI;
import java.net.URISyntaxException;

public class RegisterPage extends AppCompatActivity {

    EditText regUsername;
    EditText regEmail;
    EditText regPassword;
    FrontendAPIProvider websocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.re_rgister_page);

        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
    }


    public void initWebSocket() {
        try {
            URI uri = new URI("ws://10.0.2.2:8080/backend-api");
            websocket = new FrontendAPIProvider(uri);
            websocket.connect();  // 异步连接
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public void sendRegisterRequest() {

        // Send register request to backend
        initWebSocket();

    }
}
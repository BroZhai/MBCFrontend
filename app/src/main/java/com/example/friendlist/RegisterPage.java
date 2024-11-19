package com.example.friendlist;

import static java.lang.Thread.sleep;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.FrontendApi.FrontendAPIProvider;

import org.json.JSONException;

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
        initWebSocket();
    }


    public void initWebSocket() {
        try {
            URI uri = new URI("ws://www.gnetwork.space:8085/backend-api");
            websocket = new FrontendAPIProvider(uri);
            websocket.connect();  // 异步连接
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void goBack(View view) {
        finish();
    }


    public void sendRegisterRequest(View view) throws JSONException, InterruptedException {

        // Send register request to backend
        String username = regUsername.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterPage.this, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("RegisterPage", "用户正在注册，用户名: " + username + " 邮箱: " + email + " 密码: " + password);
            websocket.register(username, email, password);
            sleep(600);
            if (websocket.success) {
                Toast.makeText(RegisterPage.this, "Register Success! " + username, Toast.LENGTH_SHORT).show();
                Log.d("RegisterSuccess", "用户注册成功，用户名: " + username + " 邮箱: " + email + " 密码: " + password);
                regUsername.setText("");
                regEmail.setText("");
                regPassword.setText("");
                finish();
            } else {
                Toast.makeText(RegisterPage.this, "Register Failed! Email maybe duplicated!", Toast.LENGTH_SHORT).show();
                Log.e("RegisterFailure", "用户注册失败");
            }
        }
    }
}
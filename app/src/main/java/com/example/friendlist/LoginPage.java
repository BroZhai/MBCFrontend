package com.example.friendlist;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.content.SharedPreferences;
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
import org.w3c.dom.Text;

import java.net.URI;
import java.net.URISyntaxException;

public class LoginPage extends AppCompatActivity {

     EditText inputEmail;
     EditText inputPassword;
     FrontendAPIProvider websocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.re_welcome_page);

        inputEmail = findViewById(R.id.emailInput);
        inputPassword = findViewById(R.id.passwordInput);

        // 尝试通过读取SharedPreferences实现'自动登录'
        SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
        if(sp!=null && sp.getBoolean("loginStatus", true)) {
            String email = sp.getString("email", "null");
            String password = sp.getString("password", "null");
            Log.d("AutoLogin", "\""+email+"\"自动登录中...");
            Toast.makeText(LoginPage.this, "\""+email+"\"自动登录中...", Toast.LENGTH_SHORT).show();
            // 测试页面跳转
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        }
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

    public void login(View view) {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginPage.this, "Email and password are required", Toast.LENGTH_SHORT).show();
            Log.d("Login", "用户未输入邮箱或密码");
            return;
        }
//        Toast.makeText(LoginPage.this, "已取得email: " + email +" 密码: " + password, Toast.LENGTH_SHORT).show();
        Log.d("Login", "已取得email: " + email +" 密码: " + password);

        // 测试页面跳转
        Intent intent = new Intent(LoginPage.this, MainActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        inputEmail.setText("");
        inputPassword.setText("");
        startActivity(intent);
        finish();


        /*try {
            // 诶? 好像UG的前端Api里面还没有登录的方法
            sleep(600);
            if(websocket.success) {
                Toast.makeText(LoginPage.this, "Login success", Toast.LENGTH_SHORT).show();
                Log.d("Login", "登录成功");
            } else {
                Toast.makeText(LoginPage.this, "Login failed", Toast.LENGTH_SHORT).show();
                Log.d("Login", "登录失败");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
    }
}
package com.example.friendlist;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.FrontendApi.FrontendAPIProvider;
import com.example.friendlist.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    String loginEmail;
    String loginPassword;
    String receivedUid;
    String receivedUsername;
    boolean autoLoginStatus = false;

    User currentUser;
    FrontendAPIProvider websocket;
    JSONObject serverResponse; // 修改了前端的API，需要一个新的JSONObject来接收返回的(用户)数据
    // 给服务器丢个邮箱，会返回对应 用户的json数据(email, password, uid)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 先读一下SharedPreferences
        SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
        autoLoginStatus = sp.getBoolean("loginStatus", false);

        // 凡是涉及到'网络请求'的操作，我们都需要在请求之后'sleep'缓一下
        initWebSocket();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(autoLoginStatus == false) {
            Log.e("AutoLoginStatus", "用户是首次登录，准备从Intent中获取登录数据...");
            // 用户是'首次登录'，准备从Intent中获取登录数据，并存储到SharedPreferences中
            loginEmail = getIntent().getStringExtra("email");
            loginPassword = getIntent().getStringExtra("password");

            // 向服务器请求用户uid (通过邮箱)
            try {
                websocket.getUserInfoByEmail(loginEmail);
                sleep(600);
                serverResponse = websocket.response;
                receivedUid = serverResponse.getString("uid");
                receivedUsername = serverResponse.getString("uname");
                Log.d("JsonReceivedUid", "获取到的当前用户的uid为: " + receivedUid + ", 用户名为: " + receivedUsername);
            } catch (JSONException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 根据已有的数据创建一个新的用户对象，同时准备存储到SharedPreferences中 User(name, email, uid)
            currentUser = new User(receivedUsername, loginEmail, receivedUid);  // 不用password
            storeUserData(receivedUsername, loginEmail, loginPassword, receivedUid);
            Log.d("Intent", "主页已取得登录用户名:" + receivedUsername +", 登录密码:" + loginPassword + ", 首次从服务器请求回的uid:" + receivedUid);
            Toast.makeText(MainActivity.this, "Welcome back, " + receivedUsername, Toast.LENGTH_SHORT).show();
        }else{

            // 立即读取SharedPreferences存的登录数据，并和服务器返回的数据进行对比 (主要就是看'显示的'username是否和服务器一致)
            Log.d("AutoLoginStatus", "检测到SharedPreferences中已有用户数据，正在读取...");
            String username = sp.getString("username", "null");
            String email = sp.getString("email", "null");
            String password = sp.getString("password", "null");
            String uid = sp.getString("uid", "null");
            boolean loginStatus = sp.getBoolean("loginStatus", false); // 好像有点多此一举，但是先留着

            Log.d("MainPageSP", "尝试从SharedPreferences中获取的 用户名:" + username + "用户邮箱:"+ email+", 密码:" + password + ",  用户Uid:"+ uid+", 登录状态为:" + loginStatus);
            currentUser = new User(email, password, uid);
            Toast.makeText(MainActivity.this, "Welcome back, " + username, Toast.LENGTH_SHORT).show();

        }






        // Set the default fragment_page for the first time entry
        replaceFragment(new MessageFragment());

        // Onclick listener for bottom navigation items
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            // Judge which item is clicked
            switch (item.getItemId()) {
                case R.id.messagesItem: // messages Page
                    replaceFragment(new MessageFragment());
                    break;
                case R.id.contactsItem: // contacts Page
                    replaceFragment(new ContactFragment());
                    break;
                case R.id.addContactItem: // add contact Page
                    replaceFragment(new AddContactFragment());
                    break;
                case R.id.settingsItem: // settings Page
                    replaceFragment(new SettingFragment());
                    break;
            }
        return true;
        });
    }

    // swap the fragment_page for displaying different pages (creating new replacing old)
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    // Save user login data once successfully login
    public void storeUserData(String uname,String email, String password, String uid) {
        // 将用户数据保存至SharedPreferences方法
        SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", uname);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("uid", uid);
        editor.putBoolean("loginStatus",true);
        editor.apply();
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
}
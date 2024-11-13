package com.example.friendlist;

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

import com.example.friendlist.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String loginEmail;
    String loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginEmail = getIntent().getStringExtra("email");
        loginPassword = getIntent().getStringExtra("password");
        Log.d("MainPage", "主页已取得登录email: " + loginEmail +" 登录密码: " + loginPassword);
        Toast.makeText(MainActivity.this, "Login Success! Welcome back, " + loginEmail, Toast.LENGTH_SHORT).show();

        // 用户成功登录来到此页，存储用户登录信息
        storeUserData(loginEmail, loginPassword);

        // 测试能否读取SharedPreferences存的登录数据
        SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
        String email = sp.getString("email", "null");
        String password = sp.getString("password", "null");
        boolean loginStatus = sp.getBoolean("loginStatus", false);
        Log.d("MainPage", "尝试从SharedPreferences中获取的email:" + email +" 密码:" + password + " 登录状态为:" + loginStatus);

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
    public void storeUserData(String email, String password) {
        // 将用户数据保存至SharedPreferences方法
        SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putBoolean("loginStatus",true);
        editor.apply();
    }
}
package com.example.friendlist;

import android.content.Intent;
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
        Toast.makeText(MainActivity.this, "Welcome back! " + loginEmail, Toast.LENGTH_SHORT).show();

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
}
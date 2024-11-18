package com.example.friendlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    EditText username;
    EditText password;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        Button changeBtn = view.findViewById(R.id.changeBtn); // 修改按钮
        Button logoutBtn = view.findViewById(R.id.logoutBtn); // 登出按钮
        SharedPreferences sp = getActivity().getSharedPreferences("userdata", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit(); // 创建sp存储的'修改器'
        String userEmail = sp.getString("email", "null"); // 拿取SP里面的用户数据

        // 获取用户输入'要改变的' email和password (输入控件)
        username = view.findViewById(R.id.changeUsernameInput);
        password = view.findViewById(R.id.changePasswordInput);

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入的新email和password
                String newUsername = username.getText().toString().trim();
                String newPassword = password.getText().toString().trim();

                if(newUsername.isEmpty() && newPassword.isEmpty()) {
                    // 用户俩都没填，直接点button (NO)
                    Toast.makeText(getActivity(), "Please fill in one of the change field!", Toast.LENGTH_SHORT).show();
                    Log.d("ChangeField", "用户俩空都没填就点了button :(");
                    return;
                }else{
                    if(newUsername.isEmpty() && !newPassword.isEmpty()) {
                        // 用户只填了password，没填username
                        Toast.makeText(getActivity(), "Changed password: "+newPassword, Toast.LENGTH_SHORT).show();
                        Log.d("PasswordChange", "修改的密码为: "+newPassword);
                        //服务器操作

                    }else if(!newUsername.isEmpty() && newPassword.isEmpty()) {
                        // 用户只填了username
                        Toast.makeText(getActivity(), "Changed username: "+newUsername, Toast.LENGTH_SHORT).show();
                        Log.d("UsernameChange", "修改的用户名为: "+newUsername);
                        //服务器操作

                    }else{
                        // 用户俩都填了
                        Toast.makeText(getActivity(), "Changed username: "+username + " and password: "+password, Toast.LENGTH_SHORT).show();

                        // 服务器操作


                        }
                    }

                // 服务器操作成功后，修改本地SP数据 (否则在if-else if 跳出，并提示用户'修改失败')
//                editor.putString("username", newUsername);
//                editor.putString("password", newPassword);
//                editor.apply();
//                Log.d("SharedPreferences", "用户数据已修改");
//                Toast.makeText(getActivity(), "用户数据已修改", Toast.LENGTH_SHORT).show();
            }
        });


        // 登出按钮的监听
        logoutBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Toast.makeText(getActivity(),userEmail + " 现已登出", Toast.LENGTH_SHORT).show();
                 // 用户登出，清除SharedPreferences里面的用户登录信息
                 editor.clear(); // 清除'当前用户'的 所有数据
                 editor.putBoolean("loginStatus", false);
                 editor.apply();
                 Log.d("SharedPreferences", "用户数据已登出");

                 // 跳转回登录页面
                 Intent intent = new Intent(getActivity(), LoginPage.class);
                 startActivity(intent);
                 getActivity().finish();
             }
         });
        // Inflate the layout for this fragment
        return view;
    }


}
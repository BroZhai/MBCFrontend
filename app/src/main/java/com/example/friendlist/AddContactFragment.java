package com.example.friendlist;

import static java.lang.Thread.sleep;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.FrontendApi.FrontendAPIProvider;

import org.json.JSONException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddContactFragment extends Fragment {

    FrontendAPIProvider websocket;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddContactFragment newInstance(String param1, String param2) {
        AddContactFragment fragment = new AddContactFragment();
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
        try {
            websocket = new FrontendAPIProvider(new URI("ws://10.0.2.2:8080/backend-api"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);
        EditText input_email = view.findViewById(R.id.emailReq);
        Button sendBtn = view.findViewById(R.id.sendReq);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = input_email.getText().toString();
                if(emailStr.isEmpty()){
                    input_email.setError("Email is required to send request!");
                    input_email.requestFocus();
                }else{ // 肯定不可能这么简单 (现在准备并入UG的前端Api)
                    // Send request to the email
                    String uid = "184bc12a-2b5e-41a4-8342-d997ca0e7666";
                    try {
                        websocket.addNewFriend(uid, emailStr);

                        if(websocket.success){
                            Toast.makeText(getContext(), "Request sent to: " + emailStr, Toast.LENGTH_SHORT).show();
                            input_email.setText("");
                            Log.d("WebSocketRegisiter", "服务器已成功响应" + emailStr);
                        }else{
                            Toast.makeText(getContext(), "Request failed to: " + emailStr, Toast.LENGTH_SHORT).show();
                            Log.d("WebSocketRegisiter", "加好友失败QAQ..." );
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // Display a toast message to the user
                    Toast.makeText(getContext(), "Request sent to: " + emailStr, Toast.LENGTH_SHORT).show();
                    input_email.setText("");
                }
            }
        });

        return view;
    }
}
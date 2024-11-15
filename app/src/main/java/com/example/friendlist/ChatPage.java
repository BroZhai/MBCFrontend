package com.example.friendlist;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChatPage extends AppCompatActivity {

    String friendName;
    String friendUid;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_chatbox_page);
        friendName = getIntent().getStringExtra("friendName");
        friendUid = getIntent().getStringExtra("friendUid");
        myUid = getIntent().getStringExtra("myUid");
        TextView title = findViewById(R.id.friendTitle);
        title.setText("Chat with " + friendName);
    }

    // 发送按钮
    public void sendMessage(View view){
        // 发送消息
        EditText messageField = findViewById(R.id.bufferMsg);
        String msg = messageField.getText().toString();
        if(msg.isEmpty()){
            // 消息为空
            Toast.makeText(this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
            messageField.setText("");
            // 消息不为空 (AI gen的注释，感觉可以参考，但以实际情况为准)
            // 发送消息
            // 1. 将消息发送到服务器
            // 2. 服务器将消息发送给对方
            // 3. 对方收到消息后，将消息显示在自己的聊天框中
            // 4. 对方回复消息
            // 5. 重复1-4
            // 6. 退出聊天
            // 7. 服务器将聊天记录存储到数据库
            // 8. 退出聊天页面
        }
    }

    // 返回按钮
    public void exitChat(View view){
        finish();
    }
}
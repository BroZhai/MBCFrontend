package com.example.friendlist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ChatPage extends AppCompatActivity {

    String friendName;
    String friendUid;
    String myUid;
    String myName;
    ArrayList<Message> messageList = new ArrayList<>();
    ListView msgListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_chatbox_page);
        SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE); // 从SP中读'本地用户'的用户名
        friendName = getIntent().getStringExtra("friendName");
        friendUid = getIntent().getStringExtra("friendUid");
        myUid = getIntent().getStringExtra("myUid");
        myName = sp.getString("username", "null");
        TextView title = findViewById(R.id.friendTitle);
        title.setText("Chat with " + friendName);

        msgListView = findViewById(R.id.chattingList);
        msgListView.setAdapter(new MyAdapter());
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
            Message newMsg = new Message(myUid, friendUid, msg);
            newMsg.setFriendMsg(false);
            messageList.add(newMsg); // 将'发送消息'添加到'消息列表messageList'
            updateMessageView();

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

    // 更新'消息视图'
    public void updateMessageView(){
        // 从服务器获取最新的消息记录
        // 1. 从服务器获取最新的消息记录
        // 2. 将消息记录显示在聊天框中
        // 3. 重复1-2
        msgListView.setAdapter(new MyAdapter());
    }

    // 返回按钮
    public void exitChat(View view){
        finish();
    }


    class MyAdapter extends BaseAdapter {
        //对于每个Adapter，我们都要去重写以下四个方法 (必须重写！)
        @Override
        public int getCount() { // 获取数据的'个数'
            return messageList.size();
        }

        @Override
        public Object getItem(int i) { //获取具体某个'元素'，上面会传入'下标'进来给你定位
            return messageList.get(i);
        }

        @Override
        public long getItemId(int i) {//获取每一个item的唯一标识符"id"(偷懒可直接用下标)
            return i;
        }

        @Override
        //重点: 获取每一个item的'展示样式' (单独开个layout文件设置每个Item的样式)
        public View getView(int i, View view, ViewGroup viewGroup) {
            //参数说明: i (item的下标位置)，
            //view (缓存着'划出视野'的item, 优化listView用)
            //viewGroup (listView对象本身，用的少)

            //1. 使用View.inflate()加载布局 inflate(Activity对象，布局文件，不知道就填null);
            View item_view = view.inflate(ChatPage.this, R.layout.msg_item_list, null);

            //2. 从取得的View对象中，获取里面 要进行'动态设置'的'小组件' (注意要指定为上面的inflate()的view对象来找[不然默认就会去找'全局的View']XD)
            TextView cap = (TextView) item_view.findViewById(R.id.msgCap);
            TextView content = (TextView) item_view.findViewById(R.id.messageContent);
            //Tips: priceLable不用动(设置)，它就是放在那里展示的

            //3. 根据"原数据"设置各个控件的'展示数据'
            boolean isFriendMsg = messageList.get(i).isFriendMsg();
            if (isFriendMsg){
                // 该消息Item是好友发过来的，设置为 "好友名字的首字母"
                cap.setText(friendName.substring(0, 1));
            }else{
                // 是'本地用户'发的
                cap.setText(myName.substring(0, 1));
            }
            content.setText(messageList.get(i).getContent());

            //4. 返回该View对象 (这样以后，这个Adapter就算建立好了，接下来将这个"格式"应用到listView控件中去)
            return item_view;
        }
    }
}
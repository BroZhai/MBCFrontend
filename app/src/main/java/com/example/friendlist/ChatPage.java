package com.example.friendlist;

import static java.lang.Thread.sleep;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.FrontendApi.FrontendAPIProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class ChatPage extends AppCompatActivity {

    String friendName;
    String friendUid;
    String myUid;
    String myName;
//    ArrayList<Message> messageList = new ArrayList<>();
    ListView msgListView;
    MessageList messageList = new MessageList();
    MessageObserver msgObserver = new MessageObserver();

    Message previousMessage = null;

    // 来一个'定时器'，每隔一段时间就去'同步'一次消息
    Timer timer = new Timer();;
//    Timer timer;
    MyTimerTask timerTask = new MyTimerTask();
    private Handler handler = new Handler(Looper.getMainLooper());

    FrontendAPIProvider websocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_chatbox_page);

        messageList = new MessageList();
        msgObserver = new MessageObserver();
        messageList.addObserver(msgObserver);

        initWebSocket();

        SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE); // 从SP中读'本地用户'的用户名
        friendName = getIntent().getStringExtra("friendName");
        friendUid = getIntent().getStringExtra("friendUid");
        myUid = getIntent().getStringExtra("myUid");
        myName = sp.getString("username", "null");
        Log.d("ChatPage", "当前正在和好友: " + friendName + "聊天, friendUid是: " + friendUid);
        TextView title = findViewById(R.id.friendTitle);
        title.setText("Chat with " + friendName);

        msgListView = findViewById(R.id.chattingList);
        msgListView.setAdapter(new MyAdapter());

        timer.schedule(timerTask, 1000, 300); // 每隔2秒同步一次消息
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
            Log.e("Info","myUid:"+myUid +", friendUid:"+friendUid + "msg:"+msg);
            newMsg.setFriendMsg(false); // 这是'本地用户'发的消息
            messageList.addMsg(newMsg); // 将'发送消息'添加到'消息列表messageList'
            try {
                websocket.uid = myUid;
                websocket.fid = friendUid;
                websocket.sendNewMessage(myUid, friendUid, msg); // 发送消息到服务器
                sleep(400);
            } catch (JSONException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
            messageField.setText("");
//            timer = new Timer();
//            timer.schedule(timerTask, 1000, 2000);
//            timer.schedule(timerTask, 1000, 2000); // 发送完消息，开始'同步消息'
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

    public void getLatestMessage() throws InterruptedException, JSONException {
        websocket.getLatestMessage(myUid, friendUid);
        sleep(200);
        JSONObject newMsg = websocket.latest_message;
        System.out.println("newMsg: " + newMsg.toString());
        if(newMsg==null){
            Log.d("ChatPage", "目前还没有新消息");
        }else {
            String judge = newMsg.getString("sid");
            String content = newMsg.getString("content");
            if(judge.equals(myUid)){
                Log.d("ChatPage", "我有新消息: " + newMsg.toString());
                String newTime = newMsg.getString("timestamp");
                Message newMessage = new Message(judge, friendUid, content);
                if(previousMessage!=null && previousMessage.getTime().equals(newTime)){
                    Log.d("ChatPage", "消息重复，不添加到消息列表中");
                    return;
                }
                previousMessage = newMessage;
                previousMessage.setTime(newTime);

                // 这是'好友'发的'新消息'
                newMessage.setFriendMsg(true);
                messageList.addMsg(newMessage);
                Log.d("ChatPage", "已将新消息添加到消息列表中");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                            updateMessageView();
                        ((MyAdapter)msgListView.getAdapter()).updateData();
                    }
                });
            }
        }
            // 有新消息 (尝试对消息进行读取)
            // sid,content,timestamp

//            Message newMessage = new Message(senderID, myUid, content);

    }

    // 更新'消息视图' [旧方法]
    public void updateMessageView(){
        // 从服务器获取最新的消息记录
        // 1. 从服务器获取最新的消息记录
        // 2. 将消息记录显示在聊天框中
        // 3. 重复1-2
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MyAdapter)msgListView.getAdapter()).updateData();
            }
        });
    }

    // 返回按钮
    public void exitChat(View view){
        finish();
    }

    // 初始化WebSocket连接
    public void initWebSocket() {
        try {
            URI uri = new URI("ws://10.0.2.2:8080/backend-api");
            websocket = new FrontendAPIProvider(uri);
            websocket.connect();  // 异步连接
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        websocket.close();
    }
/*---------------------------------------------------------分割线 -------------------------------------------------------*/
    // '真正的'消息列表 (被Observer监视的对象)
    public class MessageList extends Observable {
        private ArrayList<Message> list = new ArrayList<>();

        public ArrayList<Message> getMsgList() {
            return list;
        }

        public void addMsg(Message message){
            list.add(message);
            setChanged();
            notifyObservers();
        }

        public void removeAll(){
            list.clear();
            setChanged();
            notifyObservers();
        }

        public int getSize(){
            return list.size();
        }

        public boolean contains(Message message){
            return list.contains(message);
        }
    }

    public class MessageObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            if (o instanceof MessageList) {
                System.out.println("检测到消息列表中有新消息，已更新");
//                updateMessageView();
                ((MyAdapter)msgListView.getAdapter()).updateData();
            }
        }
    }



    // 定时器
    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("消息此刻同步了: " + System.currentTimeMillis());
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getLatestMessage();
                        } catch (InterruptedException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class MyAdapter extends BaseAdapter {
        //对于每个Adapter，我们都要去重写以下四个方法 (必须重写！)
        @Override
        public int getCount() { // 获取数据的'个数'
            return messageList.getMsgList().size();
        }

        @Override
        public Object getItem(int i) { //获取具体某个'元素'，上面会传入'下标'进来给你定位
            return messageList.getMsgList().get(i);
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
            boolean isFriendMsg = messageList.getMsgList().get(i).isFriendMsg();
            if (isFriendMsg){
                // 该消息Item是好友发过来的，设置为 "好友名字的首字母"
                cap.setText(friendName.substring(0, 1));
            }else{
                // 是'本地用户'发的
                cap.setText(myName.substring(0, 1));
            }
            content.setText(messageList.getMsgList().get(i).getContent());

            return item_view;
        }
        public void updateData() {
            notifyDataSetChanged();
        }
    }

}
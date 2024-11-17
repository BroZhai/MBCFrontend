package com.example.friendlist;

import static java.lang.Thread.sleep;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.FrontendApi.FrontendAPIProvider;

import org.json.JSONException;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddContactFragment extends Fragment {

    FrontendAPIProvider websocket;
//    ArrayList<UserRequest> requestArray = new ArrayList<>(); // 原来的'好友请求列表'

    RequestList requestList = new RequestList();
    RequestObserver requestObserver = new RequestObserver();;

    ListView lv;
    TextView showNothing;


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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);

        requestList.addObserver(requestObserver);

        EditText input_email = view.findViewById(R.id.emailReq);
        lv = view.findViewById(R.id.requestList);
        showNothing = view.findViewById(R.id.noRequestDisplay);
        Button sendBtn = view.findViewById(R.id.sendReq);
        SharedPreferences sp = getContext().getSharedPreferences("userdata", getContext().MODE_PRIVATE);
        String currentUid = sp.getString("uid", "null");

        // 手动添加一个默认的'好友申请'item
        UserRequest request1 = new UserRequest("Alice", "Alice@email.com",currentUid ,"184bc12a-2b5e-41a4-8342-d997ca0e7666");
        UserRequest request2 = new UserRequest("Bob", "bob@bombmail.com",currentUid ,"184bc12a-2b5e-41a4-8342-d997ca0e7666");
        requestList.getRequestList().add(request1);
        requestList.getRequestList().add(request2);


        if(requestList.getRequestList().isEmpty()){
            // 如果没有请求，就不显示ListView，只显示一个TextView
            lv.setVisibility(View.GONE);
            showNothing.setVisibility(View.VISIBLE);
        }else{
            // 如果有请求，就只显示ListView
            lv.setVisibility(View.VISIBLE);
            showNothing.setVisibility(View.GONE);
            MyAdapter adapter = new MyAdapter();
            lv.setAdapter(adapter);
            }



        initWebSocket(); // 创建该界面时，初始化WebSocket并尝试建立连接

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailStr = input_email.getText().toString().trim();
                if(emailStr.isEmpty()){
                    input_email.setError("Email is required to send request!");
                    input_email.requestFocus();
                }else{ // 肯定不可能这么简单 (现在准备并入UG的前端Api)
                    // Send request to the email
                    Log.d("WebSocketRegisiter", "程序已执行至96行，输入的邮箱为: " + emailStr);
                    try {
                        websocket.addNewFriend(currentUid, emailStr);
                        sleep(600);
                        if(websocket.success){
                            Toast.makeText(getContext(), "好友请求已成功发至: " + emailStr, Toast.LENGTH_SHORT).show();
                            Log.d("WebSocketRegisiter", "服务器已成功响应好友请求" + emailStr);
                            input_email.setText("");
                        }else{
                            Toast.makeText(getContext(), "服务器响应了失败QAQ..." + emailStr, Toast.LENGTH_SHORT).show();
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (websocket != null) {
            websocket.close();
        }
    }

    // 原来的'清楚请求'方法 (更新了Observer后，这个方法应该就不需要了)
    public void clearPendingRequests(){
        for(int i=0; i<requestList.getRequestList().size(); i++){
            if(requestList.getRequestList().get(i).isPending()==false){
                Log.d("ClearPendingRequests", "已清除:"+ requestList.getRequestList().get(i).getName() +"的好友请求");
                requestList.getRequestList().remove(i);
                if(requestList.getRequestList().isEmpty()){
                    lv.setVisibility(View.GONE);
                    showNothing.setVisibility(View.VISIBLE);
                }else {
                    //更新前端的ListView显示
                    lv.setVisibility(View.VISIBLE);
                    showNothing.setVisibility(View.GONE);
                    MyAdapter adapter = new MyAdapter();
                    lv.setAdapter(adapter);
                }
            }
        }
    }

    // '被监听的'好友列表 (被Observer监视的对象)
    class RequestList extends Observable {
        private ArrayList<UserRequest> list = new ArrayList<>();

        public ArrayList<UserRequest> getRequestList() {
            return list;
        }

        public void addRequest(UserRequest request){
            list.add(request);
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

    }

    // 设置好友列表的'监听器'
    public class RequestObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            if (o instanceof AddContactFragment.RequestList) {
                Log.d("RequestObserver", "检测到好友请求列表中有新请求，已更新");
                AddContactFragment.RequestList requestList = (AddContactFragment.RequestList) o;
                // 这里可以根据需求对新的请求进行处理，比如更新UI等
                // 假设这里是更新ListView相关逻辑
                ArrayList<UserRequest> requests = requestList.getRequestList();
                // 根据requests的内容更新ListView，这里需要结合ListView的Adapter来实现具体更新逻辑
            }
        }
    }

    /* 以下部分为Adapter, 准备给ListView使用*/
    class MyAdapter extends BaseAdapter {
        //对于每个Adapter，我们都要去重写以下四个方法 (必须重写！)
        @Override
        public int getCount() { // 获取数据的'个数'
            return requestList.getRequestList().size();
        }

        @Override
        public Object getItem(int i) { //获取具体某个'元素'，上面会传入'下标'进来给你定位
            return requestList.getRequestList().get(i);
        }

        @Override
        public long getItemId(int i) {//获取每一个item的唯一标识符"id"(偷懒可直接用下标)
            return i;
        }

        @Override
        //重点: 获取每一个item的'展示样式' (单独开个layout文件设置每个Item的样式)
        public View getView(int i, View view, ViewGroup viewGroup) {
            //参数说明: i (item的下标位置)，用于定位'各个item'
            //view (缓存着'划出视野'的item, 优化listView用)
            //viewGroup (listView对象本身，用的少)

            //1. 使用View.inflate()加载布局 inflate(Activity对象，布局文件，不知道就填null);
            //todo: 这里的还没有写出每个'请求item'的layout样式
            View item_view = view.inflate(AddContactFragment.this.getActivity(), R.layout.request_item_list, null);

            // todo: 以下部分要把'请求item'的layout样式写出来之后，才能确认(要修改哪些地方，以及对应'操作按钮'的监听)

            //2. 从取得的View对象中，获取里面 要进行'动态设置'的'小组件' (注意要指定为上面的inflate()的view对象来找[不然默认就会去找'全局的View']XD)
            TextView capital = (TextView) item_view.findViewById(R.id.capLetter);
            TextView nameString = (TextView) item_view.findViewById(R.id.nameStr);
            Button acceptBtn = (Button) item_view.findViewById(R.id.acceptBtn);
            Button declineBtn = (Button) item_view.findViewById(R.id.declineBtn);

            String requesterName = requestList.getRequestList().get(i).getName();

            //3. 根据"原数据"设置各个控件的'展示数据'
            capital.setText(requesterName.substring(0,1));
            nameString.setText(requesterName);

            //3.5 在这里设置'各个item按钮'的监听器
            acceptBtn.setOnClickListener(new View.OnClickListener() { //接受好友请求
                @Override
                public void onClick(View view) {
                    // Accept the request
//                    requestArray.get(i).acceptRequest();
                    Toast.makeText(getContext(), "Accepted request from: " + requesterName  , Toast.LENGTH_SHORT).show();
                    Log.d("AcceptButton", "已接受好友请求:" + requesterName);
                    requestList.getRequestList().get(i).acceptRequest(); // 更新'请求数组'里面的ispending状态，清楚该请求栏

//                   clearPendingRequests(); // 原来的视图更新方法

                    // 尚未将操作提交至服务器

                }
            });

            declineBtn.setOnClickListener(new View.OnClickListener() { //接受好友请求
                @Override
                public void onClick(View view) {
                    // Accept the request
//                    requestArray.get(i).acceptRequest();
                    Toast.makeText(getContext(), "Declined request from: " + requesterName  , Toast.LENGTH_SHORT).show();
                    Log.d("DeclinedButton", "已拒绝好友请求:" + requesterName);
                    requestList.getRequestList().get(i).rejectRequest(); // 同上
//                    clearPendingRequests(); // 原来的视图更新方法

                    // 尚未将操作提交至服务器
                }
            });

            //4. 返回该View对象 (这样以后，这个Adapter就算建立好了，接下来将这个"格式"应用到listView控件中去)
            return item_view;
        }
    }
}
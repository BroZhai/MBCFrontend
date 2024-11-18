package com.example.friendlist;

import static java.lang.Thread.sleep;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;

import com.example.FrontendApi.FrontendAPIProvider;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AddContactFragment extends Fragment {

    private FrontendAPIProvider websocket;
    private RequestList requestList;
    private RequestObserver requestObserver;
    private ListView lv;
    private TextView showNothing;
    private JSONArray requestJson;
    private String currentUid; // 当前用户的uid

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
        if (getArguments()!= null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // 初始化WebSocket
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
        EditText input_email = view.findViewById(R.id.emailReq);
        lv = view.findViewById(R.id.requestList);
        showNothing = view.findViewById(R.id.noRequestDisplay);
        Button sendBtn = view.findViewById(R.id.sendReq);
        SharedPreferences sp = getContext().getSharedPreferences("userdata", getContext().MODE_PRIVATE);
        currentUid = sp.getString("uid", "null");

        // 手动添加一个默认的'好友申请'item

        requestList = new RequestList();
        requestObserver = new RequestObserver();
        requestList.addObserver(requestObserver);


        if (requestList.getRequestList().isEmpty()) {
            // 如果没有请求，就不显示ListView，只显示一个TextView
            lv.setVisibility(View.GONE);
            showNothing.setVisibility(View.VISIBLE);
        } else {
            // 如果有请求，就只显示ListView
            lv.setVisibility(View.VISIBLE);
            showNothing.setVisibility(View.GONE);
            MyAdapter adapter = new MyAdapter();
            lv.setAdapter(adapter);
        }


        initWebSocket(); // 创建该界面时，初始化WebSocket并尝试建立连接

        try {
            sleep(400); // 等待WebSocket连接建立
            websocket.getFriendRequestList(currentUid);
            sleep(200);
        } catch (InterruptedException | JSONException e) {
            throw new RuntimeException(e);
            }
//        System.out.println(websocket.request_friendList); // 测试'打印数据'
        requestJson = websocket.request_friendList;
        if (requestJson != null) {
            System.out.println("返回的Json好友请求列表不为空！正在读取数据...");
            System.out.println(requestJson);
            for (int i = 0; i < requestJson.length(); i++) {
                try {
                    String friendName = requestJson.getJSONObject(i).getString("uname");
                    String email = requestJson.getJSONObject(i).getString("email");
                    String fid = requestJson.getJSONObject(i).getString("uid");
                    System.out.println("好友请求列表中的第" + i + "个请求: " + "用户名: " + friendName + " 邮箱: " + email  + " 好友fid: " + fid);
                    UserRequest request = new UserRequest(friendName, email, fid);
                    requestList.addRequest(request);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            System.out.println("阿偶，返回的Json好友请求列表为空!?");
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailStr = input_email.getText().toString().trim();
                if (emailStr.isEmpty()) {
                    input_email.setError("Email is required to send request!");
                    input_email.requestFocus();
                } else {
                    // Send request to the email
                    Log.d("WebSocketRegisiter", "程序已执行至96行，输入的邮箱为: " + emailStr);
                    try {
                        websocket.addNewFriend(currentUid, emailStr);
                        // 这里假设addNewFriend是异步操作，并且有合适的回调机制来处理结果，
                        // 原代码中的sleep(600)不是一个好的处理异步操作的方式，todo: 找到更好的解决方式
                        sleep(600);
                        if (websocket.success) {
                            Toast.makeText(getContext(), "Friend request has successfully send to: " + emailStr, Toast.LENGTH_SHORT).show();
                            Log.d("WebSocketRegisiter", "服务器已成功响应好友请求" + emailStr);
                            input_email.setText("");
                            return;
                        } else {
                            Toast.makeText(getContext(), "User email does not exist, try again :(" + emailStr, Toast.LENGTH_SHORT).show();
                            Log.d("WebSocketRegisiter", "加好友失败QAQ...");
                            return;
                        }
                    } catch (JSONException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        return view;
    }



    @Override
    // 当Fragment被显示时，注册监听器
    public void onResume() {
        super.onResume();
        requestList.addObserver(requestObserver);
        SharedPreferences sp = getContext().getSharedPreferences("userdata", getContext().MODE_PRIVATE);
        String currentUid = sp.getString("uid", "null");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (websocket!= null) {
            websocket.close();
        }
    }




    // '被监听的'好友列表 (被Observer监视的对象)
    class RequestList extends Observable {
        private ArrayList<UserRequest> list = new ArrayList<>();

        public ArrayList<UserRequest> getRequestList() {
            return list;
        }

        public void addRequest(UserRequest request) {
            list.add(request);
            setChanged();
            notifyObservers();
        }

        public void removeAll() {
            list.clear();
            setChanged();
            notifyObservers();
        }

        public int getSize() {
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
                ArrayList<UserRequest> requests = requestList.getRequestList();
                if (requests.isEmpty()) {
                    lv.setVisibility(View.GONE);
                    showNothing.setVisibility(View.VISIBLE);
                } else {
                    lv.setVisibility(View.VISIBLE);
                    showNothing.setVisibility(View.GONE);
                    MyAdapter adapter = new MyAdapter();
                    lv.setAdapter(adapter);
                }
            }
        }
    }


    /* 以下部分为Adapter, 准备给ListView使用*/
    class MyAdapter extends BaseAdapter {
        //对于每个Adapter，我们都要去重写以下四个方法 (必须重写！)
        @Override
        public int getCount() {
            return requestList.getRequestList().size();
        }

        @Override
        public Object getItem(int i) {
            return requestList.getRequestList().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        //重点: 获取每一个item的'展示样式' (单独开个layout文件设置每个Item的样式)
        public View getView(int i, View view, ViewGroup viewGroup) {
            //参数说明: i (item的下标位置)，用于定位'各个item'
            //view (缓存着'划出视野'的item, 优化listView用)
            //viewGroup (listView对象本身，用的少)

            //1. 使用View.inflate()加载布局 inflate(Activity对象，布局文件，不知道就填null);
            View item_view = view.inflate(AddContactFragment.this.getActivity(), R.layout.request_item_list, null);

            //2. 从取得的View对象中，获取里面 要进行'动态设置'的'小组件' (注意要指定为上面的inflate()的view对象来找[不然默认就会去找'全局的View']XD)
            TextView capital = (TextView) item_view.findViewById(R.id.capLetter);
            TextView nameString = (TextView) item_view.findViewById(R.id.nameStr);
            Button acceptBtn = (Button) item_view.findViewById(R.id.acceptBtn);
            Button declineBtn = (Button) item_view.findViewById(R.id.declineBtn);

            String requesterName = requestList.getRequestList().get(i).getFName();

            //3. 根据"原数据"设置各个控件的'展示数据'
            capital.setText(requesterName.substring(0, 1));
            nameString.setText(requesterName);

            //3.5 在这里设置'各个item按钮'的监听器
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                //接受好友请求
                @Override
                public void onClick(View view) {
                    // Accept the request
                    Toast.makeText(getContext(), "Accepted request from: " + requesterName, Toast.LENGTH_SHORT).show();
                    Log.d("AcceptButton", "已接受好友请求:" + requesterName);

                    String fid = requestList.getRequestList().get(i).getFUid();
                    try {
                        websocket.isFriendRequestAccept(currentUid, fid,"accepted");
                        sleep(500);
                    } catch (JSONException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    requestList.getRequestList().get(i).acceptRequest(); // 更新'请求数组'里面的ispending状态，清除该请求栏
                    // 更新UI显示
                    clearPendingRequests();
                }
            });

            declineBtn.setOnClickListener(new View.OnClickListener() {
                //拒绝好友请求
                @Override
                public void onClick(View view) {
                    // Accept the request
                    Toast.makeText(getContext(), "Declined request from: " + requesterName, Toast.LENGTH_SHORT).show();
                    Log.d("DeclinedButton", "已拒绝好友请求:" + requesterName);
                    String fid = requestList.getRequestList().get(i).getFUid();
                    try {
                        websocket.isFriendRequestAccept(currentUid, fid,"blocked");
                        sleep(500);
                    } catch (JSONException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    requestList.getRequestList().get(i).rejectRequest(); // 同上
                    // 更新UI显示
                    clearPendingRequests();
                }
            });

            //4. 返回该View对象 (这样以后，这个Adapter就算建立好了，接下来将这个"格式"应用到listView控件中去)
            return item_view;
        }
    }
    public void clearPendingRequests(){
        for(int i=0; i<requestList.getRequestList().size(); i++){
            if(requestList.getRequestList().get(i).isPending()==false){
                Log.d("ClearPendingRequests", "已清除:"+ requestList.getRequestList().get(i).getFName() +"的好友请求");
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
}
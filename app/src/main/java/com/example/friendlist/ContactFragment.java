package com.example.friendlist;

import static android.content.Context.MODE_PRIVATE;

import static java.lang.Thread.sleep;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.FrontendApi.FrontendAPIProvider;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

//    ArrayList<User> friendList = new ArrayList<>();

    ListView listView;
    public static UserList userList;
    UserObserver userObserver;
    TextView friendTip;
    FrontendAPIProvider websocket;

    JSONArray friendList;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
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
        // 注册'好友监听器'
        userList = new UserList();
        userObserver = new UserObserver();
        userList.addObserver(userObserver);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        listView = view.findViewById(R.id.contactListView);
        listView.setAdapter(new MyAdapter());

        friendTip = view.findViewById(R.id.noFriendTip);

        // 读取SharedPreferences中的'本地用户数据' (目前只要了uid, 其他要的后面再加)
        SharedPreferences sp = getActivity().getSharedPreferences("userdata", MODE_PRIVATE);
        String myUid = sp.getString("uid", "null");

        // 初始化WebSocket连接，等待响应
        initWebSocket();
        try {
            sleep(400);
            websocket.getUserFriendList(myUid); // 向服务器请求: '当前用户'的好友列表
            sleep(100);
            friendList = websocket.friend_list;
        } catch (InterruptedException | JSONException e) {
            throw new RuntimeException(e);
        }

        if(friendList != null){// 如果好友列表不为空
            System.out.println("当前用户好友列表不为空，正在读取好友列表...");
            userList.getUserList().clear(); // 清空之前的'已缓存'好友列表
            System.out.println(friendList);

            // 从服务器读取到的'好友列表'加载'好友数据'
            for(int i = 0; i < friendList.length(); i++){
                try {
                    String fname = friendList.getJSONObject(i).getString("uname");
                    String femail = friendList.getJSONObject(i).getString("email");
                    String fuid = friendList.getJSONObject(i).getString("uid");
                    User u = new User(fname, femail, fuid);
                    userList.addUser(u);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(userList.getSize() > 0){
            // 列表有好友
            listView.setVisibility(View.VISIBLE);
            friendTip.setVisibility(View.GONE);
        }else {
            // 列表无好友
            listView.setVisibility(View.GONE);
            friendTip.setVisibility(View.VISIBLE);
        }

        // 设置短按item的监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ContactFragment.this.getActivity(), "You are not chatting with " + userList.getUserList().get(i).getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ContactFragment.this.getActivity(), ChatPage.class);
                intent.putExtra("friendName", userList.getUserList().get(i).getName());
                intent.putExtra("friendUid", userList.getUserList().get(i).getUid());
                intent.putExtra("myUid", myUid);
                startActivity(intent);
            }
        });

        // 设置长按item的监听器 (弹窗提示是否要删除好友)
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                // 可以在这里使用item对象进行后续操作
                AlertDialog.Builder bdr = new AlertDialog.Builder(ContactFragment.this.getActivity());
                // 创建一个弹窗的"建立对象", "()"内传入Activity(作用的上下文对象)
                bdr.setCancelable(true); // 设置是否可以通过"点击对话框外部"取消对话框

                bdr.setTitle("Delete Friend"); // 设置对话框标题
                bdr.setMessage("Are you sure to delete " + userList.getUserList().get(position).getName() + "?"); // 设置对话框内容
                bdr.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    // 在此之后，我们就可以直接用 最外面的'position'来进行item定位了
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ContactFragment.this.getActivity(), "You've canceled the deletion", Toast.LENGTH_SHORT).show();
                    }
                });
                bdr.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 删除好友item 并 更新视图
                        Toast.makeText(ContactFragment.this.getActivity(), "You've deleted " + userList.getUserList().get(position).getName(), Toast.LENGTH_SHORT).show();

                        // 向服务器发送删除好友请求
                        try {
                            String friendUid = userList.getUserList().get(position).getUid();
                            String friendName = userList.getUserList().get(position).getName();
                            websocket.deleteFriend(myUid,friendUid);
                            sleep(700);
                            if(websocket.success){
                                Toast.makeText(ContactFragment.this.getActivity(), "You have deleted", Toast.LENGTH_SHORT).show();
                                Log.d("DeleteFriend", "服务器端删除"+friendName+"好友成功");
                            }
                        } catch (InterruptedException | JSONException e) {
                            e.printStackTrace();
                        }

                        // 及时更新本地数据 和 视图
                        userList.getUserList().remove(position);
                        listView.setAdapter(new MyAdapter());
                    }
                });

                AlertDialog adg = bdr.create();// 实例化"创建对象" 为 "正式弹窗"
                adg.show(); // 显示"正式弹窗"
                return true; // 返回true表示"长按"事件被消费了，不会再触发"短按"事件
            }
        });



        return view;
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

    class MyAdapter extends BaseAdapter {
        //对于每个Adapter，我们都要去重写以下四个方法 (必须重写！)
        @Override
        public int getCount() { // 获取数据的'个数'
            return userList.getSize();
        }

        @Override
        public Object getItem(int i) { //获取具体某个'元素'，上面会传入'下标'进来给你定位
            return userList.getUserList().get(i);
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
            View item_view = view.inflate(ContactFragment.this.getActivity(), R.layout.re_item_list, null);

            //2. 从取得的View对象中，获取里面 要进行'动态设置'的'小组件' (注意要指定为上面的inflate()的view对象来找[不然默认就会去找'全局的View']XD)
            TextView capital = (TextView) item_view.findViewById(R.id.capLetter);
            TextView nameString = (TextView) item_view.findViewById(R.id.nameStr);
            //Tips: priceLable不用动(设置)，它就是放在那里展示的

            //3. 根据"原数据"设置各个控件的'展示数据'
            String fName = userList.getUserList().get(i).getName();
            capital.setText(fName.substring(0,1));
            nameString.setText(fName);

            //4. 返回该View对象 (这样以后，这个Adapter就算建立好了，接下来将这个"格式"应用到listView控件中去)
            return item_view;
        }
    }

    // 监听的'好友对象'列表
    public static class UserList extends Observable{
        public static ArrayList<User> userList = new ArrayList<>();
        public void addUser(User user){
            userList.add(user);
            setChanged();
            notifyObservers();
        }
        public void removeUser(User user){
            userList.remove(user);
            setChanged();
            notifyObservers();
        }
        public ArrayList<User> getUserList(){
            return userList;
        }

        public int getSize(){
            return userList.size();
        }
    }

    // 监听'好友对象'的Observer
    class UserObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            if (o instanceof UserList) {
                Log.d("FriendListObserver", "好友列表发生变动，已更新");
//                ContactFragment.UserList ul = (ContactFragment.UserList) o;
//                ArrayList<User> list = ul.getUserList();
//                if(list.isEmpty()){
//                    listView.setVisibility(View.GONE);
//                    friendTip.setVisibility(View.VISIBLE);
//                }else {
//                    listView.setVisibility(View.VISIBLE);
//                    friendTip.setVisibility(View.GONE);
//                }
                ((MyAdapter)listView.getAdapter()).notifyDataSetChanged();
            }
        }
    }

}


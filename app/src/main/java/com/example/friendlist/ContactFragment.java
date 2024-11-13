package com.example.friendlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

    private String[] friendName = {
            "Pancake Tekon", "Vvokos", "China Boy","Leafeon BaiLong",
            "Pinkcandy Zhou", "DanielL 04", "Rokidna UG",
            "Ice Wings","Joy Project", "White sheep",
            "Dreamland Palesky","Sliver Cat", "多摩 aac1"};

    class MyAdapter extends BaseAdapter {
        //对于每个Adapter，我们都要去重写以下四个方法 (必须重写！)
        @Override
        public int getCount() { // 获取数据的'个数'
            return friendName.length;
        }

        @Override
        public Object getItem(int i) { //获取具体某个'元素'，上面会传入'下标'进来给你定位
            return friendName[i];
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
            capital.setText(friendName[i].substring(0,1));
            nameString.setText(friendName[i]);

            //4. 返回该View对象 (这样以后，这个Adapter就算建立好了，接下来将这个"格式"应用到listView控件中去)
            return item_view;
        }
    }



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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ListView listView = view.findViewById(R.id.contactListView);
        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ContactFragment.this.getActivity(), "You've clicked " + friendName[i], Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
// 晚上再看看，先忙
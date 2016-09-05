package com.wp.mobileguard.mobileguard.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wp.mobileguard.R;
import com.wp.mobileguard.mobileguard.domain.ContactBean;
import com.wp.mobileguard.mobileguard.engine.ReadContactsEngine;
import com.wp.mobileguard.mobileguard.utils.MyConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示所有好友信息的界面
 * Created by WangPeng on 2016/4/1.
 * Email:wp20082009@foxmail.com
 */
public class FriendsActivity extends ListActivity {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ProgressDialog pd;
    private MyAdapter adapter;
    private ListView lv_datas;

    //获取联系人的数据
    List<ContactBean> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用ListView组件来显示好友信息
        lv_datas = getListView();
        adapter = new MyAdapter();
        lv_datas.setAdapter(adapter);

        //填充数据
        initData();
        //初始化事件
        initEvent();
    }

    /**
     * 初始化Listview的条目点击事件
     */
    private void initEvent() {
        lv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //处理条目点击事件
                //获取当前条目的数据
                ContactBean contactBean = datas.get(position);
                String phone = contactBean.getPhone();
                Intent intent = new Intent();
                //保存安全号码
                intent.putExtra(MyConstant.SAFENUMBER, phone);
                //设置数据
                setResult(1, intent);
                //关闭自己
                finish();
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //更新界面
            switch (msg.what) {
                case LOADING://正在加载数据
                    //显示加载数据的对话框
                    pd = new ProgressDialog(FriendsActivity.this);
                    pd.setTitle("注意");
                    pd.setMessage("正在玩命加载数据。。。。");
                    pd.show();//显示对话框
                    break;
                case FINISH:
                    pd.dismiss();
                    pd = null;
                    //数据显示在ListView中,是通过适配器来通知listview
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //显示数据
            View view = View.inflate(getApplicationContext(), R.layout.item_friends_listview, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_friends_item_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_friends_item_phone);
            ContactBean bean = datas.get(position);
            tv_name.setText(bean.getName());
            tv_phone.setText(bean.getPhone());
            return view;
        }
    }

    private void initData() {
        /*
        获取数据，本地数据或网络数据
        子线程来访问数据
         */
        new Thread() {
            @Override
            public void run() {
                //显示获取数据的进度
                Message msg = Message.obtain();
                msg.what = LOADING;
                handler.sendMessage(msg);

                //仅用来测试，休眠2秒
                SystemClock.sleep(2000);
                //获取数据
                datas = ReadContactsEngine.readContacts(getApplicationContext());
                //数据获取完成,发送数据加载完成的消息
                msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }
}

package com.wp.mobileguard.mobileguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wp.mobileguard.R;
import com.wp.mobileguard.mobileguard.dao.BlackDao;
import com.wp.mobileguard.mobileguard.domain.BlackBean;
import com.wp.mobileguard.mobileguard.domain.BlackTable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 通讯卫视数据的处理，短信和电话
 * 在当前的界面中用一个FrameLayout包含了三个组件。通过设置<br/>
 * view属性来做到分别控制。
 * Created by wp 2016/4/12.
 *
 */
public class TelSmsSafeActivity extends Activity {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ListView lv_safenumbers;
    private Button bt_addSafeNumber;
    private TextView tv_nodata;
    ProgressBar pb_loading;
    private BlackDao blackDao;
    private MyAdapter adapter;
    //动态加载数据的临时容器
    private List<BlackBean> moreDatas;

    private final int MOREDATASCOUNTS = 20;//分批加载的数据个数

    private List<BlackBean> datas = new ArrayList<>();//存放黑名单数据的容器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化界面
        initData();//初始化数据
        initEvent();//初始化事件
    }

    /**
     * 给每个组件设置事件
     */
    private void initEvent() {
        //给ListView设置滑动事件
        lv_safenumbers.setOnScrollListener(new AbsListView.OnScrollListener() {
            /*
             * 状态改变调用此方法
             * SCROLL_STATE_FLING
             *      惯性滑动
             * SCROLL_STATE_IDLE
             *      静止滑动
             * SCROLL_STATE_TOUCH_SCROLL
             *      按住滑动
             * 三种状态，每种状态改变都会触发此方法
             * @param view
             * @param scrollState
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //监控静止状态SCROLL_STATE_IDLE
                //当出现SCROLL_STATE_IDLE的状态时候，判断是否显示最后一条数据，如果显示最后一条数据，
                //那就加载更多的数据
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //当出现SCROLL_STATE_IDLE的状态时候

                    //判断是否显示最后一条数据，如果显示最后一条数据，那就加载更多的数据，
                    //获取最后显示的数据位置
                    int lastVisiblePosition = lv_safenumbers.getLastVisiblePosition();

                    if (lastVisiblePosition == datas.size()-1){//最后显示的位置是最后一条数据
                        //加载更多的数据
                        initData();
                    }
                }
            }

            /*
             *按住滑动时调用此方法
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING://正在加载黑名单数据
                    //显示加载数据的进度
                    pb_loading.setVisibility(View.VISIBLE);
                    //隐藏ListView
                    lv_safenumbers.setVisibility(View.GONE);
                    //隐藏没有数据
                    tv_nodata.setVisibility(View.GONE);
                    break;
                case FINISH://数据加载完成
                    //判断是否有数据
                    //有数据
                    if (moreDatas.size() != 0) {
                        lv_safenumbers.setVisibility(View.VISIBLE);
                        tv_nodata.setVisibility(View.GONE);
                        pb_loading.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();//通知更新数据
                    } else {
                        if(datas.size()!=0){
                            Toast.makeText(getApplicationContext(),"没有更多数据",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //没有数据
                        lv_safenumbers.setVisibility(View.GONE);
                        tv_nodata.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    /*20160907修改*/
    private void initData() {
        //从db中取黑名单数据，从子线程中取
        new Thread() {
            @Override
            public void run() {
                //取数据之前，发个消息显示正在加载的进度条
                handler.obtainMessage(LOADING).sendToTarget();
                //取数据
                //SystemClock.sleep(2000);
                //获取分批加载的数据
                moreDatas = blackDao.getMoreDatas(MOREDATASCOUNTS,datas.size());

                datas.addAll(moreDatas);
                //取数据完成，发消息通知取数据完成
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_telsmssafe);
        //显示安全号码
        lv_safenumbers = (ListView) findViewById(R.id.lv_telsms_safenumbers);
        bt_addSafeNumber = (Button) findViewById(R.id.bt_telsms_addsafenumber);
        tv_nodata = (TextView) findViewById(R.id.tv_telsms_nodata);//没有数据显示的文本
        pb_loading = (ProgressBar) findViewById(R.id.pb_telsms_loading);

        //输入的跳转页
        EditText et_gotoPage = (EditText) findViewById(R.id.et_telsms_gotopage);

        //总页数
        TextView tv_totalPages = (TextView) findViewById(R.id.tv_telsms_totalpages);



        //黑名单业务对象
        blackDao = new BlackDao(getApplicationContext());
        adapter = new MyAdapter();//黑名单的适配器
        /*
        首先调用adapter的getCount()方法来获取多少条数据，如果为0，不显示任何数据，
        否则调用getView()方法
         */
        lv_safenumbers.setAdapter(adapter);
    }


    /**
     * 界面封装类
     */
    private class ItemView {
        //显示黑名单号码
        TextView tv_phone;
        //显示黑名单号码拦截模式
        TextView tv_model;
        //删除黑名单数据的按钮
        ImageView iv_delete;
    }

    /**
     * 黑名单数据的适配器
     */
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
            ItemView itemView = null;//组件封装类
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_telsmssafe_listview, null);
                itemView = new ItemView();
                //显示黑名单号码
                itemView.tv_phone = (TextView) convertView.findViewById(R.id.tv_telsmssafe_listview_item_number);
                //显示黑名单号码拦截模式
                itemView.tv_model = (TextView) convertView.findViewById(R.id.tv_telsmssafe_listview_item_model);
                //删除黑名单数据的按钮
                itemView.iv_delete = (ImageView) convertView.findViewById(R.id.iv_telsmssafe_listview_item_delete);
                convertView.setTag(itemView);
            } else {//存在缓存
                itemView = (ItemView) convertView.getTag();
            }

            //获取当前位置的数据
            BlackBean bean = datas.get(position);
            itemView.tv_phone.setText(bean.getPhone());
            switch (bean.getMode()) {
                case BlackTable.SMS:
                    itemView.tv_model.setText("短信拦截");
                    break;
                case BlackTable.TEL:
                    itemView.tv_model.setText("电话拦截");
                    break;
                case BlackTable.ALL:
                    itemView.tv_model.setText("全部拦截");
                    break;
            }
            return convertView;
        }

    }

}

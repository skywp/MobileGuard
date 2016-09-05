package com.wp.mobileguard.mobileguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 通讯卫士数据的处理，短信和电话
 * 在当前的界面中用一个FrameLayout包含了三个组件。通过设置<br/>
 * view属性来做到分别控制。
 * Created by WangPeng on 2016/4/12.
 */
public class TelSmsSafeActivityPage extends Activity {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ListView lv_safenumbers;
    private Button bt_addSafeNumber;
    private TextView tv_nodata;
    private ProgressBar pb_loading;
    private BlackDao blackDao;
    private MyAdapter adapter;
    private EditText et_gotoPage;
    private TextView tv_totalPages;

    private int totalPages;//总页数
    private int currentPage = 1;//当前页的数据,默认值是0
    private static final int perPage = 20;//每页显示20条数据

    private List<BlackBean> datas = new ArrayList<>();//存放黑名单数据的容器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化界面
        initData();//初始化数据
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
                    if (datas.size() != 0) {
                        lv_safenumbers.setVisibility(View.VISIBLE);
                        tv_nodata.setVisibility(View.GONE);//隐藏没有数据
                        pb_loading.setVisibility(View.GONE);//隐藏加载数据的进度
                        adapter.notifyDataSetChanged();//通知更新数据
                        //初始化总页数和当前页的值
                        tv_totalPages.setText(currentPage+"/"+totalPages);
                    } else {
                        //没有数据
                        lv_safenumbers.setVisibility(View.GONE);
                        tv_nodata.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    private void initData() {
        //从db中取黑名单数据，从子线程中取
        new Thread() {
            @Override
            public void run() {
                //取数据之前，发个消息显示正在加载的进度条
                handler.obtainMessage(LOADING).sendToTarget();
                //取数据
                //SystemClock.sleep(2000);

                //获取当前的数据currentPage当前页，perPage每页数据个数
                datas = blackDao.getPageDatas(currentPage,perPage);

                totalPages = blackDao.getTotalPages(perPage);//获取总页数

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
        et_gotoPage = (EditText) findViewById(R.id.et_telsms_gotopage);
        //总页数的信息
        tv_totalPages = (TextView) findViewById(R.id.tv_telsms_totalpages);

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

    /**
     * 下一页
     * @param view
     */
    public void nextPage(View view){
        //10页，最后一页，再点击下页：1.给用户提醒最后一页，2.回到第一页
        currentPage++;//下一页
        //处理越界
        currentPage = currentPage % totalPages;
        //取当前页的数据
        initData();
    }

    /**
     * 上一页
     * @param view
     */
    public void prevPage(View view){
        //10页，第一页，再点击上页：1.给用户提醒第一页，2.回到尾页
        currentPage--;
        if (currentPage == 0){//到第一页
            currentPage = totalPages;
        }
        initData();
    }

    /**
     * 跳转页
     * @param view
     */
    public void jumpPage(View view){
        //获取跳转页的页码
        String jumpPageStr = et_gotoPage.getText().toString().trim();
        if (TextUtils.isEmpty(jumpPageStr)){
            Toast.makeText(getApplicationContext(),"跳转页不能空",Toast.LENGTH_SHORT).show();
            return;
        }
        //将字符串的页码转成整数
        int jumpPage = Integer.parseInt(jumpPageStr);
        if (jumpPage >= 1 && jumpPage <= totalPages){
            //把跳转页设置给当前页
            currentPage = jumpPage;
            initData();//初始数据
        } else {
            Toast.makeText(getApplicationContext(),"亲，请按套路出牌！",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 尾页
     * @param view
     */
    public void endPage(View view){
        currentPage = totalPages;
        initData();
    }

}

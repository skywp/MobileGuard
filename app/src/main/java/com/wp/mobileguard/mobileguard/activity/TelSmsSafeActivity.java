package com.wp.mobileguard.mobileguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
    private AlertDialog dialog;

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
        public View getView(final int position, View convertView, ViewGroup parent) {
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
            final BlackBean bean = datas.get(position);
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
            //设置删除数据的事件20161003
            itemView.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(TelSmsSafeActivity.this);
                    ab.setTitle("注意");
                    ab.setMessage("是否真的删除该数据?");
                    ab.setPositiveButton("真删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //从数据库中删除当前数据
                            blackDao.delete(bean.getPhone());//取出当前行数据里的黑名单号码

                            //删除容器中对应的数据
                            datas.remove(position);

                            //通知界面更新数据，让用户看到删除数据不存在
                            adapter.notifyDataSetChanged();//让listView重新显示当前位置的数据
                        }
                    });
                    ab.setNegativeButton("点错了",null);//自动关闭对话框
                    ab.show();
                }
            });
            return convertView;
        }

    }

    /**
     * 添加黑名单号码
     * @param v
     */
    public void addBlackNumber(View v){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(),R.layout.dialog_add_blacknumber,null);
        //黑名单号码编辑框
        final EditText et_blackNumber = (EditText) view.findViewById(R.id.et_telsmssafe_blacknumber);
        //短信拦截复选框
        final  CheckBox cb_sms = (CheckBox) view.findViewById(R.id.cb_telsmssafe_smsmode);
        //电话拦截复选框
        final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_telsmssafe_phonemode);
        //添加黑名单号码按钮
        Button bt_add = (Button) view.findViewById(R.id.bt_telsmssafe_add);
        //取消黑名单号码按钮
        Button bt_cancel = (Button) view.findViewById(R.id.bt_telsmssafe_cancel);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加黑名单数据
                String phone = et_blackNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(getApplicationContext(),"黑名单号码不能为空",Toast.LENGTH_SHORT).show();
                }

                if (!cb_phone.isChecked() && !cb_sms.isChecked()){
                    //两个拦截都没有选择
                    Toast.makeText(getApplicationContext(),"至少选择一种拦截模式",Toast.LENGTH_SHORT).show();
                    return;
                }

                int mode = 0;
                if(cb_phone.isChecked()){
                    mode |= BlackTable.TEL;//设置电话拦截模式
                }
                if(cb_sms.isChecked()){
                    mode |= BlackTable.SMS;//设置短信拦截模式
                }

                //界面看到用户添加的数据
                BlackBean bean = new BlackBean();
                bean.setMode(mode);
                bean.setPhone(phone);

                blackDao.add(bean);//添加数据到黑名单表中
                datas.remove(bean);//该删除方法要靠equals和hashCode两个方法共同判断
                datas.add(0,bean);//添加数据到List容器中

                //让listview显示第一条数据
                //lv_safenumbers.setSelection(0);
                adapter = new MyAdapter();
                lv_safenumbers.setAdapter(adapter);
                dialog.dismiss();
            }
        });
        ab.setView(view);

        //创建对话框
        dialog = ab.create();
        dialog.show();

    }
}

package com.wp.mobileguard.mobileguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wp.mobileguard.R;
import com.wp.mobileguard.mobileguard.utils.Md5Utils;
import com.wp.mobileguard.mobileguard.utils.MyConstant;
import com.wp.mobileguard.mobileguard.utils.SpTools;

import static com.wp.mobileguard.R.id.gv_home_menus;

/**
 * Created by WangPeng on 2016/3/22.
 * 主界面
 */
public class HomeActivity extends Activity {
    private GridView gv_menus;
    private MyAdapter adapter;
    private AlertDialog dialog;
    private int icons[] = {R.drawable.safe, R.drawable.callmsgsafe, R.drawable.taskmanager,
            R.drawable.netmanager, R.drawable.app, R.drawable.sysoptimize, R.drawable.atools,
            R.drawable.settings,
            R.drawable.atools};
    private String names[] = {"手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计", "病毒查杀",
            "缓存清理", "高级工具", "设置中心"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化界面
        initData();//给GridView设置数据
        initEvent();//初始化事件
    }

    //根据Activity的生命周期可知
    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();//通知GridView重新取数据
        super.onResume();
    }

    /**
     * 初始化组件的事件
     */
    private void initEvent() {
        gv_menus.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断点击的位置
                switch (position) {
                    case 1://通讯卫士
                        Intent intent = new Intent(HomeActivity.this,TelSmsSafeActivityPage.class);
                        startActivity(intent);
                        break;
                    case 8://设置中心
                        Intent setting = new Intent(HomeActivity.this,SettingCenterActivity.class);
                        startActivity(setting);
                        break;
                    case 0://手机防盗
                        //自定义对话框-设置密码对话框
                        //应做一个判断，没有设置密码，弹出设置密码的对话框
                        //如果设置密码，登录的对话框
                        //从sp中去对应password的密码信息
                        if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstant.PASSWORD, ""))) {
                            //没有设置过密码
                            //设置密码对话框
                            showSettingPassDialog();
                        } else {//设置过密码
                            //输入密码的对话框
                            showEnterPassDialog();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 显示自定义输入密码的对话框
     */
    private void showEnterPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(),R.layout.dialog_enter_password,null);
        final EditText et_passone = (EditText) view.findViewById(R.id.et_dialog_enter_password_passone);
        Button bt_setpass = (Button) view.findViewById(R.id.bt_dialog_enter_password_login);
        Button bt_cancelpass = (Button) view.findViewById(R.id.bt_dialog_enter_password_cancel);
        builder.setView(view);
        bt_setpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置密码
                String passone = et_passone.getText().toString().trim();
                if (TextUtils.isEmpty(passone)) {
                    Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }  else {
                    //密码判断 md5 1次加密
                    passone = Md5Utils.md5(passone);
                    //读取sp中保存的密文进行判断
                    if (passone.equals(SpTools.getString(getApplicationContext(),MyConstant.PASSWORD,""))){
                        //密码一致-进入手机防盗界面
                        Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
                        startActivity(intent);
                        //Toast.makeText(getApplicationContext(),"密码正确",Toast.LENGTH_SHORT).show();
                    } else {
                        //密码不一致
                        Toast.makeText(getApplicationContext(),"密码不正确",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dialog.dismiss();
                }
            }
        });
        bt_cancelpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//关闭对话框
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 显示设置密码的对话框
     */
    private void showSettingPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(),R.layout.dialog_setting_password,null);
        final EditText et_passone = (EditText) view.findViewById(R.id.et_dialog_setting_password_passone);
        final EditText et_passtwo = (EditText) view.findViewById(R.id.et_dialog_setting_password_passtwo);
        Button bt_setpass = (Button) view.findViewById(R.id.bt_dialog_setting_password_setpass);
        Button bt_cancelpass = (Button) view.findViewById(R.id.bt_dialog_setting_password_cancelpass);
        builder.setView(view);
        bt_setpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置密码
                String passone = et_passone.getText().toString().trim();
                String passtwo = et_passtwo.getText().toString().trim();
                if (TextUtils.isEmpty(passone) || TextUtils.isEmpty(passtwo)) {
                    Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                } else if (!passone.equals(passtwo)) {//密码不一致
                    Toast.makeText(getApplicationContext(),"前后两次输入的密码不一致",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //保存密码 保存到SharedPreferences中
                    //进行加密处理
                    passone = Md5Utils.md5(passone);
                    SpTools.putString(getApplicationContext(), MyConstant.PASSWORD,passone);
                    Toast.makeText(getApplicationContext(),"密码设置成功！",Toast.LENGTH_SHORT).show();
                    System.out.println("保存密码");
                    dialog.dismiss();
                }
            }
        });
        bt_cancelpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//关闭对话框
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return icons.length;//返回坐标的个数
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(),R.layout.item_home_gridview,null);
            //获取组件
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_item_home_gv_icon);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_item_home_gv_name);
            //设置数据
            //设置图片
            iv_icon.setImageResource(icons[position]);
            //设置文字
            tv_name.setText(names[position]);

            if(position==0){
                //判断是否存在新的手机防盗名
                if (!TextUtils.isEmpty(SpTools.getString(getApplicationContext(),
                        MyConstant.LOSTFINDNAME, ""))){
                    //有新的手机防盗名字
                    tv_name.setText(SpTools.getString(getApplicationContext(),
                            MyConstant.LOSTFINDNAME, ""));
                }
            }
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
    /**
     * 初始化组件的数据
     */
    private void initData() {
        adapter = new MyAdapter();
        gv_menus.setAdapter(adapter);//设置gridView适配器数据
    }

    private void initView() {
        setContentView(R.layout.activity_home);
        gv_menus = (GridView) findViewById(gv_home_menus);
    }
}

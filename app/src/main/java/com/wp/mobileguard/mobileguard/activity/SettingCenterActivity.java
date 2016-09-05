package com.wp.mobileguard.mobileguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wp.mobileguard.R;
import com.wp.mobileguard.mobileguard.utils.MyConstant;
import com.wp.mobileguard.mobileguard.utils.SpTools;
import com.wp.mobileguard.mobileguard.view.SettingCenterItemView;

/**
 *
 * Created by WangPeng on 2016/4/7.
 *
 */
public class SettingCenterActivity extends Activity {
    private SettingCenterItemView sciv_autoupdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化界面
        initEvent();//初始化组件的事件
        initData();
    }

    private void initData() {
        //初始化自动更新复选框的初始值
        sciv_autoupdate.setChecked(SpTools.getBoolean(getApplicationContext(),MyConstant.AUTOUPDATE,false));
    }

    private void initEvent() {
//        //给黑名单item加的点击事件
//        sciv_autoupdate.setBlackItemClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"黑名单选项",Toast.LENGTH_SHORT).show();
//            }
//        });
        sciv_autoupdate.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //原来的复选框状态的功能不变
                sciv_autoupdate.setChecked(!sciv_autoupdate.isChecked());
                //添加新的功能
                //如果复选框选中，自动更新已经开启，否则不开启
                //记录复选框的状态
                SpTools.putBoolean(getApplicationContext(), MyConstant.AUTOUPDATE,sciv_autoupdate.isChecked());
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_setting_center);
        //获取自动更新的自定义view
        sciv_autoupdate = (SettingCenterItemView) findViewById(R.id.sciv_setting_center_autoupdate);
    }
}

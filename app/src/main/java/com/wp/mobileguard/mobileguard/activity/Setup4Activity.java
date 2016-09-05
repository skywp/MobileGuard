package com.wp.mobileguard.mobileguard.activity;


import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.wp.mobileguard.R;
import com.wp.mobileguard.mobileguard.service.LostFindService;
import com.wp.mobileguard.mobileguard.utils.MyConstant;
import com.wp.mobileguard.mobileguard.utils.ServiceUtils;
import com.wp.mobileguard.mobileguard.utils.SpTools;

/**
 * 第一个设置向导界面
 * Created by WangPeng on 2016/3/30.
 *
 *
 */
public class Setup4Activity extends BaseSetupActivity {
    private CheckBox cb_isProtected;

    public void initView() {
        setContentView(R.layout.activity_setup4);
        cb_isProtected = (CheckBox) findViewById(R.id.cb_setup4_is_protected);
        }

        @Override
        public void initData() {
            //初始化复选框的值 看服务是否开启
            //如果服务开启，打钩，否则不打勾
            if (ServiceUtils.isServiceRunning(getApplicationContext(),
                    "com.wp.mobileguard.mobileguard.service.LostFindService")){
                //服务在运行
                cb_isProtected.setChecked(true);
        } else {
            cb_isProtected.setChecked(false);//初始化复选框的状态
        }
        super.initData();
    }

    /*
         *初始化复选框的事件
         */
    @Override
    public void initEvent() {
        cb_isProtected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //如果选择打钩，开启防盗保护,动态开启防盗保护服务
                if (isChecked) {
                    //true，开启防盗保护
                    SpTools.putBoolean(getApplicationContext(),MyConstant.LOSTFIND,true);
                    Intent service = new Intent(Setup4Activity.this, LostFindService.class);
                    startService(service);
                } else {
                    SpTools.putBoolean(getApplicationContext(),MyConstant.LOSTFIND,false);
                    //关闭防盗保护
                    Intent service = new Intent(Setup4Activity.this, LostFindService.class);
                    stopService(service);
                }
            }
        });
        super.initEvent();
    }

    @Override
    public void nextActivity() {
        //跳转到手机防盗界面
        //保存设置完成的状态
        SpTools.putBoolean(getApplicationContext(), MyConstant.ISSETUP,true);
        startMyActivity(LostFindActivity.class);
    }

    @Override
    public void prevActivity() {
        //跳转到第三个设置界面
        startMyActivity(Setup3Activity.class);
    }
}

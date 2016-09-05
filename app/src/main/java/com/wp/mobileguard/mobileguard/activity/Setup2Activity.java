package com.wp.mobileguard.mobileguard.activity;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.wp.mobileguard.R;
import com.wp.mobileguard.mobileguard.utils.MyConstant;
import com.wp.mobileguard.mobileguard.utils.SpTools;

/**
 * 第一个设置向导界面
 * Created by WangPeng on 2016/3/30.
 * Email:wp20082009@foxmail.com
 */
public class Setup2Activity extends BaseSetupActivity {
    private Button bt_bind;
    private ImageView iv_isBind;
    public void initView() {
        setContentView(R.layout.activity_setup2);
        bt_bind = (Button) findViewById(R.id.bt_setup2_bindsim);
        iv_isBind = (ImageView) findViewById(R.id.iv_setup2_isbind);
        if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(),MyConstant.SIM,""))){
            //未绑定SIM卡
            iv_isBind.setImageResource(R.drawable.unlock);//设置解锁的图片
        } else {
            //已绑定SIM卡
            iv_isBind.setImageResource(R.drawable.lock9);//设置加锁的图片
        }
    }

    /**
     * 初始化组件的数据
     */
    @Override
    public void initData() {
        super.initData();
    }

    /**
     * 添加自己的事件
     */
    @Override
    public void initEvent() {
        bt_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//绑定和解绑操作
                if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(),MyConstant.SIM,""))){
                    //此时没有绑定sim卡，需要绑定
                    //绑定sim卡，存储sim卡信息
                    //获取sim卡信息
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    //sim卡信息
                    String simSerialNumber = telephonyManager.getSimSerialNumber();
                    //保存sim卡信息，保存到SP中
                    SpTools.putString(getApplicationContext(), MyConstant.SIM,simSerialNumber);
                    {
                        //切换是否绑定sim卡的图标
                        iv_isBind.setImageResource(R.drawable.lock9);//设置加锁的图片
                    }
                } else {//此时绑定sim卡-解绑操作
                    SpTools.putString(getApplicationContext(), MyConstant.SIM,"");
                    iv_isBind.setImageResource(R.drawable.unlock);//设置解锁的图片
                }
            }
        });
        super.initEvent();
    }


    @Override
    public void next(View v) {
        if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(),MyConstant.SIM,""))){
            Toast.makeText(getApplicationContext(),"请先绑定SIM卡",Toast.LENGTH_SHORT).show();
            return;//没有绑定sim卡就return，不跳到下一个Activity
        }
        super.next(v);//调用
    }

    /**
     * 进入下一个页面的逻辑处理
     */
    @Override
    public void nextActivity() {
        //跳转到第三个设置界面
        startMyActivity(Setup3Activity.class);
    }

    @Override
    public void prevActivity() {
        //跳转到第一个设置界面
        startMyActivity(Setup1Activity.class);
    }
}

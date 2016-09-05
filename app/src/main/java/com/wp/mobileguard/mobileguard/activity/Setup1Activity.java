package com.wp.mobileguard.mobileguard.activity;

import com.wp.mobileguard.R;

/**
 * 第一个设置向导界面
 * Created by WangPeng on 2016/3/30.
 *
 */
public class Setup1Activity extends BaseSetupActivity {

    public void initView() {
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void nextActivity() {
        //跳转到第二个设置界面
        startMyActivity(Setup2Activity.class);
    }

    @Override
    public void prevActivity() {

    }

}

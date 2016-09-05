package com.wp.mobileguard.mobileguard.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wp.mobileguard.R;
import com.wp.mobileguard.mobileguard.utils.EncryptTools;
import com.wp.mobileguard.mobileguard.utils.MyConstant;
import com.wp.mobileguard.mobileguard.utils.SpTools;

/**
 * 第一个设置向导界面
 * Created by WangPeng on 2016/3/30.
 *
 */
public class Setup3Activity extends BaseSetupActivity {

    private EditText et_safeNumber;//安全号码的编辑框
    public void initView() {
        setContentView(R.layout.activity_setup3);
        et_safeNumber = (EditText) findViewById(R.id.et_setup3_safenumber);
    }

    @Override
    public void initData() {
        String safeNumber = SpTools.getString(getApplicationContext(), MyConstant.SAFENUMBER, "");
        et_safeNumber.setText(EncryptTools.decryption(MyConstant.MUSIC,safeNumber));
        super.initData();
    }

    @Override
    public void nextActivity() {
        //跳转到第四个设置界面
        startMyActivity(Setup4Activity.class);
    }

    /**
     * 从手机联系人中获取安全号码
     * @param v
     */
    public void selectSafeNumber(View v){
        //弹出新的Activity显示所有好友的信息
        Intent friends = new Intent(this,FriendsActivity.class);
        startActivityForResult(friends, 1);//启动好友显示界面
        //
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            //用户选择数据来关闭联系人界面，而不是直接点击返回按钮
            //取数据
            String phone = data.getStringExtra(MyConstant.SAFENUMBER);
            //显示安全号码
            et_safeNumber.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void prevActivity() {
        //跳转到第二个设置界面
        startMyActivity(Setup2Activity.class);
    }

    @Override
    public void next(View v) {
        /*
        保存安全号码
        如果没有安全号码，不进行下一步的跳转
         */
        String safeNumber = et_safeNumber.getText().toString().trim();
        if (TextUtils.isEmpty(safeNumber)) {
            Toast.makeText(getApplicationContext(),"安全号码不能为空",Toast.LENGTH_SHORT).show();
            return;
        } else {
            //对安全号码加密
            safeNumber = EncryptTools.encrypt(MyConstant.MUSIC,safeNumber);
            //保存安全号码
            SpTools.putString(getApplicationContext(), MyConstant.SAFENUMBER,safeNumber);
        }
        super.next(v);
    }
}

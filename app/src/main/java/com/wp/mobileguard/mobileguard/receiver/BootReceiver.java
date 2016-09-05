package com.wp.mobileguard.mobileguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.wp.mobileguard.mobileguard.service.LostFindService;
import com.wp.mobileguard.mobileguard.utils.EncryptTools;
import com.wp.mobileguard.mobileguard.utils.MyConstant;
import com.wp.mobileguard.mobileguard.utils.SpTools;

/**
 * 开机启动的广播接收者
 * Created by WangPeng on 2016/4/3.
 * Email:wp20082009@foxmail.com
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //手机启动完成，检测SIM卡是否变化
        //获取原来保存的sim卡信息
        String oldSim = SpTools.getString(context, MyConstant.SIM,"");
        //获取当前手机的sim卡信息
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();
        //判断是否变化
        if (!oldSim.equals(simSerialNumber)){
            //sim卡发生变化，发送报警短信
            //取出安全号码，肯定有的
            String safeNumber = SpTools.getString(context,MyConstant.SAFENUMBER,"");
            safeNumber = EncryptTools.decryption(MyConstant.MUSIC,safeNumber);
            //发送短信给安全号码
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(safeNumber,"","我是小偷",null,null);
        }
        //自动启动防盗服务
        if (SpTools.getBoolean(context,MyConstant.LOSTFIND,false)){
            //true 开机启动防盗服务
            Intent service = new Intent(context, LostFindService.class);
            context.startService(service);
        }

    }
}

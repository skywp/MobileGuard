package com.wp.mobileguard.mobileguard.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;

import com.wp.mobileguard.R;

/**
 * Created by WangPeng on 2016/4/2.
 * Email:wp20082009@foxmail.com
 */
public class LostFindService extends Service{
    private SmsReceiver receiver;
    private boolean isPlay;//false 音乐播放的标记
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 短信的广播接收者
     */
    private class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //实现短信拦截的功能
            Bundle extras = intent.getExtras();
            Object datas[] = (Object[]) extras.get("pdus");

            for (Object data:datas){
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) data);
                //System.out.println(smsMessage.getMessageBody()+":"+ smsMessage.getDisplayOriginatingAddress());
                String mess = smsMessage.getMessageBody();//获取短信内容
                if (mess.equals("#*gps*#")){//获取定位信息
                    //耗时的定位，把定位的功能放在服务中执行
                    Intent service = new Intent(context,LocationService.class);
                    startService(service);//启动定位的服务
                    abortBroadcast();//终止广播
                } else if (mess.equals("#*lockscreen*#")){
                    DevicePolicyManager dpm= (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    dpm.resetPassword("123",0);//将其重新设置密码
                    //一键锁屏
                    dpm.lockNow();
                    abortBroadcast();//终止广播
                } else if (mess.equals("#*wipedata*#")) {//远程清除数据
                    DevicePolicyManager dpm= (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    //清除sd卡数据
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    abortBroadcast();//终止广播
                } else if (mess.equals("#*music*#")){//播放音乐
                    abortBroadcast();//终止广播
                    if (isPlay){
                        return;
                    }
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.error_sound);
                    mp.setVolume(1,1);//设置左右声道声音为最大值
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            //音乐播放完毕，触发此方法
                            isPlay = false;
                        }
                    });
                    isPlay = true;
                }
            }
        }
    }
    @Override
    public void onCreate() {
        //注册短信的监听广播
        receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);//级别一样，清单文件，谁先注册谁先执行，如果级别一样，代码比清单高
        //注册短信监听
        registerReceiver(receiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //取消注册短信的监听广播
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}

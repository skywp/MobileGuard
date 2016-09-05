package com.wp.mobileguard.mobileguard.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.wp.mobileguard.mobileguard.utils.EncryptTools;
import com.wp.mobileguard.mobileguard.utils.MyConstant;
import com.wp.mobileguard.mobileguard.utils.SpTools;

import java.util.List;

/**
 * 定位管理器，用来获取定位信息
 * Created by wp on 2016/4/3.
 *
 */
public class LocationService extends Service {
    private LocationManager locationManager;
    private LocationListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //获取定位管理器
        //定位 定位管理器
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float accuracy = location.getAccuracy();//精确度，以米为单位
                double altitude = location.getAltitude();//获取海拔高度
                double longitude = location.getLongitude();//获取经度
                double latitude = location.getLatitude();//获取纬度
                float speed = location.getSpeed();//速度

                StringBuilder tv_mess = new StringBuilder();
                tv_mess.append("accuracy:" + accuracy + "\n")
                        .append("altitude:" + altitude + "\n")
                        .append("longitude:" + longitude + "\n")
                        .append("latitude:" + latitude + "\n")
                        .append("speed:" + speed + "\n");
                //发送短信
                //发送短信给安全号码
                String safeNumber = SpTools.getString(LocationService.this, MyConstant.SAFENUMBER, "");
                safeNumber = EncryptTools.decryption(MyConstant.MUSIC,safeNumber);
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safeNumber, "", "我是小偷", null, null);
                //关闭GPS
                stopSelf();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //动态获取手机的最佳定位方式
        List<String> allProviders = locationManager.getAllProviders();
        for (String string: allProviders){
            System.out.println(string+"-->>定位方式");
        }
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);//是否产生费用
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//精准度

        String bestProvider = locationManager.getBestProvider(criteria,true);
        //注册监听回调
        locationManager.requestLocationUpdates(bestProvider,0,0,listener);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //取消定位的监听
        locationManager.removeUpdates(listener);
        locationManager = null;
        super.onDestroy();
    }
}

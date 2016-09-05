package com.wp.mobileguard.mobileguard.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 判断服务的状态
 * Created by wp on 2016/4/2.
 *
 */
public class ServiceUtils {
    /**
     *
     * @param context
     * @param serviceName
     *          service完整的名字：包名+类名
     * @return
     *      该service是否运行
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        boolean isRunning = false;
        /*
        判断运行中的服务状态
        Android中两个比较强的功能类：
        1.PackageManager，静态的信息（安装信息）
        2.ActivityManager动态信息（内存的使用，每个进程的状态）
         */
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取Android手机中运行的所有服务
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(50);
        for (ActivityManager.RunningServiceInfo runningServiceInfo:runningServices){
            //判断服务的名字是否包含我们指定的服务名
            if (runningServiceInfo.service.getClassName().equals(serviceName)){
                isRunning = true;
                break;
            }
            //System.out.println(runningServiceInfo.service.getClassName());
        }
        return isRunning;
    }
}

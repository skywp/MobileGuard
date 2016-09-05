package com.wp.mobileguard.mobileguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wp.mobileguard.R;
import com.wp.mobileguard.mobileguard.domain.UrlBean;
import com.wp.mobileguard.mobileguard.utils.MyConstant;
import com.wp.mobileguard.mobileguard.utils.SpTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;
import android.widget.Toast;

/**
 * 主要是做了检查并更新版本和软件的Splash界面
 *
 */
public class SplashActivity extends Activity {

    private static final int LOADMAIN = 1;//加载主界面
    private static final int SHOWUPDATEDIALOG = 2;//显示是否有更新的对话框
    private static final int ERROR = 3;//错误的统一代号
    private RelativeLayout rl_splash_root;
    private int versionCode;//自身的版本号
    private String versionName;//自身的版本名
    private TextView tv_versionName;//显示版本名组件
    private UrlBean parseJson;//url信息封装bean
    private long startTimeMillis;//记录开始访问网络的时间
    private ProgressBar pb_download;//下载进度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化界面
        initData();//初始化数据
        initAnimation();//动画



    }

    /**
     * 耗时的功能封装，只要耗时的处理，都放到此方法中
     */
    private void timeInitialization(){
        //一开始动画，就应该干耗时的业务（网络，本地数据初始化，数据的拷贝等）
        if (SpTools.getBoolean(getApplicationContext(),MyConstant.AUTOUPDATE,false)){
            //true 自动更新
            //检测服务器的版本
            checkVersion();
        }
        //增加自己的耗时功能处理

    }

    private void initData() {
        //获取自己的版本信息-获取清单文件的信息
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
            //设置TextView
            tv_versionName.setText(versionName);//实质还是1.0
        } catch (PackageManager.NameNotFoundException e) {
            //can not reach 异常不会发生
        }
    }

    /**
     * 访问服务器，获取最新的版本信息
     */
    private void checkVersion() {
        //访问服务器，获取数据url
        //在子线程中执行
        new Thread() {
            public void run() {
                BufferedReader reader = null;
                HttpURLConnection conn = null;
                int errorCode = -1;//正常，没有错误
                try {
                    startTimeMillis = System.currentTimeMillis();//毫秒显示当前的时间
                    URL url = new URL("http://192.168.56.1:8080/guardversion.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);//设置读取数据的超时时间
                    conn.setConnectTimeout(5000);//设置网络连接的超时设置
                    conn.setRequestMethod("GET");//设置请求方式
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        //获取字节流
                        InputStream is = conn.getInputStream();
                        //字节流转字符流用InputStreamReader
                        reader = new BufferedReader(new InputStreamReader(is));
                        String line = reader.readLine();//读取一行数据
                        StringBuilder jsonString = new StringBuilder();
                        while (line != null) {
                            jsonString.append(line);
                            //继续读取
                            line = reader.readLine();
                        }
                        //解析json数据，返回数据封装信息
                        parseJson = parseJson(jsonString);

                        //System.out.println(parseJson.getVersionCode() + "");

                    } else {
                        errorCode = 404;
                    }
                } catch (MalformedURLException e) {//4002
                    errorCode = 4002;
                    e.printStackTrace();
                } catch (IOException e) {//4001
                    //网络连接不成功
                    errorCode = 4001;
                    e.printStackTrace();
                } catch (JSONException e) {
                    //4003
                    errorCode = 4003;
                    e.printStackTrace();
                } finally {
//                    if (errorCode == -1) {
//                        isNewVersion(parseJson);
//                    } else {
//                        Message msg = Message.obtain();
//                        msg.what = ERROR;
//                        msg.arg1 = errorCode;
//                        handler.sendMessage(msg);//发送错误信息
//                    }
                    Message msg = Message.obtain();
                    if (errorCode == -1) {
                        //检测是否有新版本
                        msg.what = isNewVersion(parseJson);
                    } else {
                        msg.what = ERROR;
                        msg.arg1 = errorCode;
                    }
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTimeMillis < 3000) {
                        //时间不超过3秒，补足3秒
                        SystemClock.sleep(3000 - (endTime - startTimeMillis));
                    }
                    handler.sendMessage(msg);//发送信息
                    try {
                        if (reader == null || conn == null) {
                            return;
                        }
                        reader.close();//关闭输入流
                        conn.disconnect();//断开连接
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //处理消息
            switch (msg.what) {
                case LOADMAIN://加载主界面
                    loadMain();
                    break;
                case ERROR:
                    switch (msg.arg1) {
                        case 404://资源找不到
                            Toast.makeText(getApplicationContext(), "404资源找不到", Toast.LENGTH_SHORT).show();
                            break;
                        case 4001://找不到网络
                            Toast.makeText(getApplicationContext(), "4001没有网络", Toast.LENGTH_SHORT).show();
                            break;
                        case 4003://json格式错误
                            Toast.makeText(getApplicationContext(), "4003json格式错误", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                    loadMain();//都要进入主界面
                    break;
                case SHOWUPDATEDIALOG:
                    showUpdateDialog();
                    break;
                default:
                    break;
            }
        }
    };

    private void loadMain() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);//进入主界面
        //启动界面后关闭自己，以免出现点击回退键之后又回到splash界面
        finish();
    }

    /**
     * 显示是否更新新版本的对话框
     */
    private void showUpdateDialog() {
        //对话框的上下文是Activity的class，AlertDialog是Activity的一部分
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //让用户禁用取消操作
        //builder.setCancelable(false);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //取消事件的处理
                //进入主界面
                loadMain();
            }
        })
                .setTitle("提醒")
                .setMessage("是否更新新版本？新版本具有如下特性：" + parseJson.getDesc())
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //更新apk
                        System.out.println("更新主apk");
                        //访问网络，下载新的apk
                        downLoadNewApk();//创建新的版本
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //进入主界面
                        loadMain();
                    }
                });
        builder.show();
    }

    /**
     * xUtils
     * 新版本的下载和安装
     */
    protected void downLoadNewApk() {
        HttpUtils utils = new HttpUtils();
        //target 本地路径
        //先删除旧版本的apk
        File file = new File("/mnt/sdcard/MobileGuard.apk");
        file.delete();
        utils.download(parseJson.getUrl(), "/mnt/sdcard/MobileGuard.apk", new RequestCallBack<File>() {
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                pb_download.setVisibility(View.VISIBLE);//设置进度条的显示
                pb_download.setMax((int) total);//设置进度条的最大值
                pb_download.setProgress((int) current);//设置当前进度
                super.onLoading(total, current, isUploading);

            }

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                //下载成功
                //在主线程中执行
                Toast.makeText(getApplicationContext(), "下载新版本成功", Toast.LENGTH_SHORT).show();
                //安装apk
                installAppk();//安装apk
                pb_download.setVisibility(View.GONE);//隐藏进度条
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //下载失败
                Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show();
                pb_download.setVisibility(View.GONE);//隐藏进度条
            }
        });
    }

    /**
     * 安装下载的新版本
     */
    protected void installAppk() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        String type = "application/vnd.android.package-archive";
        Uri data = Uri.fromFile(new File("/mnt/sdcard/MobileGuard.apk"));
        intent.setDataAndType(data, type);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果用户取消apk，那么直接进入主界面
        loadMain();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 在子线程中执行
     * 判断是否有新版本
     *
     * @param parseJson
     */
    protected int isNewVersion(UrlBean parseJson) {
        //获取服务器的版本
        int serverCode = parseJson.getVersionCode();

        if (serverCode == versionCode) {//版本一致
            //进入主界面
//            Message msg = Message.obtain();
//            msg.what = LOADMAIN;
//            handler.sendMessage(msg);
            return LOADMAIN;
        } else { //有新版本
            //弹出对话框，显示新版本的描述信息，让用户点击是否更新
//            Message msg = Message.obtain();
//            msg.what = SHOWUPDATEDIALOG;
//            handler.sendMessage(msg);
            return SHOWUPDATEDIALOG;
        }
    }

    /**
     * @param jsonString 从服务器获取的json数据
     *                   解析json数据
     * @return url信息封装的对象
     */
    protected UrlBean parseJson(StringBuilder jsonString) throws JSONException {
        UrlBean bean = new UrlBean();

        //把json字符串数据封装成json对象
        JSONObject jsonObject = new JSONObject(jsonString + "");
        int version = jsonObject.getInt("version");
        String apkPath = jsonObject.getString("url");
        String desc = jsonObject.getString("desc");
        bean.setDesc(desc);//描述信息
        bean.setUrl(apkPath);//新apk下载路径
        bean.setVersionCode(version);//新的版本号


        return bean;
    }

    /**
     * 界面的初始化<br/>
     * 组件初始化
     */
    private void initView() {
        setContentView(R.layout.activity_splash);
        rl_splash_root = (RelativeLayout) findViewById(R.id.rl_splash_root);
        tv_versionName = (TextView) findViewById(R.id.tv_splash_version_name);
        pb_download = (ProgressBar) findViewById(R.id.tv_splash_download_progress);
    }

    /**
     * 动画演示
     */
    private void initAnimation() {
        //创建Alpha动画
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        //设置动画播放的时间
        aa.setDuration(3000);
        //界面停留在动画结束的状态
        aa.setFillAfter(true);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                timeInitialization();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画播完进入主界面
                //loadMain();
                //判断是否进行应用程序的版本检测
                if (!SpTools.getBoolean(getApplicationContext(), MyConstant.AUTOUPDATE, false)) {
                    //不做版本检测，直接进入主界面
                    loadMain();
                } else {
                    //界面的衔接是由自动更新来完成的，在此不做处理
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rl_splash_root.startAnimation(aa);
    }
}

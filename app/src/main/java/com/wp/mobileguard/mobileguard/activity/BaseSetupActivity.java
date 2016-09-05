package com.wp.mobileguard.mobileguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import com.wp.mobileguard.R;

/**
 * Created by WangPeng on 2016/3/30.
 * Email:wp20082009@foxmail.com
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initGesture();//初始化手势识别器
        initData();//初始化组件的数据
        initEvent();//初始化组件的事件
    }

    public void initData() {

    }

    public void initEvent() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //绑定onTouch事件
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void initGesture() {
        //初始化手势识别器，要想手势识别器生效，绑定onTouch事件
        gestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
            /*
            * 覆盖此方法完成手势的切换效果
            * e1,按下的点
            * e2，松开屏幕的点
            * velocityX:x轴方向的速度
            * velocityY:y轴方向的速度
            * 华为手机默认是不支持动画的
            * */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //x轴方向的速度是否满足横向滑动的条件 pix/s
                if (velocityX > 200) {//速度大于400像素每秒
                    //可以完成滑动
                    float dx = e2.getX()-e1.getX();//x轴方向滑动的间距
                    if (Math.abs(dx) < 100) {
                        return false;//如果间距不符合，直接无效
                    }
                    if (dx <0){//从右向左滑动
                        next(null);//不是组件的事件调用
                    } else {//从左往右滑动
                        prev(null);
                    }
                }
                return true;
            }
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }


        });
    }

    public abstract void initView();

    /**
     * 下一个页面的事件处理
     * @param v
     */
    public void next(View v) {
        /*
        1.完成界面的切换
        2.完成动画的播放
         */
        nextActivity();
        nextAnimation();//界面之间的动画切换
    }

    public abstract void nextActivity();



    /**
     * 上一个页面的事件处理
     * @param v
     */
    public void prev(View v){
         /*
        1.完成界面的切换
        2.完成动画的播放
         */
        prevActivity();
        prevAnimation();//界面之间的动画切换
    }

    /**
     * 上一个界面进来的动画
     */
    private void prevAnimation() {
        overridePendingTransition(R.animator.prev_in,R.animator.prev_out);
    }

    /**
     * 下一个界面显示的动画
     */
    private void nextAnimation() {
        //第一个参数是进来的动画，第二个是出去的动画
        overridePendingTransition(R.animator.next_in,R.animator.next_out);
    }

    public abstract void prevActivity();

    /**
     * 共有的界面跳转封装
     * @param type
     */
    public void startMyActivity(Class type) {
        Intent next = new Intent(this,type);
        startActivity(next);
        finish();
    }
}

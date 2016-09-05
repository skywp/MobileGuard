package com.wp.mobileguard.mobileguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wp.mobileguard.R;
import com.wp.mobileguard.mobileguard.utils.MyConstant;
import com.wp.mobileguard.mobileguard.utils.SpTools;

/**
 * 手机防盗界面
 * Created by WangPeng on 2016/3/30.
 * Email:wp20082009@foxmail.com
 */
public class LostFindActivity extends Activity {
    private AlertDialog dialog;//修改手机防盗界面的Dialog
    private LinearLayout ll_bottom_menu;//防盗菜单底部的两个按钮
    private boolean isShowMenu = true;
    private View popupView;
    private PopupWindow popupWindow;
    private ScaleAnimation scaleAnimation;
    private RelativeLayout rl_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果第一次访问该界面，要先进入设置向导界面
        if (SpTools.getBoolean(getApplicationContext(), MyConstant.ISSETUP, false)) {
            //进入过设置向导界面-直接显示本界面
            initView();//手机防盗界面
            initPopupView();//初始化修改名字的界面
            initPopupWindow();//初始化弹出窗体
        } else {
            //进入设置向导界面
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }

    }

    private void initPopupWindow() {
        //弹出窗体
        popupWindow = new PopupWindow(popupView, -2, -2);
        popupWindow.setFocusable(true);//获取焦点
        //窗体显示的动画
        scaleAnimation = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0f);
        scaleAnimation.setDuration(1000);
    }

    private void initView() {
        setContentView(R.layout.activity_lostfind);
        ll_bottom_menu = (LinearLayout) findViewById(R.id.ll_lostfind_menu_bottom);
        //根布局
        rl_root = (RelativeLayout) findViewById(R.id.rl_lostfind_root);
    }

    /**
     * 重新进入设置向导界面
     *
     * @param view
     */
    public void enterSetup(View view) {
        Intent setup1 = new Intent(this, Setup1Activity.class);
        startActivity(setup1);
        finish();
    }

    /**
     * 创建菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 处理菜单事件
     *
     * @param featureId
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_modify_name:
                Toast.makeText(getApplicationContext(), "修改菜单名", Toast.LENGTH_SHORT).show();
                //弹出对话框，让用户输入新的手机防盗名
                showModifyNameDialog();
                break;
            case R.id.mn_test_menu:
                Toast.makeText(getApplicationContext(), "测试菜单", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * 修改手机防盗名的对话框
     */
    private void showModifyNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);//设置view
        dialog = builder.create();//创建对话框
        dialog.show();
    }

    public void initPopupView() {
        //对话框显示的界面
        popupView = View.inflate(getApplicationContext(), R.layout.dialog_modify_name, null);

        //处理界面事件
        final EditText et_name = (EditText) popupView.findViewById(R.id.et_dialog_lostfind_modify_name);
        Button bt_modify = (Button) popupView.findViewById(R.id.bt_dialog_lostfind_modify);
        Button bt_cancel = (Button) popupView.findViewById(R.id.bt_dialog_lostfind_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        bt_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取修改的名字
                String name = et_name.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "名字不允许为空！", Toast.LENGTH_SHORT).show();
                }
                //保存新名字到Sp中
                SpTools.putString(getApplicationContext(), MyConstant.LOSTFINDNAME, name);
                popupWindow.dismiss();//对话框消失
                Toast.makeText(getApplicationContext(), "名字修改成功！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //处理menu键的事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            } else {
//                int[] location = new int[2];
//                popupView.getLocationInWindow(location);
                //设置弹出窗体的背景
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupView.startAnimation(scaleAnimation);
                //设置弹出窗体显示的初始位置
                int height = getWindowManager().getDefaultDisplay().getHeight();
                int width = getWindowManager().getDefaultDisplay().getWidth();
                popupWindow.showAtLocation(rl_root, Gravity.LEFT | Gravity.TOP, width / 4,height / 4);
            }
        }
        /*if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (isShowMenu) {
                //显示菜单
                ll_bottom_menu.setVisibility(View.VISIBLE);
            } else {
                //不显示菜单
                ll_bottom_menu.setVisibility(View.GONE);
            }
            isShowMenu = !isShowMenu;//对其进行取反
        }*/
        return super.onKeyDown(keyCode, event);//不能设置为true，否则back键无法使用
    }
}

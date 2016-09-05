package com.wp.mobileguard.mobileguard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wp.mobileguard.R;


/**
 * 自定义组合控件
 * Created by wp on 2016/4/7.
 *
 */
public class SettingCenterItemView extends LinearLayout {
    private TextView tv_title;
    private TextView tv_content;
    private CheckBox cb_check;
    private String[] contents;
    private View view;
    private View blackitem;
    /**
     * 代码实例化调用该构造函数
     * @param context
     */
    public SettingCenterItemView(Context context) {
        super(context);
        initView();
    }

    /**
     * item根布局设置点击事件
     * @param listener
     */
    public void setItemClickListener(OnClickListener listener){
        //通过自定义组合控件，把事件传递给子组件
        view.setOnClickListener(listener);
    }

    /**
     * 黑名单item设置点击事件
     * @param listener
     */
    public void setBlackItemClickListener(OnClickListener listener){
        blackitem.setOnClickListener(listener);
    }

    /**
     * 设置view里的checkbox的状态
     * @param isChecked
     */
    public void setChecked(boolean isChecked){
        cb_check.setChecked(isChecked);
    }

    /**
     * 返回view里的checkbox的状态
     * @return
     */
    public boolean isChecked() {
        return cb_check.isChecked();
    }

    private void initEvent() {
        //item 相对布局
        /*view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_check.setChecked(!cb_check.isChecked());
            }
        });*/

        cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //设置选中的颜色为绿色
                    tv_content.setTextColor(Color.GREEN);
                    tv_content.setText(contents[1]);
                } else {
                    //设置未选中的颜色为红色
                    tv_content.setTextColor(Color.RED);
                    tv_content.setText(contents[0]);
                }
            }
        });
    }

    /**
     * 配置文件中，反射实例化设置属性参数
     *
     * @param context
     * @param attrs
     */
    public SettingCenterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initEvent();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.myAttrs);
        String title = typedArray.getString(R.styleable.myAttrs_titleName);
        String content = typedArray.getString(R.styleable.myAttrs_content);
        contents = content.split("-");
        tv_title.setText(title);
        //初始设置未选中的颜色为红色
        tv_content.setTextColor(Color.RED);
        tv_content.setText(contents[0]);
        //tv_content.setText(contents[0] + ":" + contents[1]);
    }

    public SettingCenterItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化LinearLayout的子组件
     */
    private void initView() {
        //给LinearLayout加子组件
        view = View.inflate(getContext(), R.layout.item_settingcenter_view, null);
        //显示标题
        tv_title = (TextView) view.findViewById(R.id.tv_settingcenter_autoupdate_title);
        //显示的内容
        tv_content = (TextView) view.findViewById(R.id.tv_settingcenter_autoupdate_content);
        cb_check = (CheckBox) view.findViewById(R.id.cb_settingcenter_autoupdate_checked);
        addView(view,0);//设置中心item

//        blackitem = View.inflate(getContext(),R.layout.item_telsmssafe_listview,null);
//        addView(blackitem,1);//黑名单item


    }

}

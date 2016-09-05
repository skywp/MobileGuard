package com.wp.mobileguard.mobileguard.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wp on 2016/4/13.
 */
public class BlackDB extends SQLiteOpenHelper {
    /**
     * 初始化版本信息
     * @param context
     */
    public BlackDB(Context context) {
        super(context, "black.db", null, 1);
    }

    //数据库初始化时
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table black_tb(_id integer primary key autoincrement," +
                "   phone text,mode integer)");
    }

    //当版本更新时用到的方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //清空数据
        db.execSQL("drop table black_tb");
    }
}

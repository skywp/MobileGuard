package com.wp.mobileguard.mobileguard.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wp.mobileguard.mobileguard.db.BlackDB;
import com.wp.mobileguard.mobileguard.domain.BlackBean;
import com.wp.mobileguard.mobileguard.domain.BlackTable;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单数据的业务封装类
 * Created by wp on 2016/4/13.
 *
 */
public class BlackDao {
    private BlackDB blackDB;

    public BlackDao(Context context) {
        this.blackDB = new BlackDB(context);
    }

    /**
     * @return 总数据个数
     */
    public int getTotalRows() {
        SQLiteDatabase db = blackDB.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(1) from " + BlackTable.BLACKTABLE, null);
        cursor.moveToNext();
        int totalRows = cursor.getInt(0);

        cursor.close();
        return totalRows;
    }

    /**
     * @param perPage 指定每页显示的数据量
     * @return 总页数
     */
    public int getTotalPages(int perPage) {
        int totalRows = getTotalRows();
        //ceil(6.1)=7.0 返回不小于该数的最小整数
        int totalPages = (int) Math.ceil(totalRows * 1.0 / perPage);
        return totalPages;
    }

    /**
     * @param perPage     每页显示多少条数据
     * @param currentPage 当前页的数据
     * @return 当前页的数据
     */
    public List<BlackBean> getPageDatas(int currentPage, int perPage) {
        List<BlackBean> datas = new ArrayList<>();
        SQLiteDatabase db = blackDB.getReadableDatabase();
        //获取black_tb的所有数据的游标
        Cursor cursor = db.rawQuery("select " + BlackTable.PHONE + "," + BlackTable.MODE + " from " +
                BlackTable.BLACKTABLE +" limit ?,? ", new String[]{((currentPage-1)*perPage)+"",perPage+""});
        while (cursor.moveToNext()) {
            //有数据，数据封装
            BlackBean bean = new BlackBean();
            //封装黑名单号码
            bean.setPhone(cursor.getString(0));
            //封装拦截模式
            bean.setMode(cursor.getInt(1));
            datas.add(bean);
        }
        cursor.close();
        db.close();
        return datas;
    }

    /**
     * 添加黑名单号码
     *
     * @param phone 黑名单号码
     * @param mode  拦截模式
     */
    public void add(String phone, int mode) {
        //获取黑名单数据库
        SQLiteDatabase db = blackDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BlackTable.PHONE, phone);//设置黑名单
        values.put(BlackTable.MODE, mode);//设置拦截模式

        //往黑名单中插入一条记录
        db.insert(BlackTable.BLACKTABLE, null, values);
        db.close();
    }

    /**
     * 添加黑名单
     *
     * @param bean 黑名单信息的封装bean
     */
    public void add(BlackBean bean) {
        add(bean.getPhone(), bean.getMode());
    }

    /**
     * @return 返回所有的黑名单数据
     */
    public List<BlackBean> getAllDatas() {
        List<BlackBean> datas = new ArrayList<>();
        SQLiteDatabase db = blackDB.getReadableDatabase();
        //获取black_tb的所有数据的游标
        Cursor cursor = db.rawQuery("select " + BlackTable.PHONE + "," + BlackTable.MODE + " from " +
                BlackTable.BLACKTABLE, null);
        while (cursor.moveToNext()) {
            //有数据，数据封装
            BlackBean bean = new BlackBean();
            //封装黑名单号码
            bean.setPhone(cursor.getString(0));
            //封装拦截模式
            bean.setMode(cursor.getInt(1));
            datas.add(bean);
        }
        cursor.close();
        db.close();
        return datas;
    }

    /**
     * 更新数据
     *
     * @param phone 更新手机号码
     * @param mode  更新拦截方式
     */
    public void update(String phone, int mode) {
        SQLiteDatabase db = this.blackDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BlackTable.MODE, mode);
        //根据号码更新新的模式
        db.update(BlackTable.BLACKTABLE, values, BlackTable.PHONE + "=?", new String[]{phone});
        db.close();//关闭数据库
    }

    /**
     * 删除黑名单号码
     *
     * @param phone 要删除的黑名单号码
     */
    public void delete(String phone) {
        SQLiteDatabase db = this.blackDB.getWritableDatabase();
        db.delete(BlackTable.BLACKTABLE, BlackTable.PHONE + "=?", new String[]{phone});
        db.close();
    }

}

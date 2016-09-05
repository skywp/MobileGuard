package com.wp.mobileguard.mobileguard.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.wp.mobileguard.mobileguard.domain.ContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 读取手机联系人的功能类
 * Created by wp on 2016/3/31.
 *
 */
public class ReadContactsEngine {
    /**
     * 读取手机联系人
     */
    public static List<ContactBean> readContacts(Context context){
        List<ContactBean> datas = new ArrayList<>();
        Uri uriContacts = Uri.parse("content://com.android.contacts/contacts");
        Uri uriDatas = Uri.parse("content://com.android.contacts/data");

        Cursor cursor=context.getContentResolver().query(uriContacts,new String[]{"_id"},null,null,null);
        while(cursor.moveToNext()){
            //好友信息的封装bean
            ContactBean bean = new ContactBean();
            String id = cursor.getString(0);
            Cursor cursor2 = context.getContentResolver().query(uriDatas,new String[]{"data1","mimetype"}," raw_contact_id = ?",new String[]{id},null);
            //循环每条数据信息都是一个好友的一部分信息
            while (cursor2.moveToNext()){
                String data = cursor2.getString(0);
                String mimeType = cursor2.getString(1);
                if (mimeType.equals("vnd.android.cursor.item/name")){
                    //System.out.println("第"+id+"个用户:名字:"+data);
                    bean.setName(data);
                } else if (mimeType.equals("vnd.android.cursor.item/phone_v2")){
                    //System.out.println("第"+id+"个用户:电话:"+data);
                    bean.setPhone(data);
                }
            }
            cursor2.close();
            datas.add(bean);//加一条好友信息
        }
        cursor.close();
        return datas;
    }
}

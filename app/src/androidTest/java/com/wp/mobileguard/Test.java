package com.wp.mobileguard;

import android.test.AndroidTestCase;

import com.wp.mobileguard.mobileguard.dao.BlackDao;
import com.wp.mobileguard.mobileguard.domain.BlackBean;
import com.wp.mobileguard.mobileguard.engine.ReadContactsEngine;
import com.wp.mobileguard.mobileguard.utils.EncryptTools;
import com.wp.mobileguard.mobileguard.utils.ServiceUtils;

import java.util.List;

/**
 * Created by WangPeng on 2016/3/22.
 * Email:wp20082009@foxmail.com
 */
public class Test extends AndroidTestCase{
    public void testReadContacts(){
        //获取虚拟的上下文
        ReadContactsEngine.readContacts(getContext());
    }
    public void testRunningService() {
        ServiceUtils.isServiceRunning(getContext(),"");
    }

    public void testEncrypt(){
        System.out.println(EncryptTools.encrypt(110,"123456"));
    }
    public void testDecryption(){
        System.out.println(EncryptTools.decryption(110, "_\\]Z[X"));
    }

    public void testAddBlackNumber(){
        BlackDao dao = new BlackDao(getContext());
        for (int i = 0;i<300;i++){
            dao.add("1234567"+i,1);
        }
    }

    public void testFindAllDatas(){
        BlackDao dao = new BlackDao(getContext());
        List<BlackBean> datas= dao.getAllDatas();
        System.out.println(datas);
    }
}

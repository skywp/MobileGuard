package com.wp.mobileguard.mobileguard.domain;

/**
 * 定义黑名单的常量
 * Created by WangPeng on 2016/4/13.
 * Email:wp20082009@foxmail.com
 */
public interface BlackTable {
    String PHONE = "phone";//黑名单号码
    String MODE = "mode";
    String BLACKTABLE = "black_tb";

    /**
     * 标记性常量，每一个标记只对应一个位为1
     */
    int SMS = 1 << 0;
    int TEL = 1 << 1;
    int ALL = SMS | TEL;
}

package com.wp.mobileguard.mobileguard.domain;

/**
 * 手机联系人的信息封装
 * Created by WangPeng on 2016/4/1.
 * Email:wp20082009@foxmail.com
 */
public class ContactBean {
    private String phone;
    private String name;

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }
}

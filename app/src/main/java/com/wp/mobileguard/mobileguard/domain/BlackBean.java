package com.wp.mobileguard.mobileguard.domain;

/**
 * 黑名单数据的封装类
 * Created by wp on 2016/4/13.
 *
 */
public class BlackBean {
    private String phone;
    private int mode;
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int hashCode(){
        return phone.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof BlackBean){
            BlackBean bean = (BlackBean) o;
            return phone.equals(bean.getPhone());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "BlackBean{" +
                "phone='" + phone + '\'' +
                ", mode=" + mode +
                '}';
    }
}

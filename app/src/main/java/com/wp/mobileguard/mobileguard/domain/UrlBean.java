package com.wp.mobileguard.mobileguard.domain;

/**
 * Created by wp on 2016/3/22.
 *
 */
public class UrlBean {
    private String url;//apk下载的路径
    private int versionCode;//版本号
    private String desc;//新版本的描述信息

    public int getVersionCode() {
        return versionCode;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

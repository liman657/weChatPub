package com.learn.message.request;

/**
 * author : liman
 * create time : 2018/9/5
 * QQ:657271181
 * e-mail:liman65727@sina.com
 *
 * 图片消息
 */
public class ImageMessage extends BaseMessage{

    private String PicUrl;

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }
}

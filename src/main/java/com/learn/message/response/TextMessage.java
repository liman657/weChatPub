package com.learn.message.response;

/**
 * author : liman
 * create time : 2018/9/5
 * QQ:657271181
 * e-mail:liman65727@sina.com
 *
 * 响应的文本消息
 */
public class TextMessage extends BaseMessage{

    private String Content;

    public String getContent() {
        return this.Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }
}

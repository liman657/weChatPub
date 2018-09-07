package com.learn.token;

/**
 * 描述accessToken
 */
public class AccessToken {

    private String accessToken;

    private int expireSeconds;

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpireSeconds() {
        return this.expireSeconds;
    }

    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}

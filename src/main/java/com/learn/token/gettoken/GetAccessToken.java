package com.learn.token.gettoken;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.learn.util.WeChatApiUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * author:liman
 * createtime:2018/9/12
 * mobile:15528212893
 * email:657271181@qq.com
 * <p>
 * 获取AccessToken，定时获取
 */
public class GetAccessToken implements Runnable{

    //获取access_token接口
    private static final String GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    //appId
    private static final String appId = "wx67d380625fc54a61";

    //appSecret
    private static final String appSecret = "daf984a43081e615348be9f7ae55f688";

    public static String getTokenUrl(String appId, String appSecret) {
        return String.format(GET_ACCESS_TOKEN, appId, appSecret);
    }



    /**
     * 获取access_token
     * @return
     */
//    public static String getAccessToken() {
//
//        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
//
//        scheduledExecutorService.scheduleAtFixedRate(new GetAccessToken(),0,10,TimeUnit.HOURS);
//
//
//    }

    @Override
    public void run() {
        try{
            TimeUnit.HOURS.sleep(1);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}


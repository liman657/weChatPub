package com.learn.web.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.learn.token.AccessToken;
import com.learn.token.AccessTokenInfo;
import com.learn.util.HttpHelperUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author : liman
 * create time : 2018/9/7
 * QQ:657271181
 * e-mail:liman65727@sina.com
 */
@WebServlet(name = "AccessTokenServlet",
        urlPatterns = {"/AccessTokenServlet"},
        loadOnStartup = 1,
        initParams = {
            @WebInitParam(name="appID",value="wx67d380625fc54a61"), @WebInitParam(name="appsecret",value="daf984a43081e615348be9f7ae55f688")
        })
public class AccessTokenServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("启动webservlet");
        super.init();

        final String appID = getInitParameter("appID");
        final String appsecret = getInitParameter("appsecret");

        //开启一个线程去获取AccessToken
        new Thread(new Runnable() {
            public void run() {
                while(true){
                    try {
                        AccessTokenInfo.accessToken = getAccessToken(appID,appsecret);

                        if(AccessTokenInfo.accessToken!=null){
                            Thread.sleep(7000*1000);
                        }else{
                            Thread.sleep(1000*3);
                        }
                    }catch (Exception e){
                        System.out.println("发生异常："+e.getMessage());
                        e.printStackTrace();

                        try{
                            Thread.sleep(1000*10);
                        }catch (Exception e1){
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private AccessToken getAccessToken(String appID,String appsecret){
        HttpHelperUtil httpHelperUtil = new HttpHelperUtil();

        String url = String.format(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appID,appsecret);

        String result = httpHelperUtil.getHttpResponse(url,"");

        System.out.println("获取到的accesstoken为："+result);

        JSONObject json = JSON.parseObject(result);
        AccessToken token = new AccessToken();
        token.setAccessToken(json.getString("access_token"));
        token.setExpireSeconds(json.getInteger("expires_in"));

        return token;
    }
}

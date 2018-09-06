package com.learn.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * author : liman
 * create time : 2018/9/5
 * QQ:657271181
 * e-mail:liman65727@sina.com
 *
 * 获取AccessToken
 */
public class AccessTokenUtil {

    /**
     * String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
     *
     *             + "wx0ab188e12389a8bd"+ "&secret=" + "a63c60a3dbef49b66da8a8bde413aaff";
     */

    private static String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";

    private static String grant_type = "client_credential";

    private static String appid="wx67d380625fc54a61";

    private static String appSecret="daf984a43081e615348be9f7ae55f688";

    public static String getTargetUrl(){
        StringBuffer url = new StringBuffer("");
        url.append(getTokenUrl).append("?grant_type=")
                .append(grant_type).append("&appid=")
                .append(appid).append("&secret=")
                .append(appSecret).append("\"");

        return url.toString();
    }


    public static String getAccessToken(){
        String accessToken = null;

        String url = getTargetUrl();

        try {

            URL urlGet = new URL(url);

            HttpURLConnection http = (HttpURLConnection) urlGet

                    .openConnection();

            http.setRequestMethod("GET"); // 必须是get方式请求

            http.setRequestProperty("Content-Type",

                    "application/x-www-form-urlencoded");

            http.setDoOutput(true);

            http.setDoInput(true);

            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒

            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒

            http.connect();

            InputStream is = http.getInputStream();

            int size = is.available();

            byte[] jsonBytes = new byte[size];

            is.read(jsonBytes);

            String message = new String(jsonBytes, "UTF-8");

//            JSONObject demoJson = JSONObject.fromObject(message);

//            accessToken = demoJson.getString("access_token");

            System.out.println(accessToken);

            is.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return accessToken;
    }
}

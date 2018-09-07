package com.learn.util;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * author : liman
 * create time : 2018/9/7
 * QQ:657271181
 * e-mail:liman65727@sina.com
 *
 * Http请求工具类
 */
public class HttpHelperUtil {

    /**
     * 发起HTTP请求
     * @param reqUrl 请求的url地址
     * @param requestMethod 请求的方法
     * @return 响应的字段
     */
    public String getHttpResponse(String reqUrl,String requestMethod){
        URL url = null;
        InputStream inputStream = null;
        String resultData = "";

        try {
            url = new URL(reqUrl);
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            TrustManager[] tm = {xtm};

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null,tm,null);

            connection.setSSLSocketFactory(ctx.getSocketFactory());
            connection.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            //允许输入流，允许下载？
            connection.setDoInput(true );
            //允许输出，
            connection.setDoOutput(true);
            //不使用缓存
            connection.setUseCaches(false);

            if(null!=requestMethod && !requestMethod.equals("")){
                connection.setRequestMethod(requestMethod);
            }else{
                connection.setRequestMethod("GET");
            }

            //获得输入流，这里才是真正建立连接
            inputStream = connection.getInputStream();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while((inputLine = bufferedReader.readLine())!=null){
                resultData += inputLine+"\n";
            }

            System.out.println(resultData);

        }catch (Exception e){
            e.printStackTrace();
        }
        return resultData;
    }

    private X509TrustManager xtm = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

}

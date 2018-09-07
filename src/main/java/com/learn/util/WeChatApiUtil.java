package com.learn.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * author : liman
 * create time : 2018/9/7
 * QQ:657271181
 * e-mail:liman65727@sina.com
 *
 * 微信在消息多媒体消息的时候，会先去获取access token，之后再响应消息。
 */
public class WeChatApiUtil {

    //获取access_token接口
    private static final String ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    //素材上传 post
    private static final String UPLOAD_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s";

    //素材下载 不支持视频文件的下载 get
    private static final String DOWNLAND_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";

    public static String getTokenUrl(String appId,String appSecret){
        return String.format(ACCESS_TOKEN,appId,appSecret);
    }

    public static String getDownlandUrl(String token,String mediaId){
        return String.format(DOWNLAND_MEDIA,token,mediaId);
    }

    /**
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public static String getToken(String appId,String appSecret){
        if(appId == null || appSecret == null){
            return null;
        }

        String token = null;
        String tokenUrl = getTokenUrl(appId,appSecret);
        String response = httpsRequestToString(tokenUrl,"GET","");

        JSONObject jsonObject = JSON.parseObject(response);
        if(null!=jsonObject){
            try{
                token = jsonObject.getString("access_token");
            }catch (Exception e){
                token = null;
            }
        }
        return token;
    }

    /**
     * 微信服务器素材上传
     * @param file 表单名称media
     * @param token access_token
     * @param type 支持四种类型，video/image/voice/thumb
     * @return
     */
    public static JSONObject uploadMedia(File file, String token, String type){
        if(file == null ||  token == null || type == null){
            return null;
        }

        if(!file.exists()){
            System.out.println("上传文件不存在，请检查");
            return null;
        }

        String url = UPLOAD_MEDIA;

        JSONObject jsonObject = null;

        PostMethod post = new PostMethod(url);
        post.setRequestHeader("Connection","Keep-Alive");
        post.setRequestHeader("Cache-Control","no-cache");
        FilePart media;

        HttpClient httpClient = new HttpClient();

        //信任任何类型的证书
        Protocol myhttps = new Protocol("https",new SSLProtocolSocketFactory(),443);
        Protocol.registerProtocol("https",myhttps);

        try{
            media = new FilePart("media",file);

            Part[] parts = new Part[]{
             new StringPart("access_token",token),new StringPart("type",type),media
            };

            MultipartRequestEntity entity = new MultipartRequestEntity(parts,post.getParams());
            post.setRequestEntity(entity);

            int status = httpClient.executeMethod(post);
            if(status == HttpStatus.SC_OK){
                String text = post.getResponseBodyAsString();
                jsonObject = JSONObject.parseObject(text);
            }else{
                System.out.println("upload media failture status is :"+status);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * HTTPS的方式发送请求，返回请求响应的字符串。
     * @param path
     * @param method
     * @param body
     * @return
     */
    public static String httpsRequestToString(String path ,String method,String body){
        if(path == null || method == null){
            return null;
        }

        String response = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        HttpsURLConnection conn = null;
        try{
            TrustManager[] tm = {new JEEWeiXinX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL","SunJSSE");
            sslContext.init(null,tm,new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            System.out.println(path);

            URL url = new URL(path);
            conn = (HttpsURLConnection)url.openConnection();

            conn.setSSLSocketFactory(sslSocketFactory);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(method);

            if(null!=body){
                //建立HTTPS连接
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(body.getBytes("UTF-8"));
                outputStream.close();
            }

            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
            bufferedReader  = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while((str = bufferedReader.readLine())!=null){
                buffer.append(str);
            }

            response = buffer.toString();
        }catch (Exception e){

        }finally{
            if(conn!=null){
                conn.disconnect();
            }try{
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            }catch (Exception e){

            }
        }

        return response;
    }


}

class JEEWeiXinX509TrustManager implements X509TrustManager{

    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}

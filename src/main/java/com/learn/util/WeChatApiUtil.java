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
import java.net.HttpURLConnection;
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
 * 微信在消息多媒体消息的时候，会先去获取access_token，之后再响应消息。
 */
public class WeChatApiUtil {

    //获取access_token接口
    private static final String GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    //素材上传 post
    private static final String UPLOAD_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s";

    //素材下载 不支持视频文件的下载，只能用get请求
    private static final String DOWNLOAD_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";

    //appId
    private static final String appId = "wx67d380625fc54a61";

    //appSecret
    private static final String appSecret="daf984a43081e615348be9f7ae55f688";

    //access_token
    private static String access_token=null;

    public static String getTokenUrl(String appId,String appSecret){
        return String.format(GET_ACCESS_TOKEN,appId,appSecret);
    }

    public static String getDownloadUrl(String token,String mediaId){
        return String.format(DOWNLOAD_MEDIA,token,mediaId);
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
        if(access_token==null){ //access_token为空的时候，会去请求，不为空的时候，直接返回
            String tokenUrl = getTokenUrl(appId,appSecret);
            String response = httpsRequestToString(tokenUrl,"GET","");
            JSONObject jsonObject = JSON.parseObject(response);
            if(null!=jsonObject){
                try{
                    access_token = jsonObject.getString("access_token");
                }catch (Exception e){
                    access_token = null;
                }
            }
        }
        return access_token;
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

        //http上传的文件
        FilePart media;

        HttpClient httpClient = new HttpClient();

        //信任任何类型的证书
        Protocol myhttps = new Protocol("https",new SSLProtocolSocketFactory(),443);
        Protocol.registerProtocol("https",myhttps);

        try{
            media = new FilePart("media",file);

            //传入进来的access_token需要上送到腾讯服务器
            Part[] parts = new Part[]{
             new StringPart("access_token",token),new StringPart("type",type),media
            };

            MultipartRequestEntity entity = new MultipartRequestEntity(parts,post.getParams());
            post.setRequestEntity(entity);

            int status = httpClient.executeMethod(post);
            if(status == HttpStatus.SC_OK){//文件上传成功
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
     * 多媒体下载接口
     *
     * @param fileName 素材存储文件路径
     * @param token    认证token
     * @param mediaId  素材ID（对应上传后获取到的ID）
     * @return 素材文件
     * @comment 不支持视频文件的下载
     */
    public static File downloadMedia(String fileName, String token,
                                     String mediaId) {
        //获取下载的url
        String url = getDownloadUrl(token, mediaId);
        return httpRequestToFile(fileName, url, "GET", null);
    }

    /**
     * 以http方式发送请求,并将请求响应内容输出到文件
     *
     * @param path   请求路径
     * @param method 请求方法
     * @param body   请求数据
     * @return 返回响应的存储到文件
     */
    public static File httpRequestToFile(String fileName, String path, String method, String body) {
        if (fileName == null || path == null || method == null) {
            return null;
        }

        File file = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        FileOutputStream fileOut = null;
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(method);
            if (null != body) {
                //这里才是真正的请求服务器文件
                System.out.println("开始请求服务器，下载文件");
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(body.getBytes("UTF-8"));
                outputStream.close();
            }

            inputStream = conn.getInputStream();
            if (inputStream != null) {
                file = new File(fileName);
            } else {
                return file;
            }

            //写入到文件
            fileOut = new FileOutputStream(file);
            if (fileOut != null) {
                int c = inputStream.read();
                while (c != -1) {
                    fileOut.write(c);
                    c = inputStream.read();
                }
            }
        } catch (Exception e) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

            /*
             * 必须关闭文件流
             * 否则JDK运行时，文件被占用其他进程无法访问
             */
            try {
                if(inputStream!=null){
                    inputStream.close();
                }

                if(fileOut!=null){
                    fileOut.close();
                }
            } catch (IOException execption) {
                execption.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 上传素材
     * @param filePath 媒体文件路径(绝对路径)
     * @param type 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
     * @return
     */
    public static JSONObject uploadMedia(String filePath,String type){
        File f = new File(filePath); // 获取本地文件
        if(!f.exists()){
            System.out.println("文件不存在");
            throw new RuntimeException("上传文件不存在！");
        }
        String token = WeChatApiUtil.getToken(appId, appSecret);
        JSONObject jsonObject = uploadMedia(f, token, type);
        return jsonObject;
    }



    /**
     * 多媒体下载接口
     *
     * @param fileName 素材存储文件路径
     * @param mediaId  素材ID（对应上传后获取到的ID）
     * @return 素材文件
     * @comment 不支持视频文件的下载
     */
    public static File downloadMedia(String fileName, String mediaId) {
        String appId = "wxbe4d433e857e8bb1";
        String appSecret = "ccbc82d560876711027b3d43a6f2ebda";
        String token = WeChatApiUtil.getToken(appId, appSecret);
        return downloadMedia(fileName,token,mediaId);
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
package com.learn.weChatPub;

import com.alibaba.fastjson.JSONObject;
import com.learn.util.WeChatApiUtil;
import org.junit.Test;

import java.io.File;

/**
 * author:liman
 * createtime:2018/9/11
 * mobile:15528212893
 * email:657271181@qq.com
 */
public class WeChatApiUtilTest {

    @Test
    public void testUploadMedia(){
        String  filePath = "E:\\weChat_Public\\weChatPub\\src\\main\\webapp\\media\\image\\picFight.jpg";
        String type = "image";
        JSONObject uploadResult = WeChatApiUtil.uploadMedia(filePath,type);
        System.out.println(uploadResult.toString());

        String mediaId = uploadResult.getString("media_id");

        System.out.println(mediaId);

//        File file = WeChatApiUtil.downloadMedia("E:\\"+mediaId+".jpg",mediaId);
//        System.out.println(file.getName());
    }

    @Test
    public void testUploadVoice(){
        String  filePath = "E:\\weChat_Public\\weChatPub\\src\\main\\webapp\\media\\voice\\voice.mp3";
        String type = "voice";
        JSONObject uploadResult = WeChatApiUtil.uploadMedia(filePath,type);
        System.out.println(uploadResult.toString());

        String mediaId = uploadResult.getString("media_id");

        System.out.println(mediaId);

//        File file = WeChatApiUtil.downloadMedia("E:\\"+mediaId+".jpg",mediaId);
    }

    @Test
    public void testUploadVideo(){
        String filePath = "E:\\weChat_Public\\weChatPub\\src\\main\\webapp\\media\\video\\littleApple.mp4";
        String type = "video";
        JSONObject uploadResult = WeChatApiUtil.uploadMedia(filePath,type);
        System.out.println(uploadResult.toString());

        String mediaId = uploadResult.getString("media_id");

        System.out.println(mediaId);

//        File file = WeChatApiUtil.downloadMedia("E:\\"+mediaId+".jpg",mediaId);
    }

}

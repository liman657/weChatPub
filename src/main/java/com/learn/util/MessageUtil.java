package com.learn.util;

import com.learn.message.MessageType;
import com.learn.message.response.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author : liman
 * create time : 2018/9/5
 * QQ:657271181
 * e-mail:liman65727@sina.com
 * <p>
 * 消息工具类
 */
public class MessageUtil {

    /**
     * 返回消息类型：文本
     */
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";

    /**
     * 返回消息类型：音乐
     */
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";

    /**
     * 返回消息类型：图文
     */
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";

    /**
     * 请求消息类型：文本
     */
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";

    /**
     * 请求消息类型：图片
     */
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

    /**
     * 请求消息类型：链接
     */
    public static final String REQ_MESSAGE_TYPE_LINK = "link";

    /**
     * 请求消息类型：地理位置
     */
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";

    /**
     * 请求消息类型：音频
     */
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";

    /**
     * 请求消息类型：推送
     */
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";

    /**
     * 请求消息类型：视频
     */
    public static final String REQ_MESSAGE_TYPE_VIDEO = "video";

    /**
     * 事件类型：subscribe(订阅)
     */
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

    /**
     * 事件类型：unsubscribe(取消订阅)
     */
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

    /**
     * 事件类型：CLICK(自定义菜单点击事件)
     */
    public static final String EVENT_TYPE_CLICK = "CLICK";

    /**
     * 解析微信发来的请求（XML）
     *
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();

        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList)
            map.put(e.getName(), e.getText());

        // 释放资源
        inputStream.close();

        return map;
    }

    /**
     * 文本消息对象转换成xml
     *
     * @param textMessage 文本消息对象
     * @return xml
     */
    public static String textMessageToXml(TextMessage textMessage) {
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }

    /**
     * 音乐消息对象转换成xml
     *
     * @param musicMessage 音乐消息对象
     * @return xml
     */
    public static String musicMessageToXml(MusicMessage musicMessage) {
        xstream.alias("xml", musicMessage.getClass());
        return xstream.toXML(musicMessage);
    }

    /**
     * 图文消息对象转换成xml
     *
     * @param newsMessage 图文消息对象
     * @return xml
     */
    public static String newsMessageToXml(NewsMessage newsMessage) {
        xstream.alias("xml", newsMessage.getClass());
        xstream.alias("item", new Article().getClass());
        return xstream.toXML(newsMessage);
    }

    /**
     * 扩展xstream，使其支持CDATA块
     *
     * @date 2013-05-19
     */
    private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;

                @SuppressWarnings("unchecked")
                public void startNode(String name) {
                    super.startNode(name);
                }

                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });

    /**
     * 根据消息类型构造返回消息
     *
     * @param map
     * @return
     */
    public static String buildResponseMessage(Map map) {
        //响应消息
        String responseMsg = "";
        //得到的消息类型
        String msgType = map.get("MsgType").toString();
        System.out.println("MsgType:" + msgType);
        //消息类型
        MessageType messageEnumType = MessageType.valueOf(MessageType.class, msgType.toUpperCase());

        switch (messageEnumType) {
            case TEXT:
                //处理文本消息
                responseMsg = handleTextMessage(map);
                break;
            case IMAGE:
                responseMsg = handleImageMessage(map);
                break;
            case VOICE:
                responseMsg = handleVoiceMessage(map);
                break;
            case VIDEO:
                responseMsg = handleVideoMessage(map);
                break;
            case SHORTVIDEO:
                responseMsg = handleShortVideoMessage(map);
                break;
            case LOCATION:
                responseMsg = handleLocationMessage(map);
                break;
            case LINK:
                responseMsg = handleLinkMessage(map);
                break;
            case EVENT:
                responseMsg = handleEventMessage(map);
                break;
            default:
                break;
        }

        return responseMsg;
    }

    /**
     * 关注时发送的消息
     *
     * @param map
     * @return
     */
    private static String buildWelcomeTextMessage(Map<String, String> map) {
        String responseMessageXml;
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");

        responseMessageXml = String.format("<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[text]]></MsgType>" +
                        "<Content><![CDATA[%s]]></Content>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(),
                "感谢您关注我的个人公众号，请回复如下关键词来使用公众号提供的服务：\n文本\n图片\n语音\n视频\n音乐\n图文");

        return responseMessageXml;
    }

    /**
     * 构建回复的文本消息
     *
     * @param map
     * @param content
     * @return
     */
    private static String buildTextMessage(Map<String, String> map, String content) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 文本消息XML数据格式
         * <xml>
         <ToUserName><![CDATA[toUser]]></ToUserName>
         <FromUserName><![CDATA[fromUser]]></FromUserName>
         <CreateTime>1348831860</CreateTime>
         <MsgType><![CDATA[text]]></MsgType>
         <Content><![CDATA[this is a test]]></Content>
         <MsgId>1234567890123456</MsgId>
         </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[text]]></MsgType>" +
                        "<Content><![CDATA[%s]]></Content>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), content);
    }

    /**
     * 构建回复的图片消息
     *
     * @param map
     * @param mediaId
     * @return
     */
    private static String buildImageMessage(Map<String, String> map, String mediaId) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 图片消息XML数据格式
         *<xml>
         <ToUserName><![CDATA[toUser]]></ToUserName>
         <FromUserName><![CDATA[fromUser]]></FromUserName>
         <CreateTime>12345678</CreateTime>
         <MsgType><![CDATA[image]]></MsgType>
         <Image>
         <MediaId><![CDATA[media_id]]></MediaId>
         </Image>
         </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[image]]></MsgType>" +
                        "<Image>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "</Image>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), mediaId);
    }

    /**
     * 构建回复的音乐消息
     *
     * @param map
     * @param music
     * @return
     */
    private static String buildMusicMessage(Map<String, String> map, Music music) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 音乐消息XML数据格式
         *<xml>
         <ToUserName><![CDATA[toUser]]></ToUserName>
         <FromUserName><![CDATA[fromUser]]></FromUserName>
         <CreateTime>12345678</CreateTime>
         <MsgType><![CDATA[music]]></MsgType>
         <Music>
         <Title><![CDATA[TITLE]]></Title>
         <Description><![CDATA[DESCRIPTION]]></Description>
         <MusicUrl><![CDATA[MUSIC_Url]]></MusicUrl>
         <HQMusicUrl><![CDATA[HQ_MUSIC_Url]]></HQMusicUrl>
         <ThumbMediaId><![CDATA[media_id]]></ThumbMediaId>
         </Music>
         </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[music]]></MsgType>" +
                        "<Music>" +
                        "   <Title><![CDATA[%s]]></Title>" +
                        "   <Description><![CDATA[%s]]></Description>" +
                        "   <MusicUrl><![CDATA[%s]]></MusicUrl>" +
                        "   <HQMusicUrl><![CDATA[%s]]></HQMusicUrl>" +
                        "</Music>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), music.title, music.description, music.musicUrl, music.hqMusicUrl);
    }

    /**
     * 构建返回的视频消息
     * @param map
     * @param video
     * @return
     */
    private static String buildVideoMessage(Map<String, String> map, Video video) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 音乐消息XML数据格式
         *<xml>
         <ToUserName><![CDATA[toUser]]></ToUserName>
         <FromUserName><![CDATA[fromUser]]></FromUserName>
         <CreateTime>12345678</CreateTime>
         <MsgType><![CDATA[video]]></MsgType>
         <Video>
         <MediaId><![CDATA[media_id]]></MediaId>
         <Title><![CDATA[title]]></Title>
         <Description><![CDATA[description]]></Description>
         </Video>
         </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[video]]></MsgType>" +
                        "<Video>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "   <Title><![CDATA[%s]]></Title>" +
                        "   <Description><![CDATA[%s]]></Description>" +
                        "</Video>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), video.mediaId, video.title, video.description);
    }

    /**
     * 构造语音消息
     * @param map 封装了解析结果的Map
     * @param mediaId 通过素材管理接口上传多媒体文件得到的id
     * @return 语音消息XML字符串
     */
    private static String buildVoiceMessage(Map<String, String> map, String mediaId) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 语音消息XML数据格式
         *<xml>
         <ToUserName><![CDATA[toUser]]></ToUserName>
         <FromUserName><![CDATA[fromUser]]></FromUserName>
         <CreateTime>12345678</CreateTime>
         <MsgType><![CDATA[voice]]></MsgType>
         <Voice>
         <MediaId><![CDATA[media_id]]></MediaId>
         </Voice>
         </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[voice]]></MsgType>" +
                        "<Voice>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "</Voice>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), mediaId);
    }

    /**
     * 构造图文消息
     * @param map 封装了解析结果的Map
     * @return 图文消息XML字符串
     */
    private static String buildNewsMessage(Map<String, String> map) {
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        NewsItem item = new NewsItem();
        item.Title = "spring boot 学习总结（一）——spring boot开发环境搭建";
        item.Description = "工欲善其事，必先利其器。要做微信公众号开发，那么要先准备好两样必不可少的东西：\n" +
                "\n" +
                "　　1、要有一个用来测试的公众号。\n" +
                "\n" +
                "　　2、用来调式代码的开发环境";
        item.PicUrl = "http://images2015.cnblogs.com/blog/289233/201601/289233-20160121164317343-2145023644.png";
        item.Url = "http://www.cnblogs.com/xdp-gacl/p/5149171.html";
        String itemContent1 = buildSingleItem(item);

        NewsItem item2 = new NewsItem();
        item2.Title = "微信开发学习总结（二）——微信开发入门";
        item2.Description = "微信服务器就相当于一个转发服务器，终端（手机、Pad等）发起请求至微信服务器，微信服务器然后将请求转发给我们的应用服务器。应用服务器处理完毕后，将响应数据回发给微信服务器，微信服务器再将具体响应信息回复到微信App终端。";
        item2.PicUrl = "";
        item2.Url = "http://www.cnblogs.com/xdp-gacl/p/5151857.html";
        String itemContent2 = buildSingleItem(item2);


        String content = String.format("<xml>\n" +
                "<ToUserName><![CDATA[%s]]></ToUserName>\n" +
                "<FromUserName><![CDATA[%s]]></FromUserName>\n" +
                "<CreateTime>%s</CreateTime>\n" +
                "<MsgType><![CDATA[news]]></MsgType>\n" +
                "<ArticleCount>%s</ArticleCount>\n" +
                "<Articles>\n" + "%s" +
                "</Articles>\n" +
                "</xml> ", fromUserName, toUserName, getMessageCreateTime(), 2, itemContent1 + itemContent2);
        return content;

    }

    /**
     * 生成图文消息的一条记录
     *
     * @param item
     * @return
     */
    private static String buildSingleItem(NewsItem item) {
        String itemContent = String.format("<item>\n" +
                "<Title><![CDATA[%s]]></Title> \n" +
                "<Description><![CDATA[%s]]></Description>\n" +
                "<PicUrl><![CDATA[%s]]></PicUrl>\n" +
                "<Url><![CDATA[%s]]></Url>\n" +
                "</item>", item.Title, item.Description, item.PicUrl, item.Url);
        return itemContent;
    }

    /**
     * 生成消息创建时间（整型）
     *
     * @return
     */
    private static Object getMessageCreateTime() {
        Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");// 设置显示格式
        String nowTime = df.format(dt);
        long dd = (long) 0;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception e) {

        }
        return String.valueOf(dd);
    }

    /**
     * 处理事件消息
     *
     * @param map
     * @return
     */
    private static String handleEventMessage(Map map) {
        String responseMessage = buildWelcomeTextMessage(map);
        return responseMessage;
    }

    private static String handleLinkMessage(Map map) {
        String title = map.get("Title").toString();
        String description = map.get("Description").toString();
        String url = map.get("Url").toString();
        String result = String.format("已经收到您发来的链接，链接标题为:%s，\n描述为:%s\n,链接地址为:", title, description, url);

        return buildTextMessage(map,result);
    }

    private static String handleLocationMessage(Map map) {
        String latitude = map.get("Location_X").toString();  //纬度
        String longitude = map.get("Location_Y").toString();  //经度
        String label = map.get("Label").toString();  //地理位置精度
        String result = String.format("纬度：%s\n经度：%s\n地理位置：%s", latitude, longitude, label);
        return buildTextMessage(map, result);
    }

    private static String handleShortVideoMessage(Map map) {
        String thumbMediaId = map.get("ThumbMediaId").toString();
        String mediaId = map.get("MediaId").toString();
        System.out.print("thumbMediaId:" + thumbMediaId);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的小视频，小视频中素材ID为：%s,\n小视频Id为：%s", thumbMediaId, mediaId);
        return buildTextMessage(map, result);
    }

    private static String handleVideoMessage(Map map) {
        String thumbMediaId = map.get("ThumbMediaId").toString();
        String mediaId = map.get("MediaId").toString();
        System.out.print("thumbMediaId:" + thumbMediaId);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的视频，视频中的素材ID为：%s\n视频Id为：%s", thumbMediaId, mediaId);
        return buildTextMessage(map, result);
    }

    private static String handleVoiceMessage(Map map) {
        String format = map.get("Format").toString();
        String mediaId = map.get("MediaId").toString();
        System.out.print("format:" + format);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的语音，语音格式为：%s\n语音素材Id为：%s", format, mediaId);
        return buildTextMessage(map, result);
    }

    private static String handleImageMessage(Map map) {
        String picUrl = map.get("PicUrl").toString();
        String mediaId = map.get("MediaId").toString();
        System.out.print("picUrl:" + picUrl);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的图片，图片Url为：%s\n图片素材Id为：%s", picUrl, mediaId);
        return buildTextMessage(map, result);
    }

    /**
     * 处理文本消息
     * @param map
     * @return
     */
    private static String handleTextMessage(Map map) {
        //响应消息
        String responseMessage;
        // 消息内容
        String content = map.get("Content").toString();
        switch (content) {
            case "文本":
                String msgText = "又要开始写博客总结了,欢迎朋友们访问我在csdn上面写的博客\n" +
                        "<a href=\"https://blog.csdn.net/liman65727\">liman65727的专栏</a>";
                responseMessage = buildTextMessage(map, msgText);
                break;
            case "图片":
                //通过素材管理接口上传图片时得到的media_id
                String imgMediaId = "d4QXc-uZQGpiRfuR_-KH7p4rYnSdPk3vsGyxuC5-Ns6rTqlIXw_6PZcyClMe6TkE";
                responseMessage = buildImageMessage(map, imgMediaId);
                break;
            case "语音":
                //通过素材管理接口上传语音文件时得到的media_id
                String voiceMediaId = "l3spO_qrC0B1FMnoMujvYrNCzaobtyZALY9_h40OzXHRCqcuOrVXxrg-9f9L8Dxe";
                responseMessage = buildVoiceMessage(map,voiceMediaId);
                break;
            case "图文":
                responseMessage = buildNewsMessage(map);
                break;
            case "音乐":
                Music music = new Music();
                music.title = "赵丽颖、许志安 - 乱世俱灭";
                music.description = "电视剧《蜀山战纪》插曲";
                music.musicUrl = "http://xwtest.tushengkeji.com/weChatPub/media/music/music.mp3";
                music.hqMusicUrl = "http://xwtest.tushengkeji.com/weChatPub/media/music/music.mp3";
                responseMessage = buildMusicMessage(map, music);
                break;
            case "视频":
                Video video = new Video();
                video.mediaId = "n4T9hI51USaQcltFly549C2nB0IHvFZcqQ9DdLxdGtqZ-5_V6mFXz7P8t2TZYjDd";
                video.title = "小苹果";
                video.description = "小苹果搞笑视频";
                responseMessage = buildVideoMessage(map, video);
                break;
            default:
                responseMessage = buildWelcomeTextMessage(map);
                break;

        }
        //返回响应消息
        return responseMessage;
    }

}

/**
 * 图文消息
 */
class NewsItem {
    public String Title;

    public String Description;

    public String PicUrl;

    public String Url;
}

/**
 * 音乐消息
 */
class Music {
    public String title;
    public String description;
    public String musicUrl;
    public String hqMusicUrl;
}

/**
 * 视频消息
 */
class Video {
    public String title;
    public String description;
    public String mediaId;
}
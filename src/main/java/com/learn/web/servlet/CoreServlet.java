package com.learn.web.servlet;

import com.learn.service.CoreService;
import com.learn.util.SignUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * author : liman
 * create time : 2018/9/5
 * QQ:657271181
 * e-mail:liman65727@sina.com
 */
@WebServlet(urlPatterns = "/CoreServlet")
public class CoreServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        System.out.println("===================开始消息的处理==================");

        System.out.println(request.getContextPath());

        Map<String, String[]> parameterMap = request.getParameterMap();

        System.out.println("请求参数列表");
        for(Map.Entry<String,String[]> entity: parameterMap.entrySet()){
            System.out.println(entity.getKey());
            for(String str:entity.getValue()){
                System.out.print(str+" ");
            }
            System.out.println();
        }

        String respMessage = "";

        try{
            // 调用核心业务类接收消息、处理消息
            respMessage = CoreService.processRequest(request);

            System.out.println(respMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
        response.getWriter().println(respMessage);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            out.print(echostr);
        }
        out.close();
    }
}

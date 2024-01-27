package com.hspedu.server.service;

import com.hspedu.common.Message;
import com.hspedu.common.MessageType;
import com.hspedu.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class SendNewsToAllService extends Thread{

    @Override
    public void run() {
        while (true){
            System.out.println("等待推播中...請輸入推播內容 : ");
            String content = Utility.readString(1000);
            // if exit shutdown this thread
            if("exit".equals(content)){
                break;
            }

            //create a news
            Message message = new Message();
            message.setSender("server");
            message.setMesType(MessageType.MESSAGE_NEWS);
            message.setContent(content);
            message.setSendTime(new Date().toString());
            System.out.println("sever 推播 : " + content);

            //send the news to everyone
            HashMap<String, ServerConnectClientThread> hm = ManageServerConnectClientThread.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()){
                String userId = iterator.next();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(hm.get(userId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

package com.hspedu.client.service;

import com.hspedu.common.Message;
import com.hspedu.common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class MessageClientService {

    //send message to one person
    public void sendMessageToOne(String content,String senderId, String getterId){
        //create Message obj
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setContent(content);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSendTime(new Date().toString());

        //send message
        try{
        ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread
                .getClientConnectServerThread(senderId).getSocket().getOutputStream());
        oos.writeObject(message);
            System.out.println("訊息已發送給" + getterId);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    //send message for all people
    public void sendMessageToAll(String content, String senderId){
        //create Message obj
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_MES_TO_ALL);
        message.setContent(content);
        message.setSender(senderId);
        message.setSendTime(new Date().toString());

        //send message
        try{
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread
                    .getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}

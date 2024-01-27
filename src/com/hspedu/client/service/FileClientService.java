package com.hspedu.client.service;

import com.hspedu.common.Message;
import com.hspedu.common.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class FileClientService {
    private boolean on = true;

    public void sendFileToOne(String src, String senderId, String getterId,String dest) {
        on = false;
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE);
        message.setSrc(src);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setDest(dest);

        FileInputStream fileInputStream = null;
        byte[] fileByte = new byte[(int) new File(src).length()];
        try {
            // read the file prepared to send
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileByte);
            // encapsulate the file's byte to message
            message.setFileByte(fileByte);

            // send the file
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread
                    .getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(senderId + " is sending a file to " + getterId + " ...");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public boolean stopMenuInput(){
        return on;
    }
}

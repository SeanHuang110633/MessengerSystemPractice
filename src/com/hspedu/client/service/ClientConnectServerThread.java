package com.hspedu.client.service;

import com.hspedu.qqcommon.Message;
import com.hspedu.qqcommon.MessageType;

import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread {
    //線程必須持有Socket
    private Socket socket;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //因為thread需要在後台不段和伺服器通訊，所以用無限循環
        while(true){

            try {
                System.out.println("客戶端線程等待伺服器訊息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message =(Message) ois.readObject();
                //Process accordingly based on the different types of messages returned by the server
                if(message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIENDS)){
                    String[] list = message.getContent().split(" ");
                    System.out.println("\n======== Online Friends List =========");
                    for(int i = 0; i < list.length ;i++){
                        System.out.println("用戶 : " + list[i]);
                    }
                }else{
                    //later...
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    //方便取用socket
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
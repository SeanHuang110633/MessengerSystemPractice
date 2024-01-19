package com.hspedu.server.service;

import com.hspedu.common.Message;
import com.hspedu.common.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userID;//the user whose ID connected to the server

    public ServerConnectClientThread(Socket socket, String userID) {
        this.socket = socket;
        this.userID = userID;
    }

    @Override
    public void run() {

        while (true) {
            //accept info
            try {
                System.out.println(" keep connecting with client: " + userID);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                //send online friends list back
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIENDS)) {
                    System.out.println(message.getSender() + " requests a online friends List");
                    //create a message and set its content according to hm
                    Message mes = new Message();
                    mes.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIENDS);
                    mes.setContent(ManageServerConnectClientThread.retOnlineUser());
                    //send mes back to client
                    ObjectOutputStream oos = new ObjectOutputStream(ManageServerConnectClientThread.getServerConnectClientThread(userID).socket.getOutputStream());
                    oos.writeObject(mes);
                }

                //logout
                if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    ManageServerConnectClientThread.removeServerConnectClientThread(message.getSender());
                    socket.close();
                    System.out.println("client " + userID + " logout");
                    break;
                }

                //receive private message request from client and send to target getter
                if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    //find the getter's socket on the server side from threads collection hm
                    ServerConnectClientThread receiverThread = ManageServerConnectClientThread
                            .getServerConnectClientThread(message.getGetter());
                    System.out.println(receiverThread.userID + " is already to receive a message...");
                    //send the message to getter
                    ObjectOutputStream oos = new ObjectOutputStream(receiverThread.socket.getOutputStream());
                    oos.writeObject(message);
                    System.out.println("send the message to receiver "+ receiverThread.userID);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

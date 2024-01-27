package com.hspedu.server.service;

import com.hspedu.common.Message;
import com.hspedu.common.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userID; // The client's userId connected to the server

    public ServerConnectClientThread(Socket socket, String userID) {
        this.socket = socket;
        this.userID = userID;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {

        while (true) {
            try {
                // Maintaining the connection with the client
                System.out.println("Continuously connected with client: " + userID);
//                ArrayList<Message> messages = ManageOfflineMessage.getOffLineMesDB().get(userID);
//                if(messages == null || messages.isEmpty() ){
//                    System.out.println("沒有留言");
//                }else{
//                    try {
//                        int count = 1;
//                        for(Message mes : messages){
//                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//                            oos.writeObject(mes);
//                            System.out.println("發送留言 "+count);
//                            count++;
//                            System.out.println("dmflkasnflalk");
//                        }
//                        messages.clear();
//                        System.out.println("aldfnlanflkalk");
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }

                System.out.println("等待客戶訊息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();




                // Processing request for online friends list
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIENDS)) {
                    System.out.println(message.getSender() + " requests the online friends list.");
                    // Create a message and set its content
                    Message mes = new Message();
                    mes.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIENDS);
                    mes.setContent(ManageServerConnectClientThread.getOnlineUser());
                    // Send the message back to the client
                    ObjectOutputStream oos = new ObjectOutputStream(
                            ManageServerConnectClientThread.getServerConnectClientThread(userID).socket.getOutputStream());
                    oos.writeObject(mes);
                }

                // Handling client logout
                if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    ManageServerConnectClientThread.removeServerConnectClientThread(message.getSender());
                    socket.close();
                    System.out.println("Client " + userID + " has logged out.");
                    break;
                }

                // Processing private message requests from clients and forwarding to the intended recipient
                if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    System.out.println("收到來自" + message.getSender() + "要給"+message.getGetter()+"的訊息");
                    // Find the recipient's socket on the server using the collection of threads
                    ServerConnectClientThread receiverThread = ManageServerConnectClientThread
                            .getServerConnectClientThread(message.getGetter());
                    if (receiverThread != null) {
                        // Send the message to the recipient
                        ObjectOutputStream oos = new ObjectOutputStream(receiverThread.socket.getOutputStream());
                        oos.writeObject(message);
                        System.out.println("Message sent to the recipient " + receiverThread.userID);
                    }else{
                        System.out.println(message.getGetter()+"不在線上");
                        if(ManageOfflineMessage.getOffLineMesDB().get(message.getGetter()) == null){
                            System.out.println("還沒有留言，建立一個arrayList");
                            ManageOfflineMessage.addOffLineMesDB(message.getGetter(),new ArrayList<Message>());
                            ManageOfflineMessage.getOffLineMesDB().get(message.getGetter()).add(message);
                        }else{
                            System.out.println("已經有其他留言了，再加");
                            ManageOfflineMessage.getOffLineMesDB().get(message.getGetter()).add(message);
                        }
                    }
                }

                if (message.getMesType().equals(MessageType.MESSAGE_MES_TO_ALL)) {
                    // iterate the threads collection hm and send message to everyone expect the sender.
                    HashMap<String, ServerConnectClientThread> hm = ManageServerConnectClientThread.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()) {
                        String onlineUserId = iterator.next();
                        System.out.println(onlineUserId);
                        if (!onlineUserId.equals(message.getSender())) { // exclude sender
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUserId).socket.getOutputStream());
                            oos.writeObject(message);
                            //System.out.println("群聊訊息已發給" + iterator.next());
                        }
                    }
                }

                if (message.getMesType().equals(MessageType.MESSAGE_FILE)) {
                    // Find the recipient's socket on the server using the collection of threads
                    ServerConnectClientThread receiverThread = ManageServerConnectClientThread
                            .getServerConnectClientThread(message.getGetter());
                    // Send the message to the recipient
                    ObjectOutputStream oos = new ObjectOutputStream(receiverThread.socket.getOutputStream());
                    oos.writeObject(message);
                    System.out.println("send " + receiverThread.userID + " a file");


                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}


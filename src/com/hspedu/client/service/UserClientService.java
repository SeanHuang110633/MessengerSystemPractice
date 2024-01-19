package com.hspedu.client.service;

import com.hspedu.common.Message;
import com.hspedu.common.MessageType;
import com.hspedu.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

//完成用戶登錄驗證和註冊等功能
public class UserClientService {
    private User user = new User();
    private Socket socket;

    public boolean checkUser(String userID, String password) {
        boolean b = false;
        user.setUserID(userID);
        user.setPassword(password);
        try {
            // Connect to the server and send the user object
            socket = new Socket(InetAddress.getByName("172.20.10.2"), 9999);
            System.out.println("connect succeed");
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);
            System.out.println("successfully send User data ");

            // Receive the message object returned by the server
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message o = (Message) ois.readObject();

            // Decide how to proceed
            if (o.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {

                // User authentication succeeded,
                // create a thread to maintain communication with the server
                // -> create ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                clientConnectServerThread.start();

                //For future expansion convenience, add the started thread to
                // a hashmap collection for management
                ManageClientConnectServerThread.addClientConnectServerThread(userID, clientConnectServerThread);
                b = true;
            } else {
                // If authentication fails, close the established socket as communication
                // with the server is not possible
                socket.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return b;
    }

    //request online friends list from server
    public void onlineFriendList() {
        //create a message sent to server
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIENDS);
        message.setSender(user.getUserID());

        //get the socket of the current threads then create an OutPutStream
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ManageClientConnectServerThread.getClientConnectServerThread(user.getUserID())
                            .getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //logout system
    public void exit() {
        //send server a exit message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(user.getUserID());

        //get the socket of the current threads then create an OutPutStream
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ManageClientConnectServerThread.getClientConnectServerThread(user.getUserID())
                            .getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println("client " + user.getUserID() + " logout");
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //send private messages
    public void sendPrivateMessage(String getter, String content){
        //set the information of Message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSender(user.getUserID());
        message.setGetter(getter);
        message.setContent(content);

        //get the socket of the current threads then create an OutPutStream
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ManageClientConnectServerThread.getClientConnectServerThread(user.getUserID())
                            .getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println("client " + user.getUserID() + " send message to " + message.getGetter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

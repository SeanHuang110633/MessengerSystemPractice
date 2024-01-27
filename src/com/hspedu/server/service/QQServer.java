package com.hspedu.server.service;

import com.hspedu.common.Message;
import com.hspedu.common.MessageType;
import com.hspedu.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

//
public class QQServer {
    ServerSocket serverSocket = null;
    //create a HashMap to store valid user data
    private static HashMap<String, User> validUsers = new HashMap<>();
    //private static HashMap<String, ArrayList<Message>> offLineMesDB = new HashMap<>();


    static {
        validUsers.put("1", new User("100", "1"));
        validUsers.put("2", new User("200", "1"));
        validUsers.put("3", new User("300", "1"));
    }

    public boolean check(String userID, String password) {
        if (validUsers.get(userID) == null) {
            return false;
        }
        if (!(validUsers.get(userID).getPassword().equals(password))) {
            return false;
        }
        return true;
    }

    public QQServer() {
        try {
            System.out.println("伺服器在9999端口監聽");
            new SendNewsToAllService().start();
            serverSocket = new ServerSocket(9999);

            while (true) { //use while loop to keep listening
                Socket socket = serverSocket.accept();

                //get User object from client
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User u = (User) ois.readObject();

                Message message = new Message();

                //authenticate
                if (check(u.getUserID(), u.getPassword())) {
                    //login succeed
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //send back a succeed message to client
                    oos.writeObject(message);

                    //send the offline message to client
                    ArrayList<Message> messages = ManageOfflineMessage.getOffLineMesDB().get(u.getUserID());
                    if (!(messages == null || messages.isEmpty())) {
                        try {
                            int count = 1;
                            for (Message mes : messages) {
                                oos = new ObjectOutputStream(socket.getOutputStream());
                                oos.writeObject(mes);
                                System.out.println("發送留言 " + count);
                                count++;
                            }
                            messages.clear();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    //create a thread to keep connecting with client
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserID());
                    serverConnectClientThread.start();
                    //For future expansion convenience, add the started thread to
                    // a hashmap collection for management
                    ManageServerConnectClientThread.addServerConnectClientThread(u.getUserID(), serverConnectClientThread);

                } else {
                    //login failed
                    System.out.println(u.getUserID() + " 登入失敗,輸入密碼為: " + u.getPassword());
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally { // get out of while loop means not listen the request anymore thus close the socketserver
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

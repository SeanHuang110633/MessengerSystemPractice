package com.hspedu.server.service;

import com.hspedu.common.Message;
import com.hspedu.common.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userID; // The user ID of the client connected to the server

    public ServerConnectClientThread(Socket socket, String userID) {
        this.socket = socket;
        this.userID = userID;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Maintaining the connection with the client
                System.out.println("Continuously connected with client: " + userID);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                // Processing request for online friends list
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIENDS)) {
                    System.out.println(message.getSender() + " requests the online friends list.");
                    // Create a message and set its content
                    Message mes = new Message();
                    mes.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIENDS);
                    mes.setContent(ManageServerConnectClientThread.retOnlineUser());
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
                if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    // Find the recipient's socket on the server using the collection of threads
                    ServerConnectClientThread receiverThread = ManageServerConnectClientThread
                            .getServerConnectClientThread(message.getGetter());
                    System.out.println(receiverThread.userID + " is ready to receive a message.");
                    // Send the message to the recipient
                    ObjectOutputStream oos = new ObjectOutputStream(receiverThread.socket.getOutputStream());
                    oos.writeObject(message);
                    System.out.println("Message sent to the recipient " + receiverThread.userID);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}


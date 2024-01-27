package com.hspedu.client.service;

import com.hspedu.common.Message;
import com.hspedu.common.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnectServerThread extends Thread {
    // The thread must maintain a Socket connection
    private Socket socket;


    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // Use an infinite loop because the thread needs to continuously communicate with the server in the background
        while (true) {

            try {
                System.out.println(this.getName() + " is waiting for a message from the server");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                // Process the message based on the different types received from the server
                // If the server sends back the online friends list
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIENDS)) {
                    String[] list = message.getContent().split(" ");
                    System.out.println("\n======== Online Friends List =========");
                    for (int i = 0; i < list.length; i++) {
                        System.out.println("User: " + list[i]);
                    }
                } else {
                    // Additional processing for other message types to be added later...
                }

                // Processing a private message sent by another client through the server
                if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    System.out.println("\nSender: " + message.getSender());
                    System.out.println("Message content: " + message.getContent());
                }

                // Processing the group message
                if (message.getMesType().equals(MessageType.MESSAGE_MES_TO_ALL)) {
                    System.out.println("\nSender: " + message.getSender());
                    System.out.println("Message content: " + message.getContent());
                }


                // Processing the File message
                if (message.getMesType().equals(MessageType.MESSAGE_FILE)) {
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileByte());
                    fileOutputStream.close();
                    System.out.println("\n文件已儲存");
                }

                if (message.getMesType().equals(MessageType.MESSAGE_NEWS)) {
                    System.out.println(message.getSender() + " 廣播: " + message.getContent());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                //later
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}

package com.hspedu.server.service;

import java.util.HashMap;
import java.util.Iterator;

public class ManageServerConnectClientThread {
    private static HashMap<String,ServerConnectClientThread> hm = new HashMap<>();

    public static void addServerConnectClientThread(String userID,ServerConnectClientThread serverConnectClientThread){
        hm.put(userID,serverConnectClientThread);
    }

    public static ServerConnectClientThread getServerConnectClientThread(String userID){
        return hm.get(userID);
    }



    public static String getOnlineUser(){
        String onlineUsers = "";
        Iterator<String> iterator = hm.keySet().iterator();
        while (iterator.hasNext()){
            onlineUsers += iterator.next() +" ";
        }
        return onlineUsers;
    }

    public static void removeServerConnectClientThread(String userID){
        hm.remove(userID);
    }

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }
}

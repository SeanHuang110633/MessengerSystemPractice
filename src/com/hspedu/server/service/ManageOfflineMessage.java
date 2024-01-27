package com.hspedu.server.service;

import com.hspedu.common.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ManageOfflineMessage {
    private static ConcurrentHashMap<String, ArrayList<Message>> offLineMesDB = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ArrayList<Message>> getOffLineMesDB() {
        return offLineMesDB;
    }

    public static void addOffLineMesDB(String getterId, ArrayList<Message> offlineMes) {
        offLineMesDB.put(getterId,offlineMes);
    }

}

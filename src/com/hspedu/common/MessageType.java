package com.hspedu.common;

//表示消息類型有哪些
public interface MessageType {
    //在接口中定義常量，不同常量值代表不同消息類型
    String MESSAGE_LOGIN_SUCCEED = "1";
    String MESSAGE_LOGIN_FAIL = "2";
    String MESSAGE_COMM_MES = "3";
    String MESSAGE_GET_ONLINE_FRIENDS = "4";
    String MESSAGE_RET_ONLINE_FRIENDS = "5";
    String MESSAGE_CLIENT_EXIT = "6";
    String MESSAGE_MES_TO_ALL = "7";
    String MESSAGE_FILE = "8";
    String MESSAGE_NEWS = "9";
}

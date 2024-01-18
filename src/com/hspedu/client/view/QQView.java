package com.hspedu.client.view;

import com.hspedu.qqclient.service.UserClientService;
import com.hspedu.qqclient.utils.Utility;

public class QQView {
    private boolean loop = true; //控制是否顯示菜單
    private String key = ""; //接收用戶鍵盤輸入
    private UserClientService userClientService = new UserClientService();

    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("退出");
    }


    private void mainMenu() {

        while (loop) {
            System.out.println("========== 歡迎登錄 ===========");
            System.out.println("\t\t 1.登入系統");
            System.out.println("\t\t 9.退出系統");
            System.out.print("起輸入你的選擇: ");
            key = Utility.readString(1);
            switch (key) {
                case "1":
                    System.out.print("請輸入UserID :");
                    String userID = Utility.readString(50);
                    System.out.print("請輸入Password :");
                    String password = Utility.readString(50);
                    //接續到伺服器端驗證用戶，編寫UserClientService類處理
                    if (userClientService.checkUser(userID,password)) {
                        System.out.println("====歡迎" + userID + "======");
                        while (loop) {
                            System.out.println("\n======= " + userID + " 的第二級選單=======");
                            System.out.println("\t\t 1.顯示在線用戶");
                            System.out.println("\t\t 2.群發訊息");
                            System.out.println("\t\t 3.私聊訊息");
                            System.out.println("\t\t 4.發送文件");
                            System.out.println("\t\t 9.退出系統");
                            System.out.print("請輸入選擇");
                            key = Utility.readString(1);
                            switch (key){
                                case "1":
                                    System.out.println("顯示在線用戶");
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.println("群發訊息");
                                    break;
                                case "3":
                                    System.out.println("私聊訊息");
                                    break;
                                case "4":
                                    System.out.println("發送文件");
                                    break;
                                case "9":
                                    userClientService.exit();
                                    loop = false;
                                    break;
                            }
                        }
                    }else{
                        System.out.println("======= 登入失敗 =======");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }
}

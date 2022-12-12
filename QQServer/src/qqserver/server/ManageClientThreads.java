package qqserver.server;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author 李
 * @version 1.0
 * 该类用于管理和客户端通信的线程
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    //返回hashmap
    public static HashMap<String ,ServerConnectClientThread> getHm(){
        return hm;
    }


    //添加线程对象到 hm集合中
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    //根据userId返回ServerConnectClientThread线程
    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return hm.get(userId);
    }

    //这里编写方法，可以返回在线用户列表
    public static String getOnlineUser() {
        //遍历集合，遍历 hashmap 的 key
        Iterator<String> iterator = hm.keySet().iterator();//获取hm集合的ketSet集合的迭代器（这里的ketSet就是userId）
        String onlineUserList = "";
        while (iterator.hasNext()) {//遍历
            onlineUserList += iterator.next().toString() + " ";//遍历所有的userId，用空格拼接起来
        }
        return onlineUserList;
    }

    //增加一个方法，从集合中移除某个对象
    public static void removeServerConnectClientThread(String userId){
        hm.remove(userId);
    }

}

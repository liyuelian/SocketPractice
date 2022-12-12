package qqserver.server;

import qqcommon.Message;
import qqcommon.MessageType;
import qqserver.utlis.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


/**
 * @author 李
 * @version 1.0
 * 该类完成服务端新闻推送功能
 */
public class SendNewsToAllService implements Runnable {

    @Override
    public void run() {

        //为了可以推送多次新闻，使用while
        while (true) {
            System.out.println("请输入服务器要推送的新闻/消息[输入exit表示退出推送服务]");
            String news = Utility.readString(100);

            if ("exit".equals(news)) {
                break;
            }

            //构建一个消息，群发消息
            Message message = new Message();
            message.setSender("服务器");//发送者
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);//设置消息发送类型
            message.setContent(news);//新闻内容
            message.setSendTime(new Date().toString());//发送时间
            System.out.println("服务器推送消息给所有人 说：" + news);

            //遍历当前所有的通信线程，得到其socket，并发送 message

            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {//遍历
                String onlineUserId = iterator.next().toString();
                try {
                    ObjectOutputStream oos =
                            new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

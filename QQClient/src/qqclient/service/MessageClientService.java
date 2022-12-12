package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * @author 李
 * @version 1.0
 * 该类提供和消息（私聊、群聊）相关的服务方法
 */
public class MessageClientService {

    /**
     * 群发消息功能
     * @param content 内容
     * @param senderId 发送者
     */
    public void sendMessageToAll(String content,String senderId){
        //构建 message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);//设置消息类型是群发消息
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(new Date().toString());//发送时间也封装到 message对象中
        System.out.println(senderId + " 对大家说 " + content);

        //发送给服务端
        try {//在管理线程的集合中，通过userId来获取线程，通过线程来获取对应的socket，再通过socket获取输出流
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 私聊消息功能
     * @param content  内容
     * @param senderId 发送用户id
     * @param getterId 接收用户id
     */
    public void sendMessageToOne(String content, String senderId, String getterId) {
        //构建 message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);//设置消息类型是普通的聊天类型
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new Date().toString());//发送时间也封装到message对象中
        System.out.println(senderId + " 对 " + getterId + " 说 " + content);

        //发送给服务端
        try {//在管理线程的集合中，通过userId来获取线程，通过线程来获取对应的socket，再通过socket获取输出流
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

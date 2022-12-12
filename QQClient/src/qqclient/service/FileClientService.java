package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.*;

/**
 * @author 李
 * @version 1.0
 * 该类完成 文件传输服务功能
 */
public class FileClientService {
    /**
     * @param src      源文件
     * @param dest     把该文件传输到对方的哪个目录
     * @param senderId 发送用户的id
     * @param getterId 接收用户的id
     */
    public void sendFileToOne(String src, String dest, String senderId, String getterId) {
        //读取src文件--封装-->message对象
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);//设置为文件类型
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        //需要将文件读取到程序中
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];//创建一个和文件大小对应的字节数组

        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);//将src的文件读入到程序的字节数组
            //将文件对应的字节数组设置成message对象
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    //关闭流
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("\n" + senderId + " 给 " + getterId + " 发送文件：" + src +
                " 到对方的电脑目录： " + dest);

        //发送
        try {
            //从管理线程的集合中，拿到发送者id的线程，
            // 在该线程对应的属性socket中得到输出流，将输出流转为对象输出流
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

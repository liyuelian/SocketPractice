package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author 李
 * @version 1.0
 */
public class ClientConnectServerThread extends Thread {
    //该线程需要持有socket
    private Socket socket;

    //构造器可以接收一个Socket对象
    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //因为Thread需要在后台和服务器通信，因此我们使用while循环
        while (true) {
            try {
                System.out.println("客户端线程，等待读取从服务端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                //如果服务器没有发送Message对象，线程会阻塞在这里
                Message message = (Message) ois.readObject();

                //判断Message的类型，然后做相应的业务处理
                //如果读取到的是 服务端返回的在线用户列表(MESSAGE_RET_ONLINE_FRIEND)
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    //取出在线用户列表信息，并展示
                    //这里假定返回的用户列表是用空格隔开的id名（如：100 200 紫霞仙子 至尊宝 唐僧）
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n=======当前在线用户列表=======");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户：" + onlineUsers[i]);
                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    //接收到的是普通的聊天消息
                    //就把服务器转发的消息，显示到控制台即可
                    System.out.println("\n" + message.getSendTime() + "\n" + message.getSender()
                            + " 对" + message.getGetter() + " 说： " + "\n" + message.getContent());
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    //接收到的是群发的消息
                    //就把服务器转发的消息，显示到控制台即可
                    System.out.println("\n" + message.getSendTime() + "\n" + message.getSender()
                            + " 对大家说： " + "\n" + message.getContent());
                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    //接收到的是文件类型的消息
                    //把服务器转发的message对象
                    System.out.println("\n" + message.getSender() + " 给 " + message.getGetter() +
                            " 发送文件： " + message.getSrc() + " 到我的电脑的目录：" + message.getDest());
                    //取出message文件的字节数组，通过文件输出流写出到磁盘
                    FileOutputStream fileOutputStream =
                            new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n保存文件成功~");
                } else {
                    System.out.println("读取到的是其他类型的message，暂时不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //为了更方便地得到socket，提供get方法
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}

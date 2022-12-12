package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author 李
 * @version 1.0
 * 该类完成用户登录验证和用户注册、拉取在现用户列表等功能
 */
public class UserClientService {
    //因为我们可能在其他地方使用User信息，因此做成成员属性
    private User u = new User();
    //因为可能在其他地方使用Socket，因此也做成成员属性
    private Socket socket;

    //根据用户输入的 userId 和 pwd，到服务器去验证该用户是否合法
    public boolean checkUser(String userId, String pwd) {
        boolean b = false;
        //创建User对象
        u.setUserId(userId);
        u.setPassword(pwd);

        try {
            //连接服务器，发送u对象
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);//指定服务端的ip和端口
            //获取ObjectOutputStream对象(对象输出流)
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//向服务端发送User对象，服务器会进行验证

            //服务器验证后，客户端读取从服务端回送的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();//强转为Message类型

            /**
             * 取出服务端返回的Message对象中的getMesType属性
             * 如果为MESSAGE_LOGIN_SUCCEED则说明登录成功,
             * 否则登录失败
             * */
            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {//登录成功
                //创建一个服务器保持通信的线程
                // -->创建一个类 ClientConnectServerThread，
                // 把socket传到该线程里面，然后把线程放到一个集合里面去管理
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端的线程
                clientConnectServerThread.start();
                //这里为了后面客户端的扩展，我们将线程放入到集合里面
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                b = true;
            } else {//登录失败
                //如果登录失败，就不启动和服务器通讯的线程，直接关闭socket
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    //向服务器端请求在线用户列表
    public void onlineFriendList() {
        //向服务器发送一个Message，类型MESSAGE_GET_ONLINE_FRIEND,要求返回在线用户列表
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        //发送给服务器
        try {
            //从管理线程的集合里面，通过userId，得到这个线程对象
            ClientConnectServerThread clientConnectServerThread =
                    ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId());
            //通过这个线程中获取关联的socket
            Socket socket = clientConnectServerThread.getSocket();
            //得到当前线程的Socket对应的ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);//发送一个Message对象向服务器，要求在线用户列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //编写方法，退出客户端，并给服务器端发送一个退出系统的 message对象
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指定是那个客户端，服务端要根据这个userId移除集合中的线程

        //发送message
        try {
            //从管理线程的集合里面，通过userId，得到这个线程对象
            ClientConnectServerThread clientConnectServerThread =
                    ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId());
            //通过这个线程中获取关联的socket
            Socket socket = clientConnectServerThread.getSocket();
            //得到当前线程的Socket对应的ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId()+"退出系统");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

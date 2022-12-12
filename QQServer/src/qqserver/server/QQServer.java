package qqserver.server;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 李
 * @version 1.0
 * 这是服务端，在监听端口9999，等待有客户端连接，并保持通信
 */
public class QQServer {

    private ServerSocket ss = null;

    /*
    创建一个集合，存放多个用户数据，如果是在集合里面的用户登录，就认为是合法的（模拟数据库）
    这里也可以使用 ConcurrentHashMap，可以处理并发的集合，没有线程安全问题
    HashMap 没有处理线程安全，因此在多线程的情况下是不安全的
    ConcurrentHashMap 处理的线程安全，即线程同步处理，在多线程的情况下是安全的
    */
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    /*
   同样创建一个集合，存放多个用户发送的离线消息或者文件
   使用 ConcurrentHashMap，可以处理并发的集合，没有线程安全问题
   存放的形式为
   key = getter id   [接收者id]
   value = ArrayList<Message>   在一个ArrayList集合中可以存放多条 message对象，实现多条留言或者文件
   */
    static ConcurrentHashMap<String, ArrayList<Message>> offlineMessage = new ConcurrentHashMap<>();

    static {//在静态代码块，初始化 validUsers
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("jack", new User("jack", "123456"));
        validUsers.put("tom", new User("tom", "123456"));
        validUsers.put("菩提老祖", new User("菩提老祖", "123456"));
        validUsers.put("至尊宝", new User("至尊宝", "123456"));
        validUsers.put("紫霞仙子", new User("紫霞仙子", "123456"));
    }

    /**
     * @param getterId 接收离线数据的用户userId
     * @param socket   用户对应的通信socket
     *                 写一个方法，当有用户登录成功时，获取该用户id名，
     *                 在离线集合中搜索该id，如果有，就返回给该用户，然后删除该离线留言或者文件
     */
    public void isOfflineMessage(String getterId, Socket socket) {
        //搜索 map集合中是否有该用户的id
        if (offlineMessage.containsKey(getterId)) {
            //如果有，就说明该用户有离线留言或数据要接收
            //在map中获取该 message数据集合arrayList
            ArrayList<Message> arrayListMessage = offlineMessage.get(getterId);
            //遍历arrayList集合将一个或者多个message数据发送给该用户
            for (Message message : arrayListMessage) {
                try {
                    //获得输出对象
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);//发送数据
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            offlineMessage.remove(getterId);//遍历完则在数据库中删除该数据
        }
    }


    //验证用户是否有效的方法
    public boolean checkUser(String userId, String password) {
        User user = validUsers.get(userId);//在HashMap（模拟数据库）里面找key=userId对应的value=User对象
        //过关的验证方式
        if (user == null) {//如果User为空（即Value为空）就说明 userId对应的key不存在
            return false;
        }
        if (!user.getPassword().equals(password)) {//如果userId正确，但是密码错误
            return false;
        }
        return true;//如果userId和密码都正确
    }

    public QQServer() {

        //注意：端口可以写在配置文件里面
        try {
            System.out.println("服务端在9999端口监听...");
            ss = new ServerSocket(9999);

            //启动推送新闻的线程
            new Thread(new SendNewsToAllService()).start();

            while (true) {//循环监听，当和某个客户端建立连接后，会继续监听，因此使用while
                Socket socket = ss.accept();//如果没有客户端连接，就会阻塞在这里，直到有新的客户端来连接

                //得到socket关联的对象输入流
                ObjectInputStream ois =
                        new ObjectInputStream(socket.getInputStream());
                User u = (User) ois.readObject();//读取客户端发送的User对象

                /***
                 * 下面这里其实是要到数据库区验证User的信息，但是因为还没学数据库，先用规定的数据进行校验
                 * HashMap模拟数据库，可以多个用户登录
                 */
                //创建一个Message对象，用来回复客户端
                Message message = new Message();
                //得到socket关联的对象输出流
                ObjectOutputStream oos =
                        new ObjectOutputStream(socket.getOutputStream());
                //验证
                if (checkUser(u.getUserId(), u.getPassword())) {//登录通过
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //将Message对象回复给客户端
                    oos.writeObject(message);
                    //创建一个线程，和客户端保持通信，该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket, u.getUserId());
                    //启动该线程
                    serverConnectClientThread.start();
                    //把该线程对象放入到一个集合中，进行管理
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);

                    //调用方法，查看是否有离线数据
                    isOfflineMessage(u.getUserId(), socket);

                } else {//登录失败
                    System.out.println("用户 id=" + u.getUserId() + " pwd=" + u.getPassword() + " 验证失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    //关闭socket
                    socket.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //如果服务器退出了while循环，说明服务器不再监听，因此关闭ServerSock
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

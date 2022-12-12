package qqserver.server;

import qqcommon.Message;
import qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static qqserver.server.QQServer.offlineMessage;

/**
 * @author 李
 * @version 1.0
 * 该类的一个对象和某个客户端保持通信
 */
public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;//连接到服务端的用户id

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }


    public void processOfData(Message message) throws IOException {
        //业务三or五：客户请求给某个用户发送-->普通的聊天消息or文件

        //当用户给某用户发送 message时，如果接收用户不在线（即通过getterId找不到该用户的通讯线程）
        if (ManageClientThreads.getServerConnectClientThread(message.getGetter()) == null) {
            //就将离线 message放入这个用户对应的arraylist集合中，再把该arrayList集合放到 map中

            /*
             * 这里有个缺陷，因为map的key值不允许重复（重复的话，value会覆盖为最新值），
             * 所以当有多个用户分别给某一个用户进行留言，该接收用户只能接收到最近一个人的离线留言
             * 为了解决这个问题，这里每次添加留言之前 都会将该接收用户的所有留言复制一份，再在 最后添加新的留言
             * 然后把所有的留言再放回 map集合中，这样效果等于追加新留言
             */
            // 先判断map中该接收用户的userId是否存在
            // 如果有，就说明已经有人给该用户留言了，就获取map集合对应 getter id的arraylist留言表
            // 然后在该用户的留言表中"追加"留言即可
            if (offlineMessage.containsKey(message.getGetter())) {//如果接收用户的留言表已经存在
                //获取 接收留言的用户的 留言集合
                ArrayList<Message> arrayListMessage = offlineMessage.get(message.getGetter());
                //在该集合中追加新的留言
                arrayListMessage.add(message);//增加新的留言
                //留言表再覆盖进去，这样相当于在留言表中追加留言
                offlineMessage.put(message.getGetter(), arrayListMessage);
            } else {//如果接收用户的留言表不存在
                //如果map集合中没有接收用户的id，说明还没人给这个用户留言，要先创建一个留言表arrayListMessage
                ArrayList<Message> arrayListMessage = new ArrayList<>();
                //把信息添加到新创建的留言表中
                arrayListMessage.add(message);
                //将新留言表添加到 map集合中
                offlineMessage.put(message.getGetter(), arrayListMessage);
            }
        } else {//接收用户在线，就直接发送数据
            //根据接收的message对象的getter id 获取到对应的线程，将message对象进行转发
            //先拿到线程
            ServerConnectClientThread serverConnectClientThread =
                    ManageClientThreads.getServerConnectClientThread(message.getGetter());
            //获取socket,将socket输出流转为对象流
            ObjectOutputStream oos =
                    new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
            //转发
            oos.writeObject(message);
        }
    }

    @Override
    public void run() {//这里线程处于run的状态，可以发送/接收消息

        while (true) {
            try {
                System.out.println("服务端和客户端" + userId + "保持通信，读取数据...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                //后面会使用message,根据message的类型，做相应的业务处理

                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //业务一：客户请求拉取在线用户列表
                    //假定返回的用户列表是用空格隔开的id名（如：100 200 紫霞仙子 至尊宝 唐僧）
                    System.out.println(message.getSender() + " 要在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();

                    //返回message
                    //构建一个Message对象（这个Message对象包含了在线用户列表信息），返回给客户端
                    Message message2 = new Message();
                    //设置消息类型--返回的在线用户列表类型-客户端会根据返回的消息类型来进行相应的业务处理
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);//返回用户消息列表
                    //服务器发送的消息的接收者Getter 就是服务器接收的信息 的发送者Sender
                    message2.setGetter(message.getSender());

                    //返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    //业务二：客户请求退出系统
                    System.out.println(message.getSender() + " 退出");
                    //将客户端对应的线程从集合中删除
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    socket.close();//关闭的是当前的线程持有的socket属性
                    break;//退出线程的循环
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    //业务三：客户请求和某用户私聊
                    //调用方法，判断用户是否在线，在线就直接发送，不在线就将message对象先存在集合中
                    processOfData(message);

                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    //业务四：客户请求群发消息需要遍历管理线程的集合，把所有线程的socket都得到，然后将 message进行转发即可
                    //得到hm
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    //遍历
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()) {
                        //取出所有userId
                        String onlineUserId = iterator.next().toString();
                        //取出除了发送者的所有用户id
                        if (!onlineUserId.equals(message.getSender())) {
                            //转发message
                            //从集合中取出线程，在线程中取出socket，根据socket获得输出流，将socket的输出流转化为对象输出流
                            ObjectOutputStream oos =
                                    new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    //业务五：客户请求给某用户发送文件
                    //调用方法，判断用户是否在线，在线就直接发送，不在线就将message对象先存在集合中
                    processOfData(message);

                } else {
                    System.out.println("其他类型的message，暂时不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

# 多用户即时通讯系统02

## 4.编码实现01

### 4.1功能实现-用户登录

#### 4.1.1功能说明

因为还没有学习数据库，我们人为规定 用户名/id = 100，密码为 123456 就可以登录，其他用户不能登录，后面使用HashMap模拟数据库，这样就可以多个用户登录。

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220920184736385.png" alt="image-20220920184736385" style="zoom:67%;" />

#### 4.1.2思路分析+框架图

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220921230655619.png" alt="image-20220921230655619" style="zoom:80%;" />

用户的登录功能的流程：

1. 用户进入系统界面，选择登录

2. 输入登录信息之后，客户端与服务端建立连接，把信息发送给服务端

3. 服务端接收信息，在数据库中进行校验，作出判断

4. 服务端将判断返回客户端

5. 客户端接收信息后，进行下一步操作（成功则进入二级菜单，失败则请求用户重新输入）

   

#### 4.1.3代码实现

##### 4.1.3.1客户端代码

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220921233227770.png" alt="image-20220921233227770" style="zoom:67%;" /> <img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220922000812144.png" alt="image-20220922000812144" style="zoom:67%;" />

###### 1.User类

用户输入登录信息后，在客户端发送信息给服务端的过程中，为了方便数据的解析（比如用户id、用户密码等），使用对象来进行数据的传输

```java
package qqcommon;

import java.io.Serializable;

/**
 * @author 李
 * @version 1.0
 * 表示一个用户信息
 */
public class User implements Serializable {//要序列化某个对象，实现接口Serializable
    private static final long serialVersionUID = 1L;//声明序列化版本号，提高兼容性
    private String userId;//用户id/用户名
    private String password;//用户密码

    public User() {
    }

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

###### 2.Message类

表示客户端和服务器端通讯时的消息对象，目的同User

```java
package qqcommon;

import java.io.Serializable;

/**
 * @author 李
 * @version 1.0
 * 表示客户端和服务器端通讯时的消息对象
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;//声明序列化版本号，提高兼容性
    //因为客户端之间的通信都要依靠服务端，因此信息必须要写明接收者和发送者等
    private String sender;//发送者
    private String getter;//接收者
    private String content;//消息内容
    private String sendTime;//发送时间  -因为发送时间也要被序列化，因此这里也用String类型
    private String mesType;//消息类型[可以在接口中定义消息类型]

    public String getMesType() {
        return mesType;
    }

    public void setMesType(String mesType) {
        this.mesType = mesType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
```

###### 3.MessageType接口

```java
package qqcommon;

/**
 * @author 李
 * @version 1.0
 * 表示消息类型
 */
public interface MessageType {
    //在接口中定义类一些常量，不同的常量的表示不同的消息类型
    String MESSAGE_LOGIN_SUCCEED = "1";//表示登录成功
    String MESSAGE_LOGIN_FAIL = "2";//表示登录失败

}
```

###### 4.QQView类

主程序入口，显示菜单

```java
package qqclient.view;


import qqclient.service.UserClientService;
import qqclient.utils.Utility;

/**
 * @author 李
 * @version 1.0
 */
public class QQView {
    private boolean loop = true;//控制是否显示菜单
    private String key = "";//用来接收用户的键盘输入
    private UserClientService userClientService = new UserClientService();//该对象用于登录服务/注册用户

    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("客户端退出系统......");
    }

    //显示主菜单
    public void mainMenu() {
        while (loop) {
            System.out.println("===========欢迎登陆网络通信系统===========");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择：");
            key = Utility.readString(1);//读取键盘输入的指定长度的字符串

            //根据用户的输入，来处理不同的逻辑
            switch (key) {
                case "1":
                    System.out.print("请输入用户号：");
                    String userId = Utility.readString(50);//读取键盘输入的指定长度的字符串
                    System.out.print("请输入密  码：");
                    String pwd = Utility.readString(50);

                    // 到服务端去验证用户是否合法
                    //这里有很多代码,我们这里编写一个类UserClientService[提供用户登录/注册等功能]
                    if (userClientService.checkUser(userId, pwd)) {//验证成功
                        System.out.println("=========欢迎（用户 " + userId + " 登录成功）=========");
                        //进入到二级菜单
                        while (loop) {
                            System.out.println("\n=========网络通讯系统二级菜单（用户 " + userId + " ）==========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("请输入你的选择：");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    System.out.println("显示在线用户列表");
                                    break;
                                case "2":
                                    System.out.println("群发消息");
                                    break;
                                case "3":
                                    System.out.println("私聊消息");
                                    break;
                                case "4":
                                    System.out.println("发送文件");
                                    break;
                                case "9":
                                    loop = false;//退出循环
                                    break;
                            }
                        }
                    } else {//验证失败
                        System.out.println("=========登录失败========");
                    }
                    break;
                case "9":
                    loop = false;//退出循环
                    break;
            }
        }
    }
}
```

###### 5.Utility类

工具类，用于处理各种情况的用户输入，并且能够按照程序员的需求，得到用户的控制台输入。

```java
package qqclient.utils;


/**
 * 工具类的作用:
 * 处理各种情况的用户输入，并且能够按照程序员的需求，得到用户的控制台输入。
 */

import java.util.Scanner;

/**


 */
public class Utility {
    //静态属性。。。
    private static Scanner scanner = new Scanner(System.in);


    /**
     * 功能：读取键盘输入的一个菜单选项，值：1——5的范围
     * @return 1——5
     */
    public static char readMenuSelection() {
        char c;
        for (; ; ) {
            String str = readKeyBoard(1, false);//包含一个字符的字符串
            c = str.charAt(0);//将字符串转换成字符char类型
            if (c != '1' && c != '2' &&
                    c != '3' && c != '4' && c != '5') {
                System.out.print("选择错误，请重新输入：");
            } else break;
        }
        return c;
    }

    /**
     * 功能：读取键盘输入的一个字符
     * @return 一个字符
     */
    public static char readChar() {
        String str = readKeyBoard(1, false);//就是一个字符
        return str.charAt(0);
    }

    /**
     * 功能：读取键盘输入的一个字符，如果直接按回车，则返回指定的默认值；否则返回输入的那个字符
     * @param defaultValue 指定的默认值
     * @return 默认值或输入的字符
     */

    public static char readChar(char defaultValue) {
        String str = readKeyBoard(1, true);//要么是空字符串，要么是一个字符
        return (str.length() == 0) ? defaultValue : str.charAt(0);
    }

    /**
     * 功能：读取键盘输入的整型，长度小于2位
     * @return 整数
     */
    public static int readInt() {
        int n;
        for (; ; ) {
            String str = readKeyBoard(10, false);//一个整数，长度<=10位
            try {
                n = Integer.parseInt(str);//将字符串转换成整数
                break;
            } catch (NumberFormatException e) {
                System.out.print("数字输入错误，请重新输入：");
            }
        }
        return n;
    }

    /**
     * 功能：读取键盘输入的 整数或默认值，如果直接回车，则返回默认值，否则返回输入的整数
     * @param defaultValue 指定的默认值
     * @return 整数或默认值
     */
    public static int readInt(int defaultValue) {
        int n;
        for (; ; ) {
            String str = readKeyBoard(10, true);
            if (str.equals("")) {
                return defaultValue;
            }

            //异常处理...
            try {
                n = Integer.parseInt(str);
                break;
            } catch (NumberFormatException e) {
                System.out.print("数字输入错误，请重新输入：");
            }
        }
        return n;
    }

    /**
     * 功能：读取键盘输入的指定长度的字符串
     * @param limit 限制的长度
     * @return 指定长度的字符串
     */

    public static String readString(int limit) {
        return readKeyBoard(limit, false);
    }

    /**
     * 功能：读取键盘输入的指定长度的字符串或默认值，如果直接回车，返回默认值，否则返回字符串
     * @param limit 限制的长度
     * @param defaultValue 指定的默认值
     * @return 指定长度的字符串
     */

    public static String readString(int limit, String defaultValue) {
        String str = readKeyBoard(limit, true);
        return str.equals("") ? defaultValue : str;
    }


    /**
     * 功能：读取键盘输入的确认选项，Y或N
     * 将小的功能，封装到一个方法中.
     * @return Y或N
     */
    public static char readConfirmSelection() {
        System.out.println("请输入你的选择(Y/N): 请小心选择");
        char c;
        for (; ; ) {//无限循环
            //在这里，将接受到字符，转成了大写字母
            //y => Y n=>N
            String str = readKeyBoard(1, false).toUpperCase();
            c = str.charAt(0);
            if (c == 'Y' || c == 'N') {
                break;
            } else {
                System.out.print("选择错误，请重新输入：");
            }
        }
        return c;
    }

    /**
     * 功能： 读取一个字符串
     * @param limit 读取的长度
     * @param blankReturn 如果为true ,表示 可以读空字符串。
     *                   如果为false表示 不能读空字符串。
     *
     * 如果输入为空，或者输入大于limit的长度，就会提示重新输入。
     * @return
     */
    private static String readKeyBoard(int limit, boolean blankReturn) {

        //定义了字符串
        String line = "";

        //scanner.hasNextLine() 判断有没有下一行
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();//读取这一行

            //如果line.length=0, 即用户没有输入任何内容，直接回车
            if (line.length() == 0) {
                if (blankReturn) return line;//如果blankReturn=true,可以返回空串
                else continue; //如果blankReturn=false,不接受空串，必须输入内容
            }

            //如果用户输入的内容大于了 limit，就提示重写输入
            //如果用户如的内容 >0 <= limit ,我就接受
            if (line.length() < 1 || line.length() > limit) {
                System.out.print("输入长度（不能大于" + limit + "）错误，请重新输入：");
                continue;
            }
            break;
        }

        return line;
    }
}
```

###### 6.UserClientService类

该类完成用户登录验证和用户注册等功能

```java
package qqclient.service;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author 李
 * @version 1.0
 * 该类完成用户登录验证和用户注册等功能
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
        u.setUerId(userId);
        u.setPassword(pwd);

        try {
            //连接服务器，发送u对象
            socket = new Socket(InetAddress.getByName("192.168.1.6"), 9999);//指定服务端的ip和端口
            //获取ObjectOutputStream对象(对象输出流)
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//向服务端发送User对象，服务器会进行验证

            //socket.shutdownOutput();

            //服务器验证后，客户端读取从服务端回送的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();//强转为Message类型

            /**取出服务端返回的Message对象中的getMesType属性
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
}
```

###### 7.ClientConnectServerThread类

客户端与服务端通过socket连接，考虑到一个客户端会有多个socket的情况（服务端同此），将socket放在线程内

```java
package qqclient.service;

import qqcommon.Message;

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
                //注意，后面我们需要使用message

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
```

###### 8.ManageClientConnectServerThread类

将线程都放入集合中，便于管理

```java
package qqclient.service;

import java.util.HashMap;

/**
 * @author 李
 * @version 1.0
 * 该类管理客户端连接到服务器端的线程的类
 */
public class ManageClientConnectServerThread {
    //把多个线程放入到HashMap集合，key就是用户id，value就是线程
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();

    //将某个线程加入到集合
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread) {
        hm.put(userId, clientConnectServerThread);
    }

    //通过userId可以得到一个对应的线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId) {
        return hm.get(userId);
    }
}
```



##### 4.1.3.2服务端代码

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220921234743331.png" alt="image-20220921234743331" style="zoom:67%;" /> <img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220922000858338.png" alt="image-20220922000858338" style="zoom:67%;" />

服务端的User、Message、MessageType和客户端一致，不再赘述

###### 1.QQFrame

```java
package qqframe;

import qqserver.server.QQServer;

/**
 * @author 李
 * @version 1.0
 * 该类创建QQServer，启动后台的服务
 */
public class QQFrame {
    public static void main(String[] args) {
        new QQServer();
    }
}
```

###### 2.QQServer



```java
package qqserver.server;

import qqcommon.Message;
import qqcommon.MessageType;
import qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 李
 * @version 1.0
 * 这是服务端，在监听端口9999，等待有客户端连接，并保持通信
 */
public class QQServer {

    private ServerSocket ss = null;
    //创建一个集合，存放多个用户数据，如果是在集合里面的用户登录，就认为是合法的（模拟数据库）
    //这里也可以使用 ConcurrentHashMap，可以处理并发的集合，没有线程安全问题
    // HashMap 没有处理线程安全，因此在多线程的情况下是不安全的
    // ConcurrentHashMap 处理的线程安全，即线程同步处理，在多线程的情况下是安全的
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static {//在静态代码块，初始化 validUsers
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("至尊宝", new User("至尊宝", "123456"));
        validUsers.put("紫霞仙子", new User("紫霞仙子", "123456"));
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
        System.out.println("服务端在9999端口监听...");
        try {
            ss = new ServerSocket(9999);

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
```

###### 3.ServerConnectClientThread

线程类，与客户端的线程类同理

```java
package qqserver.server;

import qqcommon.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

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

    @Override
    public void run() {//这里线程处于run的状态，可以发送/接收消息

        while (true) {
            try {
                System.out.println("服务端和客户端" + userId + "保持通信，读取数据...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                //后面会使用Message
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

###### 4.ManageClientThreads

使用集合来存放线程，便于管理

```java
package qqserver.server;

import java.util.HashMap;

/**
 * @author 李
 * @version 1.0
 * 该类用于管理和客户端通信的线程
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    //添加线程对象到 hm集合中
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    //根据userId返回ServerConnectClientThread线程
    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return hm.get(userId);
    }
}
```

运行截图：

1. 先运行服务端：

   <img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220921235610095.png" alt="image-20220921235610095" style="zoom: 60%;" /> 

 

2. 运行客户端，并输入信息：

   <img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220921235756369.png" alt="image-20220921235756369" style="zoom: 67%;" /> 

此时服务端：

​        <img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220922000032580.png" alt="image-20220922000032580" style="zoom:63%;" /> 

可以看到服务端成功地从客户端获取用户登录信息，匹配相应用户后返回了信息，客户端成功获取到了服务端返回的信息，并进入了二级菜单。


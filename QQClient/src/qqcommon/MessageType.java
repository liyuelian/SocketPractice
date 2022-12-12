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
    String MESSAGE_COMM_MES = "3";//表示普通信息包-私聊
    String MESSAGE_GET_ONLINE_FRIEND = "4";//要求返回在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND = "5";//返回的在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";//客户端请求退出
    String MESSAGE_TO_ALL_MES = "7";//表示群发消息包
    String MESSAGE_FILE_MES = "8";//表示发送文件消息

}

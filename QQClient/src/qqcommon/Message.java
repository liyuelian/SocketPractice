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

    //进行扩展 和文件相关的成员
    private byte[] fileBytes;
    private int fileLen = 0;
    private String dest ;//将文件传输到哪里
    private String src;//源文件路径

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public int getFileLen() {
        return fileLen;
    }

    public void setFileLen(int fileLen) {
        this.fileLen = fileLen;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

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

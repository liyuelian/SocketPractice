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

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }


    public void setUerId(String uerId) {
        this.userId = uerId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

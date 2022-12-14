# 多用户即时通讯系统01

## 1.项目开发流程

![](https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/%E9%A1%B9%E7%9B%AE%E5%BC%80%E5%8F%91%E6%B5%81%E7%A8%8B.png)



## 2.需求分析

1. 用户登录
2. 拉取在线用户列表
3. 无异常退出（包括客户端和服务端）
4. 私聊
5. 群聊
6. 发文件
7. 服务器推送新闻/广播



## 3.设计阶段

#### 3.1界面设计

1. 用户登录：

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220920182438430.png" alt="image-20220920182438430" style="zoom:67%;" />

2. 拉取在线用户列表:

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220920182554305.png" alt="image-20220920182554305" style="zoom:67%;" />



3. 私聊：

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220920182653008.png" alt="image-20220920182653008" style="zoom: 50%;" />

***

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220920182712818.png" alt="image-20220920182712818" style="zoom:50%;" />



4. 群聊：

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220920182846445.png" alt="image-20220920182846445" style="zoom:50%;" />

***

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220920182903138.png" alt="image-20220920182903138" style="zoom:50%;" />

***

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220920182920638.png" alt="image-20220920182920638" style="zoom:50%;" />



5. 发文件：

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220920183046013.png" alt="image-20220920183046013" style="zoom: 67%;" />

***

<img src="https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/image-20220920183214104.png" alt="image-20220920183214104" style="zoom: 67%;" />



6. 文件服务器推送新闻：

![](https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%B9%BF%E6%92%AD%E7%95%8C%E9%9D%A2%E8%AE%BE%E8%AE%A1.png)



#### 3.2通讯系统整体设计



![](https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/%E9%80%9A%E8%AE%AF%E7%B3%BB%E7%BB%9F%E6%95%B4%E4%BD%93%E5%88%86%E6%9E%903.0.png)

![](https://liyuelian.oss-cn-shenzhen.aliyuncs.com/imgs/%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%E6%95%B4%E4%BD%93%E5%88%86%E6%9E%904.0.png)



1. 对传输数据的分析：

   因为在通讯的时候信息的种类和信息比较多，如果使用文本的方式来传递数据，那么服务器拿到信息的时候对其进行拆解会很麻烦。因此使用对象的方式来进行数据的传输（同时使用对象流来传输）

   

2. 对于socket的分析：

   在客户端连接服务器的过程中，服务器端通过端口监听，通过accept方法返回，得到一个Socket对象。客户端和服务端就是通过Socket来获取数据通道上的信息来进行相互通讯的。这意味着服务器在监听的过程中，随着连接的客户端数量的增多，服务端对应产生的Socket对象也会不断增多。

   考虑一个问题：如果服务端要同时和多个客户端进行通讯，怎么样才能保持服务端产生的所有Socket对象，分别同时和许多客户端进行通讯呢？答案是使用线程！

   

3. 对服务端socket的线程的分析：

   服务端广播的时候，服务器需要对所有客户端进行通知，需要对服务器端的所有socket进行处理，为了便于管理，这时候就需要有一个管理线程的集合

   

4. 对客户端socket的分析：

   在服务端里，有一个管理线程的集合，集合中的每个线程都有一个socket，每个socket都对应着一个数据通道。现在来考虑：在一个客户端中，也有可能有与服务器创建多个连接，也就是一个客户端和服务端产生多个数据通道的情况，即在一个客户端中，也有可能产生多个socket。因此在客户端也应该使用线程。

   同时为了便于管理，客户端也应该有一个管理线程的集合。



**总结：**

服务端：

1. 当有客户端连接到服务器后，服务端会得到一个socket

2. 启动一个线程，该线程持有该socket对象，也就是说socket是该线程的属性

3. 为了更好的管理线程，需要使用集合来管理（如:HashMap）

客户端：

1. 和服务端通信时，使用对象方式，可以使用对象流来读写

2. 当客户端连接到服务端后，也会得到socket

3. 启动一个线程，该线程持有socket

4. 为了更好的管理线程，也将该线程放入到集合中



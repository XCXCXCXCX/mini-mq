







## 第一阶段 设计

### 需求分析（√）

### 系统架构设计（√）

### 项目架构设计（√）



## 第二阶段 开发

### 抽象类、接口、基类设计（√）

### tools模块开发（√）

提供工具类：

1. jvm监控（√）
2. 配置文件加载（√）
3. 日志分类（√）
4. 线程组和线程池管理（√）
5. 方法调用耗时情况监控（√）



#### jvm监控（√）

* 监控内容：jstack、jmap（√）
* 定期dump（√）

#### 配置文件加载（√）

* 配置文件加载器（typesafe提供）（√）
* 配置文件java类（√）

#### 日志分类（√）

* 日志管理类（√）

#### 线程组和线程池管理（√）

* 线程组管理类（√）
* 线程池管理类（包括线程池分类、线程池默认配置、线程池配置java类）（√）


#### 方法调用耗时情况监控（√）

* 耗时监控类（需要能监控树形结构的方法耗时情况及平均值/最大值/最小值）


### network模块开发（√）

提供底层通讯类：

1. netty server（√）
2. netty client（√）
3. netty connection（√）
4. netty数据包的编解码（√）

#### Netty server（√）

* NettyTcpServer（抽象类，继承自BaseService）

#### Netty client（√）

* NettyTcpClient（抽象类，继承自BaseService）

#### Netty connection（√）

* NettyConnection（实现类，实现Connection接口）

#### Netty数据包的编解码（√）

* PacketDecoder（实现类，继承自ByteToMessageDecoder，是Netty通信的组件）
* PacketEncoder（实现类，继承自MessageToByteEncoder，是Netty通信的组件）



### common模块开发

提供底层通用类：

1. Packet/PacketWrapper（所有的Packet类型）（√）
2. PacketHandler（所有的PacketHandler类型）（√）
3. PacketDispatcher（消息分发器）（√）
4. Topic（√）
5. Patition（√）
6. PatitionRouter
7. ConsumerRouter
8. FlowControl
9. producer（√）
10. consumer（√）



### monitor模块开发

提供监控类：

1. jvm信息定期dump
2. 线程信息和线程状态统计
3. 线程池配置和线程池状态统计
4. 流量监控



### registry模块开发

提供服务注册/发现功能：

1. 通用注册中心类
2. zk实现类
3. 提供服务注册/发现的接口



### core模块开发

提供核心功能：

1. 消息存储
2. 消息转发
3. 发送消息/接受消息/处理消息



### client模块开发

提供应用连接API入口：

1. 创建producer并连接到broker
2. 创建consumer并连接到broker
3. 创建/删除/查看topic
4. producer send接口
5. consumer receive接口
6. consumer receiveAck接口
7. producer/consumer配置类



### spring 整合

提供spring配置集成方式：

1. 使用spring规范创建produer/consumer bean
2. 支持自动配置
3. 整合MiniTemplate模式



### spring cloud stream整合

提供spring cloud stream binder调用模式：

1. 使用spring cloud stream规范配置
2. 按照spring cloud stream规范使用，屏蔽底层实现





## 第三阶段 测试

### 基础功能测试

### 核心功能测试

### 性能测试



## 第四阶段 发布

### 编写mini-mq部署文档

### client API jar包发布

### 提供简单DEMO和性能测试数据

### 发布架构图并介绍核心设计理念
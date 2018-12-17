# mini-mq配置







## 生产者配置

producerId 全局唯一的生产者ID，String类型

topicId 生产者所服务的topicId，String类型

properties 生产者的其他配置，HashMap，可用于扩展，提供给目前未考虑到的配置项

​	MAX_SEND_WINDOW_SIZE 最大发送窗口容量，用于做生产者的流控





## 消费者配置

consumerGroupId 全局唯一的消费组ID，String类型

topicId 消费者所订阅的topicId，String类型

properties 消费者的其他配置，HashMap，可用于扩展，提供给目前未考虑到的配置项

​	MAX_FETCH_MESSAGE_NUM 最大拉取消息数量，用于做消费者的流控

​	MAX_FETCH_MESSAGE_SIZE 最大拉取消息的容量，用于做消费者的流控





## broker配置
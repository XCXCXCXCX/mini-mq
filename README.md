# mini-mq
基于netty实现的轻量级消息中间件mini-mq，致力于解决分布式事务问题，支持可靠性消息，并具备较强的扩展性和较完善的监控系统。        
    
## 1.0版本(预计)     
       
-1>. 对于同一条消息，支持生产者生产且仅生产一次消息并持久化到mq        
    
-2>. 对于同一条消息，支持消费者消费且仅消费一次消息        
     
-3>. 支持jvm监控、线程监控、耗时监控、流量控制    
     
-4>. 默认使用fastjson序列化，支持扩展  
    
-5>. 提供spi扩展的方式进行扩展      
    
-6>. 支持静态配置文件进行全面配置    
    
-7>. 持久化方式目前仅支持db(mysql)，支持扩展    
    
-8>. 支持多机同组多消费者竞争消费    
    

package com.xcxcxcxcx.persistence.db.mybatis;

import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.spi.Spi;
import com.xcxcxcxcx.mini.api.spi.persistence.MessageStatusEntity;
import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceService;
import com.xcxcxcxcx.mini.api.spi.persistence.TopicEntity;
import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import com.xcxcxcxcx.mini.tools.log.LogUtils;
import com.xcxcxcxcx.persistence.db.mybatis.mapper.MessageMapper;
import com.xcxcxcxcx.persistence.db.mybatis.mapper.MessageStatusMapper;
import com.xcxcxcxcx.persistence.db.mybatis.mapper.TopicMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
@Spi(order = 1)
public class DbService implements PersistenceService<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbService.class);

    /**
     * 消息等待确认超时时间默认为3分钟
     */
    public static final long MESSAGE_WAIT_ACK_EXPIRED = 300 * 1000;

    /**
     * 消息等待处理超时时间默认为3分钟
     */
    public static final long MESSAGE_PROCCESS_EXPIRED = 300 * 1000;

    /**
     * 消息最大pulled次数默认为5次
     */
    public static final int MESSAGE_MAX_PULLED = 5;

    private SqlSessionFactory sqlSessionFactory;

    private void init() throws IOException {
        //加载配置
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);

        //构建properties
        Properties properties = new Properties();
        properties.setProperty("url", MiniConfig.mini.dataSource.url);
        properties.setProperty("username", MiniConfig.mini.dataSource.username);
        properties.setProperty("password", MiniConfig.mini.dataSource.password);

        //创建SqlSessionFactory
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, properties);

    }

    public DbService() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取mybatis-config.xml配置文件失败");
        }
    }

    /**
     * push消息
     *
     * insert message
     * @param message
     * @return
     */
    @Override
    public Message push(Message message) {

        List<Message> messages = batchPush(Collections.singletonList(message));
        if(messages == null || messages.size() < 1){
            return null;
        }

        return messages.get(0);
    }

    @Override
    public List<Message> batchPush(List<Message> messages) {
        if(messages == null || messages.isEmpty()){
            return null;
        }
        SqlSession session = sqlSessionFactory.openSession(false);
        MessageMapper messageMapper = session.getMapper(MessageMapper.class);
        try {
            messageMapper.batchPush(messages);
            List<Long> ids = messages.stream().map(message -> message.getMid()).collect(Collectors.toList());
            List<Message> result = messageMapper.queryById(ids, Message.MessageStatus.NEW.getId());
            session.commit();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            session.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    @Override
    public Long ackPush(Long id) {
        List<Long> ids = batchAckPush(Collections.singletonList(id));
        if(ids == null || ids.size() < 1){
            return null;
        }

        return ids.get(0);
    }

    @Override
    public List<Long> batchAckPush(List<Long> ids) {
        if(ids == null || ids.isEmpty()){
            return null;
        }
        SqlSession session = sqlSessionFactory.openSession(false);
        MessageMapper messageMapper = session.getMapper(MessageMapper.class);
        try {
            messageMapper.batchAckPush(ids);
            List<Long> resultIds =
                    messageMapper.queryIdById(ids, Message.MessageStatus.NEW_ACK.getId());
            session.commit();
            return resultIds;
        }catch (Exception e){
            e.printStackTrace();
            session.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    @Override
    public Long rejectPush(Long id) {
        List<Long> ids = batchRejectPush(Collections.singletonList(id));
        if(ids == null || ids.size() < 1){
            return null;
        }

        return ids.get(0);
    }

    @Override
    public List<Long> batchRejectPush(List<Long> ids) {
        if(ids == null || ids.isEmpty()){
            return null;
        }
        SqlSession session = sqlSessionFactory.openSession(false);
        MessageMapper messageMapper = session.getMapper(MessageMapper.class);
        try {
            if(ids == null || ids.isEmpty()){
                return null;
            }
            messageMapper.batchRejectPush(ids);
            session.commit();
            return ids;
        }catch (Exception e){
            e.printStackTrace();
            session.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    /**
     * 第一次pull，插入记录
     * @param topicId
     * @param consumerGroupId
     * @param key
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<Message> prePullIfAbsent(String topicId,
                                         String consumerGroupId,
                                         String key,
                                         Integer pageNum,
                                         Integer pageSize) {
        SqlSession session = sqlSessionFactory.openSession(false);
        MessageStatusMapper messageStatusMapper = session.getMapper(MessageStatusMapper.class);

        try {
            List<Long> messageIds = messageStatusMapper.
                    queryAbsent(topicId,consumerGroupId,key,pageNum,pageSize);
            if(messageIds == null || messageIds.isEmpty()){
                return null;
            }
            List<MessageStatusEntity> messageStatusEntities = messageIds.stream()
                    .map(id -> {
                        MessageStatusEntity entity = new MessageStatusEntity();
                        entity.setMid(id);
                        entity.setConsumerGroupId(consumerGroupId);
                        entity.setExpired(System.currentTimeMillis() + MESSAGE_PROCCESS_EXPIRED);
                        return entity;
                    }).collect(Collectors.toList());
            messageStatusMapper.batchInsert(messageStatusEntities);
            List<Message> result = messageStatusMapper
                    .queryByIdsAndGroup(messageIds, consumerGroupId, Message.MessageStatus.PROCCESSIGN.getId());
            session.commit();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            session.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    /**
     * 不是第一次pull，筛选出被客户端确认拒绝的消息，重试消费
     * @param topicId
     * @param consumerGroupId
     * @param key
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<Message> prePull(List<Long> messageRejectIds,
                                 String topicId,
                                 String consumerGroupId,
                                 String key,
                                 Integer pageNum,
                                 Integer pageSize) {
        if(messageRejectIds == null || messageRejectIds.isEmpty()){
            return null;
        }
        SqlSession session = sqlSessionFactory.openSession(false);
        MessageStatusMapper messageStatusMapper = session.getMapper(MessageStatusMapper.class);

        try {
            List<Long> ids = messageStatusMapper.
                    queryNotAbsent(messageRejectIds, topicId, consumerGroupId, key, pageNum,pageSize);
            messageStatusMapper.batchUpdate(ids, consumerGroupId, MESSAGE_MAX_PULLED,
                    System.currentTimeMillis() + MESSAGE_PROCCESS_EXPIRED);
            List<Message> result =
                    messageStatusMapper.queryByIdsAndGroup(ids, consumerGroupId, Message.MessageStatus.PROCCESSIGN.getId());
            session.commit();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            session.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    @Override
    public Long ackPull(Long id, String consumerGroupId) {
        List<Long> ids = batchAckPull(Collections.singletonList(id), consumerGroupId);
        if(ids == null || ids.size() < 1){
            return null;
        }

        return ids.get(0);
    }

    @Override
    public List<Long> batchAckPull(List<Long> ids, String consumerGroupId) {
        if(ids == null || ids.isEmpty()){
            return null;
        }

        SqlSession session = sqlSessionFactory.openSession(false);
        MessageStatusMapper messageStatusMapper = session.getMapper(MessageStatusMapper.class);
        try {
            messageStatusMapper.batchAckPull(ids, consumerGroupId);
            List<Long> result = messageStatusMapper.queryIdByIdsAndGroup(ids, consumerGroupId, 3);
            session.commit();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            session.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    @Override
    public Long rejectPull(Long id, String consumerGroupId) {
        List<Long> ids = batchRejectPull(Collections.singletonList(id), consumerGroupId);
        if(ids == null || ids.size() < 1){
            return null;
        }

        return ids.get(0);
    }

    @Override
    public List<Long> batchRejectPull(List<Long> ids, String consumerGroupId) {
        if(ids == null || ids.isEmpty()){
            return null;
        }

        SqlSession session = sqlSessionFactory.openSession(false);
        MessageStatusMapper messageStatusMapper = session.getMapper(MessageStatusMapper.class);
        try {
            messageStatusMapper.batchRejectPull(ids, consumerGroupId);
            List<Long> result = messageStatusMapper.queryIdByIdsAndGroup(ids, consumerGroupId, 4);
            session.commit();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            session.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    @Override
    public Boolean cleanExpired() {
        SqlSession session = sqlSessionFactory.openSession(false);
        MessageMapper messageMapper = session.getMapper(MessageMapper.class);

        try {
            messageMapper.cleanExpired();
            session.commit();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            session.rollback();
            return null;
        }finally {
            session.close();
        }
    }

    /**
     * 创建topic
     *
     * @param topicId
     * @return
     */
    public Boolean createTopic(String topicId, Integer partitionNum) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            TopicMapper mapper = sqlSession.getMapper(TopicMapper.class);

            int row = mapper.createTopic(topicId, partitionNum);
            sqlSession.commit();
            return row > 0;
        }catch (Exception e){
            LogUtils.persistence.error("create topic error : " + e);
            sqlSession.rollback();
            return false;
        }finally {
            sqlSession.close();
        }

    }
    /**
     * 删除topic，同时删除属于它的消息
     * 需要开启事务
     *
     * @param topicId
     * @return
     */
    public Boolean removeTopic(String topicId) {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);

        try {
            TopicMapper mapper = sqlSession.getMapper(TopicMapper.class);
            MessageMapper mapper2 = sqlSession.getMapper(MessageMapper.class);

            int row = mapper.removeTopic(topicId);
            mapper2.deleteByTopicId(topicId);

            sqlSession.commit();
            return row > 0;
        }catch (Exception e){
            LogUtils.persistence.error("remove topic error : " + e);
            sqlSession.rollback();
            return false;
        }finally {
            sqlSession.close();
        }
    }

    @Override
    public List<TopicEntity> loadAllTopic() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        TopicMapper mapper = sqlSession.getMapper(TopicMapper.class);
        try{
            return mapper.getAllTopic();
        }finally {
            sqlSession.close();
        }
    }

    public static void main(String[] args) {
        DbService service = new DbService();
        SqlSession session = service.sqlSessionFactory.openSession();
        MessageMapper mapper = session.getMapper(MessageMapper.class);
        TopicMapper mapper2 = session.getMapper(TopicMapper.class);

        List<Message> messages = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (int i = 4; i < 100; i++) {
            Message message = new Message();
            Long id = Long.parseLong("" + i);
            message.setMid(id);
            message.setTopicId("topicIdtopicId" + i);
            message.setStatus(0);
            message.setExpired(100000L + i);
            message.setContent("contentcontent" + i);
            //mapper.updateSelective(message);
            System.out.println(message);
            messages.add(message);
            ids.add(id);
        }


        mapper2.createTopic("testTopic",1);

        System.out.println(mapper2.getAllTopic().toString());

        mapper2.removeTopic("testTopic");

        session.close();

//        List<Message> messageList3 = mapper.query(null,null,
//                null,null,
//                null,null,
//                null,1,10);
//        System.out.println("commit after query:" + messageList3);

    }
}

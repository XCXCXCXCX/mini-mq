package com.xcxcxcxcx.persistence.db.mybatis;

import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.spi.Spi;
import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceService;
import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import com.xcxcxcxcx.mini.tools.log.LogUtils;
import com.xcxcxcxcx.persistence.db.mybatis.mapper.MessageMapper;
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

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
@Spi(order = 1)
public class DbService implements PersistenceService<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbService.class);

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

    private SqlSession openSession() {
        //生产SqlSession
        return sqlSessionFactory.openSession(ExecutorType.BATCH, false);
    }

    private void commitSession(SqlSession sqlSession) {
        sqlSession.commit();
    }

    private void closeSession(SqlSession sqlSession) {
        sqlSession.close();
    }

    public Boolean save(Message message) {
        return saveList(Collections.singletonList(message));
    }

    public Boolean saveList(List<Message> messages) {
        SqlSession session = openSession();
        try {
            MessageMapper mapper = session.getMapper(MessageMapper.class);

            for(Message message : messages){
                mapper.updateSelective(message);
            }

            commitSession(session);
            return true;
        } catch (Exception e) {
            LOGGER.error("batchUpdate error : messages = " + messages.toString() + ", exception = " + e);
            return false;
        } finally {
            closeSession(session);
        }
    }

    public Boolean saveIfAbsent(Message message) {
        return saveListIfAbsent(Collections.singletonList(message));
    }

    public Boolean saveListIfAbsent(List<Message> messages) {
        SqlSession session = openSession();
        try {
            MessageMapper mapper = session.getMapper(MessageMapper.class);

            for(Message message : messages){
                mapper.insertSelective(message);
            }

            commitSession(session);
            return true;
        } catch (Exception e) {
            LOGGER.error("batchInsert error : messages = " + messages.toString() + ", exception = " + e);
            return false;
        } finally {
            closeSession(session);
        }
    }

    public Boolean remove(Long id) {
        SqlSession session = openSession();
        try {
            MessageMapper mapper = session.getMapper(MessageMapper.class);

            mapper.batchDelete(Collections.singletonList(id),null,null,null,null);

            commitSession(session);
            return true;
        } catch (Exception e) {
            LOGGER.error("batchDelete error : exception = " + e);
            return false;
        } finally {
            closeSession(session);
        }
    }

    public Boolean removeList(List<Long> idList, String topicId, Integer status, Integer pulledTimes, Long expired) {
        SqlSession session = openSession();
        try {
            MessageMapper mapper = session.getMapper(MessageMapper.class);

            mapper.batchDelete(idList, topicId, status, pulledTimes, expired);

            commitSession(session);
            return true;
        } catch (Exception e) {
            LOGGER.error("batchDelete error : messageIds = " + idList.toString() + ", exception = " + e);
            return false;
        } finally {
            closeSession(session);
        }
    }

    public List<Message> query(Message message, int pageNum, int pageSize) {
        SqlSession session = openSession();
        try {
            MessageMapper mapper = session.getMapper(MessageMapper.class);

            List<Message> messages = mapper.query(message, pageNum, pageSize);

            commitSession(session);
            return messages;
        } catch (Exception e) {
            LOGGER.error("query error : exception = " + e);
            return null;
        } finally {
            closeSession(session);
        }
    }

    /**
     * 创建topic
     *
     * @param topicId
     * @return
     */
    public Boolean createTopic(String topicId) {
        SqlSession session = openSession();
        try {
            TopicMapper mapper = session.getMapper(TopicMapper.class);

            int row = mapper.createTopic(topicId);

            commitSession(session);
            return row > 0 ? true : false;
        } catch (Exception e) {
            LogUtils.persistence.error("create topic error : " + e);
            return false;
        } finally {
            closeSession(session);
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
        SqlSession session = openSession();

        try {
            TopicMapper mapper = session.getMapper(TopicMapper.class);
            MessageMapper mapper2 = session.getMapper(MessageMapper.class);

            int row = mapper.removeTopic(topicId);
            mapper2.batchDelete(null, topicId, null, null, null);

            commitSession(session);
            return row > 0 ? true : false;
        } catch (Exception e) {
            LogUtils.persistence.error("remove topic error : " + e);
            session.rollback();
            return false;
        } finally {
            closeSession(session);
        }
    }

    public static void main(String[] args) {
        DbService service = new DbService();
        SqlSession session = service.openSession();
        MessageMapper mapper = session.getMapper(MessageMapper.class);

        List<Message> messages = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (int i = 2; i < 100; i++) {
            Message message = new Message();
            Long id = Long.parseLong("" + i);
            message.setMid(id);
            message.setTopicId("topicIdtopicId" + i);
            message.setStatus(0);
            message.setPulledTimes(0);
            message.setExpired(100000L + i);
            message.setContent("contentcontent" + i);
            //mapper.insertSelective(message);
            message.setPulledTimes(3);
            //mapper.updateSelective(message);
            System.out.println(message);
            ids.add(id);
            messages.add(message);
        }

        System.out.println(mapper.query(new Message(),1,10));

        //mapper.batchDelete(ids, null, null, null, null);

        session.commit();
        //System.out.println(messages);

        System.out.println(mapper.query(new Message(),1,20));
        session.close();

    }
}

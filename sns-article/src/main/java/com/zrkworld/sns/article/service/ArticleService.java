package com.zrkworld.sns.article.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.zrkworld.sns.article.client.NoticeClient;
import com.zrkworld.sns.article.mapper.ArticleMapper;
import com.zrkworld.sns.article.pojo.Article;
import com.zrkworld.sns.article.pojo.Notice;
import jdk.nashorn.internal.ir.CallNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/10 0010 23:15
 */
@Service
public class ArticleService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private ArticleMapper articleMapper;
    @Autowired
    private NoticeClient noticeClient;

    @Resource
    private IdWorker idWorker;

    public List<Article> findAll() {
        return articleMapper.selectList(null);
    }

    public Article findById(String articleId) {
        return articleMapper.selectById(articleId);
    }

    public void save(Article article) {
        //TODO: 使用jwt鉴权获取当前用户的信息，用户id，也就是文章作者id
        String userId = "3";
        article.setUserid(userId);

        //使用分布式id生成器
        String id = idWorker.nextId() + "";
        article.setId(id);

        //初始化数据
        article.setVisits(0);   //浏览量
        article.setThumbup(0);  //点赞数
        article.setComment(0);  //评论数

        //新增
        articleMapper.insert(article);

        //新增文章后，创建消息，通知给订阅者


        //获取订阅者信息
        //存放作者订阅者信息的集合key，里面存放订阅者id
        String authorKey = "article_author_" + userId;
        Set<String> set = redisTemplate.boundSetOps(authorKey).members();
        Notice notice = null;
        // 给订阅者创建消息通知
        for (String uid : set) {
            // 创建消息对象
            notice = new Notice();

            // 接收消息用户的ID
            notice.setReceiverId(uid);
            // 进行操作用户的ID
            notice.setOperatorId(userId);
            // 操作类型（评论，点赞等）
            notice.setAction("publish");
            // 被操作的对象，例如文章，评论等
            notice.setTargetType("article");
            // 被操作对象的id，例如文章的id，评论的id'
            notice.setTargetId(id);
            // 通知类型
            notice.setType("sys");

            noticeClient.save(notice);
        }


    }

    public void updateById(Article article) {
        // 根据主键id修改
        articleMapper.updateById(article);

        // 根据条件修改
        // 创建条件对象，是Wrapper抽象类的实体子类
        // EntityWrapper<Article> wrapper = new EntityWrapper<>();
        // 设置条件(eq是equals的缩写)
        // wrapper.eq("id", article.getId());
        // articleMapper.update(article, wrapper);
    }

    public void deleteById(String articleId) {
        articleMapper.deleteById(articleId);
    }

    public Page<Article> findByPage(Map<String, Object> map, Integer page, Integer size) {
        //设置查询条件，这里注意EntityWrapper已经被废弃
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {

            //第一个参数是否把后面的条件加入到查询条件中
            //eq方法有两个重载版本，区别是是否有第一个boolean，用来实现动态sql

            wrapper.eq(map.get(key) != null, key, map.get(key));
        }

        //设置分页参数
        Page<Article> pageData = new Page<>(page, size);

        //执行查询
        /**
         * 3.x 的 page 可以进行取值,多个入参记得加上注解
         * 自定义 page 类必须放在入参第一位
         * 返回值可以用 IPage<T> 接收 也可以使用入参的 MyPage<T> 接收
         */
        //第一个是分页参数，第二个是查询条件
        IPage<Article> iPage = articleMapper.selectPage(pageData, wrapper);

        pageData.setRecords(iPage.getRecords());

        //返回
        return pageData;
    }

    public boolean subscribe(String articleId, String userId) {
        //根据文章id查询作者id
        String authorId = articleMapper.selectById(articleId).getUserid();

        //存放用户订阅的集合key,存放作者id
        String userKey = "article_subscribe_"+userId;
        //存放作者粉丝的集合key,里面存放用户id
        String authorKey = "article_author_" + authorId;
        //查询用户的订阅关系,是否有订阅该作者
        Boolean flag = redisTemplate.boundSetOps(userKey).isMember(authorId);
        if(flag==true) {
            //如果订阅了,就取消订阅
        redisTemplate.boundSetOps(userKey).remove(authorId);
        redisTemplate.boundSetOps(authorKey).remove(userId);

        return false;
        }else {
            //没有订阅就进行订阅
            redisTemplate.boundSetOps(userKey).add(authorId);
            redisTemplate.boundSetOps(authorKey).add(userId);
            return true;

        }
    }



    //文章点赞
    public void thumpup(String articleId, String userId) {
        Article article = articleMapper.selectById(articleId);
        article.setThumbup(article.getThumbup() + 1);
        articleMapper.updateById(article);

        //点赞成功后，需要发送消息给文章作者（点对点消息）
        Notice notice = new Notice();
        // 接收消息用户的ID
        notice.setReceiverId(article.getUserid());
        // 进行操作用户的ID
        notice.setOperatorId(userId);
        // 操作类型（评论，点赞等）
        notice.setAction("publish");
        // 被操作的对象，例如文章，评论等
        notice.setTargetType("article");
        // 被操作对象的id，例如文章的id，评论的id'
        notice.setTargetId(articleId);
        // 通知类型
        notice.setType("user");

        //保存消息
        noticeClient.save(notice);
    }
}

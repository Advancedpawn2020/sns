package com.zrkworld.sns.article.service;

import com.zrkworld.sns.article.pojo.Comment;
import com.zrkworld.sns.article.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/11 0010 23:07
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IdWorker idWorker;
    //使用MongoDB模板来操作较复杂的功能，如点赞修改点赞值
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Comment> findAll() {
        List<Comment> list = commentRepository.findAll();
        return list;
    }

    public Comment findById(String commentId) {
        //JDK1.8后出现的新类，可以用使用.get()来获取真正的值，但可以来防止空指针
        Optional<Comment> optional = commentRepository.findById(commentId);
        /*
         *         if(optional.isPresent()){
         *             return optional.get();
         *         }
         *         这句可以被下面一行替代，意思是如果当前对象不为空返回，为空则返回null
         */
        return optional.orElse(null);
    }

    public void save(Comment comment) {
        //分布式id生成器生成id
        String id = idWorker.nextId() + "";
        comment.set_id(id);

        //初始化点赞数据，发布时间等
        comment.setThumbup(0);
        comment.setPublishdate(new Date());

        //保存数据
        commentRepository.save(comment);
    }

    public void updateById(Comment comment) {
        //使用的是MongoRepository的方法
        //其中save方法，主键如果存在，执行修改，如果不存在执行新增
        commentRepository.save(comment);
    }

    public void deleteById(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Comment> findByArticleId(String articleId) {
        //调用持久层，根据文章id查询
        List<Comment> list = commentRepository.findByArticleid(articleId);
        return list;
    }

    public void thumbup(String commentId) {
        // //根据评论id查询评论数据，这种方法有线程安全的问题！！！需要用分布式锁来解决，用redis/zookeeper
        // Comment comment = commentRepository.findById(commentId).get();
        // //对评论点赞数据加一
        // comment.setThumbup(comment.getThumbup() + 1);
        // //保存修改数据
        // commentRepository.save(comment);

        //点赞功能优化
        //封装修改的条件
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(commentId));

        //封装修改的数值
        Update update = new Update();
        //使用inc列值增长
        update.inc("thumbup", 1);

        //直接修改数据
        //第一个参数是修改的条件
        //第二个参数是修改的数值
        //第三个参数是MongoDB的集合名称
        mongoTemplate.updateFirst(query, update, "comment");
    }
}

package com.zrkworld.sns.article.repository;
import com.zrkworld.sns.article.pojo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/12 0012 1:03
 * MongoDB的数据接入层，不能和mysql的dao放在同一个包中
 * 和mybatisplus的mapper类使用方法一样，都需要继承父类，其中继承父类的泛型：操作的数据类型、主键类型
 */
    public interface CommentRepository extends MongoRepository<Comment, String> {

        //SpringDataMongoDB，支持通过查询方法名进行查询定义的方式（直接在方法名上写要查询的字段，动态代理时会获取方法名，得到条件，然后进行组装代码代理映射）
        //根据文章id查询文章评论数据
        List<Comment> findByArticleid(String articleId);


        //根据发布时间和点赞数查询查询
        // List<Comment> findByPublishdateAndThumbup(Date date, Integer thumbup);

        //根据用户id查询，并且根据发布时间倒序排序
        // List<Comment> findByUseridOrderbOrderByPublishdateDesc(String userid);
    }



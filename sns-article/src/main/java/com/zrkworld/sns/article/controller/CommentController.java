package com.zrkworld.sns.article.controller;


import com.zrkworld.sns.article.pojo.Comment;
import com.zrkworld.sns.article.service.CommentService;
import entity.Result;
import entity.StatusCode;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/11 0010 23:06
 */
@RestController
@RequestMapping("comment")
public class CommentController {
    /**
     * 和mybatis一样，在注入时使用动态代理实现了接口，将实现了接口的实现类注入至引用
     */
    @Resource
    private CommentService commentService;

    @Resource
    private RedisTemplate redisTemplate;

    //PUT /comment/thumbup/{commentId} 根据评论id点赞评论
    @RequestMapping(value = "thumbup/{commentId}", method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String commentId) {
        //把用户点赞信息保存到Redis中
        //每次点赞之前，先查询用户点赞信息
        //如果没有点赞信息，用户可以点赞
        //如果有点赞信息，用户不能重复点赞
        //也可以换成，重复点赞时去掉赞

        //模拟用户id
        String userId = "123";
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //查询用户点赞信息，根据用户id和评论id
        Object flag = redisTemplate.opsForValue().get("thumbup_" + userId + "_" + commentId);

        //判断查询到的结果是否为空
        if (flag == null) {
            //如果为空，表示用户没有点过赞，可以点赞
            commentService.thumbup(commentId);

            //点赞成功，保存点赞信息
            redisTemplate.opsForValue().set("thumbup_" + userId + "_" + commentId, 1);

            return new Result(true, StatusCode.OK, "点赞成功");
        }

        //如果不为空，表示用户点过赞，不可以重复点赞
        return new Result(false, StatusCode.REPERROR, "不能重复点赞");

    }

    //GET /comment/article/{articleId} 根据文章id查询文章评论
    @RequestMapping(value = "article/{articleId}", method = RequestMethod.GET)
    public Result findByArticleId(@PathVariable String articleId) {
        List<Comment> list = commentService.findByArticleId(articleId);
        return new Result(true, StatusCode.OK, "查询成功", list);
    }

    //GET /comment 查询所有评论
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        List<Comment> list = commentService.findAll();

        return new Result(true, StatusCode.OK, "查询成功", list);
    }


    //GET /comment/{commentId} 根据评论id查询评论数据
    @RequestMapping(value = "{commentId}", method = RequestMethod.GET)
    public Result findById(@PathVariable String commentId) {
        Comment comment = commentService.findById(commentId);
        return new Result(true, StatusCode.OK, "查询成功", comment);
    }

    //POST /comment 新增评论
    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Comment comment) {
        commentService.save(comment);
        return new Result(true, StatusCode.OK, "新增成功");
    }

    //PUT /comment/{commentId} 修改评论
    @RequestMapping(value = "{commentId}", method = RequestMethod.PUT)
    public Result updateById(@PathVariable String commentId, @RequestBody Comment comment) {
        //设置评论主键
        comment.set_id(commentId);
        //执行修改
        commentService.updateById(comment);

        return new Result(true, StatusCode.OK, "修改成功");
    }

    //DELETE /comment/{commentId} 根据id删除评论
    @RequestMapping(value = "{commentId}", method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable String commentId) {
        commentService.deleteById(commentId);
        return new Result(true, StatusCode.OK, "删除成功");
    }

}

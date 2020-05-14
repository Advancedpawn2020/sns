package com.zrkworld.sns.article.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrkworld.sns.article.pojo.Article;
import com.zrkworld.sns.article.service.ArticleService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/10 0010 23:16
 */
@RestController
@RequestMapping("article")
//解决跨域注解
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @Autowired
    private RedisTemplate redisTemplate;

    //根据文章id点赞文章
    //http://127.0.0.1:9004/article/thumbup/{articleId} PUT
    @RequestMapping(value = "thumbup/{articleId}", method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String articleId) {
        //TODO 使用JWT鉴权的方式获取当前用户的id
        String userId = "4";

        //查询用户对文章的点赞信息，根据用户id和文章id
        String key = "thumbup_article_" + userId + "_" + articleId;
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        Object flag = redisTemplate.opsForValue().get(key);

        //判断查询到的结果是否为空
        if (flag == null) {
            //如果为空，表示用户没有点过赞，就可以进行点赞操作
            articleService.thumpup(articleId,userId);
            //点赞成功，保存点赞信息
            redisTemplate.opsForValue().set(key, 1);

            return new Result(true, StatusCode.OK, "点赞成功");

        } else {
            //如果不为空，表示用户已经点过赞，不可以重复点赞
            return new Result(false, StatusCode.REPERROR, "不能重复点赞");
        }
    }

    //根据文章id和用户id,建立订阅关系,保存的是该文章作者的id和用户id
    //http:127.0.0.1:9004/article/subscribe post
    @RequestMapping(value = "subscribe", method = RequestMethod.POST)
    public Result subscribe(@RequestBody Map map){
        //返回一个状态,如果返回true就是订阅该文章作者,如果false就是取消订阅
        boolean flag = articleService.subscribe(map.get("articleId").toString(),map.get("userId").toString());
        //判断是订阅还是取消订阅
        if(flag == true) {
            return new Result(true, StatusCode.OK, "订阅成功");
        }else{
            return new Result(true,StatusCode.OK,"取消订阅成功");
        }
    }
    //测试公共异常处理
    @RequestMapping(value = "exception", method = RequestMethod.GET)
    public Result test() {
        int a = 1 / 0;

        return null;
    }

    // POST /article/search/{page}/{size} 文章分页
    @RequestMapping(value = "search/{page}/{size}", method = RequestMethod.POST)
    //之前接受文章数据，使用pojo，但是现在根据条件查询
    //而所有的条件都需要进行判断，遍历pojo的所有属性需要使用反射的方式，成本较高，性能较低（遍历所有属性有三个方法，1用if一个一个判断，2用反射循环判断，3用map循环判断）
    //直接使用集合的方式遍历，这里接受数据改为Map集合
    public Result findByPage(@PathVariable Integer page,
                             @PathVariable Integer size,
                             @RequestBody Map<String, Object> map) {
        //根据条件分页查询
        Page<Article> pageData = articleService.findByPage(map, page, size);

        //封装分页返回对象
        PageResult<Article> pageResult = new PageResult<>(
                pageData.getTotal(), pageData.getRecords()
        );

        //返回数据
        return new Result(true, StatusCode.OK, "查询成功", pageResult);

    }

    // DELETE /article/{articleId} 根据ID删除文章
    @RequestMapping(value = "{articleId}", method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable String articleId) {
        articleService.deleteById(articleId);

        return new Result(true, StatusCode.OK, "删除成功");
    }


    // PUT /article/{articleId} 修改文章
    @RequestMapping(value = "{articleId}", method = RequestMethod.PUT)
    public Result updateById(@PathVariable String articleId,
                             @RequestBody Article article) {
        //设置id
        article.setId(articleId);
        // 执行修改
        articleService.updateById(article);

        return new Result(true, StatusCode.OK, "修改成功");
    }

    // POST /article 增加文章，注意前端传来的是json格式数据，需要用RequestBody注解来转换成对象
    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Article article) {
        articleService.save(article);
        return new Result(true, StatusCode.OK, "新增成功");
    }


    // GET /article/{articleId} 根据ID查询文章
    @RequestMapping(value = "{articleId}", method = RequestMethod.GET)
    public Result findById(@PathVariable String articleId) {
        Article article = articleService.findById(articleId);
        return new Result(true, StatusCode.OK, "查询成功", article);
    }

    // GET /article 文章全部列表
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        List<Article> list = articleService.findAll();
        return new Result(true, StatusCode.OK, "查询成功", list);
    }
}

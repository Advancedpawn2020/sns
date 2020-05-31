package com.zrkworld.sns.article.controller;

import com.zrkworld.sns.article.pojo.Article;
import com.zrkworld.sns.article.service.ArticleService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import io.jsonwebtoken.Claims;
import org.apache.commons.fileupload.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private HttpServletRequest request;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据文章id点赞文章
     * @param articleId
     * @return
     */
    //http://127.0.0.1:9004/article/thumbup/{articleId} PUT
    @RequestMapping(value = "thumbup/{articleId}", method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String articleId) {
        // 验证是否登录，并且拿到当前登录用户的id
        Claims claims_user = (Claims) request.getAttribute("claims_user");
        if (null == claims_user) {
            // 说明当前用户没有user角色
            return new Result(false, StatusCode.LOGINERROR.getCode(), "权限不足");
        }
        // 得到当前登录的用户id
        String userId = claims_user.getId();
        //查询用户对文章的点赞信息，根据用户id和文章id
        String key = "thumbup_article_" + userId + "_" + articleId;
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        Object flag = redisTemplate.opsForValue().get(key);

        //判断查询到的结果是否为空
        if (flag == null) {
            //如果为空，表示用户没有点过赞，就可以进行点赞操作
            articleService.addThumbup(articleId);
            //点赞成功，保存点赞信息
            redisTemplate.opsForValue().set(key, 1);

            return new Result(true, StatusCode.OK.getCode(), "点赞成功");

        } else {
            //如果不为空，表示用户已经点过赞，不可以重复点赞
            return new Result(false, StatusCode.REPERROR.getCode(), "不能重复点赞");
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
            return new Result(true, StatusCode.OK.getCode(), "订阅成功");
        }else{
            return new Result(true,StatusCode.OK.getCode(),"取消订阅成功");
        }
    }


    @GetMapping("/{articleId}")
    public Result findById(@PathVariable String articleId) {
        return new Result(true,  StatusCode.OK.getCode(),  "查询成功",  articleService.findById(articleId));
    }

    /**
     * 查询全部数据
     * @return
     */
    @RequestMapping(method= RequestMethod.GET)
    public Result findAll(){
        return new Result(true, StatusCode.OK.getCode(), "查询成功", articleService.findAll());
    }


    /**
     * 分页+多条件查询
     * @param searchMap 查询条件封装
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @RequestMapping(value="/search/{page}/{size}", method=RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap ,  @PathVariable int page,  @PathVariable int size){
        Page<Article> pageList = articleService.findSearch(searchMap,  page,  size);
        return  new Result(true, StatusCode.OK.getCode(), "查询成功",   new PageResult<Article>(pageList.getTotalElements(),  pageList.getContent()) );
    }

    /**
     * 根据条件查询
     * @param searchMap
     * @return
     */
    @RequestMapping(value="/search", method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true, StatusCode.OK.getCode(), "查询成功", articleService.findSearch(searchMap));
    }

    /**
     * 增加
     * @param article
     */
    @RequestMapping(method=RequestMethod.POST)
    public Result add(@RequestBody Article article  ){
        articleService.add(article);
        return new Result(true, StatusCode.OK.getCode(), "增加成功");
    }

    /**
     * 修改
     * @param article
     */
    @RequestMapping(value="/{id}", method= RequestMethod.PUT)
    public Result update(@RequestBody Article article,  @PathVariable String id ){
        article.setId(id);
        articleService.update(article);
        return new Result(true, StatusCode.OK.getCode(), "修改成功");
    }

    /**
     * 删除
     * @param id
     */
    @RequestMapping(value="/{id}", method= RequestMethod.DELETE)
    public Result delete(@PathVariable String id ){
        articleService.deleteById(id);
        return new Result(true, StatusCode.OK.getCode(), "删除成功");
    }

    /**
     * 文章审核
     * @param articleId
     * @return
     */
    @PutMapping("/examine/{articleId}")
    public Result examine(@PathVariable String articleId) {
        articleService.updateState(articleId);
        return new Result(true,  StatusCode.OK.getCode(),  "审核成功");
    }

}

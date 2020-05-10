package com.zrkworld.sns.article.service;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.zrkworld.sns.article.mapper.ArticleMapper;
import com.zrkworld.sns.article.pojo.Article;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    private ArticleMapper articleMapper;

    @Autowired
    private IdWorker idWorker;

    public List<Article> findAll() {
        Article article = articleMapper.selectById(1);
        return articleMapper.selectList(null);
    }

    public Article findById(String articleId) {
        return articleMapper.selectById(articleId);
    }

    public void save(Article article) {
        //使用分布式id生成器
        String id = idWorker.nextId() + "";
        article.setId(id);

        //初始化数据
        article.setVisits(0);   //浏览量
        article.setThumbup(0);  //点赞数
        article.setComment(0);  //评论数

        //新增
        articleMapper.insert(article);
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
            // if (map.get(key) != null) {
            //     wrapper.eq(key, map.get(key));
            // }

            //第一个参数是否把后面的条件加入到查询条件中
            //和上面的if判断的写法是一样的效果，实现动态sql

            wrapper.eq(map.get(key) != null, key, map.get(key));
        }

        //设置分页参数
        Page<Article> pageData = new Page<>(page, size);

        //执行查询
        //第一个是分页参数，第二个是查询条件
        List<Article> list = articleMapper.selectPage(pageData, wrapper);

        pageData.setRecords(list);

        //返回
        return pageData;
    }
}

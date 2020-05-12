package com.zrkworld.sns.article.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.zrkworld.sns.article.mapper.ArticleMapper;
import com.zrkworld.sns.article.pojo.Article;
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

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private IdWorker idWorker;

    public List<Article> findAll() {
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
}

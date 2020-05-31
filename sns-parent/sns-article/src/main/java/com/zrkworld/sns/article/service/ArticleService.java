package com.zrkworld.sns.article.service;

import com.zrkworld.sns.article.dao.ArticleDao;
import com.zrkworld.sns.article.pojo.Article;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import utils.IdWorker;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文章
 * 
 * @author zrk
 *
 */
@Service
public class ArticleService {

	@Autowired
	private ArticleDao articleDao;
	
	@Autowired
	private IdWorker idWorker;

	@Resource
	private RedisTemplate redisTemplate;

	@Autowired
	private RabbitTemplate rabbitTemplate;

    public void updateState(String id) {
		articleDao.updateState(id);
    }

    public void addThumbup(String id) {
		articleDao.addThumbup(id);
    }

    public Article findById(String id) {
        // 先从缓存中查询当前对象
        Article article = (Article) redisTemplate.opsForValue().get("article_" + id);
        // 如果没有取到
        if (null == article) {
            // 从数据库中查询
            article = articleDao.findById(id).orElse(null);
            // 存入缓存
            redisTemplate.opsForValue().set("article_" + id, article);
        }
        return article;
    }

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Article> findAll() {
		return articleDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Article> findSearch(Map whereMap, int page, int size) {
		Specification<Article> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return articleDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Article> findSearch(Map whereMap) {
		Specification<Article> specification = createSpecification(whereMap);
		return articleDao.findAll(specification);
	}

	/**
	 * 增加
	 * @param article
	 */
	public void add(Article article) {
		article.setId( idWorker.nextId()+"" );
		articleDao.save(article);
		//入库成功后，发送mq消息，内容是消息通知id
		rabbitTemplate.convertAndSend("article_subscribe", article.getUserid(),
				article.getId());
	}

	/**
	 * 修改
	 * @param article
	 */
	public void update(Article article) {
		articleDao.save(article);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		articleDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Article> createSpecification(Map searchMap) {

		return new Specification<Article>() {

			@Override
			public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 专栏ID
                if (searchMap.get("columnid")!=null && !"".equals(searchMap.get("columnid"))) {
                	predicateList.add(cb.like(root.get("columnid").as(String.class), "%"+(String)searchMap.get("columnid")+"%"));
                }
                // 用户ID
                if (searchMap.get("userid")!=null && !"".equals(searchMap.get("userid"))) {
                	predicateList.add(cb.like(root.get("userid").as(String.class), "%"+(String)searchMap.get("userid")+"%"));
                }
                // 标题
                if (searchMap.get("title")!=null && !"".equals(searchMap.get("title"))) {
                	predicateList.add(cb.like(root.get("title").as(String.class), "%"+(String)searchMap.get("title")+"%"));
                }
                // 文章正文
                if (searchMap.get("content")!=null && !"".equals(searchMap.get("content"))) {
                	predicateList.add(cb.like(root.get("content").as(String.class), "%"+(String)searchMap.get("content")+"%"));
                }
                // 文章封面
                if (searchMap.get("image")!=null && !"".equals(searchMap.get("image"))) {
                	predicateList.add(cb.like(root.get("image").as(String.class), "%"+(String)searchMap.get("image")+"%"));
                }
                // 是否公开
                if (searchMap.get("ispublic")!=null && !"".equals(searchMap.get("ispublic"))) {
                	predicateList.add(cb.like(root.get("ispublic").as(String.class), "%"+(String)searchMap.get("ispublic")+"%"));
                }
                // 是否置顶
                if (searchMap.get("istop")!=null && !"".equals(searchMap.get("istop"))) {
                	predicateList.add(cb.like(root.get("istop").as(String.class), "%"+(String)searchMap.get("istop")+"%"));
                }
                // 审核状态
                if (searchMap.get("state")!=null && !"".equals(searchMap.get("state"))) {
                	predicateList.add(cb.like(root.get("state").as(String.class), "%"+(String)searchMap.get("state")+"%"));
                }
                // 所属频道
                if (searchMap.get("channelid")!=null && !"".equals(searchMap.get("channelid"))) {
                	predicateList.add(cb.like(root.get("channelid").as(String.class), "%"+(String)searchMap.get("channelid")+"%"));
                }
                // URL
                if (searchMap.get("url")!=null && !"".equals(searchMap.get("url"))) {
                	predicateList.add(cb.like(root.get("url").as(String.class), "%"+(String)searchMap.get("url")+"%"));
                }
                // 类型
                if (searchMap.get("type")!=null && !"".equals(searchMap.get("type"))) {
                	predicateList.add(cb.like(root.get("type").as(String.class), "%"+(String)searchMap.get("type")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

	/**
	 * 将订阅文章的关系放置到redis中,当有作者发布文章时,可以通过websocket和mq通知用户
	 * @param userId
	 * @param articleId
	 * @return
	 */
	public Boolean subscribe(String userId, String articleId) {
		//根据文章id查询文章作者id
		String authorId = articleDao.findById(articleId).orElse(null).getUserid();;

		//创建Rabbit管理器
		RabbitAdmin rabbitAdmin = new
				RabbitAdmin(rabbitTemplate.getConnectionFactory());
		//声明exchange
		DirectExchange exchange = new
				DirectExchange("article_subscribe");
		rabbitAdmin.declareExchange(exchange);
		//创建queue
		Queue queue = new Queue("article_subscribe_" + userId, true);
		//声明exchange和queue的绑定关系，设置路由键为作者id
		Binding binding =
				BindingBuilder.bind(queue).to(exchange).with(authorId);

		//用户=>作者
		String userKey = "article_subscribe_" + userId;
		//作者=>用户
		String authorKey = "article_author_" + authorId;
		//查询该用户是否已经订阅作者
		Boolean flag =
				redisTemplate.boundSetOps(userKey).isMember(authorId);
		if (flag) {
			//如果为flag为true，已经订阅,则取消订阅
			redisTemplate.boundSetOps(userKey).remove(authorId);
			redisTemplate.boundSetOps(authorKey).remove(userId);
			//删除绑定的队列
			rabbitAdmin.removeBinding(binding);
			return false;
		} else {
			// 如果为flag为false，没有订阅，则进行订阅
			redisTemplate.boundSetOps(userKey).add(authorId);
			redisTemplate.boundSetOps(authorKey).add(userId);
			//声明队列和绑定队列
			rabbitAdmin.declareQueue(queue);
			rabbitAdmin.declareBinding(binding);
			return true;
		}
	}
}

package com.zrkworld.sns.notice.service;

import com.zrkworld.sns.notice.client.ArticleClient;
import com.zrkworld.sns.notice.client.UserClient;
import com.zrkworld.sns.notice.dao.NoticeDao;
import com.zrkworld.sns.notice.dao.NoticeFreshDao;
import com.zrkworld.sns.notice.pojo.Notice;
import com.zrkworld.sns.notice.pojo.NoticeFresh;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import utils.IdWorker;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class NoticeService {

    @Resource
    private NoticeDao noticeDao;

    @Resource
    private NoticeFreshDao noticeFreshDao;

    @Autowired
    private ArticleClient articleClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private IdWorker idWorker;

    //完善消息内容
    private Notice getInfo(Notice notice) {
        //查询用户昵称
        Result userResult = userClient.selectById(notice.getOperatorId());
        HashMap userMap = (HashMap) userResult.getData();
        //设置操作者用户昵称到消息通知中
        notice.setOperatorName(userMap.get("nickname").toString());

        //查询对象名称
        Result articleResult = articleClient.findById(notice.getTargetId());
        HashMap articleMap = (HashMap) articleResult.getData();
        //设置对象名称到消息通知中
        notice.setTargetName(articleMap.get("title").toString());
        return notice;
    }


    public Notice selectById(String id) {
        Notice notice = noticeDao.findById(id).get();

        //完善消息
        getInfo(notice);

        return notice;
    }

    public List<Notice> selectByPage(Map whereMap, int page, int size) {
        Specification<Notice> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page-1,size);
        Page temp =  noticeDao.findAll(specification,pageRequest);
        List<Notice> notices = new ArrayList<Notice>();
        for (Object n : temp.getContent()) {
            notices.add(getInfo((Notice)n));
        }

        //试图使用匿名lambda作为方法对象传入给map,遍历Page对象中的content
        //temp.map(n->getInfo((Notice)n));
        return notices;
    }

    public void save(Notice notice) {
        //设置初始值
        //设置状态 0表示未读  1表示已读
        notice.setState("0");
        notice.setCreatetime(new Date());

        //使用分布式Id生成器，生成id
        String id = idWorker.nextId() + "";
        notice.setId(id);
        noticeDao.save(notice);

        //待推送消息入库，新消息提醒,这里交给rabbitMQ来处理了
//        NoticeFresh noticeFresh = new NoticeFresh();
//        noticeFresh.setNoticeId(id);//消息id
//        noticeFresh.setUserId(notice.getReceiverId());//待通知用户的id
//        noticeFreshMapper.insert(noticeFresh);
    }

    public void updateById(Notice notice) {
        noticeDao.save(notice);
    }

    public Page<NoticeFresh> freshPage(Map whereMap, Integer page, Integer size) {
        Specification<NoticeFresh> specification = createSpecificationWithFresh(whereMap);
        PageRequest pageRequest = PageRequest.of(page-1,size);
        return noticeFreshDao.findAll(specification,pageRequest);
    }

    private Specification<NoticeFresh> createSpecificationWithFresh(Map searchMap) {
        return new Specification<NoticeFresh>() {
            //此处省略了关于创建时间的条件
            @Override
            public Predicate toPredicate(Root<NoticeFresh> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("userid") != null && !"".equals(searchMap.get("userid"))) {
                    predicateList.add(cb.like(root.get("userid").as(String.class), "%" + (String) searchMap.get("userid") + "%"));
                }
                if (searchMap.get("noticeid") != null && !"".equals(searchMap.get("noticeid"))) {
                    predicateList.add(cb.like(root.get("noticeid").as(String.class), "%" + (String) searchMap.get("noticeid") + "%"));
                }
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

            }
        };
    }

    public void freshDelete(NoticeFresh noticeFresh) {
        noticeFreshDao.deleteByUserIdAndNoticeId(noticeFresh.getUserId(),noticeFresh.getNoticeId());
    }
    /**
     * 动态条件构建
     * @param searchMap
     * @return
     */
    private Specification<Notice> createSpecification(Map searchMap) {

        return new Specification<Notice>() {
            //此处省略了关于创建时间的条件
            @Override
            public Predicate toPredicate(Root<Notice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                    predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                if (searchMap.get("receiverId")!=null && !"".equals(searchMap.get("receiverId"))) {
                    predicateList.add(cb.like(root.get("receiverId").as(String.class), "%"+(String)searchMap.get("receiverId")+"%"));
                }
                if (searchMap.get("operatorId")!=null && !"".equals(searchMap.get("operatorId"))) {
                    predicateList.add(cb.like(root.get("operatorId").as(String.class), "%"+(String)searchMap.get("operatorId")+"%"));
                }
                if (searchMap.get("action")!=null && !"".equals(searchMap.get("action"))) {
                    predicateList.add(cb.like(root.get("action").as(String.class), "%"+(String)searchMap.get("action")+"%"));
                }
                if (searchMap.get("targetType")!=null && !"".equals(searchMap.get("targetType"))) {
                    predicateList.add(cb.like(root.get("targetType").as(String.class), "%"+(String)searchMap.get("targetType")+"%"));
                }
                if (searchMap.get("targetId")!=null && !"".equals(searchMap.get("targetId"))) {
                    predicateList.add(cb.like(root.get("targetId").as(String.class), "%"+(String)searchMap.get("targetId")+"%"));
                }
                if (searchMap.get("type")!=null && !"".equals(searchMap.get("type"))) {
                    predicateList.add(cb.like(root.get("type").as(String.class), "%"+(String)searchMap.get("type")+"%"));
                }
                if (searchMap.get("state")!=null && !"".equals(searchMap.get("state"))) {
                    predicateList.add(cb.like(root.get("state").as(String.class), "%"+(String)searchMap.get("state")+"%"));
                }

                return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

            }
        };

    }
}

package com.zrkworld.sns.notice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrkworld.sns.notice.client.ArticleClient;
import com.zrkworld.sns.notice.client.UserClient;
import com.zrkworld.sns.notice.mapper.NoticeFreshMapper;
import com.zrkworld.sns.notice.mapper.NoticeMapper;
import com.zrkworld.sns.notice.pojo.Notice;
import com.zrkworld.sns.notice.pojo.NoticeFresh;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    @Resource
    private NoticeFreshMapper noticeFreshMapper;

    @Autowired
    private ArticleClient articleClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private IdWorker idWorker;

    //完善消息内容
    private void getInfo(Notice notice) {
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
    }


    public Notice selectById(String id) {
        Notice notice = noticeMapper.selectById(id);

        //完善消息
        getInfo(notice);

        return notice;
    }

    public Page<Notice> selectByPage(Notice notice, Integer page, Integer size) {
        //封装分页对象
        Page<Notice> pageData = new Page<>(page, size);

        //执行分页查询
        IPage<Notice> iPage = noticeMapper.selectPage(pageData,new QueryWrapper<>(notice));
        List<Notice> list = iPage.getRecords();
        //完善消息
        for (Notice n : list) {
            getInfo(n);
        }

        //设置结果集到分页对象中
        pageData.setRecords(list);

        //返回
        return pageData;
    }

    public void save(Notice notice) {
        //设置初始值
        //设置状态 0表示未读  1表示已读
        notice.setState("0");
        notice.setCreatetime(new Date());

        //使用分布式Id生成器，生成id
        String id = idWorker.nextId() + "";
        notice.setId(id);
        noticeMapper.insert(notice);

        //待推送消息入库，新消息提醒,这里交给rabbitMQ来处理了
//        NoticeFresh noticeFresh = new NoticeFresh();
//        noticeFresh.setNoticeId(id);//消息id
//        noticeFresh.setUserId(notice.getReceiverId());//待通知用户的id
//        noticeFreshMapper.insert(noticeFresh);
    }

    public void updateById(Notice notice) {
        noticeMapper.updateById(notice);
    }

    public Page<NoticeFresh> freshPage(String userId, Integer page, Integer size) {
        //封装查询条件
        NoticeFresh noticeFresh = new NoticeFresh();
        noticeFresh.setUserId(userId);

        //创建分页对象
        Page<NoticeFresh> pageData = new Page<>(page, size);

        //执行查询
        IPage<NoticeFresh> iPage = noticeFreshMapper.selectPage(pageData,new QueryWrapper<>(noticeFresh));
        List<NoticeFresh> list = iPage.getRecords();

        //设置查询结果集到分页对象中
        pageData.setRecords(list);

        //返回结果
        return pageData;
    }

    public void freshDelete(NoticeFresh noticeFresh) {
        noticeFreshMapper.delete(new QueryWrapper<>(noticeFresh));
    }
}

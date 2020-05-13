package com.zrkworld.sns.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrkworld.sns.base.mapper.LabelMapper;
import com.zrkworld.sns.base.pojo.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LabelService {

    @Resource
    private LabelMapper labelMapper;

    @Resource
    private IdWorker idWorker;

    public List<Label> findAll() {
        //select * from tb_label
        return labelMapper.selectList(null);
    }

    public Label findById(String id) {
        return labelMapper.selectById(id);
    }

    public void add(Label label) {
        label.setId(idWorker.nextId() + "");
        labelMapper.insert(label);
    }

    public void update(Label label) {

        labelMapper.updateById(label);
    }

    public void delete(Label label) {
        labelMapper.deleteById(label.getId());
    }

    public IPage search(Map whereMap, int page, int size) {
        QueryWrapper<Label> wrapper = new QueryWrapper<>();
        Set<String> keySet = whereMap.keySet();
        for (String key : keySet) {

            //第一个参数是否把后面的条件加入到查询条件中
            //eq方法有两个重载版本，区别是是否有第一个boolean，用来实现动态sql

            wrapper.eq(whereMap.get(key) != null, key, whereMap.get(key));
        }
        // 执行查询
        IPage<Label> p = new Page<Label>(page, size);
        p = labelMapper.selectPage(p, wrapper);
        return p;
    }
}

package com.zrkworld.sns.spit.controller;

import com.zrkworld.sns.spit.pojo.Spit;
import com.zrkworld.sns.spit.service.SpitService;
import entity.Result;
import entity.StatusCode;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/spit")
public class SpitController {
    @Resource(name = "spitService")
    private SpitService service;
    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpServletRequest request;
    @GetMapping("")
    public Result findAll() {
        return new Result(true, StatusCode.OK.getCode(), "查询成功", service.findAll());
    }

    @GetMapping("/{spitId}")
    public Result findById(@PathVariable String spitId) {
        return new Result(true, StatusCode.OK.getCode(), "查询成功", service.findById(spitId));
    }

    @PostMapping("")
    public Result save(@RequestBody Spit spit) {
        service.save(spit);
        return new Result(true, StatusCode.OK.getCode(), "保存成功");
    }

    @PutMapping("/{spitId}")
    public Result update(@RequestBody Spit spit, @PathVariable String spitId) {
        spit.set_id(spitId);
        service.update(spit);
        return new Result(true, StatusCode.OK.getCode(), "修改成功");
    }

    @DeleteMapping("/{spitId}")
    public Result delete(@PathVariable String spitId) {
        service.deleteById(spitId);
        return new Result(true, StatusCode.OK.getCode(), "删除成功");
    }

    @PutMapping("/thumbup/{spitId}")
    public Result thumbup(@PathVariable String spitId) {
        // 验证是否登录，并且拿到当前登录用户的id
        Claims claims_user = (Claims) request.getAttribute("claims_user");
        if (null == claims_user) {
            // 说明当前用户没有user角色
            return new Result(false, StatusCode.LOGINERROR.getCode(), "权限不足");
        }
        // 得到当前登录的用户id
        String userId = claims_user.getId();
        // 判断当前用户是否已经点赞
        if (redisTemplate.opsForValue().get("thumbup_spit_" + userId) != null) {
            // 已经点赞了
            return new Result(false, StatusCode.REPERROR.getCode(), "不能重复点赞");
        }
        service.thumbup(spitId);
        redisTemplate.opsForValue().set("thumbup_spit_" + userId, 1);
        return new Result(true, StatusCode.OK.getCode(), "点赞成功");
    }
}

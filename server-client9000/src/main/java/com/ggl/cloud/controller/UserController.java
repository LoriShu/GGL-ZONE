package com.ggl.cloud.controller;

import javax.annotation.Resource;

import com.ggl.cloud.entity.CommonResult;
import com.ggl.cloud.entity.User;
import com.ggl.cloud.service.IUserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("server/user")
@Slf4j
public class UserController {
    @Resource
    private IUserService service;
    @PostMapping("registry")
    public CommonResult registry(@RequestBody User user) {
        log.warn(user.toString());
        return service.registry(user);
    }
    @PostMapping("delete")
    public CommonResult delete(@RequestBody User user) {
        return service.deleteUser(user);
    }

    @PostMapping("update")
    public CommonResult update(@RequestBody User user) {
        return service.updateUser(user);
    }

    @PostMapping("getStatistics")
        public CommonResult getStatistics(){
                return service.getStatistics();
        }
}
package com.yoghurt.myblog.controller;


import com.yoghurt.myblog.common.lang.Result;
import com.yoghurt.myblog.entity.User;
import com.yoghurt.myblog.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author anonymous
 * @since 2021-07-17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;


    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user) {
        return Result.success(user);
    }

    @RequiresAuthentication
    @GetMapping("/index")
    public Object index() {
        Object data = service.getById(1);
        return Result.success("200", "successful",data);
    }
}

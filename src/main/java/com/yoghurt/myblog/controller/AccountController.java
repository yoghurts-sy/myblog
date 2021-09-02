package com.yoghurt.myblog.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yoghurt.myblog.common.dto.LoginDto;
import com.yoghurt.myblog.common.lang.Result;
import com.yoghurt.myblog.entity.User;
import com.yoghurt.myblog.service.UserService;
import com.yoghurt.myblog.shiro.JwtToken;
import com.yoghurt.myblog.utils.JwtUtils;
import com.yoghurt.myblog.utils.uploadCOSUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@RestController
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse httpServletResponse) {
        User user = userService.getOne(new QueryWrapper<User>().eq("email", loginDto.getEmail()));
        Assert.notNull(user, "用户不存在");

        if (!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
            return Result.fail("密码不正确");
        }
        String jwt = jwtUtils.generateToken(user.getId());
        httpServletResponse.setHeader("Authorization", jwt);
        httpServletResponse.setHeader("Access-control-Expose-Headers", "Authorization");


        return Result.success(
                MapUtil.builder()
                        .put("id", user.getId())
                        .put("username", user.getUsername())
                        .put("avatar", user.getAvatar())
                        .put("email", user.getEmail())
                        .map()
        );
    }

    @RequiresAuthentication
    @GetMapping("/logout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.success(null);
    }

    @PostMapping("/register")
    public Result register(@Validated @RequestBody User user, HttpServletResponse httpServletResponse) {
        System.out.println(user);
        User checkUser = userService.getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        User checkUser2 = userService.getOne(new QueryWrapper<User>().eq("email", user.getUsername()));
        if (checkUser != null) {
            return Result.fail("Repeat of Username!");
        }
        if (checkUser2 != null) {
            return Result.fail("Repeat of Email!");
        }
        user.setPassword(SecureUtil.md5(user.getPassword()));
        user.setStatus(0);
        LocalDateTime date = LocalDateTime.now();
        user.setCreated(date);
        user.setLastLogin(date);
        userService.save(user);

        checkUser = userService.getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        System.out.println(checkUser);
        String jwt = jwtUtils.generateToken(checkUser.getId());
        httpServletResponse.setHeader("Authorization", jwt);
        httpServletResponse.setHeader("Access-control-Expose-Headers", "Authorization");
        return Result.success(
                MapUtil.builder()
                        .put("id", checkUser.getId())
                        .put("username", checkUser.getUsername())
                        .put("avatar", checkUser.getAvatar())
                        .put("email", checkUser.getEmail())
                        .map()
        );
    }

    @PostMapping("/upload")
    public Object upload(@RequestParam(value = "file") MultipartFile file){
        if (file == null){
            System.out.println("文件为空！");
            return Result.fail("文件为空");
        }
        String uploadfile = uploadCOSUtils.uploadfile(file, 0);
        return Result.success(uploadfile);
    }

}

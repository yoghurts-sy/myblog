package com.yoghurt.myblog.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yoghurt.myblog.common.lang.Result;
import com.yoghurt.myblog.entity.Blog;
import com.yoghurt.myblog.entity.User;
import com.yoghurt.myblog.service.BlogService;
import com.yoghurt.myblog.service.UserService;
import com.yoghurt.myblog.shiro.AccountProfile;
import com.yoghurt.myblog.utils.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author anonymous
 * @since 2021-07-17
 */
@RestController
public class BlogController {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    /**
     * 分页查询blogs
     * @param currentPage
     * @return
     */
    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {

        AccountProfile user = (AccountProfile)SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            return Result.fail("用户未登录！");
        }
        Page page = new Page(currentPage, 5);
        IPage pages = blogService.page(page, new QueryWrapper<Blog>().eq("user_id", user.getId()).orderByDesc("created"));
        return Result.success(pages);
    }

    @GetMapping("/blogs/{username}")
    public Result listByUsername(@RequestParam(defaultValue = "1") Integer currentPage, @PathVariable(name = "username") String username) {
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        Long userId = user.getId();
        Page page = new Page(currentPage, 5);
        IPage pages = blogService.page(page, new QueryWrapper<Blog>().eq("user_id", userId).orderByDesc("created"));
        return Result.success(pages);
    }

    /**
     * 通过blog的id查询单个blog
     * @param id
     * @return
     */
    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable(name="id") Long id) {
        Blog blog = blogService.getById(id);
        Assert.notNull(blog,"This Blog has been teared down.");
        return Result.success(blog);
    }

    /**
     * edit blog
     * @param blog
     * @return
     */
    @RequiresAuthentication
    @PostMapping("/blog/edit")
    public Result edit(@Validated @RequestBody Blog blog) throws InterruptedException {
        Blog temp = null;
        if (blog.getId() != null) { // update blog
            temp = blogService.getById(blog.getId());
            long longValue = ((AccountProfile)SecurityUtils.getSubject().getPrincipal()).getId().longValue();//强制类型转换要关掉devtool
            Assert.isTrue(temp.getUserId().longValue() == longValue, "You don't have authorization to edit it.");
        } else { // new blog
            temp = new Blog();
            temp.setUserId(((AccountProfile)SecurityUtils.getSubject().getPrincipal()).getId());
            temp.setStatus(0);
        }
        temp.setCreated(LocalDateTime.now());
        BeanUtil.copyProperties(blog, temp, "id","userId", "created","status");
        blogService.saveOrUpdate(temp);

        Thread.currentThread().sleep(500);//模拟效果

        return Result.success(null);
    }


    @RequiresAuthentication
    @PostMapping("/blog/delete")
    public Result delete(@Validated @RequestBody Blog blog) {
        if (blog.getId() != null) {
            boolean res = blogService.removeById(blog.getId());
            if (res) {
                return Result.success(null);
            }
        }
        return Result.fail("该博客不存在！");
    }
}

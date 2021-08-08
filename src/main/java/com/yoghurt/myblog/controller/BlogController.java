package com.yoghurt.myblog.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yoghurt.myblog.common.lang.Result;
import com.yoghurt.myblog.entity.Blog;
import com.yoghurt.myblog.service.BlogService;
import com.yoghurt.myblog.shiro.AccountProfile;
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
    private BlogService blogService;

    /**
     * 分页查询blogs
     * @param currentPage
     * @return
     */
    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {
        Page page = new Page(currentPage, 5);
        IPage pages = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));
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
    public Result edit(@Validated @RequestBody Blog blog) {
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

        return Result.success(null);
    }
}

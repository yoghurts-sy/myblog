package com.yoghurt.myblog.controller;


import com.yoghurt.myblog.common.lang.Result;
import com.yoghurt.myblog.entity.Blog;
import com.yoghurt.myblog.service.BlogService;
import com.yoghurt.myblog.service.UserService;
import com.yoghurt.myblog.utils.uploadCOSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/md")
public class MarkdownController {

    @Autowired
    private BlogService blogService;

    @PostMapping("/convert2String")
    public Result convert2String(@RequestParam(value = "file") MultipartFile file) {
        FileReader fileReader = null;

        String originalFilename = file.getOriginalFilename();
        String out = null;
        try {
            byte[] bytes = file.getBytes();
            out = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Blog blog = new Blog();
        blog.setUserId(3L);
        blog.setStatus(0);
        blog.setDescription("test md");
        blog.setContent(out);
        blog.setTitle("test md");
        blog.setCreated(LocalDateTime.now());
        blogService.saveOrUpdate(blog);

        System.out.println(out);
        return Result.success(out);
    }

    @PostMapping("/upload")
    public Object upload(@RequestParam(value = "file") MultipartFile file){
        if (file == null){
            System.out.println("图片为空！");
            return Result.fail("图片为空！");
        }
        String uploadfile = uploadCOSUtils.uploadfile(file, 1);
        return Result.success(uploadfile);
    }
}

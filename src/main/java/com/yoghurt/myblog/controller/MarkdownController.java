package com.yoghurt.myblog.controller;


import com.yoghurt.myblog.common.lang.Result;
import com.yoghurt.myblog.entity.Blog;
import com.yoghurt.myblog.service.BlogService;
import com.yoghurt.myblog.service.UserService;
import com.yoghurt.myblog.utils.uploadCOSUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Map;

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

    @RequiresAuthentication
    @GetMapping("/download")//post
    public void download(Long id, String title, HttpServletRequest request, HttpServletResponse response) {
        System.out.println(id +" "+title);

        Blog blog = blogService.getById(id);
        String content = blog.getContent();
        FileOutputStream fos = null;
        String fileName = title+".md";
        File file = null;
        try {
            file = File.createTempFile(title,".md");

            fos = new FileOutputStream(file);
            Writer writer = new OutputStreamWriter(new FileOutputStream(file));

            writer.write(content);
            writer.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        response.setContentType("application/octet-stream;charset=UTF-8");   // --x强制下载
        response.addHeader("Content-Disposition","attachment;fileName=" + fileName);
        response.setContentType("application/octet-stream;charset=UTF-8");
        //加上设置大小下载下来的.xlsx文件打开时才不会报“Excel 已完成文件级验证和修复。此工作簿的某些部分可能已被修复或丢弃”
        response.addHeader("Content-Length", String.valueOf(file.length()));

        byte[] buffer = new byte[2048];
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

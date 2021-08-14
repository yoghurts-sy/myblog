package com.yoghurt.myblog.utils;



import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;

import com.qcloud.cos.region.Region;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class uploadCOSUtils {
    //存储桶的名字
    private static final String bucketName = "myblog-1305163160";
    //secretId 秘钥id
    private static final String SecretId = "AKIDrulUzILIuaImjQu9nEhpsnHPK7HbQjRs";
    //SecretKey 秘钥
    private static final String SecretKey = "1nLgW7vN6lJckicBU4PUJo4C4mSqyx2V";
    // 腾讯云 自定义文件夹名称
    private static final String prefix = "avatar/";
    // 访问域名
    public static final String URL = "https://myblog-1305163160.cos.ap-shanghai.myqcloud.com/";
    //public static final String URL = "https://markdowngraph-1305163160.cos.ap-shenzhen-fsi.myqcloud.com/";
    // 创建COS 凭证
    private static COSCredentials credentials = new BasicCOSCredentials(SecretId,SecretKey);
    // 配置 COS 区域 就购买时选择的区域 我这里是 广州（guangzhou）
    private static Region region = new Region("ap-shanghai");
    //private static Region region = new Region("ap-shenzhen-fsi");
    private static ClientConfig clientConfig = new ClientConfig(region);


    public static String uploadfile(MultipartFile file){
        // 创建 COS 客户端连接
        COSClient cosClient = new COSClient(credentials, clientConfig);
        String fileName = file.getOriginalFilename();
        try {
            String substring = fileName.substring(fileName.lastIndexOf("."));
            File localFile = File.createTempFile(String.valueOf(System.currentTimeMillis()),substring);
            file.transferTo(localFile);
            Date date = new Date();
            String strDateFormat = "yyyy-MM-dd&HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            System.out.println(sdf.format(date));
            fileName = prefix+sdf.format(date)+"myblog"+substring;

            System.out.println(fileName);
            // 将 文件上传至 COS
            PutObjectRequest objectRequest = new PutObjectRequest(bucketName,fileName,localFile);
            cosClient.putObject(objectRequest);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cosClient.shutdown();
        }
        return URL+fileName;
    }
}

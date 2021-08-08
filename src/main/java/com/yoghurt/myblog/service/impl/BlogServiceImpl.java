package com.yoghurt.myblog.service.impl;

import com.yoghurt.myblog.entity.Blog;
import com.yoghurt.myblog.mapper.BlogMapper;
import com.yoghurt.myblog.service.BlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author anonymous
 * @since 2021-07-17
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

}

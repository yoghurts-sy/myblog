package com.yoghurt.myblog.service.impl;

import com.yoghurt.myblog.entity.User;
import com.yoghurt.myblog.mapper.UserMapper;
import com.yoghurt.myblog.service.UserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}

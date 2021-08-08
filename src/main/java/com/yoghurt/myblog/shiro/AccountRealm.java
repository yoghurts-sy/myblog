package com.yoghurt.myblog.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.yoghurt.myblog.entity.User;
import com.yoghurt.myblog.service.UserService;
import com.yoghurt.myblog.utils.JwtUtils;
import io.jsonwebtoken.Jwt;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 进行身份验证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) authenticationToken;
        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        User user = userService.getById(Long.parseLong(userId));
        if (user == null) {
            throw new UnknownAccountException("unknownAccount");
        }
        if (user.getStatus() == -1) {
            throw new LockedAccountException("lockedAccount");
        }
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
        return new SimpleAuthenticationInfo(profile,jwtToken.getCredentials(),getName());
    }
}

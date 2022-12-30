package com.ggl.cloud.security;

import java.util.function.Supplier;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ggl.cloud.entity.User;
import com.ggl.cloud.utils.SecurityRedisUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GglAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
  private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
                .getContextHolderStrategy();

  private StringRedisTemplate stringRedisTemplate;
  private ObjectMapper objectMapper;
  public GglAuthorizationManager(StringRedisTemplate stringRedisTemplate,
      ObjectMapper objectMapper) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.objectMapper = objectMapper;
  }
      @Override
      @Nullable
      public AuthorizationDecision check(Supplier<Authentication> authentication,
      RequestAuthorizationContext requestAuthorizationContext) {
        try {
          log.warn("进入授权check");
          String userStr = SecurityRedisUtil.checkAuth(requestAuthorizationContext.getRequest(), stringRedisTemplate);
          if (StringUtils.hasLength(userStr)) {
            User user = objectMapper.readValue(userStr, User.class);
            // 设置权限
            
            SecurityContext context = securityContextHolderStrategy.getContext();
            context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(user.getUserId(), null,
                AuthorityUtils.createAuthorityList(user.getAuth())));
            return new AuthorizationDecision(true);
          } else {
            return new AuthorizationDecision(false);
          }
        } catch (Exception e) {
          log.warn("授权过程出现异常" + e.getMessage());
          return new AuthorizationDecision(false);
        }
      }
      
}
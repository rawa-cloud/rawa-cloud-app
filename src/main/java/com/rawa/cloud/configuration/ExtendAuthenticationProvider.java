package com.rawa.cloud.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 扩展的用户名密码认证提供者。
 * @author zhangyin
 * @date 2021/2/8 14:25Ø
 */
@Component
@Slf4j
public class ExtendAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  @Resource
  UserDetailsService userDetailsService;

  @Resource
  private PasswordEncoder passwordEncoder;

  /**
   * 校验密码有效性.
   * @param userDetails    .
   * @param authentication .
   * @throws AuthenticationException .
   */
  @Override
  protected void additionalAuthenticationChecks(
      UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {
    if (authentication.getCredentials() == null) {
      logger.debug("Authentication failed: no credentials provided");

      throw new BadCredentialsException(messages.getMessage(
          "AbstractUserDetailsAuthenticationProvider.badCredentials",
          "Bad credentials"));
    }

    String presentedPassword = authentication.getCredentials().toString();
    String principal = (String)authentication.getPrincipal();
    String[] userStrs = principal.split(":");
    boolean pass;
    if (userStrs.length > 1) { // 免登录方式
      pass = true;
    } else {
      pass = passwordEncoder.matches(presentedPassword, userDetails.getPassword())
        || passwordEncoder.matches(presentedPassword, userDetails.getPassword().replace("{bcrypt}", ""    ))
      ;
    }
    if (!pass) {
      logger.debug("Authentication failed: password does not match stored value");

      throw new BadCredentialsException(messages.getMessage(
          "AbstractUserDetailsAuthenticationProvider.badCredentials",
          "Bad credentials"));
    }
  }

  /**
   * 获取用户.
   *
   * @param username       .
   * @param authentication .
   * @return
   * @throws AuthenticationException .
   */
  @Override
  protected UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {
    UserDetails loadedUser;
    try {
      loadedUser = userDetailsService.loadUserByUsername(username);
    } catch (Exception e) {
      throw new InternalAuthenticationServiceException(e.getMessage(), e);
    }
    if (loadedUser == null) {
      throw new InternalAuthenticationServiceException(
          "UserDetailsService returned null, which is an interface contract violation");
    }
    return loadedUser;
  }
}

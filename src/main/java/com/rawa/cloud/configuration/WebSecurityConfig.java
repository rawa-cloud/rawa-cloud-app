package com.rawa.cloud.configuration;

import com.rawa.cloud.bean.RestAuthHandler;
import com.rawa.cloud.repository.UserRepository;
import com.rawa.cloud.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    LogService logService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RestAuthHandler handler;


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs",//swagger api json
                "/swagger-resources/configuration/ui",//用来获取支持的动作
                "/swagger-resources",//用来获取api-docs的URI
                "/webjars/springfox-swagger-ui/**",//用来获取api-docs的URI
                "/swagger-resources/configuration/security",//安全选项
                "/swagger-ui.html",

                "/shares/**", // 分享相关
                "/file-assist/**", //
                "/dicts/initDict"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()
            .and().formLogin()
                .successHandler(handler)
                .failureHandler(handler)
            .and().logout()
                .logoutSuccessHandler(handler)
                .invalidateHttpSession(true)
            .and().exceptionHandling()
                .accessDeniedHandler(handler)
                .authenticationEntryPoint(handler)
            .and().csrf().disable()
            .cors();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        return username -> {
            com.rawa.cloud.domain.User user = userRepository.findUserByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
            return user;
        };
    }
}


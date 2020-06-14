package com.rawa.cloud.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfig implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (p instanceof String) return Optional.of((String)p);
            UserDetails user = (UserDetails) p;
            return Optional.of(user.getUsername());
        } catch (Exception e) {
            return Optional.of("anonymous");
        }
    }

    @Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter();
    }
}

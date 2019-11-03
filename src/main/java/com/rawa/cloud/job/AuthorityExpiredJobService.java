package com.rawa.cloud.job;

import com.rawa.cloud.domain.DeptAuthority;
import com.rawa.cloud.domain.UserAuthority;
import com.rawa.cloud.repository.DeptAuthorityRepository;
import com.rawa.cloud.repository.UserAuthorityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthorityExpiredJobService {

    @Autowired
    DeptAuthorityRepository deptAuthorityRepository;

    @Autowired
    UserAuthorityRepository userAuthorityRepository;

    // 每天凌晨一点执行一次, 删除过期的权限
    @Scheduled(cron = "0 0 1 * * ?")
    public void clear () {
        List<UserAuthority> userAuthorities = userAuthorityRepository.findAllByExpiryTimeIsNotNull();
        List<UserAuthority> expiredUserAuthorities = userAuthorities
                .stream()
                .filter(s -> new Date().after(s.getExpiryTime()))
                .collect(Collectors.toList());
        userAuthorityRepository.deleteInBatch(expiredUserAuthorities);

        List<DeptAuthority> deptAuthorities = deptAuthorityRepository.findAllByExpiryTimeIsNotNull();
        List<DeptAuthority> expiredDeptAuthorities = deptAuthorities
                .stream()
                .filter(s -> new Date().after(s.getExpiryTime()))
                .collect(Collectors.toList());
        deptAuthorityRepository.deleteInBatch(expiredDeptAuthorities);
        log.info("execute authority expired job, delete " + (userAuthorities.size() + deptAuthorities.size()) + "records");
    }
}

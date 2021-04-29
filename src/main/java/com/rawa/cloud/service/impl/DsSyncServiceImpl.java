package com.rawa.cloud.service.impl;

import com.rawa.cloud.domain.*;
import com.rawa.cloud.ds.DeptEntity;
import com.rawa.cloud.ds.UserEntity;
import com.rawa.cloud.ds.UserRoleEntity;
import com.rawa.cloud.repository.*;
import com.rawa.cloud.service.DsSyncService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DsSyncServiceImpl implements DsSyncService {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    DeptRepository deptRepository;

    @Resource
    UserRepository userRepository;

    @Resource
    RoleRepository roleRepository;

    @Resource
    AreaRepository areaRepository;

    @Resource
    UnitRepository unitRepository;

    @Override
    public void syncData() {
        Map<String, Dept> deptMap = syncDept();

        syncUser(deptMap);

        syncArea();

        syncUnit();
    }

    @Transactional
    Map<String, Dept> syncDept () {
       String sql = "select bm as code, mc as name, ssjgbm as parentCode from v_organization";

       List<DeptEntity> origins =
               jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DeptEntity.class));

       List<Dept> depts = deptRepository.findAll();

       Map<String, Dept> deptMap = depts.stream()
                .filter(s -> s.getCode() != null)
                .collect(Collectors.toMap(Dept::getCode, s -> s));

       List<Dept> newDepts = new LinkedList<>();
       origins.forEach(s -> {
         Dept item = deptMap.get(s.getCode());
         if (item == null) {
             item = new Dept();
             item.setCode(s.getCode());
             deptMap.put(item.getCode(), item);
         }
         item.setName(s.getName());
         item.setParentCode(s.getParentCode());
         item.setSynced(true);
         newDepts.add(item);
       });

       List<Dept> savedNewDepts = deptRepository.saveAll(newDepts);
        Map<String, Dept> newDeptMap = savedNewDepts.stream()
                .collect(Collectors.toMap(Dept::getCode, s-> s));
        savedNewDepts.forEach(s -> {
            Dept parent = s.getParentCode() == null ? null : newDeptMap.get(s.getParentCode());
            s.setParent(parent);
        });
        deptRepository.saveAll(savedNewDepts);

        List<Dept> deletedDepts = depts.stream()
                .filter(s -> !Boolean.TRUE.equals(s.getSynced()))
                .collect(Collectors.toList());

        deptRepository.deleteAll(deletedDepts);
        return newDeptMap;
    }

    @Transactional
    void syncUser (Map<String, Dept> deptMap) {
        String sql = "select bm as code, xm as name, zzjgbm as deptCode from v_user";

        List<UserEntity> origins =
                jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserEntity.class));

        List<User> users = userRepository.findAll();

        Map<String, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getUsername, s -> s));

        List<Role> superRole = roleRepository.findAllByRoleCodeIn(Collections.singletonList(Role.ROLE_SUPER));
        List<Role> userRole = roleRepository.findAllByRoleCodeIn(Collections.singletonList(Role.ROLE_USER));

        Map<String, UserRoleEntity> adminUserRoleMap = getAdminUserRoleMap();

        List<User> newUsers = new LinkedList<>();
        origins.forEach(s -> {
            User item = userMap.get(s.getCode());
            if (item == null) {
                item = new User();
                item.setUsername(s.getCode());
                item.setPassword("******");
                item.setStatus(true);
            }
            item.setCname(s.getName());
            Dept dept = Optional.ofNullable(s.getDeptCode())
                    .map(deptMap::get).orElse(null);
            item.setDept(dept);
            item.setRoleList(
                    Optional.ofNullable(adminUserRoleMap.get(item.getUsername()))
                    .map(w -> superRole)
                    .orElse(userRole)
            );
            item.setSynced(true);
            newUsers.add(item);
        });

        List<User> savedNewUsers = userRepository.saveAll(newUsers);

        List<User> deletedUsers = users.stream()
                .filter(s -> !Boolean.TRUE.equals(s.getSynced()))
                .collect(Collectors.toList());

        userRepository.deleteAll(deletedUsers);
    }

    Map<String, UserRoleEntity> getAdminUserRoleMap () {
        String sql = "select bm as userCode, xtjsbm as roleCode from v_user_role t " +
                "where t.jsbm = 'ROLE_TZ_ADMIN'";

        List<UserRoleEntity> origins =
                jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserRoleEntity.class));

        return origins.stream()
                .collect(Collectors.toMap(UserRoleEntity::getUserCode, s -> s, (v1,v2)->v1));
    }

    @Transactional
    void syncArea () {
        String sql = "select xqbm as code, mc as name from v_tyqx_xq";

        List<Area> origins =
                jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Area.class));

        areaRepository.deleteAll();
        areaRepository.saveAll(origins);
    }

    @Transactional
    void syncUnit () {
        String sql = "select bm as code, mc as name from v_t_dw_zddw";

        List<Unit> origins =
                jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Unit.class));

        unitRepository.deleteAll();
        unitRepository.saveAll(origins);
    }
}

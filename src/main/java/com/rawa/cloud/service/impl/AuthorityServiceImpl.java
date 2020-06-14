package com.rawa.cloud.service.impl;

import com.google.common.collect.Lists;
import com.rawa.cloud.annotation.HasAdminRole;
import com.rawa.cloud.annotation.HasSuperRole;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.Umask;
import com.rawa.cloud.domain.*;
import com.rawa.cloud.domain.common.Authority;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.DateHelper;
import com.rawa.cloud.model.authority.AuthorityAddModel;
import com.rawa.cloud.model.authority.AuthorityPatchModel;
import com.rawa.cloud.model.authority.AuthorityQueryModel;
import com.rawa.cloud.repository.*;
import com.rawa.cloud.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.rawa.cloud.helper.DateHelper.toDate;
import static com.rawa.cloud.helper.DateHelper.toLocalDate;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeptRepository deptRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserAuthorityRepository userAuthorityRepository;

    @Autowired
    DeptAuthorityRepository deptAuthorityRepository;

    @Override
    public boolean hasAuthority(Long principleId, Long fileId, boolean isUser, Umask... umasks) {
        Authority authority = null;
        if (isUser) {
            authority = findAuthorityByUser(principleId, fileId, true);
        } else {
            authority = findAuthorityByDept(principleId, fileId, true);
        }
        if (authority == null) return false;
        return Umask.hasAny(authority.getUmask(), umasks);
    }

    @Override
    public Long umask(Long principleId, Long fileId, boolean isUser, boolean implicit) {
        Authority authority = null;
        if (!implicit) {
            if (isUser) {
                authority = get(principleId, true, fileId);
            }
            if (authority != null) return authority.getUmask();

            File file = fileRepository.findById(fileId)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));

            List<Dept> depts = null;
            if (isUser) {
                User user = userRepository.findById(principleId)
                        .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, principleId));
                depts = user.getDept() == null ? null : user.getDept().getParents();
            } else {
                Dept dept = deptRepository.findById(principleId)
                        .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, principleId));
                depts = dept.getParents();
            }

            if (depts != null) authority = deptAuthorityRepository.findByPrincipleInAndFile(depts, file);

            return authority != null ? authority.getUmask() : null;
        }
        if (isUser) {
            authority = findAuthorityByUser(principleId, fileId, true);
        } else {
            authority = findAuthorityByDept(principleId, fileId, true);
        }
        return authority != null ? authority.getUmask() : null;
    }

    @Override
    @HasAdminRole
    public Long add(AuthorityAddModel model) {
        Long fileId = model.getFileId();
        Boolean isUser = model.getIsUser();
        hasOpt(fileId);
        Long validDays = model.getValidDays();
        Long principleId = model.getPrincipleId();
        Long umask = model.getUmask();
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
        if (!isUser) {
            Dept dept = deptRepository.findById(principleId)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, principleId));
            DeptAuthority deptAuthority = deptAuthorityRepository.findAuthorityByPrincipleAndFile(dept, file);
            if (deptAuthority == null) {
                deptAuthority = new DeptAuthority();
                deptAuthority.setFile(file);
                deptAuthority.setPrinciple(dept);
            }
            deptAuthority.setUmask(umask);
            if (validDays != null) deptAuthority.setValidDays(model.getValidDays());
            return deptAuthorityRepository.save(deptAuthority).getId();
        } else {
            User user = userRepository.findById(principleId)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, principleId));
            UserAuthority userAuthority = userAuthorityRepository.findAuthorityByPrincipleAndFile(user, file);
            if (userAuthority == null) {
                userAuthority = new UserAuthority();
                userAuthority.setFile(file);
                userAuthority.setPrinciple(user);
            }
            userAuthority.setUmask(umask);
            if (validDays != null) userAuthority.setValidDays(model.getValidDays());
            return userAuthorityRepository.save(userAuthority).getId();
        }
    }

    @Override
    @HasAdminRole
    public void delete(boolean isUser, List<Long> ids) {
        if (!isUser) {
            deptAuthorityRepository.deleteAll(deptAuthorityRepository.findAllById(ids));
        } else {
            userAuthorityRepository.deleteAll(userAuthorityRepository.findAllById(ids));
        }
    }

    @Override
    public void update(Long id, boolean isUser, AuthorityPatchModel model) {
        Long validDays = model.getValidDays();
        Long umask = model.getUmask();
        if (!isUser) {
            DeptAuthority deptAuthority = deptAuthorityRepository.findById(id)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.AUTHORITY_NOT_FOUND, id));
            if (umask != null) deptAuthority.setUmask(umask);
            if (validDays != null) deptAuthority.setValidDays(validDays);
            deptAuthorityRepository.save(deptAuthority);
        } else {
            UserAuthority userAuthority = userAuthorityRepository.findById(id)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.AUTHORITY_NOT_FOUND, id));
            if (umask != null) userAuthority.setUmask(umask);
            if (validDays != null) userAuthority.setValidDays(validDays);
            userAuthorityRepository.save(userAuthority);
        }
    }

    @Override
    public List<? extends Authority> query(AuthorityQueryModel model) {
        Long fileId = model.getFileId();
        Long principleId = model.getPrincipleId();
        boolean isUser = Boolean.TRUE.equals(model.getIsUser());
        boolean implicit = Boolean.TRUE.equals(model.getImplicit());

        if (implicit) {
            if (fileId == null || principleId == null) throw new AppException(HttpJsonStatus.VALID_FAIL, "权限隐式查询 file principle 必填");
            Authority authority = isUser ? findAuthorityByUser(principleId, fileId, true) : findAuthorityByDept(principleId, fileId, true);
            List<Authority> ret = Lists.newArrayList();
            if (authority != null) {
                if (!authority.getPrincipleId().equals(principleId)  || !authority.getFileId().equals(fileId)) authority.setImplicit(true);
                ret.add(authority);
            }
            return ret;
        }

        final File file = fileId != null ? fileRepository.findById(fileId).orElse(null) : null;
        final Dept dept = (principleId != null && !isUser) ? deptRepository.findById(principleId).orElse(null) : null;
        final User user = (principleId != null && isUser) ? userRepository.findById(principleId).orElse(null) : null;

        List<DeptAuthority> deptList = Lists.newArrayList();
        List<UserAuthority> userList = Lists.newArrayList();

        boolean noPrinciple = dept == null && user == null;
        if(dept != null || noPrinciple) {
            deptList = deptAuthorityRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> {
                Predicate predicate = criteriaBuilder.conjunction();
                if (dept != null) predicate.getExpressions().add(
                        criteriaBuilder.equal(root.get("principle"), dept));

                Predicate p1 = criteriaBuilder.isNull(root.get("expiryTime"));
                Predicate p2 = criteriaBuilder.greaterThan(root.get("expiryTime"), DateHelper.trunc(new Date()));
                predicate.getExpressions().add(criteriaBuilder.or(p1, p2));
                if (file != null) {
                    predicate.getExpressions().add(
                            criteriaBuilder.equal(root.get("file"), file));
                }
                return predicate;
            }));
        }
        if(noPrinciple || user != null) {
            userList = userAuthorityRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> {
                Predicate predicate = criteriaBuilder.conjunction();
                if (user != null) predicate.getExpressions().add(
                        criteriaBuilder.equal(root.get("principle"), user));

                Predicate p1 = criteriaBuilder.isNull(root.get("expiryTime"));
                Predicate p2 = criteriaBuilder.greaterThan(root.get("expiryTime"), DateHelper.trunc(new Date()));
                predicate.getExpressions().add(criteriaBuilder.or(p1, p2));
                if (file != null) {
                    predicate.getExpressions().add(
                            criteriaBuilder.equal(root.get("file"), file));
                }
                return predicate;
            }));
        }
        List<Authority> ret = Lists.newArrayList();
        ret.addAll(deptList);
        ret.addAll(userList);
        return ret;
    }

    @Override
    public Authority get(Long principleId, boolean isUser, Long fileId) {
        File file = fileRepository.findById(fileId).orElse(null);
        if (file == null) return null;
        if (!isUser) {
            Dept dept = deptRepository.findById(principleId).orElse(null);
            if (dept == null) return null;
            return deptAuthorityRepository.findAuthorityByPrincipleAndFile(dept, file);
        } else {
            User user = userRepository.findById(principleId).orElse(null);
            if (user == null) return null;
            return userAuthorityRepository.findAuthorityByPrincipleAndFile(user, file);
        }
    }

    @Override
    public Authority get(Long id, boolean isUser) {
        return null;
    }

    // inner
    private Authority findAuthorityByUser(Long userId, Long fileId, boolean cascade) {
        User user = userRepository.findById(userId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, userId));
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
        Authority authority = null;
        while(authority == null) {
            authority = userAuthorityRepository.findAuthorityByPrincipleAndFile(user, file);
            if (file.getParent() == null) break;
            file = file.getParent();
        }
        if (authority == null && cascade && user.getDept() != null) {
            authority = findAuthorityByDept(user.getDept().getId(), fileId, true);
        }
        return authority;
    }

    private Authority findAuthorityByDept(Long deptId, Long fileId, boolean cascade) {
        Dept dept = deptRepository.findById(deptId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, deptId));
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));

        DeptAuthority authority = null;
        File originFile = file;
        do {
            while(authority == null && file != null) {
                authority = deptAuthorityRepository.findAuthorityByPrincipleAndFile(dept, file);
                file = file.getParent();
            }
            dept = dept.getParent();
            file = originFile;
        } while (cascade && dept != null && authority == null);

        return authority;
    }

    private void hasOpt(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
//        if (file.getPersonal() || !file.getStatus()) throw new AppException(HttpJsonStatus.ACCESS_DENIED, null);
        User user = ContextHelper.getCurrentUser();
        if (user.hasSuperRole()) return;
        if (user.hasAdminRole() && user.isAdminFile(file)) return;
        throw new AppException(HttpJsonStatus.ACCESS_DENIED, null);
    }
}

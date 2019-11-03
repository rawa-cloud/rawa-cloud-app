package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.Umask;
import com.rawa.cloud.domain.*;
import com.rawa.cloud.domain.common.Authority;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.model.authority.AuthorityAddModel;
import com.rawa.cloud.model.authority.AuthorityPatchModel;
import com.rawa.cloud.model.authority.AuthorityQueryModel;
import com.rawa.cloud.repository.*;
import com.rawa.cloud.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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
    @Transactional
    public void deleteByFile(File file) {
        List<DeptAuthority> deptAuthorities = deptAuthorityRepository.findAllByFile(file);
        deptAuthorityRepository.deleteInBatch(deptAuthorities);
        List<UserAuthority> userAuthorities = userAuthorityRepository.findAllByFile(file);
        userAuthorityRepository.deleteInBatch(userAuthorities);
    }

    @Override
    public Long add(AuthorityAddModel model) {
        hasOpt(model.getFileId());
        if (model.getIsUser()) {
            UserAuthority authority = generateAddUserAuthority(model);
            return userAuthorityRepository.save(authority).getId();
        } else {
            DeptAuthority authority = generateAddDeptAuthority(model);
            return deptAuthorityRepository.save(authority).getId();
        }
    }

    @Override
    public Authority get(Long id, boolean isUser) {
        if (isUser) {
            return userAuthorityRepository.findById(id).orElse(null);
        }
        return deptAuthorityRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id, boolean isUser) {
        if (isUser) {
            userAuthorityRepository.deleteById(id);
        }else {
            deptAuthorityRepository.deleteById(id);
        }
    }

    @Override
    public void update(Long id, boolean isUser, AuthorityPatchModel model) {
        if (isUser) {
            UserAuthority userAuthority = userAuthorityRepository.findById(id)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.AUTHORITY_NOT_FOUND, id));
            if(model.getUmask() != null)userAuthority.setUmask(model.getUmask());
            if(model.getValidDays() != null)userAuthority.setValidDays(model.getValidDays() == -1L ? null : model.getValidDays());
            userAuthorityRepository.save(userAuthority);
        } else {
            DeptAuthority deptAuthority = deptAuthorityRepository.findById(id)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.AUTHORITY_NOT_FOUND, id));
            if(model.getUmask() != null) deptAuthority.setUmask(model.getUmask());
            if(model.getValidDays() != null) deptAuthority.setValidDays(model.getValidDays() == -1L ? null : model.getValidDays());
            deptAuthorityRepository.save(deptAuthority);
        }
    }

    @Override
    public List<Authority> query(AuthorityQueryModel model) {
        Boolean isUser = model.getIsUser();
        Long principleId = model.getPrincipleId();
        File file = null;
        if (model.getFileId() != null) {
            file = fileRepository.findById(model.getFileId())
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, model.getFileId()));
        }
        List<UserAuthority> userAuthorities = null;
        if (isUser == null || isUser) {
            User user = null;
            if (principleId != null) {
                user = userRepository.findById(principleId)
                        .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, principleId));
            }
//            TODO has Error, don't know why
//            UserAuthority userAuthority = new UserAuthority();
//            userAuthority.setPrinciple(user);
//            userAuthority.setFile(file);
//            userAuthorities = userAuthorityRepository.findAll(Example.of(userAuthority));
            if (file == null && user == null) userAuthorities = userAuthorityRepository.findAll();
            else if (file != null && user == null) userAuthorities = userAuthorityRepository.findAllByFile(file);
            else if (file == null && user != null) userAuthorities = userAuthorityRepository.findAllByPrinciple(user);
            else userAuthorities = userAuthorityRepository.findAllByPrincipleAndFile(user, file);
        }
        List<DeptAuthority> deptAuthorities = null;
        if (isUser == null || !isUser) {
            Dept dept = null;
            if (principleId != null) {
                dept = deptRepository.findById(principleId)
                        .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, principleId));
            }
            DeptAuthority deptAuthority = new DeptAuthority();
            deptAuthority.setPrinciple(dept);
            deptAuthority.setFile(file);
            deptAuthorities = deptAuthorityRepository.findAll(Example.of(deptAuthority));
        }
        List<Authority> authorities = new ArrayList<>();
        if (userAuthorities != null) authorities.addAll(userAuthorities);
        if (deptAuthorities != null) authorities.addAll(deptAuthorities);
        return authorities;
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
            while(authority == null) {
                authority = deptAuthorityRepository.findAuthorityByPrincipleAndFile(dept, file);
                if (file.getParent() == null) break;
                file = file.getParent();
            }
            dept = dept.getParent();
            file = originFile;
        } while (cascade && dept != null);

        return deptAuthorityRepository.findAuthorityByPrincipleAndFile(dept, file);
    }

    private void hasOpt(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
        User user = ContextHelper.getCurrentUser();
        if (user.hasSuperRole()) return;
        if (user.hasAdminRole()) {
            if (user.isAdminFile(file)) return;
        }
        throw new AppException(HttpJsonStatus.ACCESS_DENIED, null);
    }

    private UserAuthority generateAddUserAuthority (AuthorityAddModel model) {
        Long fileId = model.getFileId();
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
        User user = userRepository.findById(model.getPrincipleId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, model.getPrincipleId()));
        UserAuthority authority = userAuthorityRepository.findAuthorityByPrincipleAndFile(user, file);
        if (authority == null) authority = new UserAuthority();
        authority.setFile(file);
        authority.setPrinciple(user);
        authority.setValidDays(model.getValidDays());
        authority.setUmask(model.getUmask());
        return authority;
    }

    private DeptAuthority generateAddDeptAuthority (AuthorityAddModel model) {
        Long fileId = model.getFileId();
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
        Dept dept = deptRepository.findById(model.getPrincipleId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.DEPT_NOT_FOUND, model.getPrincipleId()));

        DeptAuthority authority = deptAuthorityRepository.findAuthorityByPrincipleAndFile(dept, file);
        if (authority == null) authority = new DeptAuthority();
        authority.setFile(file);
        authority.setPrinciple(dept);
        authority.setValidDays(model.getValidDays());
        authority.setUmask(model.getUmask());
        return authority;
    }
}

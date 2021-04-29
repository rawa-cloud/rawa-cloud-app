package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApiModel
@Data
@Entity
public class User extends BaseEntity implements UserDetails {

    // JPA

    @ApiModelProperty(value = "用户名")
    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @ApiModelProperty(value = "中文名称")
    private String cname;

    @ApiModelProperty(value = "登录IP段")
    private String ip;

    @ApiModelProperty(value = "是否有效")
    private Boolean status;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_role",
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roleList;

    @Transient
    private Boolean synced;


    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_file",
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<File> fileList;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private Dept dept;

    // domain

    @ApiModelProperty(value = "归属部门ID")
    @Transient
    public Long getDeptId() {
        return this.dept != null ? this.dept.getId() : null;
    }

    @ApiModelProperty(value = "归属部门名称")
    @Transient
    public String getDeptName() {
        return this.dept != null ? this.dept.getName() : null;
    }

    @ApiModelProperty(value = "拥有角色列表")
    @Transient
    public List<String> getRoles () {
        if (this.getRoleList() == null) return Collections.emptyList();
        return this.getRoleList().stream().map(role -> role.getRoleCode()).collect(Collectors.toList());
    }

    @ApiModelProperty(value = "用户管理根目录列表")
    @Transient
    public List<Long> getFiles () {
        if (this.getFileList() == null) return Collections.emptyList();
        return this.getFileList().stream().map(s -> s.getId()).collect(Collectors.toList());
    }

    // 判断文件是否属于该用户
    @JsonIgnore
    public boolean isAdminFile (File file) {
        List<File> files = this.getFileList();
        boolean matched = files.stream().anyMatch(s -> {
            File temp = file;
            while (temp != null) {
                if (s.getId().equals(temp.getId())) return true;
                temp = temp.getParent();
            }
            return false;
        });
        return matched;
    }

    // 判断文件是否属于该用户的根文件
    @JsonIgnore
    public boolean isAdminRootFile (File file) {
        List<File> files = this.getFileList();
        boolean matched = files.stream().anyMatch(s -> {
            return s.getId().equals(file.getId());
        });
        return matched;
    }

    // 是否是系统默认用户
    @JsonIgnore
    public boolean isDefaultUser () {
        return getUsername().equals("root");
    }

    // spring security provided user

    @Override
    @Transient
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> ret = new ArrayList<>();
        for(final Role role : getRoleList()) {
            GrantedAuthority s = () -> role.getRoleCode();
            ret.add(s);
        }
        return ret;
    }

    @Override
    @Transient
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEnabled() {
        return status;
    }

    // domain

    public boolean hasSuperRole () {
        return getRoleList().stream().anyMatch(r -> Role.ROLE_SUPER.equals(r.getRoleCode()));
    }

    public boolean hasAdminRole () {
        return getRoleList().stream().anyMatch(r -> Role.ROLE_ADMIN.equals(r.getRoleCode()));
    }

    @Deprecated
    @JsonIgnore
    public boolean isChildFile (File file) {
        if (hasSuperRole()) return true;
        if (hasAdminRole()) {
            List<Long> roots = getFiles();
            File temp = file;
            while (temp != null) {
                Long id = temp.getId();
                if (roots.contains(id)) return true;
                temp = temp.getParent();
            }
        }
        return false;
    }
}

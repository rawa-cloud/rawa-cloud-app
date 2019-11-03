package com.rawa.cloud.domain.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@MappedSuperclass
public class CascadeEntity<E extends CascadeEntity> extends BaseEntity{

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private E parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<E> children;

    @Transient
    public Long getParentId () {
        return parent != null ? parent.id : null;
    }
}

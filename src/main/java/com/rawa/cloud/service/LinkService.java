package com.rawa.cloud.service;

import com.rawa.cloud.domain.Link;
import com.rawa.cloud.model.link.LinkAddModel;
import com.rawa.cloud.model.link.LinkQueryModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LinkService {

    Long add(LinkAddModel model);

//    void update(Long id, LinkPatchModel model);

    Page<Link> query(LinkQueryModel model);

    Link get(Long id);

    void delete(Long id);

    void deleteInBatch(List<Long> ids);

}
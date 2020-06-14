package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.domain.LibraryCatalog;
import com.rawa.cloud.domain.Log;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.model.log.LogQueryModel;
import com.rawa.cloud.repository.LogRepository;
import com.rawa.cloud.service.LogService;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    LogRepository logRepository;


    @Override
    public Long add(LogModule module, LogType type, String remark) {
        Log log = new Log();
        log.setModule(module);
        log.setType(type);
        log.setRemark(remark);
        log.setOperateTime(new Date());
        log.setOperateBy(ContextHelper.getCurrentUsername());
        return logRepository.save(log).getId();
    }

    @Override
    public Long add(Log log) {
        log.setOperateBy(ContextHelper.getCurrentUsername());
        log.setOperateTime(new Date());
        return logRepository.save(log).getId();
    }

    @Override
    public Long add(Log log, String username) {
        log.setOperateBy(username);
        log.setOperateTime(new Date());
        return logRepository.save(log).getId();
    }

    @Override
    public Page<Log> query(LogQueryModel model) {
        LogModule module = model.getModule();
        LogType type = model.getType();
        String operateBy = model.getOperateBy();
        return logRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
                    Predicate predicate = criteriaBuilder.conjunction();
                    if (module != null) {
                        predicate.getExpressions().add(criteriaBuilder.equal(root.get("module"), module));
                    }
                    if (type != null) {
                        predicate.getExpressions().add(criteriaBuilder.equal(root.get("type"), type));
                    }
                    if (!StringUtils.isEmpty(operateBy)) {
                        predicate.getExpressions().add(criteriaBuilder.equal(root.get("operateBy"), operateBy));
                    }
                    predicate.getExpressions()
                            .add(criteriaBuilder.between(root.get("operateTime"), new Date(new Date().getTime() - 1000 * 3600 * 24 * 7), new Date()));
                    return predicate;
                }, model.toPage(false, "operateTime")
        );
    }
}

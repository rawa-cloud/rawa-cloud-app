package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.PlanStatus;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.ImportPlan;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.job.ImportPlanJobDetail;
import com.rawa.cloud.model.importplan.ImportPlanAddModel;
import com.rawa.cloud.properties.AppProperties;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.ImportPlanRepository;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.service.ImportPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ImportPlanServiceImpl implements ImportPlanService {

    @Autowired
    ImportPlanRepository importPlanRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FileService fileService;

    @Autowired
    ImportPlanJobDetail importPlanJobDetail;

    @Autowired
    AppProperties appProperties;

    private PlanStatus status = PlanStatus.not_started;

    @Override
    public Long addPlan(ImportPlanAddModel model) {
        if (exist()) throw new AppException(HttpJsonStatus.IMPORT_PLAN_EXIST, null);
        String filePath = model.getFilePath();
        Long parentId = model.getParentId();
        String cron = model.getCron();
        java.io.File importFile = new java.io.File(appProperties.getImportPath(), filePath);
        if (!importFile.exists() || !importFile.isDirectory()) {
            throw new AppException(HttpJsonStatus.EXPORT_PLAN_NOT_EXIST_DIR, filePath);
        }
        long size = importFile.getTotalSpace();
        File parentFile = fileRepository.findById(parentId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, parentId));
        if (Boolean.TRUE.equals(parentFile)) throw new AppException(HttpJsonStatus.FILE_NOT_FOUND, parentId);

        ImportPlan plan = new ImportPlan();
        plan.setFilePath(filePath);
        plan.setParentId(parentId);
        plan.setSize(size);
        plan.setCron(cron);
        plan.setExecCount(0);
        plan.setExecStatus(PlanStatus.not_started);
        plan.setStatus(true);
        Long id = importPlanRepository.save(plan).getId();
        importPlanJobDetail.register();
        return id;
    }

    @Override
    public void deletePlan() {
        ImportPlan plan = getPlan();
        if (plan == null) return;
        if (PlanStatus.ongoing.equals(this.status)) throw new AppException(HttpJsonStatus.IMPORT_PLAN_ONGOING, null);
        plan.setStatus(false);
        importPlanJobDetail.register();
        importPlanRepository.save(plan);
    }

    @Override
    public ImportPlan getPlan() {
        return importPlanRepository.findByStatus(true);
    }

    @Override
    public void execPlan() {
        ImportPlan plan = getPlan();
        if (plan == null || !plan.getStatus()) {
            log.info("无活跃的导入计划");
            return;
        }
        if (PlanStatus.ongoing.equals(this.status)) {
            log.info("导入计划Job正在执行， 放弃此次执行.....");
            return;
        }
        plan.setStartTime(new Date());
        plan.setExecStatus(PlanStatus.ongoing);
        this.status = PlanStatus.ongoing;
        plan.setExecCount(plan.getExecCount() + 1);
        importPlanRepository.save(plan);
        String importFilePath = plan.getFilePath();
        Long parentFileId = plan.getParentId();

        try {
            java.io.File importFile = new java.io.File(appProperties.getImportPath(),importFilePath);
            if (!importFile.exists() || !importFile.isDirectory()) {
                throw new RuntimeException("导入文件夹不存在： " + importFilePath);
            }
            long size = 0L; //importFile.getTotalSpace();
            File parentFile = fileRepository.findById(parentFileId).orElse(null);
            if (parentFile == null || Boolean.TRUE.equals(parentFile)) throw new RuntimeException("父级目录不存在：" + parentFileId);

            fileService.importFile(importFile, parentFile);

            plan.setSize(size);
            plan.setSuccess(true);
            plan.setRemark("导入成功");
        } catch (Exception e) {
            log.error("执行入出计划error", e);
            plan.setSuccess(false);
            plan.setRemark(e.getMessage());
        }
        plan.setExecStatus(PlanStatus.completed);
        this.status = PlanStatus.completed;
        plan.setEndTime(new Date());
        plan.setStatus(false); // 一次行Job
        importPlanRepository.save(plan);
        importPlanJobDetail.cancel(); // 失效Job
    }

    @Override
    public List<String> getImportFileList() {
        java.io.File file = new java.io.File(appProperties.getImportPath());
        return Arrays.asList(file.list());
    }

    private boolean exist () {
        return getPlan() != null;
    }
}

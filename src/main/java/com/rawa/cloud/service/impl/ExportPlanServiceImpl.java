package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.PlanStatus;
import com.rawa.cloud.domain.ExportPlan;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.job.ExportPlanJobDetail;
import com.rawa.cloud.model.exportplan.ExportPlanAddModel;
import com.rawa.cloud.properties.AppProperties;
import com.rawa.cloud.repository.ExportPlanRepository;
import com.rawa.cloud.repository.ImportPlanRepository;
import com.rawa.cloud.service.ExportPlanService;
import com.rawa.cloud.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ExportPlanServiceImpl implements ExportPlanService {

    @Autowired
    ImportPlanRepository importPlanRepository;

    @Autowired
    ExportPlanRepository exportPlanRepository;

    @Autowired
    ExportPlanJobDetail exportPlanJobDetail;

    @Autowired
    FileService fileService;

    @Autowired
    AppProperties appProperties;

    private PlanStatus status = PlanStatus.not_started;

    @Override
    public Long addPlan(ExportPlanAddModel model) {
        if (exist()) throw new AppException(HttpJsonStatus.EXPORT_PLAN_EXIST, null);
        String cron = model.getCron();
//        String filePath = model.getFilePath();
//        if (!new java.io.File(filePath).exists()) {
//            throw new AppException(HttpJsonStatus.EXPORT_PLAN_NOT_EXIST_DIR, filePath);
//        }
        ExportPlan plan = new ExportPlan();
//        plan.setFilePath(filePath);
        plan.setSize(0l);
        plan.setExecCount(0);
        plan.setCron(cron);
        plan.setExecStatus(PlanStatus.not_started);
        plan.setStatus(true);
        Long id = exportPlanRepository.save(plan).getId();
        exportPlanJobDetail.register();
        return id;
    }

    @Override
    public void deletePlan() {
        ExportPlan plan = getPlan();
        if (plan == null) return;
        if (PlanStatus.ongoing.equals(this.status)) {
            throw new AppException(HttpJsonStatus.EXPORT_PLAN_ONGOING, plan.getId());
        }
        plan.setStatus(false);
        exportPlanRepository.save(plan);
        exportPlanJobDetail.register();
    }

    @Override
    public ExportPlan getPlan() {
        return exportPlanRepository.findByStatus(true);
    }

    @Override
    public void execPlan() {
        ExportPlan plan = getPlan();
        if (plan == null || !plan.getStatus()) return;
        if (PlanStatus.ongoing.equals(this.status)) {
            log.info("导出计划Job正在执行， 放弃此次执行.....");
            return;
        }
        plan.setStartTime(new Date());
        plan.setExecStatus(PlanStatus.ongoing);
        this.status = PlanStatus.ongoing;
        plan.setExecCount(plan.getExecCount() + 1);
        exportPlanRepository.save(plan);
        String filePath = appProperties.getExportPath();
        try {
            java.io.File base = new java.io.File(filePath);
            if (!base.exists()) throw new RuntimeException("导出文件夹 " + filePath + "不存在" );
            String fileDirName = "全部文档_" + new SimpleDateFormat("yyyyMMddHHmmss" ).format(new Date());
            java.io.File dir = new File(base, fileDirName);
            dir.mkdir();
            fileService.exportFile(dir);
            if (!StringUtils.isEmpty(plan.getExportFilePath())) {
                java.io.File oldExportFile = new java.io.File(plan.getExportFilePath());
                if (oldExportFile != null && oldExportFile.exists()) {
                    FileSystemUtils.deleteRecursively(oldExportFile); // 删除上次备份
                }
            }
            plan.setExportFilePath(dir.getAbsolutePath());
            plan.setSize(0l);
            plan.setSuccess(true);
            plan.setRemark("导出成功");
        } catch (Exception e) {
            log.error("执行导出计划error", e);
            plan.setSuccess(false);
            plan.setRemark(e.getMessage());
        }
        plan.setExecStatus(PlanStatus.completed);
        this.status = PlanStatus.completed;
        plan.setEndTime(new Date());
        exportPlanRepository.save(plan);
    }

    @Override
    public List<String> getExportFileList() {
        java.io.File file = new java.io.File(appProperties.getExportPath());
        return Arrays.asList(file.list());
    }

    private boolean exist () {
        return getPlan() != null;
    }
}

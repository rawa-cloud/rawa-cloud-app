package com.rawa.cloud.service;

import com.rawa.cloud.domain.ExportPlan;
import com.rawa.cloud.model.exportplan.ExportPlanAddModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface ExportPlanService {

    Long addPlan(ExportPlanAddModel model);

    void deletePlan();

    ExportPlan getPlan ();

    void execPlan ();

    List<String> getExportFileList ();
}

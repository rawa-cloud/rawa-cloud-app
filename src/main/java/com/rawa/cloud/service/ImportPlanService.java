package com.rawa.cloud.service;

import com.rawa.cloud.domain.ImportPlan;
import com.rawa.cloud.model.importplan.ImportPlanAddModel;

import java.util.List;

public interface ImportPlanService {

    Long addPlan(ImportPlanAddModel model);

    void deletePlan();

    ImportPlan getPlan();

    void execPlan ();

    List<String> getImportFileList ();

}

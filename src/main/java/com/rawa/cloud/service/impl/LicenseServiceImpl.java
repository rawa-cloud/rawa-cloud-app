package com.rawa.cloud.service.impl;

import com.rawa.cloud.bean.Licence;
import com.rawa.cloud.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LicenseServiceImpl implements LicenseService {

    @Autowired
    Licence licence;

    @Override
    public void writeLicense(String lic) {
        licence.writeLicense(lic);
    }

    @Override
    public Licence getLicense() {
        return licence;
    }
}

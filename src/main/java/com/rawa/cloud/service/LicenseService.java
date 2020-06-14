package com.rawa.cloud.service;

import com.rawa.cloud.bean.Licence;

public interface LicenseService {

    void writeLicense (String license);

    Licence getLicense ();

}

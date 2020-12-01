package com.rawa.cloud.runner.data;

import com.rawa.cloud.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PropertyDataGenerator implements DataGenerator{

    @Autowired
    PropertyService propertyService;


    @Override
    public void generate() {
        String license = propertyService.getValue("license");
        if (StringUtils.isEmpty(license)) propertyService.add("license", "IuR6gG5cAgfvk2IxddTgVEkrPJKnLkcTaDJ74gt/LmX2ercpYowcNhAfiYSBoatEYsSH6/FOr/ltjH/nloEXsvryikm9vQWNUkRnyspbLbIVDEnOx6NtrCVXBiag+t2CcEhSmXjqvXONZxDOnD049MkziptX9Ge+Wiv64OuJdHo=");
        String pubKey = propertyService.getValue("pub_key");
        if (StringUtils.isEmpty(license)) propertyService.add("pub_key", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIV/RvjK870Fip0Dt7jjMlchjT0UBPLRSBRpUitlrdshu+3NeImXaLHl24j7kZOz7yVtXNuow6VUTENyTYMhAMaqHum7AmR+35kpIXtNNNzd5u0HM/KxanJc3/uQMrTXvauox0qrxEyCQ3mNg7RBdWiuqPXHav5OE2BtjMZIR4uwIDAQAB");
    }
}

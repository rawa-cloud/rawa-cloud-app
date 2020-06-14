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
        if (StringUtils.isEmpty(license)) propertyService.add("license", "UBLGK5n+d0C8fVIIkGIWzLdEIz5ueV4EOleO1tyHyJ/Jat8GtvhqEE/dAwfYw45MUnIoH5JIt429/KcFyo6iQ4SWR0eyt/Zwg6KURvNlSfJGfz8J9ciBlKfApJoBBNDJ7ioSIJDvHqBmEqzSqSPwETGmnYZ0y/F4GVUgOqB4bpk=");
        String pubKey = propertyService.getValue("pub_key");
        if (StringUtils.isEmpty(license)) propertyService.add("pub_key", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIV/RvjK870Fip0Dt7jjMlchjT0UBPLRSBRpUitlrdshu+3NeImXaLHl24j7kZOz7yVtXNuow6VUTENyTYMhAMaqHum7AmR+35kpIXtNNNzd5u0HM/KxanJc3/uQMrTXvauox0qrxEyCQ3mNg7RBdWiuqPXHav5OE2BtjMZIR4uwIDAQAB");
    }
}

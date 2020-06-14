package com.rawa.cloud.helper;

import org.junit.Test;

public class EncryptHelperTest {

    @Test
    public void encrypt() {
        String encryptPassword = EncryptHelper.encrypt("123456");
        System.out.println(encryptPassword);
    }
}
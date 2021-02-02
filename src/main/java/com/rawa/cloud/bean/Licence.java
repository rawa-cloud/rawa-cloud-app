package com.rawa.cloud.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.DateHelper;
import com.rawa.cloud.helper.LangHelper;
import com.rawa.cloud.helper.RSAHelper;
import com.rawa.cloud.service.PropertyService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
@Order(1000)
public class Licence implements ApplicationRunner {
    @JsonIgnore
    @Autowired
    PropertyService propertyService;

    private String text;

    @JsonIgnore
    private String pubKey;

    @JsonIgnore
    private boolean validated = false;

    private String localMac;

    private Date installDate;

    private String mac;

    private Integer duration;

    private Integer limitUser;

    private Boolean free;

    public Date getExpiredDate () {
        if (duration <= 0) return null;
        return DateHelper.addDay(duration, installDate);
    }

    public boolean checkMac () {
        if (this.free) return true;
        if (StringUtils.isEmpty(this.mac)) return true;
        return this.mac.equals(localMac);
    }

    public boolean checkLimitUser (int num) {
        if (this.free) return true;
        if (this.limitUser == null) return true;
        return this.limitUser >= num;
    }

    public boolean checkExpiredDate () {
        if (this.free) return true;
        if (this.getExpiredDate() == null) return true;
        long now = new Date().getTime();
        return this.getExpiredDate().getTime() >= now;
    }

    public void load () {
        this.text = propertyService.getValue("license");
        this.pubKey = propertyService.getValue("pub_key");
        loadInstallData();
        loadLocalMac();
        parse();
        this.validated = false;
    }

    public void writeLicense (String lic) {
        try {
            this.initParse(lic, true);
        } catch (RuntimeException e) {
            throw new AppException(HttpJsonStatus.LICENSE_ILLEGAL, lic);
        }
        propertyService.add("license", lic);
        this.load();
    }

    private void parse () {
        initParse(this.text, false);
    }

    private void initParse (String text, boolean check) {
        String json = RSAHelper.decipher(text, this.pubKey, true);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> ret = objectMapper.readValue(json, HashMap.class);
            Map<String, Object> map = new HashMap();
            String mac = String.valueOf(ret.get("mac"));
            Integer duration = Integer.valueOf(String.valueOf(ret.get("duration")));
            Integer limitUser = ret.get("limitUser") == null ? null : Integer.valueOf(String.valueOf(ret.get("limitUser")));
            Boolean free = Boolean.valueOf(String.valueOf(ret.get("free")));
            if (!check) {
                this.mac = mac;
                this.duration = duration;
                this.limitUser = limitUser;
                this.free = true;
            } else {
                check(free, mac, duration);
            }
        } catch (Exception e) {
            if (e instanceof AppException) throw (AppException)e;
            throw new RuntimeException("parse license fail", e);
        }
    }

    private void loadInstallData ()  {
        try {
            String key = "install_date";
            String date = propertyService.getValue(key);
            if (StringUtils.isEmpty(date)) {
                date = new Date().getTime() + "";
                propertyService.add(key, date);
            }
            this.installDate = new Date(Long.valueOf(date));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadLocalMac ()  {
        try {
            String key = "local_mac";
            String localMac = propertyService.getValue(key);
            if (StringUtils.isEmpty(localMac)) {
                String b1 = LangHelper.randomString(2).toLowerCase();
                String b2 = LangHelper.randomString(2).toLowerCase();
                String b3 = LangHelper.randomString(2).toLowerCase();
                String b4 = LangHelper.randomString(2).toLowerCase();
                localMac = b1 + ":" + b2 + ":" + b3 + ":" + b4;
                propertyService.add(key, localMac);
            }
            this.localMac = localMac;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void check (Boolean free, String mac, int duration) {
        if (this.free) return;
        if (!StringUtils.isEmpty(mac) && !mac.equals(localMac)) {
            throw new AppException(HttpJsonStatus.LICENSE_INVALID, mac);
        }

        Date expiredDate = null;
        if (duration > 0) expiredDate = DateHelper.addDay(duration, installDate);
        if (expiredDate != null) {
            long now = new Date().getTime();
            if(expiredDate.getTime() < now) throw new AppException(HttpJsonStatus.LICENSE_EXPIRED, expiredDate);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.load();
    }
}

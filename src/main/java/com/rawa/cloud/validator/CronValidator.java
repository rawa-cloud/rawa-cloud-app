package com.rawa.cloud.validator;

import com.rawa.cloud.validator.annotation.Cron;
import org.springframework.scheduling.support.CronTrigger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CronValidator implements ConstraintValidator<Cron, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            new CronTrigger(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}


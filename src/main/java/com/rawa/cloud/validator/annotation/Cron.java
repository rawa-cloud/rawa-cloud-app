package com.rawa.cloud.validator.annotation;

import com.rawa.cloud.validator.CronValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CronValidator.class)
public @interface Cron {
    String message() default "must be a cron expression";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

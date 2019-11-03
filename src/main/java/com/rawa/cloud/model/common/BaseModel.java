package com.rawa.cloud.model.common;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.helper.ExceptionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.ParameterizedType;

@Slf4j
public abstract class BaseModel<E> {

    public E toDomain () {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class clz = (Class<E>) type.getActualTypeArguments()[0];
        try {
            E domain = (E)clz.newInstance();
            BeanUtils.copyProperties(this, domain);
            return domain;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionHelper.throwAppException(HttpJsonStatus.MODEL_CONVERT_ERROR, null);
        }
    }
}

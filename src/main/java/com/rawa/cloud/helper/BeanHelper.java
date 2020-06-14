package com.rawa.cloud.helper;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;

public class BeanHelper {
    public static String[] getNullFields (Object obj, String... exclude) {
        List<String> ret = new ArrayList<>();
        if(obj == null) return new String[]{};
        Field[] fields = FieldUtils.getAllFields(obj.getClass());
        for(Field s : fields){
            Object value;
            try {
                s.setAccessible(true);
                value = s.get(obj);
                if (value == null) {
                    ret.add(s.getName());
                }
            } catch (IllegalAccessException e) {
            }
        }
        Set<String> set = new HashSet<>(ret);
        Set<String> excludeSet = new HashSet<>(Arrays.asList(exclude));
        set.removeAll(excludeSet);
        return set.toArray(new String[]{});
    }
}

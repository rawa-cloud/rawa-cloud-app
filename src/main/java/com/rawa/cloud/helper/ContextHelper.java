package com.rawa.cloud.helper;

import com.rawa.cloud.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.LinkedList;
import java.util.List;

public class ContextHelper {
    public static User getCurrentUser() {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(p instanceof User) return (User) p;
        return null;
    }

    public static String getCurrentUsername() {
        UserDetails user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public static List<Long> getBatchIds (String ids) {
        List<Long> ret = new LinkedList<>();
        if (ids ==  null) return ret;
        String[] arr = ids.split(",");
        for(int i = 0; i< arr.length; i++) {
            ret.add(Long.valueOf(arr[i]));
        }
        return ret;
    }
}

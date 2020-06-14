package com.rawa.cloud.helper;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LangHelper {

    public static String randomString (int len) {
        List<Integer> nums = range(48, 58);
        List<Integer> chars = range(65, 91);
        nums.addAll(chars);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int sum = new Double(Math.random() * 1000).intValue();
            int remainder = sum % nums.size();
            char ch = (char)(int)nums.get(remainder);
            sb.append(ch);
        }
        return sb.toString();
    }

    public static List<Integer> range (int start, int end) {
        List<Integer> ret = new LinkedList<>();
        for (int i = start; i < end; i++) {
            ret.add(i);
        }
        return ret;
    }

    public static <E> boolean equals (List<E> ls1, List<E> ls2) {
        if (ls1.size() != ls2.size()) return false;
        String str1 = ls1.stream().sorted().collect(Collectors.toList()).toString();
        String str2 = ls2.stream().sorted().collect(Collectors.toList()).toString();
        return str1.equals(str2);
    }

    public static <T> boolean equals (T o1, T o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == null || o2 == null) return false;
        return o1.equals(o2);
    }
}

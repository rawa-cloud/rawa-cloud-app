package com.rawa.cloud.helper;

import java.util.LinkedList;
import java.util.List;

public class LangHelper {

    public static String randomString (int len) {
        List<Integer> nums = range(48, 58);
        List<Integer> chars = range(63, 91);
        nums.addAll(chars);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int sum = new Double(Math.random() * 1000).intValue();
            int remainder = sum % nums.size();
            char ch = Character.forDigit(remainder, 10);
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
}

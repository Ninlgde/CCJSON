package com.ninlgde.ccjson.base;

import java.util.HashMap;
import java.util.Map;

public class JsonType {
    public static final int JSON_NUMBER = 0;
    public static final int JSON_INT = 1;
    public static final int JSON_STRING = 2;
    public static final int JSON_ARRAY = 3;
    public static final int JSON_OBJECT = 4;
    public static final int JSON_TRUE = 5;
    public static final int JSON_FALSE = 6;
    public static final int JSON_LONG = 7;
    public static final int JSON_NULL = 0xf;

    static Map<Integer, String> map = new HashMap<Integer, String>();
    static {
        map.put(JSON_NUMBER, "number");
        map.put(JSON_INT, "int");
        map.put(JSON_STRING, "string");
        map.put(JSON_ARRAY, "array");
        map.put(JSON_OBJECT, "object");
        map.put(JSON_TRUE, "bool");
        map.put(JSON_FALSE, "bool");
        map.put(JSON_LONG, "long");
        map.put(JSON_NULL, "null");
    }

    public static String getString(int type) {
        String result = map.get(type);
        return result == null ? "unknown" : result;
    }
}

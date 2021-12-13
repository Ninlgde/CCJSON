package com.ninlgde.ccjson.serialize.error;

import com.ninlgde.ccjson.base.JsonType;

public class JsonTypeException extends JsonException {
    public JsonTypeException(int type, int expect) {
        super("JsonTypeException :"
                + JsonType.getString(type)
                + ", expect :"
                + JsonType.getString(expect));
    }
}

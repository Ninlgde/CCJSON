package com.ninlgde.zenjson.serialize.error;

import com.ninlgde.zenjson.base.JsonType;

public class JsonTypeException extends JsonException {
    public JsonTypeException(int type, int expect) {
        super("JsonTypeException :"
                + JsonType.getString(type)
                + ", expect :"
                + JsonType.getString(expect));
    }
}

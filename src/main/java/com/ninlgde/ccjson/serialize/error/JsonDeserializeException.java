package com.ninlgde.ccjson.serialize.error;

public class JsonDeserializeException extends JsonException {
    public JsonDeserializeException(DeserializeError error) {
        super(error.name());
    }
}

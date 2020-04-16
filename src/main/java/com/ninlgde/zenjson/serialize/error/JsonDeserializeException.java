package com.ninlgde.zenjson.serialize.error;

public class JsonDeserializeException extends JsonException {
    public JsonDeserializeException(DeserializeError error) {
        super(error.name());
    }
}

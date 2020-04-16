package com.ninlgde.zenjson;

import com.ninlgde.zenjson.base.Node;
import com.ninlgde.zenjson.base.Value;
import com.ninlgde.zenjson.serialize.error.DeserializeError;
import com.ninlgde.zenjson.base.JsonType;
import com.ninlgde.zenjson.serialize.Deserializer;
import com.ninlgde.zenjson.serialize.error.JsonDeserializeException;
import com.ninlgde.zenjson.serialize.Serializer;
import com.ninlgde.zenjson.serialize.writer.ByteBufWriter;
import com.ninlgde.zenjson.serialize.writer.StringWriter;
import com.ninlgde.zenjson.serialize.writer.Writer;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public abstract class Json implements Serializable {

    protected Value root;

    protected Node tail;
    protected int size;

    public Json() {
    }

    public Json(Value value) {
        this.root = value;
    }

    public int getType() {
        return root.getType();
    }

    protected void findTail() {
        for (Node node = root.toNode(); node != null; size++ /*calculate size*/, node = node.next)
            tail = node;
    }

//    public Json(String jsonstr) throws JsonException {
//        ByteBuffer buffer = ByteBuffer.wrap(jsonstr.getBytes());
//        root = Value.typeToValue(JsonType.JSON_OBJECT, null);
//        DeserializeError error = Deserializer.parse(buffer, root);
//        if (error != DeserializeError.ERROR_NO_ERROR)
//            throw new JsonException(error);
//    }
//
//    public Json(ByteBuffer buffer) throws JsonException {
//        root = Value.typeToValue(JsonType.JSON_OBJECT, null);
//        DeserializeError error = Deserializer.parse(buffer, root);
//        if (error != DeserializeError.ERROR_NO_ERROR)
//            throw new JsonException(error);
//    }

    protected Object valueToObject(Value v) {
        if (v == null)
            return null;
        switch (v.getType()) {
            case JsonType.JSON_INT:
                return v.toInt();
            case JsonType.JSON_NUMBER:
                return v.toNumber();
            case JsonType.JSON_OBJECT:
                return new JSONObject(v);
            case JsonType.JSON_ARRAY:
                return new JSONArray(v);
            case JsonType.JSON_FALSE:
            case JsonType.JSON_TRUE:
                return v.toBool();
            case JsonType.JSON_STRING:
                return v.toStr();
            default:
                return null;
        }
    }

    protected Value objectToValue(Object object) {
        Value value;
        if (object instanceof Integer || object instanceof Byte || object instanceof Character) {
            value = new Value((Integer) object);
        } else if (object instanceof Boolean) {
            value = new Value((Boolean) object);
        } else if (object instanceof Long) {
            value = new Value((Long) object);
        } else if (object instanceof Float || object instanceof Double) {
            double d = (Double) object;
            value = Value.typeToValue(JsonType.JSON_NUMBER, Double.toString(d).getBytes());
        } else if (object instanceof String) {
            value = Value.typeToValue(JsonType.JSON_STRING, ((String) object).getBytes());
        } else if (object instanceof Json) {
            Json jo = (Json) object;
            value = jo.root; // Json obj use reference
        } else if (object instanceof List) {
            List list = (List) object;
            JSONArray array = new JSONArray(list);
            value = Value.typeToValue(JsonType.JSON_ARRAY, array.root.toNode());
        } else if (object instanceof Map) {
            Map map = (Map) object;
            JSONObject array = new JSONObject(map);
            value = Value.typeToValue(JsonType.JSON_OBJECT, array.root.toNode());
        } else {
            // default to null
            value = Value.nullToVale();
        }
        return value;
    }

    public static JSONObject parse(String jsonstr) throws JsonDeserializeException {
        ByteBuffer buffer = ByteBuffer.wrap(jsonstr.getBytes());
        Value value = Deserializer.parse(buffer);
        return new JSONObject(value);
    }

    public static JSONObject parse(ByteBuffer buffer) throws JsonDeserializeException {
        Value value = Deserializer.parse(buffer);
        return new JSONObject(value);
    }

    public static JSONArray parseArray(String jsonstr) throws JsonDeserializeException {
        ByteBuffer buffer = ByteBuffer.wrap(jsonstr.getBytes());
        Value value = Deserializer.parse(buffer);
        return new JSONArray(value);
    }

    public static JSONArray parseArray(ByteBuffer buffer) throws JsonDeserializeException {
        Value value = Deserializer.parse(buffer);
        return new JSONArray(value);
    }

    public String dump() {
        return dump(false);
    }

    public String dump(boolean formatted) {
        Writer writer = new StringWriter();
        Serializer.dump(writer, root, formatted);
        return writer.toString();
    }

    public ByteBuffer dump(ByteBuffer buffer) {
        return dump(buffer, false);
    }

    public ByteBuffer dump(ByteBuffer buffer, boolean formatted) {
        Writer writer = new ByteBufWriter(buffer);
        Serializer.dump(writer, root, formatted);
        return writer.toByteBuf();
    }
}

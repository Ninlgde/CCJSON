package com.ninlgde.zenjson.serialize;

import com.ninlgde.zenjson.base.Node;
import com.ninlgde.zenjson.base.Value;
import com.ninlgde.zenjson.base.JsonType;
import com.ninlgde.zenjson.serialize.writer.Writer;

public class Serializer {

    public static void dump(Writer writer, Value value) {
        dump(writer, value, false);
    }

    public static void dump(Writer writer, Value value, boolean formatted) {
        dump(writer, value, formatted, 0);
    }

    public static void dump(Writer writer, Value value, boolean formatted, int indent) {
        int fmt = formatted ? 1 : 0;
        switch (value.getType()) {
            case JsonType.JSON_NUMBER:
                writer.writeNumberBytes(value.toBytes());
                break;
            case JsonType.JSON_LONG:
                writer.writeLong(value.toLong());
                break;
            case JsonType.JSON_INT:
                writer.writeInt(value.toInt());
                break;
            case JsonType.JSON_STRING:
                writer.writeEscaped(value.toBytes());
                break;
            case JsonType.JSON_ARRAY:
                if (value.toNode() == null) {
                    String[] s_empty_array = new String[]{"[]", "[ ]"};
                    writer.puts(s_empty_array[fmt].getBytes());
                    break;
                }
                writer.putc((byte) '[');
                if (formatted) writer.putc((byte) '\n');
                indent++;
                for (Node n = value.toNode(); n != null; n = n.next) {
                    if (formatted) writer.writeTabs(indent);
                    dump(writer, n.value, formatted, indent);
                    if (n.next != null) {
                        writer.putc((byte) ',');
                        if (formatted) writer.putc((byte) ' ');
                    }
                    if (formatted) writer.putc((byte) '\n');
                }
                indent--;
                if (formatted) writer.writeTabs(indent);
                writer.putc((byte) ']');
                break;
            case JsonType.JSON_OBJECT:
                if (value.toNode() == null) {
                    String[] s_empty_object = new String[]{"{}", "{ }"};
                    writer.puts(s_empty_object[fmt].getBytes());
                    break;
                }
                writer.putc((byte) '{');
                if (formatted) writer.putc((byte) '\n');
                indent++;
                for (Node n = value.toNode(); n != null; n = n.next) {
                    if (formatted) writer.writeTabs(indent);
                    writer.writeEscaped(n.name);
                    if (formatted)
                        writer.puts(" : ".getBytes());
                    else
                        writer.putc((byte) ':');
                    dump(writer, n.value, formatted, indent);
                    if (n.next != null) {
                        writer.putc((byte) ',');
                        if (formatted) writer.putc((byte) ' ');
                    }
                    if (formatted) writer.putc((byte) '\n');
                }
                indent--;
                if (formatted) writer.writeTabs(indent);
                writer.putc((byte) '}');
                break;
            case JsonType.JSON_TRUE:
                writer.puts("true".getBytes());
                break;
            case JsonType.JSON_FALSE:
                writer.puts("false".getBytes());
                break;
            case JsonType.JSON_NULL:
                writer.puts("null".getBytes());
                break;
        }
    }
}

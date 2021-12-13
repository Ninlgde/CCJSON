package com.ninlgde.ccjson.serialize;

import com.ninlgde.ccjson.base.Node;
import com.ninlgde.ccjson.base.Value;
import com.ninlgde.ccjson.base.JsonType;
import com.ninlgde.ccjson.serialize.writer.Writer;

public class Serializer {

    private static byte[][] s_empty_array = new byte[2][];
    private static byte[][] s_empty_object = new byte[2][];
    private static byte[] s_escape_colon = " : ".getBytes();
    private static byte[] s_true = "true".getBytes();
    private static byte[] s_false = "false".getBytes();
    private static byte[] s_null = "null".getBytes();

    static {
        s_empty_array[0] = "[]".getBytes();
        s_empty_array[1] = "[ ]".getBytes();
        s_empty_object[0] = "{}".getBytes();
        s_empty_object[1] = "{ }".getBytes();
    }

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
                    writer.puts(s_empty_array[fmt]);
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
                    writer.puts(s_empty_object[fmt]);
                    break;
                }
                writer.putc((byte) '{');
                if (formatted) writer.putc((byte) '\n');
                indent++;
                for (Node n = value.toNode(); n != null; n = n.next) {
                    if (formatted) writer.writeTabs(indent);
                    writer.writeEscaped(n.name);
                    if (formatted)
                        writer.puts(s_escape_colon);
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
                writer.puts(s_true);
                break;
            case JsonType.JSON_FALSE:
                writer.puts(s_false);
                break;
            case JsonType.JSON_NULL:
                writer.puts(s_null);
                break;
        }
    }
}

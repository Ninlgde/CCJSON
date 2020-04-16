package com.ninlgde.zenjson.serialize;

import com.ninlgde.zenjson.base.Node;
import com.ninlgde.zenjson.base.Value;
import com.ninlgde.zenjson.utils.TwoTuple;
import com.ninlgde.zenjson.base.JsonType;
import com.ninlgde.zenjson.serialize.error.DeserializeError;
import com.ninlgde.zenjson.serialize.error.JsonDeserializeException;

import java.nio.ByteBuffer;

public class Deserializer {

    private static final int ZJSON_STACK_SIZE = 32;

    private static StringParseResult parseString(ByteBuffer s, int offset) {
        int start = offset;
        s.position(offset);
        offset = parseStringInternal(s);
        if (offset == -1)
            return null;
        int len = s.position() - start; // java no need \0
        s.position(start);
        byte[] bytes = new byte[len];
        s.get(bytes, 0, len);
        s.position(offset); // drop pend and "
        return new StringParseResult(bytes, offset);
    }

    private static int parseStringInternal(ByteBuffer s) {
        int offset = s.position();
        while (!(s.get(offset) == '"' && s.get(offset - 1) != '\\'))
            offset++;
        s.position(offset);
        return ++offset;
    }

    private static class StringParseResult {
        public byte[] bytes;
        public int offset;

        StringParseResult(byte[] bytes, int offset) {
            this.bytes = bytes;
            this.offset = offset;
        }
    }

    public static boolean isWhitespace(byte ch) {
        return (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == '\f' || ch == '\b');
    }

    private static int skipWhitespace(ByteBuffer s, int offset) {
        while (isWhitespace(s.get(offset)))
            offset++;
        return offset;
    }

    public static Value parse(ByteBuffer s) throws JsonDeserializeException {
        Node[] tails = new Node[ZJSON_STACK_SIZE];
        byte[] endChars = new byte[ZJSON_STACK_SIZE];
        int top = -1;
        Node node;

        int offset = skipWhitespace(s, 0);

        if (s.get(offset) == '{' || s.get(offset) == '[') {
            ++top;
            tails[top] = null;
            endChars[top] = (byte) (s.get(offset) + 2); // {+2=} [+2=]
        } else
            throw new JsonDeserializeException(DeserializeError.ERROR_BAD_ROOT);

        ++offset;
        for (; ; ) {
            offset = skipWhitespace(s, offset);
            if (s.get(offset) == ',') {
                if (tails[top] == null)
                    throw new JsonDeserializeException(DeserializeError.ERROR_MISMATCH_BRACKET);
                ++offset;
                offset = skipWhitespace(s, offset);
            } else if (tails[top] != null && (s.get(offset) != endChars[top])) {
                throw new JsonDeserializeException(DeserializeError.ERROR_MISMATCH_BRACKET);
            }

            while (s.get(offset) == endChars[top]) {
                ++offset;
                for (; ; ) {
                    if (top == -1)
                        throw new JsonDeserializeException(DeserializeError.ERROR_STACK_UNDERFLOW);
                    int t = endChars[top] == '}' ? JsonType.JSON_OBJECT : JsonType.JSON_ARRAY;
                    Value v = Value.listToValue(t, tails[top--]);

                    if (top == -1) {
                        return v;
                    }
                    tails[top].value = v;

                    offset = skipWhitespace(s, offset);
                    if (s.get(offset) == ',') {
                        ++offset;
                        offset = skipWhitespace(s, offset);
                        break;
                    }
                    if (s.get(offset++) != endChars[top])
                        throw new JsonDeserializeException(DeserializeError.ERROR_MISMATCH_BRACKET);
                }
            }

            if (endChars[top] == ']') { // json array
                node = new Node();
                tails[top] = Node.insertAfter(tails[top], node);
            } else {
                node = new Node();
                tails[top] = Node.insertAfter(tails[top], node);

                if (s.get(offset) != '"')
                    throw new JsonDeserializeException(DeserializeError.ERROR_UNEXPECTED_CHARACTER);
                StringParseResult tuple = parseString(s, ++offset);
                if (tuple == null)
                    throw new JsonDeserializeException(DeserializeError.ERROR_BAD_STRING);
                tails[top].name = tuple.bytes;
                offset = tuple.offset;

                offset = skipWhitespace(s, offset);
                if (s.get(offset) != ':')
                    throw new JsonDeserializeException(DeserializeError.ERROR_UNEXPECTED_CHARACTER);
                ++offset;
                offset = skipWhitespace(s, offset);
            }

            switch (s.get(offset)) {
                case '{':
                case '[':
                    ++offset;
                    if (++top == ZJSON_STACK_SIZE)
                        throw new JsonDeserializeException(DeserializeError.ERROR_STACK_OVERFLOW);
                    tails[top] = null;
                    endChars[top] = (byte) (s.get(offset - 1) + 2);
                    break;
                case '"': // str
                    ++offset;
                    StringParseResult tuple = parseString(s, offset);
                    if (tuple == null)
                        throw new JsonDeserializeException(DeserializeError.ERROR_BAD_STRING);
                    tails[top].value = Value.typeToValue(JsonType.JSON_STRING, tuple.bytes);
                    offset = tuple.offset;
                    break;
                case 'n': // null
                    if (s.get(offset + 1) == 'u' && s.get(offset + 2) == 'l' && s.get(offset + 3) == 'l') {
                        offset += 4;
                        tails[top].value = Value.nullToVale();
                    } else
                        throw new JsonDeserializeException(DeserializeError.ERROR_BAD_IDENTIFIER);
                    break;
                case 't': // true
                    if (s.get(offset + 1) == 'r' && s.get(offset + 2) == 'u' && s.get(offset + 3) == 'e') {
                        offset += 4;
                        tails[top].value = new Value(true);
                    } else
                        throw new JsonDeserializeException(DeserializeError.ERROR_BAD_IDENTIFIER);
                    break;
                case 'f': // true
                    if (s.get(offset + 1) == 'a' && s.get(offset + 2) == 'l' && s.get(offset + 3) == 's' && s.get(offset + 4) == 'e') {
                        offset += 5;
                        tails[top].value = new Value(false);
                    } else
                        throw new JsonDeserializeException(DeserializeError.ERROR_BAD_IDENTIFIER);
                    break;
                case '0': // JSON number
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '-':
                case '.':
                    offset = parseNumber(tails[top], s, offset);
                    break;
                case '\0':
                default:
                    throw new JsonDeserializeException(DeserializeError.ERROR_BREAKING_BAD);
            }
        }
    }

    private static int parseNumber(Node tailTop, ByteBuffer s, int offset) {
        int isDouble = 0;
        int start = offset;
        byte ch;
        while ((isDigit((ch = s.get(offset++))) || (isDotEe(ch))))
            isDouble += !isDigit(ch) && isDotEe(ch) ? 1:0;
        int len = --offset - start; // offset 回退1
        if (len == 0)
            System.out.print(len);
        byte[] bytes = new byte[len];
        s.position(start);
        s.get(bytes);
        s.position(offset);
        if (isDouble == 0) {
            long l = Long.parseLong(new String(bytes));
            if (l >= -2147483648L && l <= 2147483647L) {
                // int
                tailTop.value = new Value((int) l);
            } else
                tailTop.value = new Value(l);
        } else {
            tailTop.value = Value.typeToValue(JsonType.JSON_NUMBER, bytes);
        }
        return offset;
    }

    private static boolean isDigit(byte ch) {
        return ch >= 0x30 && ch <= 0x39 || ch == '-';
    }

    private static boolean isDotEe(byte ch) {
        return  ch == '.' || ch == 'e' || ch == 'E';
    }

}

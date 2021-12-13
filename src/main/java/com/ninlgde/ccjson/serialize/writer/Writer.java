package com.ninlgde.ccjson.serialize.writer;

import com.ninlgde.ccjson.serialize.dtoa.DToA;

import java.nio.ByteBuffer;

public abstract class Writer {

    public abstract void puts(byte[] bytes);

    public abstract void putc(byte b);

    public abstract int size();

    public abstract void reset();

    public abstract void writeTabs(int n);

    public abstract String toString();

    public abstract ByteBuffer toByteBuf();

    public void writeInt(int i) {
        byte[] bytes = DToA.i32toa(i);
        assert bytes != null;
        puts(bytes);
    }

    public void writeLong(long l) {
        byte[] bytes = DToA.l64toaJ(l);
        assert bytes != null;
        puts(bytes);
    }

    public void writeNumber(double d) {
        byte[] bytes = DToA.d64toaJ(d);
        assert bytes != null;
        puts(bytes);
    }

    public void writeNumberBytes(byte[] bytes) {
        assert bytes != null;
        puts(bytes);
    }

    public void writeEscaped(byte[] s) {
        byte[] s_to_hex = new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int offset = 0;
        putc((byte) '"');
        while (offset < s.length) {
            byte c = s[offset++];
            if (c < 0 || (c >= ' ' && c != '"' && c != '\\')) // java byte有符号,所以<0的必然>' '
                putc(c);
            else {
                putc((byte) '\\');
                switch (c) {
                    case '\b':
                        putc((byte) 'b');
                        break;
                    case '\r':
                        putc((byte) 'r');
                        break;
                    case '\t':
                        putc((byte) 't');
                        break;
                    case '\f':
                        putc((byte) 'f');
                        break;
                    case '\n':
                        putc((byte) 'n');
                        break;
                    case '\\':
                        putc((byte) '\\');
                        break;
                    case '\"':
                        putc((byte) '\"');
                        break;
                    default:
                        puts("u00".getBytes());
                        putc(s_to_hex[c >>> 4 & 0xf]);
                        putc(s_to_hex[c & 0xF]);
                        break;
                }
            }
        }
        putc((byte) '"');
    }
}

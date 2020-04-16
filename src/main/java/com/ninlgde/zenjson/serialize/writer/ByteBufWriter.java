package com.ninlgde.zenjson.serialize.writer;

import java.nio.ByteBuffer;

public class ByteBufWriter extends Writer {

    public ByteBuffer buffer;

    public ByteBufWriter(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteBufWriter() {
    }

    public void puts(byte[] bytes) {
        if (!checkBounds(bytes.length))
            reallocate();
        buffer.put(bytes);
    }

    public void putc(byte b) {
        if (!checkBounds(1))
            reallocate();
        buffer.put(b);
    }

    private boolean checkBounds(int g) {
        int newPos = buffer.position() + g;
        if (newPos <= buffer.limit())
            return true;
        else if (newPos <= buffer.capacity()) {
            buffer.limit(buffer.capacity());
            return true;
        } else
            return false;
    }

    private void reallocate() {
        int newCap = newCapacity();
        ByteBuffer newBuf;
        if (buffer.isDirect()) {
            newBuf = ByteBuffer.allocateDirect(newCap);
        } else {
            newBuf = ByteBuffer.allocate(newCap);
        }
        buffer.position(0);
        newBuf.put(buffer);
        newBuf.position(buffer.position());
        buffer = newBuf;
    }

    private int reallocateTimes = 0;

    private int newCapacity() {
        if (reallocateTimes++ < 5) {
            return (buffer.capacity() << 1); // reallocate 2x
        }
        return (buffer.capacity() << 1) - (buffer.capacity() >> 1); // reallocate 1.5x
    }

    public int size() {
        return buffer.position();
    }

    public void reset() {
        buffer.clear();
    }

    public void writeTabs(int n) {
        while (n-- > 0)
            putc((byte) '\t');
    }

    public String toString() {
        int pos = buffer.position();
        byte[] bytes = new byte[pos];
        buffer.position(0);
        buffer.get(bytes);
        return new String(bytes);
    }

    public ByteBuffer toByteBuf() {
        return buffer;
    }
}

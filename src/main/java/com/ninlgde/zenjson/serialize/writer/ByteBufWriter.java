package com.ninlgde.zenjson.serialize.writer;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class ByteBufWriter extends Writer {

    public ByteBuffer buffer;

    // 统计信息
    private int reallocateTimes = 0;
    protected static volatile AtomicLong reallocateTotalTimes = new AtomicLong(0);
    protected static volatile AtomicLong newTimes = new AtomicLong();

    public ByteBufWriter(ByteBuffer buffer) {
        this.buffer = buffer;
        newTimes.getAndIncrement();
    }

    public ByteBufWriter() {
        newTimes.getAndIncrement();
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
        int oldPosition = buffer.position();
        int newCap = newCapacity();
        ByteBuffer newBuf;
        if (buffer.isDirect()) {
            newBuf = ByteBuffer.allocateDirect(newCap);
        } else {
            newBuf = ByteBuffer.allocate(newCap);
        }
        buffer.position(0);
        newBuf.put(buffer);
        newBuf.position(oldPosition); // set the last position
        buffer = newBuf;
        countReallocate();
    }

    protected void countReallocate() {
    }

    private int newCapacity() {
        if (reallocateTimes++ < 10) {
            return (buffer.capacity() << 1); // reallocate 2x
        }
        return buffer.capacity() + (buffer.capacity() >> 1); // reallocate 1.5x
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

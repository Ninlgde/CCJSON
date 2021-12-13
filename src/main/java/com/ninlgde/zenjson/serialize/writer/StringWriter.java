package com.ninlgde.zenjson.serialize.writer;

import java.nio.ByteBuffer;

public class StringWriter extends ByteBufWriter {

    public StringWriter() {
        super();
        int avg = getAverageReallocateTimes();
        buffer = ByteBuffer.allocate(128 << avg);
    }

    /**
     * 过度优化的代表 哈哈哈哈
     * 计算一个平均扩容的次数,减少扩容次数
     * 毕竟memcpy也是O(n)算法
     * @return
     */
    private int getAverageReallocateTimes() {
        // this method is not threadsafe
        return  (int) (reallocateTotalTimes.get() / Math.max(10, newTimes.get()));
    }

    protected void countReallocate(){
        reallocateTotalTimes.getAndIncrement();
    }

    public String toString() {
        byte[] bytes = new byte[buffer.position()];
        buffer.flip();
        buffer.get(bytes);
        return new String(bytes);
    }

}

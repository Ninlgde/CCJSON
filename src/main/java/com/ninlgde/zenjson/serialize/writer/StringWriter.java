package com.ninlgde.zenjson.serialize.writer;

import java.nio.ByteBuffer;

public class StringWriter extends ByteBufWriter {

    public StringWriter() {
        super();
        buffer = ByteBuffer.allocate(256);
    }

    public StringWriter(ByteBuffer buffer) {
        super(buffer);
    }

    public String toString() {
        byte[] bytes = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytes);
        return new String(bytes);
    }

}

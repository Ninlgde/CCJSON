package com.ninlgde.zenjson.file;

import com.ninlgde.zenjson.JSON;
import com.ninlgde.zenjson.JSONObject;
import com.ninlgde.zenjson.serialize.error.JsonDeserializeException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class JSONFileReader {

    @Deprecated
    public static JSONObject parseFileRW(String filename) throws JsonDeserializeException, IOException {
        FileChannel fc = new RandomAccessFile(filename, "rw").getChannel();

        // mmap zero copy to buffer
        MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size());

        // copy to user space
        ByteBuffer buffer1 = ByteBuffer.allocateDirect((int) fc.size());
        buffer1.put(buffer);

        // parse to jvm heap
        return JSON.parse(buffer1);
    }


    public static JSONObject parseFile(String filename) throws JsonDeserializeException, IOException {
        FileChannel fc = new FileInputStream(filename).getChannel();

        // mmap zero copy to buffer
        MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

        // parse to jvm heap
        return JSON.parse(buffer);
    }
}

package com.ninlgde.zenjson.file;

import com.ninlgde.zenjson.JSON;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class JSONFileWriter {

    public static void writeJSON(String filename, byte[] bytes) throws IOException {
        // delete file first
        File f = new File(filename);
        if (f.exists())
            f.delete();
        // read & write file channel
        FileChannel fc = new RandomAccessFile(filename, "rw").getChannel();

        // mmap zero copy: make a kernel spaces buffer
        MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);

        // copy heap bytes to kernel spaces
        buffer.put(bytes);
    }

    public static void writeJSON(String filename, String jsonstr) throws IOException {
        byte[] bytes = jsonstr.getBytes();

        writeJSON(filename, bytes);
    }

    public static void writeJSON(String filename, JSON json) throws IOException {
        byte[] bytes = json.dump().getBytes();

        writeJSON(filename, bytes);
    }
}

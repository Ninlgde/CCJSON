package com.ninlgde.zenjson.file;

import com.ninlgde.zenjson.JSONObject;
import com.ninlgde.zenjson.Json;
import com.ninlgde.zenjson.serialize.error.JsonDeserializeException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ThreadLocalRandom;

public class JSONFileReader {

    public static JSONObject parseFile(String fname) throws JsonDeserializeException, IOException {
        FileChannel fc = new RandomAccessFile(fname, "rw").getChannel();

        MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size());

        ByteBuffer buffer1 = ByteBuffer.allocate((int) fc.size());
        buffer1.put(buffer);

        return Json.parse(buffer1);
    }

    public static void main(String[] args) throws IOException, JsonDeserializeException {

        String fname = "resources/data/test.json";

        JSONObject jsonObject = parseFile(fname);

        jsonObject.put("double", ThreadLocalRandom.current().nextDouble());
        jsonObject.put("int", ThreadLocalRandom.current().nextInt());
        jsonObject.put("name", "machicheng");

        System.out.println(jsonObject.dump());
    }
}

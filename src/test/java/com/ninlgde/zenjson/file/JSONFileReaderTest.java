package com.ninlgde.zenjson.file;

import com.ninlgde.zenjson.JSON;
import com.ninlgde.zenjson.JSONObject;
import com.ninlgde.zenjson.serialize.error.JsonDeserializeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import static com.ninlgde.zenjson.Benchmark.printResult;

public class JSONFileReaderTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parseFile() throws IOException, JsonDeserializeException {
//        String fname = "data/canada.json"; // 2251051
        String fname = "data/twitter.json"; // 631515
//        String fname = "data/citm_catalog.json"; // 1727205
//        String fname = "data/test.json";

        JSONObject jsonObject = JSONFileReader.parseFile(fname);

        System.out.println(jsonObject.dump());
//        byte[] bytes = jsonObject.dump().getBytes();
//        for (int i = 0; i < bytes.length; i++) {
//            if (bytes[i] == '\u0000') {
//                System.out.println(bytes[i]);
//            }
//        }

        JSONFileWriter.writeJSON("data/test_out.json", jsonObject);
    }

    @Test
    public void parseFileAndModify() throws IOException, JsonDeserializeException {
        String fname = "data/test.json";

        JSONObject jsonObject = JSONFileReader.parseFile(fname);

        jsonObject.put("double", ThreadLocalRandom.current().nextDouble());
        jsonObject.put("int", ThreadLocalRandom.current().nextInt());
        jsonObject.put("name", "machicheng");
        jsonObject.remove("long");

        System.out.println(jsonObject.dump(true));

        JSONFileWriter.writeJSON("data/test_out.json", jsonObject);
    }


    private static final int N = 1000;

    @Test
    public void parseFileBenchMark() throws IOException, JsonDeserializeException {
        String fname = "data/citm_catalog.json";
        long length = 1727205; // bits of twitter.json
        System.out.println("testJZenZeroCopy start -- " + new Date());

        long start = System.nanoTime();
        JSONObject object = null;
        for (int i = 0; i < N; ++i) {
            object = JSONFileReader.parseFile(fname);
        }
        printResult(fname, "parse", System.nanoTime() - start, length);
        ByteBuffer buffer2 = ByteBuffer.allocate(1024 * 1024 * 4);
        start = System.nanoTime();
        for (int i = 0; i < N; i++) {
            buffer2.position(0);
            object.dump(buffer2);
        }
        printResult(fname, "dump", System.nanoTime() - start, length);

        System.out.println("testJZenZeroCopy end -- " + new Date());
    }
}

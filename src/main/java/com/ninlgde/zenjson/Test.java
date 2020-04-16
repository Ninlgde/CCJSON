package com.ninlgde.zenjson;

import com.ninlgde.zenjson.serialize.error.JsonDeserializeException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class Test {

    private static final int N = 1000;

    private static void printResult(String fname, String name, long elapsed, long length) {
        double average = elapsed / 1000000.0 / N;
        if (name.equals("parse")) {
            double throughput = length / (1024.0 * 1024.0) / (average * 0.001);
            System.out.printf("%8s %24s    %.3f ms    %3.3f MB/s\n", name, fname, average, throughput);
        } else {
            System.out.printf("%8s %24s    %.3f ms\n", name, fname, average);
        }
    }

    private static void testJZen(String fname) throws IOException, JsonDeserializeException {

        FileChannel fc = new RandomAccessFile(fname, "rw").getChannel();
        long length = fc.size();
        System.out.println("file :" + length);
        MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, length);

        System.out.println("testJZenZeroCopy start -- " + new Date());

        long start = System.nanoTime();
        JSONObject object = null;
        for (int i = 0; i < N; ++i) {
            buffer.position(0);
            ByteBuffer buffer1 = ByteBuffer.allocateDirect((int) length);
            buffer1.put(buffer);
            object = Json.parse(buffer1);
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

    private static void testJZenNormal(String fname) throws JsonDeserializeException {


        String jsonstr = readFile(fname);
        long length = jsonstr.length();
        System.out.println("file :" + length);

        System.out.println("testJZenNormal start -- " + new Date());

        long start = System.nanoTime();
        JSONObject object = null;
        for (int i = 0; i < N; ++i) {
            String jsonstr1 = new String(jsonstr.getBytes()); // 假装copy了一次
            object = Json.parse(jsonstr1);
        }
        printResult(fname, "parse", System.nanoTime() - start, length);
        start = System.nanoTime();
        for (int i = 0; i < N; i++) {
            String _no = object.dump();
        }
        printResult(fname, "dump", System.nanoTime() - start, length);

        System.out.println("testJZenNormal end -- " + new Date());
    }


//    private static void testFastJson(String fname) {
//
//        String jsonstr = readFile(fname);
//        long length = jsonstr.length();
//        ; //fc.size();
//        System.out.println("file length:" + length);
//
//        System.out.println("testFastJson start -- " + new Date());
//
//        long start = System.nanoTime();
//        JSONObject object = null;
//        for (int i = 0; i < N; ++i) {
//            String jsonstr1 = new String(jsonstr.getBytes());
//            object = JSON.parseObject(jsonstr1);
//        }
//        printResult(fname, "parse", System.nanoTime() - start, length);
//        start = System.nanoTime();
//        for (int i = 0; i < N; i++) {
//            String str = object.toJSONString();
//        }
//        printResult(fname, "dump", System.nanoTime() - start, length);
//
//        System.out.println("testFastJson end -- " + new Date());
//    }

    public static void main(String[] args) throws IOException, JsonDeserializeException {

        String fname = "resources/data/twitter.json";

        testJZen(fname);
//        testJZenNormal(fname);
        System.in.read();
    }

    public static String readFile(String Path) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }
}

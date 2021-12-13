package com.ninlgde.ccjson.serialize.dtoa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class DToATest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private void assertBytes(byte[] result, byte[] expect) {
        assertEquals(expect.length, result.length);
        for (int i = 0; i < expect.length; i++)
            assertEquals(result[i], expect[i]);
    }

    private void i32to_test_int(int i) {
        byte[] expect = DToA.i32toaJ(i);
        byte[] result = DToA.i32toa(i);
        assertBytes(result, expect);
    }

    @Test
    public void i32toa_max_value() {
        i32to_test_int(Integer.MAX_VALUE);
    }

    @Test
    public void i32toa_min_value() {
        i32to_test_int(Integer.MIN_VALUE);
    }

    @Test
    public void i32toa_0() {
        i32to_test_int(0);
    }

    @Test
    public void i32toa() {
        int total = 10;
        int per = 1000000;
        long totalU1 = 0;
        long totalU2 = 0;
        for (int i = 0; i < total; i++) {
            int t = ThreadLocalRandom.current().nextInt();
            long start = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.i32toa(t);
            long end1 = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.i32toaJ(t);
            long end2 = System.nanoTime();
            long u1 = end1 - start;
            long u2 = end2 - end1;
            totalU1 += u1;
            totalU2 += u2;
            System.out.print("Test Number: " + t + " i32toa cost :" + u1 + " i32toaJ cost :" + u2);
            System.out.println(">>> Winner is :" + (u1 < u2 ? "i32toa" : "i32toaJ"));
        }
        System.out.println("Total Time Cost >>>> i32toa : " + totalU1 + " >>>> i32toaJ : " + totalU2);
    }

    @Test
    public void i64toa() {
        int total = 10;
        int per = 1000000;
        long totalU1 = 0;
        long totalU2 = 0;
        long totalU3 = 0;
        for (int i = 0; i < total; i++) {
            int t = ThreadLocalRandom.current().nextInt();
            System.out.print("Test Number: " + t);
            long start = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.i32toa(t);
            long end1 = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.d64toaJ(t);
            long end2 = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.l64toaJ(t);
            long end3 = System.nanoTime();
            long u1 = end1 - start;
            long u2 = end2 - end1;
            long u3 = end3 - end2;
            totalU1 += u1;
            totalU2 += u2;
            totalU3 += u3;
            System.out.println(" i32toa cost :" + u1 + " d64toaJ cost :" + u2 + " l64toaJ cost :" + u3);
//            System.out.println(">>> Winner is :" + (u1 < u2 ? "i32toa" : "d64toaJ"));
        }
        System.out.println("Total Time Cost >>>> i32toa : " + totalU1 + " >>>> d64toaJ : " + totalU2 + " >>>> l64toaJ : " + totalU3);
    }

    @Test
    public void d64toa() {
        int total = 10;
        int per = 1000000;
        long totalU1 = 0;
        long totalU2 = 0;
        for (int i = 0; i < total; i++) {
            double t = ThreadLocalRandom.current().nextDouble();
            System.out.print("Test Number: " + t);
            long start = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.d64toa(t);
            long end1 = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.d64toaJ(t);
            long end2 = System.nanoTime();
            long u1 = end1 - start;
            long u2 = end2 - end1;
            totalU1 += u1;
            totalU2 += u2;
            System.out.print(" d64toa cost :" + u1 + " d64toaJ cost :" + u2);
            System.out.println(">>> Winner is :" + (u1 < u2 ? "d64toa" : "d64toaJ"));
        }
        System.out.println("Total Time Cost >>>> d64toa : " + totalU1 + " >>>> d64toaJ : " + totalU2);
    }

    @Test
    public void d64toa2() {
        int total = 10;
        int per = 1000000;
        long totalU1 = 0;
        long totalU2 = 0;
        for (int i = 0; i < total; i++) {
            double t = ThreadLocalRandom.current().nextLong();
            System.out.print("Test Number: " + t);
            long start = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.d64toa(t);
            long end1 = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.d64toaJ(t);
            long end2 = System.nanoTime();
            long u1 = end1 - start;
            long u2 = end2 - end1;
            totalU1 += u1;
            totalU2 += u2;
            System.out.print(" d64toa cost :" + u1 + " d64toaJ cost :" + u2);
            System.out.println(">>> Winner is :" + (u1 < u2 ? "d64toa" : "d64toaJ"));
        }
        System.out.println("Total Time Cost >>>> d64toa : " + totalU1 + " >>>> d64toaJ : " + totalU2);
    }

    @Test
    public void d64toa3() {
        int total = 10;
        int per = 1000000;
        long totalU1 = 0;
        long totalU2 = 0;
        for (int i = 0; i < total; i++) {
            double t = ThreadLocalRandom.current().nextInt();
            System.out.print("Test Number: " + t);
            long start = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.d64toa(t);
            long end1 = System.nanoTime();
            for (int j = 0; j < per; j++)
                DToA.d64toaJ(t);
            long end2 = System.nanoTime();
            long u1 = end1 - start;
            long u2 = end2 - end1;
            totalU1 += u1;
            totalU2 += u2;
            System.out.print(" d64toa cost :" + u1 + " d64toaJ cost :" + u2);
            System.out.println(">>> Winner is :" + (u1 < u2 ? "d64toa" : "d64toaJ"));
        }
        System.out.println("Total Time Cost >>>> d64toa : " + totalU1 + " >>>> d64toaJ : " + totalU2);
    }
}
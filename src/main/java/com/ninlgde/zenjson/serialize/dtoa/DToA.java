package com.ninlgde.zenjson.serialize.dtoa;

import java.nio.ByteBuffer;

public class DToA {
    private final static char[] DigitsLut = new char[]{
            '0', '0', '0', '1', '0', '2', '0', '3', '0', '4', '0', '5', '0', '6', '0', '7', '0', '8', '0', '9',
            '1', '0', '1', '1', '1', '2', '1', '3', '1', '4', '1', '5', '1', '6', '1', '7', '1', '8', '1', '9',
            '2', '0', '2', '1', '2', '2', '2', '3', '2', '4', '2', '5', '2', '6', '2', '7', '2', '8', '2', '9',
            '3', '0', '3', '1', '3', '2', '3', '3', '3', '4', '3', '5', '3', '6', '3', '7', '3', '8', '3', '9',
            '4', '0', '4', '1', '4', '2', '4', '3', '4', '4', '4', '5', '4', '6', '4', '7', '4', '8', '4', '9',
            '5', '0', '5', '1', '5', '2', '5', '3', '5', '4', '5', '5', '5', '6', '5', '7', '5', '8', '5', '9',
            '6', '0', '6', '1', '6', '2', '6', '3', '6', '4', '6', '5', '6', '6', '6', '7', '6', '8', '6', '9',
            '7', '0', '7', '1', '7', '2', '7', '3', '7', '4', '7', '5', '7', '6', '7', '7', '7', '8', '7', '9',
            '8', '0', '8', '1', '8', '2', '8', '3', '8', '4', '8', '5', '8', '6', '8', '7', '8', '8', '8', '9',
            '9', '0', '9', '1', '9', '2', '9', '3', '9', '4', '9', '5', '9', '6', '9', '7', '9', '8', '9', '9'
    };

    public static char[] getDigitsLut() {
        return DigitsLut;
    }

    public static byte[] i32toa(int value) {
        // Integer.MIN_VALUE -2147483648
        if (value == Integer.MIN_VALUE)
            return new byte[]{'-', '2', '1', '4', '7', '4', '8', '3', '6', '4', '8'};
        // normal
        ByteBuffer buffer = ByteBuffer.allocate(11);
        int u = value;
        if (value < 0) {
            buffer.put((byte) '-');
            u = ~u + 1;
        }
        if (u < 10000) {
            int d1 = (u / 100) << 1;
            int d2 = (u % 100) << 1;
            if (u >= 1000)
                buffer.put((byte) DigitsLut[d1]);
            if (u >= 100)
                buffer.put((byte) DigitsLut[d1 + 1]);
            if (u >= 10)
                buffer.put((byte) DigitsLut[d2]);
            buffer.put((byte) DigitsLut[d2 + 1]);
        } else if (u < 100000000) {
            int b = u / 10000;
            int c = u % 10000;
            int d1 = (b / 100) << 1;
            int d2 = (b % 100) << 1;
            int d3 = (c / 100) << 1;
            int d4 = (c % 100) << 1;
            if (u >= 10000000)
                buffer.put((byte) DigitsLut[d1]);
            if (u >= 1000000)
                buffer.put((byte) DigitsLut[d1 + 1]);
            if (u >= 100000)
                buffer.put((byte) DigitsLut[d2]);
            buffer.put((byte) DigitsLut[d2 + 1]);
            buffer.put((byte) DigitsLut[d3]);
            buffer.put((byte) DigitsLut[d3 + 1]);
            buffer.put((byte) DigitsLut[d4]);
            buffer.put((byte) DigitsLut[d4 + 1]);
        } else {
            int a = u / 100000000; // 1 to 42
            u %= 100000000;
            if (a >= 10) {
                int i = a << 1;
                buffer.put((byte) DigitsLut[i]);
                buffer.put((byte) DigitsLut[i + 1]);
            } else
                buffer.put((byte) ('0' + a));
            int b = u / 10000; // 0 to 9999
            int c = u % 10000; // 0 to 9999
            int d1 = (b / 100) << 1;
            int d2 = (b % 100) << 1;
            int d3 = (c / 100) << 1;
            int d4 = (c % 100) << 1;
            buffer.put((byte) DigitsLut[d1]);
            buffer.put((byte) DigitsLut[d1 + 1]);
            buffer.put((byte) DigitsLut[d2]);
            buffer.put((byte) DigitsLut[d2 + 1]);
            buffer.put((byte) DigitsLut[d3]);
            buffer.put((byte) DigitsLut[d3 + 1]);
            buffer.put((byte) DigitsLut[d4]);
            buffer.put((byte) DigitsLut[d4 + 1]);
        }

        byte[] bytes = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytes);
        return bytes;
    }

    public static byte[] d64toa(double value) {
        assert !Double.isInfinite(value);
        assert !Double.isNaN(value);

        if (value == 0) {
            return new byte[]{0};
        }

        ByteBuffer buffer = ByteBuffer.allocate(38);
        if (value < 0) {
            buffer.put((byte) '-');
            value = -value;
        }
        int K = Grisu2.grisu2(value, buffer);
        Grisu2.prettify(buffer, K);

        byte[] bytes = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytes);
        return bytes;
    }

    public static byte[] i32toaJ(int value) {
        return Integer.toString(value).getBytes();
    }

    public static byte[] l64toaJ(long value) {
        return Long.toString(value).getBytes();
    }

    /**
     * benchmark: d64toaJ faster than d64toa
     * @param value
     * @return
     */
    public static byte[] d64toaJ(double value) {
        assert !Double.isInfinite(value);
        assert !Double.isNaN(value);

        if (value == 0) {
            return new byte[]{0};
        }

        return Double.toString(value).getBytes();
    }

//    public static byte[] d64toaByte()
}

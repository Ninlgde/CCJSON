package com.ninlgde.ccjson.serialize.dtoa;

import com.ninlgde.ccjson.utils.TwoTuple;

import java.nio.ByteBuffer;

@Deprecated
public class Grisu2 {

    public static int grisu2(double value, ByteBuffer buffer) {
        DiyFp v = new DiyFp(value);
        DiyFp w_m = new DiyFp();
        DiyFp w_p = new DiyFp();
        v.normalizedBoundaries(w_m, w_p);

        TwoTuple<DiyFp, Integer> c = DiyFp.getCachedPower(w_p.e);
        DiyFp c_mk = c.first;
        int K = c.second;
        DiyFp W = v.normalize().product(c_mk);

        DiyFp Wp = w_p.product(c_mk);
        DiyFp Wm = w_m.product(c_mk);
        Wm.f++;
        Wp.f--;

        return digitGen(W, Wp, Wp.f - Wm.f, buffer, K);
    }

    public static int digitGen(DiyFp W, DiyFp Mp, long delta, ByteBuffer buffer, int K) {
        long kPow10[] = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 1000000000, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L};

        DiyFp one = new DiyFp(1L << -Mp.e, Mp.e);
        DiyFp wp_w = Mp.sub(W);
        int p1 = (int) (Mp.f >>> -one.e);
        long p2 = (Mp.f & (one.f - 1));
        int kappa = countDecimalDigit32(p1);


        while (kappa > 0) {
            int d = 0;
            switch (kappa) {
                case 10:
                    d = p1 / 1000000000;
                    p1 %= 1000000000;
                    break;
                case 9:
                    d = p1 / 100000000;
                    p1 %= 100000000;
                    break;
                case 8:
                    d = p1 / 10000000;
                    p1 %= 10000000;
                    break;
                case 7:
                    d = p1 / 1000000;
                    p1 %= 1000000;
                    break;
                case 6:
                    d = p1 / 100000;
                    p1 %= 100000;
                    break;
                case 5:
                    d = p1 / 10000;
                    p1 %= 10000;
                    break;
                case 4:
                    d = p1 / 1000;
                    p1 %= 1000;
                    break;
                case 3:
                    d = p1 / 100;
                    p1 %= 100;
                    break;
                case 2:
                    d = p1 / 10;
                    p1 %= 10;
                    break;
                case 1:
                    d = p1;
                    p1 = 0;
                    break;
            }
            if (d != 0 || buffer.position() != 0) {
                byte b = (byte) ('0' + d);
                buffer.put(b);
            }
            kappa--;
            long tmp = ((long) (p1) << -one.e) + p2;
            if (tmp <= delta) {
                K += kappa;
                grisuRound(buffer, delta, tmp, (long) (kPow10[kappa]) << -one.e, wp_w.f);
                return K;
            }
        }

        // kappa = 0
        for (; ; ) {
            p2 *= 10;
            delta *= 10;
            char d = (char) (p2 >>> -one.e);
            if (d != 0 || buffer.position() != 0) {
                byte b = (byte) ('0' + d);
                buffer.put(b);
            }
            p2 &= one.f - 1;
            kappa--;
            if (p2 < delta) {
                K += kappa;
                grisuRound(buffer, delta, p2, one.f, wp_w.f * kPow10[-kappa]);
                return K;
            }
        }
    }

    private static void grisuRound(ByteBuffer buffer, long delta, long rest, long ten_kappa, long wp_w) {
        while (rest < wp_w && delta - rest >= ten_kappa &&
                (rest + ten_kappa < wp_w ||  /// closer
                        wp_w - rest > rest + ten_kappa - wp_w)) {
            buffer.mark();
            int pos = buffer.position() - 1;
            byte b = buffer.get(pos);
            buffer.put(pos, --b);
            buffer.reset();
            rest += ten_kappa;
        }
    }

    private static int countDecimalDigit32(int n) {
        if (n < 10) return 1;
        if (n < 100) return 2;
        if (n < 1000) return 3;
        if (n < 10000) return 4;
        if (n < 100000) return 5;
        if (n < 1000000) return 6;
        if (n < 10000000) return 7;
        if (n < 100000000) return 8;
        if (n < 1000000000) return 9;
        return 10;
    }

    public static void prettify(ByteBuffer buffer, int K) {
        int length = buffer.position();
        int kk = buffer.position() + K;
        if (length <= kk && kk <= 21) {
            // 1234e7 -> 12340000000
            while (kk-- > 0)
                buffer.put((byte) '0');
        } else if (0 < kk && kk <= 21) {
            // 1234e-2 -> 12.34
            bufferMove(buffer, kk + 1, kk, length - kk);
            buffer.put(kk, (byte) '.');
        } else if (-6 < kk && kk <= 0) {
            // 1234e-6 -> 0.001234
            int offset = 2 - kk;
            bufferMove(buffer, offset, 0, length);
            buffer.put(0, (byte) '0');
            buffer.put(1, (byte) '.');
            for (int i = 2; i < offset; i++)
                buffer.put(i, (byte) '0');
        } else if (length == 1) {
            // 1e30
            buffer.put((byte) 'e');
            writeExponent(buffer, kk - 1);
        } else {
            bufferMove(buffer, 2, 1, length - 1);
            buffer.put(1, (byte) '.');
            buffer.put((byte) 'e');
            writeExponent(buffer, kk-1);
        }
    }

    public static void bufferMove(ByteBuffer buffer, int dest, int src, int length) {
        // todo: optimize
        if (src > dest) {
            buffer.position(dest);
            while (length-- > 0)
                buffer.put(buffer.get(src++));
        } else if (src < dest) {
            buffer.position(dest + length);
            while (length-- > 0)
                buffer.put(dest + length, buffer.get(src + (length)));
        } else
            // no move
            buffer.position(dest + length);
    }

    public static void writeExponent(ByteBuffer buffer, int K) {
        if (K < 0) {
            buffer.put((byte) '-');
            K = -K;
        }

        if (K >= 100) {
            buffer.put((byte) ('0' + (char) (K / 100)));
            K %= 100;
            buffer.put((byte) DToA.getDigitsLut()[K * 2]);
            buffer.put((byte) DToA.getDigitsLut()[K * 2 + 1]);
        } else if (K >= 10) {
            buffer.put((byte) DToA.getDigitsLut()[K * 2]);
            buffer.put((byte) DToA.getDigitsLut()[K * 2 + 1]);
        } else
            buffer.put((byte) ('0' + (char) (K)));
    }
}

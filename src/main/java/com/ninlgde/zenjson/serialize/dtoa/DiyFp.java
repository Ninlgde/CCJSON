package com.ninlgde.zenjson.serialize.dtoa;

import com.ninlgde.zenjson.utils.TwoTuple;

import java.nio.ByteBuffer;

public class DiyFp {

    static final int kDiySignificandSize = 64;
    static final int kDpSignificandSize = 52;
    static final int kDpExponentBias = 0x3FF + kDpSignificandSize;
    static final int kDpMinExponent = -kDpExponentBias;
    static final long kDpExponentMask = 0x7FF0000000000000L;
    static final long kDpSignificandMask = 0x000FFFFFFFFFFFFFL;
    static final long kDpHiddenBit = 0x0010000000000000L;

    protected long f;
    protected int e;

    public DiyFp() {
    }

    public DiyFp(long f, int e) {
        this.f = f;
        this.e = e;
    }

    public DiyFp(double d) {
//        long u1 = Double.doubleToLongBits(d); //test which faster
        long u = castDouble(d);
        int biased_e = (int) ((u & kDpExponentMask) >>> kDpSignificandSize);
        long significand = (u & kDpSignificandMask);
        if (biased_e != 0) {
            f = significand + kDpHiddenBit;
            e = biased_e - kDpExponentBias;
        } else {
            f = significand;
            e = kDpMinExponent + 1;
        }
    }

    public DiyFp sub(DiyFp rhs) {
        assert e == rhs.e;
        assert f >= rhs.f;
        return new DiyFp(f - rhs.f, e);
    }

    public DiyFp product(DiyFp rhs) {
        long M32 = 0xFFFFFFFFL;
        long a = f >>> 32;
        long b = f & M32;
        long c = rhs.f >>> 32;
        long d = rhs.f & M32;
        long ac = a * c;
        long bc = b * c;
        long ad = a * d;
        long bd = b * d;
        long tmp = (bd >>> 32) + (ad & M32) + (bc & M32);
        tmp += 1L << 31;
        return new DiyFp(ac + (ad >>> 32) + (bc >>> 32) + (tmp >>> 32), e + rhs.e + 64);
    }

    public DiyFp normalize() {
        while ((this.f & kDpHiddenBit) == 0) {
            this.f <<= 1;
            this.e--;
        }
        this.f <<= (kDiySignificandSize - kDpSignificandSize - 1);
        this.e = this.e - (kDiySignificandSize - kDpSignificandSize - 1);
        return this;
    }

    public DiyFp normalizeBoundary() {
        while ((this.f & (kDpHiddenBit << 1)) == 0) {
            this.f <<= 1;
            this.e--;
        }
        this.f <<= (kDiySignificandSize - kDpSignificandSize - 2);
        this.e = this.e - (kDiySignificandSize - kDpSignificandSize - 2);
        return this;
    }

    public void normalizedBoundaries(DiyFp minus, DiyFp plus) {
        DiyFp pl = new DiyFp((f << 1) + 1, e - 1).normalizeBoundary();
        DiyFp mi = (f == kDpHiddenBit) ? new DiyFp((f << 2) - 1, e - 2) : new DiyFp((f << 1) - 1, e - 1);
        mi.f <<= mi.e - pl.e;
        mi.e = pl.e;
        plus.clone(pl);
        minus.clone(mi);
    }

    public static TwoTuple<DiyFp, Integer> getCachedPower(int e) {
        // 10^-348, 10^-340, ..., 10^340
        long kCachedPowers_F[] = {
                0xfa8fd5a0081c0288L, 0xbaaee17fa23ebf76L,
                0x8b16fb203055ac76L, 0xcf42894a5dce35eaL,
                0x9a6bb0aa55653b2dL, 0xe61acf033d1a45dfL,
                0xab70fe17c79ac6caL, 0xff77b1fcbebcdc4fL,
                0xbe5691ef416bd60cL, 0x8dd01fad907ffc3cL,
                0xd3515c2831559a83L, 0x9d71ac8fada6c9b5L,
                0xea9c227723ee8bcbL, 0xaecc49914078536dL,
                0x823c12795db6ce57L, 0xc21094364dfb5637L,
                0x9096ea6f3848984fL, 0xd77485cb25823ac7L,
                0xa086cfcd97bf97f4L, 0xef340a98172aace5L,
                0xb23867fb2a35b28eL, 0x84c8d4dfd2c63f3bL,
                0xc5dd44271ad3cdbaL, 0x936b9fcebb25c996L,
                0xdbac6c247d62a584L, 0xa3ab66580d5fdaf6L,
                0xf3e2f893dec3f126L, 0xb5b5ada8aaff80b8L,
                0x87625f056c7c4a8bL, 0xc9bcff6034c13053L,
                0x964e858c91ba2655L, 0xdff9772470297ebdL,
                0xa6dfbd9fb8e5b88fL, 0xf8a95fcf88747d94L,
                0xb94470938fa89bcfL, 0x8a08f0f8bf0f156bL,
                0xcdb02555653131b6L, 0x993fe2c6d07b7facL,
                0xe45c10c42a2b3b06L, 0xaa242499697392d3L,
                0xfd87b5f28300ca0eL, 0xbce5086492111aebL,
                0x8cbccc096f5088ccL, 0xd1b71758e219652cL,
                0x9c40000000000000L, 0xe8d4a51000000000L,
                0xad78ebc5ac620000L, 0x813f3978f8940984L,
                0xc097ce7bc90715b3L, 0x8f7e32ce7bea5c70L,
                0xd5d238a4abe98068L, 0x9f4f2726179a2245L,
                0xed63a231d4c4fb27L, 0xb0de65388cc8ada8L,
                0x83c7088e1aab65dbL, 0xc45d1df942711d9aL,
                0x924d692ca61be758L, 0xda01ee641a708deaL,
                0xa26da3999aef774aL, 0xf209787bb47d6b85L,
                0xb454e4a179dd1877L, 0x865b86925b9bc5c2L,
                0xc83553c5c8965d3dL, 0x952ab45cfa97a0b3L,
                0xde469fbd99a05fe3L, 0xa59bc234db398c25L,
                0xf6c69a72a3989f5cL, 0xb7dcbf5354e9beceL,
                0x88fcf317f22241e2L, 0xcc20ce9bd35c78a5L,
                0x98165af37b2153dfL, 0xe2a0b5dc971f303aL,
                0xa8d9d1535ce3b396L, 0xfb9b7cd9a4a7443cL,
                0xbb764c4ca7a44410L, 0x8bab8eefb6409c1aL,
                0xd01fef10a657842cL, 0x9b10a4e5e9913129L,
                0xe7109bfba19c0c9dL, 0xac2820d9623bf429L,
                0x80444b5e7aa7cf85L, 0xbf21e44003acdd2dL,
                0x8e679c2f5e44ff8fL, 0xd433179d9c8cb841L,
                0x9e19db92b4e31ba9L, 0xeb96bf6ebadf77d9L,
                0xaf87023b9bf0ee6bL
        };
        int kCachedPowers_E[] = {
                -1220, -1193, -1166, -1140, -1113, -1087, -1060, -1034, -1007, -980,
                -954, -927, -901, -874, -847, -821, -794, -768, -741, -715,
                -688, -661, -635, -608, -582, -555, -529, -502, -475, -449,
                -422, -396, -369, -343, -316, -289, -263, -236, -210, -183,
                -157, -130, -103, -77, -50, -24, 3, 30, 56, 83,
                109, 136, 162, 189, 216, 242, 269, 295, 322, 348,
                375, 402, 428, 455, 481, 508, 534, 561, 588, 614,
                641, 667, 694, 720, 747, 774, 800, 827, 853, 880,
                907, 933, 960, 986, 1013, 1039, 1066
        };

        double dk = (-61 - e) * 0.30102999566398114 + 347;
        int k = (int) dk;
        if (k != dk)
            k++;

        int index = ((k >>> 3) + 1);
        int K = -(-348 + (index << 3));

        DiyFp diyFp = new DiyFp(kCachedPowers_F[index], kCachedPowers_E[index]);
        return new TwoTuple<>(diyFp, K);
    }

    public void clone(DiyFp o) {
        this.f = o.f;
        this.e = o.e;
    }

    /**
     * cast double to lang
     *
     * @param d
     * @return
     */
    private static long castDouble(double d) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.mark();
        buffer.putDouble(d);
        buffer.reset();
        return buffer.getLong();
    }
}

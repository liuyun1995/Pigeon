package com.dianping.pigeon.compress;

//压缩器工厂
public class CompressFactory {

    private static Compress gzipCompress = new GZipCompress();

    private static Compress snappyCompress = new SnappyCompress();

    private CompressFactory() {}

    public static Compress getGZipCompress() {
        return gzipCompress;
    }

    public static Compress getSnappyCompress() {
        return snappyCompress;
    }
}

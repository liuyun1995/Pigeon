package com.dianping.pigeon.compress;

import org.xerial.snappy.Snappy;
import java.io.IOException;

//Snappy压缩器
public class SnappyCompress implements Compress {

    //压缩方法
    @Override
    public byte[] compress(byte[] buf) throws IOException {
        if (buf == null) {
            return null;
        }
        return Snappy.compress(buf);
    }

    //解压方法
    @Override
    public byte[] unCompress(byte[] buf) throws IOException {
        if (buf == null) {
            return null;
        }
        return Snappy.uncompress(buf);
    }

}

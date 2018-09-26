package com.dianping.pigeon.compress;

import java.io.IOException;

//压缩器
public interface Compress {

    //压缩
    byte[] compress(byte[] buf) throws IOException;

    //解压缩
    byte[] unCompress(byte[] buf) throws IOException;

}

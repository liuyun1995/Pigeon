package com.dianping.pigeon.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

//GZip压缩器
public class GZipCompress implements Compress {

    private static final int BUFFER_SIZE = 256;

    //压缩方法
    public byte[] compress(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(array);
            gzip.finish();
            gzip.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            if (gzip != null) {
                gzip.close();
            }
        }
        return out.toByteArray();
    }

    //解压方法
    public byte[] unCompress(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(array);
        GZIPInputStream zip = null;
        try {
            zip = new GZIPInputStream(in);
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = zip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (zip != null) {
                zip.close();
            }
        }
        return out.toByteArray();
    }

}

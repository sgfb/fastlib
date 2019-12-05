package com.fastlib;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sgfb on 2019\12\04.
 * 对Http协议输入流封装
 */
public class HttpInputStream extends InputStream{
    private InputStream mSocketInput;

    public HttpInputStream(InputStream socketInput) {
        mSocketInput = socketInput;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return super.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return super.skip(n);
    }

    @Override
    public int available() throws IOException {
        return super.available();
    }

    @Override
    public void close() throws IOException {

    }
}

package edu.jhu.pkss.compression;

import java.io.IOException;

public interface Compressor
{
    public void compress(byte[] data) throws IOException;
    public void finish() throws IOException;
    public Compressor dup();
}

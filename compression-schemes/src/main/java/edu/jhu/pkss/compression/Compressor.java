package edu.jhu.pkss.compression;

import java.io.IOException;

public interface Compressor
{
    byte[] compress(byte[] data) throws IOException;
    byte[] finish() throws IOException;
}

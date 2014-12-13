package edu.jhu.pkss.compression;

import java.io.IOException;

public interface Decompressor
{
    byte[] decompress(byte[] data) throws IOException;
    byte[] finish() throws IOException;
}

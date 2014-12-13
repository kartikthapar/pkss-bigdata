package edu.jhu.pkss.compression;

import java.io.IOException;

public interface CompressionScheme {
    Compressor newCompressor(java.io.OutputStream outupt);
    byte[] decompress(byte[] data) throws IOException;
}

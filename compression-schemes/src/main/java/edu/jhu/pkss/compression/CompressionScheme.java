package edu.jhu.pkss.compression;

import java.io.IOException;

public interface CompressionScheme {
    Compressor newCompressor(BitBuffer outupt);
    byte[] decompress(byte[] data) throws IOException;
}

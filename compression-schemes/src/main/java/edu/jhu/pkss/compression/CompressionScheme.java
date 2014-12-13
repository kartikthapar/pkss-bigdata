package edu.jhu.pkss.compression;

import java.io.IOException;

public interface CompressionScheme {
    Compressor newCompressor();
    byte[] decompress(byte[] data) throws IOException;
}

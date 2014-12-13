package edu.jhu.pkss.compression;

import java.io.IOException;

public interface CompressionScheme {
    Compressor newCompressor();
    Decompressor newDecompressor();
}

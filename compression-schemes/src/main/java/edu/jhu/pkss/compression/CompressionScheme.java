package edu.jhu.pkss.compression;

import java.io.IOException;

public interface CompressionScheme {
	byte[] compress(byte[] data);
	byte[] decompress(byte[] data) throws IOException;
}

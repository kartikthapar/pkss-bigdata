package edu.jhu.pkss.compression;

public interface CompressionScheme {
	byte[] compress(byte[] data);
	byte[] decompress(byte[] data);	
}

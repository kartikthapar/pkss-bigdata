package edu.jhu.pkss.compression;

import net.jpountz.lz4.*;

public class OurLz4Impl implements CompressionScheme{
	private int decompressedLength;
	private int compressedLength;
	private LZ4Factory factory;

	public OurLz4Impl() {
		factory = LZ4Factory.fastestInstance();
	}

	public byte[] compress(byte[] data) {
		decompressedLength = data.length;
		LZ4Compressor compressor = factory.fastCompressor();
		int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
		byte[] compressed = new byte[maxCompressedLength];
		compressedLength = compressor.compress(data, 0, decompressedLength, compressed, 0, maxCompressedLength);
	
		return compressed;
	}

	public byte[] decompress(byte[] compressed_data) {
		LZ4FastDecompressor decompressor = factory.fastDecompressor();
		byte[] restored = new byte[decompressedLength];
		int compressedLength2 = decompressor.decompress(compressed_data, 0, restored, 0, decompressedLength);
		return restored;
	}
}

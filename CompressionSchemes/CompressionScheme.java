public interface CompressionScheme {
	byte[] compress(byte[] data);
	byte[] decompress(byte[] data);	
}

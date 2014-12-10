public class AdaptiveArithmeticImpl implements CompressionScheme{
	
	public byte[] compress(byte[] b) throws IOException {
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BitOutputStream bitOut = new BitOutputStream(out);
		AdaptiveArithmeticCompress.compress(in, bitOut);
		bitOut.close();
		return out.toByteArray();
	}
	
	
	public byte[] decompress(byte[] b) throws IOException {
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AdaptiveArithmeticDecompress.decompress(new BitInputStream(in), out);
		return out.toByteArray();
	}
	
}

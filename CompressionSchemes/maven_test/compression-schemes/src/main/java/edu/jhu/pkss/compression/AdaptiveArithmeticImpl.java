package nayuki.arithcode;

import edu.jhu.pkss.compression.CompressionScheme;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class AdaptiveArithmeticImpl implements CompressionScheme{

		
	public byte[] compress(byte[] b) {
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			BitOutputStream bitOut = new BitOutputStream(out);
			AdaptiveArithmeticCompress.compress(in, bitOut);
			bitOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}
	
	
	public byte[] decompress(byte[] b) {
		InputStream in = new ByteArrayInputStream(b);
		ByteArrayOutputStream out = null;
 
		try {
			out = new ByteArrayOutputStream();
			AdaptiveArithmeticDecompress.decompress(new BitInputStream(in), out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}
	
}

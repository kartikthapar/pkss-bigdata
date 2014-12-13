package nayuki.arithcode;

import java.io.IOException;
import java.io.OutputStream;


/**
 * A stream where bits can be written to. Because they are written to an underlying byte stream, the end of the stream is padded with 0's up to a multiple of 8 bits. The bits are written in big endian.
 */
public interface BitOutput {
	
	// Writes a bit to the stream. The specified bit must be 0 or 1.
	public void write(int b) throws IOException;
	
	
	// Closes this stream and the underlying OutputStream. If called when this bit stream is not at a byte boundary,
	// then the minimum number of "0" bits (between 0 and 7 of them) are written as padding to reach the next byte boundary.
	public void close() throws IOException;	
}

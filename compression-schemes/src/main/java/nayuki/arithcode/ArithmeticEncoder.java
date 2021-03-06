package nayuki.arithcode;

import java.io.IOException;


public final class ArithmeticEncoder extends ArithmeticCoderBase {
	
	private BitOutput output;
	
	// Number of saved underflow bits. This value can grow without bound, so a truly correct implementation would use a BigInteger.
	private int underflow;
	
	
	
	// Creates an arithmetic coding encoder.
	public ArithmeticEncoder(BitOutput out) {
		super();
		if (out == null)
			throw new NullPointerException();
		output = out;
		underflow = 0;
	}
	
	
	
	// Encodes a symbol.
	public void write(FrequencyTable freq, int symbol) throws IOException {
		write(new CheckedFrequencyTable(freq), symbol);
	}
	
	
	// Encodes a symbol.
	public void write(CheckedFrequencyTable freq, int symbol) throws IOException {
		update(freq, symbol);
	}
	
	
	// Must be called at the end of the stream of input symbols, otherwise the output data cannot be decoded properly.
	public void finish() throws IOException {
		output.writeBit(1);
	}
	
	
	
	protected void shift() throws IOException {
		int bit = (int)(low >>> (STATE_SIZE - 1));
		output.writeBit(bit);
		
		// Write out saved underflow bits
		for (; underflow > 0; underflow--)
			output.writeBit(bit ^ 1);
	}
	
	
	protected void underflow() throws IOException {
		if (underflow == Integer.MAX_VALUE)
			throw new RuntimeException("Maximum underflow reached");
		underflow++;
	}
	
    public ArithmeticEncoder dup()
    {
        ArithmeticEncoder result = new ArithmeticEncoder(output);
        result.underflow = this.underflow;
        return result;
    }

}

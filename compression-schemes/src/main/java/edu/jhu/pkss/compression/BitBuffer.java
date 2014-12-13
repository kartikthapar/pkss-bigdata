package edu.jhu.pkss.compression;

import java.io.IOException;
import java.nio.ByteBuffer;
import nayuki.arithcode.BitOutput;

// Based on nayuki.arithcode.BitOutputStream
public class BitBuffer implements BitOutput
{
    private ByteBuffer buffer;

    private int currentByte;
    private int numBitsInCurrentByte;

    private class Marker
    {
        public Marker(int BP, int bP, int value)
        {
            bytePosition = BP;
            bitPosition = bP;
            bitValue = value;
        }
        public int bytePosition;
        public int bitPosition;
        public int bitValue;
    }

    public BitBuffer(ByteBuffer b)
    {
        buffer = b;
    }

    @Override
    public void writeBit(int b) throws IOException
    {
		if (!(b == 0 || b == 1))
			throw new IllegalArgumentException("Argument must be 0 or 1");
		currentByte = currentByte << 1 | b;
		numBitsInCurrentByte++;
		if (numBitsInCurrentByte == 8) {
			buffer.put((byte)currentByte);
			numBitsInCurrentByte = 0;
		}
    }

    @Override
    public void close() throws IOException
    {
		while (numBitsInCurrentByte != 0)
			writeBit(0);
    }

    Marker mark()
    {
        return new Marker(buffer.position(), numBitsInCurrentByte, currentByte);
    }

    void rewind(Marker m)
    {
        buffer.position(m.bytePosition);
        numBitsInCurrentByte = m.bitPosition;
        currentByte = m.bitValue;
    }
}

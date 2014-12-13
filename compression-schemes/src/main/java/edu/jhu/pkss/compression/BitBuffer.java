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

    public class Marker
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
        // Throw the exception on the first bit that won't fit into the
        // byteBuffer We could wait for the ByteBuffer to throw it, but that
        // would be 8 bits after the backing store filled up, so that's
        // probably the wrong choice
        if (buffer.position() == buffer.limit())
            throw new java.nio.BufferOverflowException();
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

    public Marker mark()
    {
        return new Marker(buffer.position(), numBitsInCurrentByte, currentByte);
    }

    public void rewind(Marker m)
    {
        buffer.position(m.bytePosition);
        numBitsInCurrentByte = m.bitPosition;
        currentByte = m.bitValue;
    }

    public void rewind()
    {
        buffer.rewind();
        numBitsInCurrentByte = 0;
        currentByte = 0;
    }

    public ByteBuffer getBuffer()
    {
        return buffer;
    }


    public boolean empty()
    {
        return numBitsInCurrentByte == 0 && buffer.position() == 0;
    }
}

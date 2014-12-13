package edu.jhu.pkss.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import nayuki.arithcode.ArithmeticEncoder;
import nayuki.arithcode.AdaptiveArithmeticDecompress;
import nayuki.arithcode.BitInputStream;
import nayuki.arithcode.BitOutputStream;
import nayuki.arithcode.FrequencyTable;
import nayuki.arithcode.FlatFrequencyTable;
import nayuki.arithcode.SimpleFrequencyTable;

import edu.jhu.pkss.compression.CompressionScheme;

public class AdaptiveArithmeticImpl implements CompressionScheme
{
    public class AdaptiveArithmeticCompressor implements Compressor
    {
        private FrequencyTable freq;
        private BitBuffer output;
        private ArithmeticEncoder enc;

        public AdaptiveArithmeticCompressor(BitBuffer output)
        {
            // Initialize with all symbol frequencies at 1
            freq = new SimpleFrequencyTable(new FlatFrequencyTable(257));
            this.output = output;
            enc = new ArithmeticEncoder(output);
        }

        @Override
        public void compress(byte[] data) throws IOException
        {
            InputStream in = new ByteArrayInputStream(data);
            while (true) {
                int b = in.read();
                if (b == -1)
                    break;
                enc.write(freq, b);
                freq.increment(b);
            }
        }

        @Override
        public void finish() throws IOException
        {
            enc.write(freq, 256);  // EOF
            enc.finish();
        }

        @Override
        public Compressor dup()
        {
            AdaptiveArithmeticCompressor result = new AdaptiveArithmeticCompressor(output);
            result.freq = this.freq.dup();
            result.enc = this.enc.dup();
            return result;
        }
    }

    @Override
    public Compressor newCompressor(BitBuffer output)
    {
        return new AdaptiveArithmeticCompressor(output);
    }

    //public byte[] compress(byte[] b) {
    //    InputStream in = new ByteArrayInputStream(b);
    //    ByteArrayOutputStream out = new ByteArrayOutputStream();

    //    BitOutputStream bitOut = new BitOutputStream(out);
    //    AdaptiveArithmeticCompress.compress(in, bitOut);
    //    bitOut.close();

    //    return out.toByteArray();
    //}

    @Override
    public byte[] decompress(byte[] b)
        throws IOException
    {
        InputStream in = new ByteArrayInputStream(b);
        ByteArrayOutputStream out = null;

        out = new ByteArrayOutputStream();
        AdaptiveArithmeticDecompress.decompress(new BitInputStream(in), out);

        return out.toByteArray();
    }
}

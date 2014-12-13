package edu.jhu.pkss.compression;

import java.io.UnsupportedEncodingException;
import edu.jhu.pkss.compression.AdaptiveArithmeticImpl;
import java.nio.ByteBuffer;
import java.io.IOException;

public class Main
{
    public static void main( String[] args ) throws IOException
    {
        OurLz4Impl lz = new OurLz4Impl();
        AdaptiveArithmeticImpl a = new AdaptiveArithmeticImpl();

        byte[] compressed = lz.compress(intToByteArray(1234));
        byte[] decompressed = lz.decompress(compressed);
        System.out.println("Lz4 compression (should be 1234): " + Integer.toString(fromByteArray(decompressed)));

        // FIXME with the new compression scheme interface
        // byte[] a_compressed = a.compress(double2Byte(new double[]{1234.5678}));
        // byte[] a_decompressed = a.decompress(a_compressed);
        //System.out.println("Adaptative Arithmetic compression (should be 1234.5678): " + Double.toString(toDouble(a_decompressed)));
        System.out.println("Did not run the Adaptive Arithmetic compression test.");
   }

public static final byte[] double2Byte(double[] inData) {
    int j=0;
    int length=inData.length;
    byte[] outData=new byte[length*8];
    for (int i=0;i<length;i++) {
      long data=Double.doubleToLongBits(inData[i]);
      outData[j++]=(byte)(data>>>56);
      outData[j++]=(byte)(data>>>48);
      outData[j++]=(byte)(data>>>40);
      outData[j++]=(byte)(data>>>32);
      outData[j++]=(byte)(data>>>24);
      outData[j++]=(byte)(data>>>16);
      outData[j++]=(byte)(data>>>8);
      outData[j++]=(byte)(data>>>0);
    }
    return outData;
  }

public static double toDouble(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getDouble();
}

public static byte[] intToByteArray(int value) {
    return new byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value};
}

public static int fromByteArray(byte[] bytes) {
     return ByteBuffer.wrap(bytes).getInt();
}

}

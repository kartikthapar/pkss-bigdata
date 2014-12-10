package edu.jhu.pkss.compression;
import java.io.UnsupportedEncodingException;
import nayuki.arithcode.AdaptiveArithmeticImpl;
import java.nio.ByteBuffer;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args )
    {
    	OurLz4Impl lz = new OurLz4Impl();
	AdaptiveArithmeticImpl a = new AdaptiveArithmeticImpl();
	
	try {
		byte[] compressed = lz.compress("1234".getBytes("UTF-8"));
		byte[] decompressed = lz.decompress(compressed);
		System.out.println("Lz4 compression (should be 1234): " + new String(decompressed));
	        //byte [] bytes = ByteBuffer.allocate(8).putDouble(5678).array();
		byte[] bytes = double2Byte(new double[]{5678});
		byte[] a_compressed = a.compress(bytes);
		byte[] a_decompressed = a.decompress(a_compressed);
		System.out.println("Adaptative Arithmetic compression (should be 5678): " + new String(a_decompressed));
   

	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	} 
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

}

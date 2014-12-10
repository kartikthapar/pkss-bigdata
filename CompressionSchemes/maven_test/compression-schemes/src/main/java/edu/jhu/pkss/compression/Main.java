package edu.jhu.pkss.compression;
import net.jpountz.lz4.OurLz4Impl;
import java.io.UnsupportedEncodingException;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args )
    {
    	OurLz4Impl lz = new OurLz4Impl();
	
	try {
		byte[] compressed = lz.compress("Steve".getBytes("UTF-8"));
		byte[] decompressed = lz.decompress(compressed);
		System.out.println(new String(decompressed));
   	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	} 
   }
}

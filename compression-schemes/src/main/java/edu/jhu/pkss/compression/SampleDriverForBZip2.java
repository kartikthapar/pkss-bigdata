package org.apache.tools.bzip2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class SampleDriverForBZip2 {
	public static void main(String[] args) throws IOException {
		
		    FileReader inFile = new FileReader("input/your_original_input");
		    BufferedReader buff = new BufferedReader(inFile);

		    FileOutputStream fout = null;
			try {
				fout = new FileOutputStream("input/your_new_input");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		    fout.write("BZ".getBytes());


		    CBZip2OutputStream czout = new CBZip2OutputStream (fout);

		    String message = buff.readLine(); // read first line

		    while(message!=null){
		        byte[] input = message.getBytes();
		        czout.write(input);
		        message = buff.readLine();
		    }

		    czout.flush();
		    czout.close();
		    fout.flush();
		    fout.close();
		    
		    InputStream fileInputStream = new FileInputStream("input/your_new_input");
		    CBZip2InputStream cin = new CBZip2InputStream(fileInputStream);
		    FileOutputStream decOut = new FileOutputStream("input/decompressed_your_new_input");


		    byte[] buf = new byte[100000];
		    int len;

		    while((len = cin.read(buf))>0){
		        decOut.write(buf, 0, len);
		    }
		    decOut.close();
		    cin.close();
	}
}


package vnoptimization.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteToFile {
	//public static void main(String[] args) throws IOException {

	//  Added by Sima
//	static File file;
//	static BufferedWriter output = null;
//	
//	public static void setHeader(String str){
//		file = new File("data\\Memory.txt");	
//	}
	
	
	public static void write(String value) {
	File file; 
	BufferedWriter output = null;

	try{
		file = new File("data\\Memory.txt");
		output = new BufferedWriter(new FileWriter(file, true));
		output.write(value);
		output.write("\r\n");
		output.close();
	}catch(Exception e){
		e.printStackTrace();
	}

}

}

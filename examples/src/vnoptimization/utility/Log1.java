/**
 * 
 */
package vnoptimization.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Sima
 *
 */
public class Log1 {

	public static void write(String value) {
		File file; 
		BufferedWriter output = null;

		try{
			file = new File("data\\Log.txt");
			output = new BufferedWriter(new FileWriter(file, true));
			output.write(value);
			output.write("\r\n");
			output.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}

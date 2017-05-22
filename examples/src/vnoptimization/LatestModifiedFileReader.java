package vnoptimization;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

public class LatestModifiedFileReader {

	
	public static String getLastFolderName(String path) {
		/* Get the newest folder  */
	    File dir = new File(path);
	    File[] files = dir.listFiles();
	    if (files.length > 0) {
	        /** The newest file comes first **/
	        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

	    }
		return files[0].getName();
	}

}

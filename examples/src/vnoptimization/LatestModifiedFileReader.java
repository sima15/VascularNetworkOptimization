package vnoptimization;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class LatestModifiedFileReader {
	/*converts the latest created 4 pov files to png*/
	

	public static String getLastFolderName() throws InterruptedException, IOException {
//	//	String filePath="D:\\iDynomics\\resultss\\Contact(20151014_1750)\\povray";
//		String ext="pov";
// File file = new File("D:\\iDynomics\\resultss");
//		String[] names = file.list();
//        Arrays.sort(names);
//        //reading the latest contact folder
//       String name = names[names.length-1];
//        
//
//		String filePath="D:\\iDynomics\\resultss\\"+name+"\\povray";
// 
///* replace header file in the povray folder with new header with the light and reflection parameters deleted*/
//ImageCleaner.clean(filePath);
		
		 
	/* Get the newest file for a specific extension */
		String filePath="E:\\Courses\\cs6600\\Project\\program\\resultss\\experiments";
	    File dir = new File(filePath);
//	    FileFilter fileFilter = new WildcardFileFilter("*." + ext);
	    File[] files = dir.listFiles();
	    String[] paths = new String[4];
	    String path;
	    if (files.length > 0) {
	        /** The newest file comes first **/
	        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
//	        for(int i=0;i<4;i++)
//	        {
//		         path= files[i].getPath();
//		         // 
//		        paths[i]=path;
//		        //conversion to png
//		       // remove below comment
//		       POVRayExecution.executer(path);
//		        
//		        
//		        
//		        
//
//	        }

	    }
		return files[0].getName();

	

	}

}

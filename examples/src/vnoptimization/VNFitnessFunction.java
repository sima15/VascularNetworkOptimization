package vnoptimization;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import com.google.common.base.Throwables;

import idyno.Idynomics;
import simulation.Controller;
import utils.ImgProcLog;


public class VNFitnessFunction extends FitnessFunction {
	  /** String containing the CVS revision. Read out via reflection!*/
	  private final static String CVS_REVISION = "$Revision: 2.1 $";
	 
	  private final String REULT_PATH = "E:\\Bio research\\GA\\resultss\\experiments";
	  private final String PROTOCOL_PATH = "E:\\Bio research\\GA\\protocols\\experiments\\";
	  private final String PROTOCOL = "Vascularperc30-quartSize-short.xml";
	  private String name;
	  private final int ITERATIONS	= 16;
	  
	  private int iterationIndex = 0;
	  public static final int MAX_BOUND = 4000;

	  /**
	   * Determine the fitness of the given Chromosome instance. The higher the
	   * return value, the more fit the instance. This method should always
	   * return the same fitness value for two equivalent Chromosome instances.
	   *
	   * @param a_subject the Chromosome instance to evaluate
	   *
	   * @return positive double reflecting the fitness rating of the given
	   * Chromosome
	   */
	  public double evaluate(IChromosome a_subject) {
		  double evolutaionStartTime = System.currentTimeMillis();
		  
		  //get values of the chromosome
		  Map<String,Double> map = new HashMap<String,Double>();
		  Double value = (Double) a_subject.getGene(0).getAllele();
		  
		  //updating chemotactic Strength with attract
		  double parameterValue0 =value.doubleValue();
		  ImgProcLog.write("-----------------------------------------");
		  ImgProcLog.write("GA iteration " + ++iterationIndex);
		  DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		  Date start = new Date();
		  ImgProcLog.write("Date/Time: "+ dateFormat.format(start));
		  
		  ImgProcLog.write("Parameter values");
		  ImgProcLog.write("chemotactic Strength With Attract: "+parameterValue0);
		  map.put("chemotactic Strength With Attract", parameterValue0 );
		
		  //updating chemotactic Strength with gradient
		  double parameterValue1 = getParameterValue(a_subject,1);
		  ImgProcLog.write("chemotactic Strength With Gradient: "+parameterValue1);
		  map.put("chemotactic Strength With Gradient", parameterValue1 );

		  //updating 
		  double parameterValue2 = getParameterValue(a_subject,2);
		  ImgProcLog.write("Vessel muMax: "+parameterValue2);
		  map.put("Vessel muMax",(double) parameterValue2);
		  
		  //updating 
		  double parameterValue3 = getParameterValue(a_subject,3);
		  ImgProcLog.write("Pipe muMax: "+parameterValue3);
		  map.put("Pipe muMax",(double) parameterValue3);
		  
		  //updating 
		  double parameterValue4 = getParameterValue(a_subject,4);
		  ImgProcLog.write("Vessel Beta: "+parameterValue4);
		  map.put("Vessel Beta",(double) parameterValue4);
		  
		  //updating 
		  double parameterValue5 = getParameterValue(a_subject,5);
		  ImgProcLog.write("Pipe Beta: "+parameterValue5);
		  map.put("Pipe Beta",(double) parameterValue5);
		  
		  //updating 
		  double parameterValue6 = getParameterValue(a_subject,6);
		  ImgProcLog.write("Vessel K: "+parameterValue6);
		  map.put("Vessel K",(double) parameterValue6);
		  
		  //update protocol xml of iDynomica with new values in chromosome
		  XMLUpdater.setPath(PROTOCOL_PATH + PROTOCOL);
		  XMLUpdater.updateParameter(map);
		  
		  //run iDynomics
		  ImgProcLog.write("Runnig iDynomics in VascularNetworkOptimization project");
		  Idynomics idynomics = new Idynomics();
		  idynomics.setProtocolPath(PROTOCOL_PATH + PROTOCOL); 
		  try {
			idynomics.func();
		} catch (Exception e) {
			ImgProcLog.write("Idynomics.main(): error met while writing out random number state file");
			String exception = Throwables.getStackTraceAsString(e);
	    	ImgProcLog.write(exception);			
	    	e.printStackTrace();
		}

		name =  LatestModifiedFileReader.getLastFolderName(REULT_PATH);
		
		Controller secondPhaseController = new Controller(name, PROTOCOL, REULT_PATH+ "\\", ITERATIONS);
		try {
			secondPhaseController.start();
		} catch (Exception e) {
			ImgProcLog.write("Exception caught in GA:");
			String exception = Throwables.getStackTraceAsString(e);
	    	ImgProcLog.write(exception);
		} 

	    double product = secondPhaseController.getProduct();
		ImgProcLog.write("product amount for this Chromosome is " + product);
		int endTime = (int) (System.currentTimeMillis()- evolutaionStartTime)/1000;
		ImgProcLog.write("duration of this iteration ("+ iterationIndex + ") = "+ endTime/3600
				+ " h "+ (endTime%3600)/60 + " m");
		
		//Return the amount of the cell factory product if it exists
		if(product > 0) return product;
		//If cell factory could not produce anything, return number of cycles and if there was a path from left to right
		else {
			int cyclesAndPath = secondPhaseController.getNumCycles()+ 
					Controller.getPathFromLeftToRightExistence();
			ImgProcLog.write("Number of cycles + path = "+ cyclesAndPath);
			return cyclesAndPath;
		}
	  }

	  /**
	   * Returns the value of the specified gene
	   * @param a_potentialSolution the chromosome containing the gene
	   * @param a_position Position of the gene in chromosome (array index)
	   * @return Value of the gene
	   */
	  public static double getParameterValue(IChromosome a_potentialSolution, int a_position) {
	    Double value = (Double) a_potentialSolution.getGene(a_position).getAllele();
	    return value.doubleValue();
	  }

}

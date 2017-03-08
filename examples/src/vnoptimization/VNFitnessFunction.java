package vnoptimization;

import java.util.HashMap;
import java.util.Map;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import idyno.Idynomics;
import simulation.Controller;
import utils.ImgProcLog;


public class VNFitnessFunction extends FitnessFunction {
	  /** String containing the CVS revision. Read out via reflection!*/
	  private final static String CVS_REVISION = "$Revision: 2.0 $";
	 
	  private final String REULT_PATH = "E:\\Bio research\\GA\\resultss\\experiments";
	  private final String PROTOCOL_PATH = "E:\\Bio research\\GA\\protocols\\experiments\\";
	  private final String PROTOCOL = "Vascularperc30-quartSize-short.xml";
	  private String name;
	  private final int ITERATIONS	= 16;
	  
	  private int evolutionIndex = 0;
	  private final int m_targetAmount;
	  int counter = 1;
	  public static final int MAX_BOUND = 4000;

	  public VNFitnessFunction(int a_targetAmount) {
	   /* if (a_targetAmount < 1 || a_targetAmount >= MAX_BOUND) {
	      throw new IllegalArgumentException(
	          "Change amount must be between 1 and " + MAX_BOUND + " cents.");
	    }*/
	    m_targetAmount = a_targetAmount;
	  }

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
		  ImgProcLog.write("Evolution number "+ ++evolutionIndex);
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
		  
		  //run idynomics
		  ImgProcLog.write("Runnig iDynomics in VascularNetworkOptimization project");
		  Idynomics idynomics = new Idynomics();
		  idynomics.setProtocolPath(PROTOCOL_PATH + PROTOCOL); 
		  try {
			idynomics.func();
		} catch (Exception e1) {
			ImgProcLog.write("Idynomics.main(): error met while writing out random number state file");
			e1.printStackTrace();
		}

		name =  LatestModifiedFileReader.getLastFolderName(REULT_PATH);
		
		Controller secondPhaseController = new Controller(name, PROTOCOL, REULT_PATH+ "\\", ITERATIONS);
		try {
			secondPhaseController.start();
		} catch (Exception e) {
			ImgProcLog.write("Exception caught in GA:");
			ImgProcLog.write(e.getMessage());
			e.printStackTrace();
		} 

	    int numCycles = secondPhaseController.getNumCycles();
	    double product = secondPhaseController.getProduct();
		ImgProcLog.write("product amount for this Chromosome is " + product);
//	    WriteToFile.write("Number of cycles for this Chromosome is " + numCycles);
		
		ImgProcLog.write("duration of this evolution ("+ evolutionIndex + ") = "+ (System.currentTimeMillis()- evolutaionStartTime));
	    return product;
		  
	  }

	  /**
	   * Bonus calculation of fitness value.
	   * @param a_maxFitness maximum fitness value appliable
	   * @param a_changeDifference change difference in coins for the coins problem
	   * @return bonus for given change difference
	   *
	   * @author Klaus Meffert
	   * @since 2.3
	   */
	/*  protected double changeDifferenceBonus(double a_maxFitness,
	                                         int a_changeDifference) {
	    if (a_changeDifference == 0) {
	      return a_maxFitness;
	    }
	    else {
	      // we arbitrarily work with half of the maximum fitness as basis for non-
	      // optimal solutions (concerning change difference)
	      if (a_changeDifference * a_changeDifference >= a_maxFitness / 2) {
	        return 0.0d;
	      }
	      else {
	        return a_maxFitness / 2 - a_changeDifference * a_changeDifference;
	      }
	    }
	  }*/

	  /**
	   * Calculates the penalty to apply to the fitness value based on the ammount
	   * of coins in the solution
	   *
	   * @param a_maxFitness maximum fitness value allowed
	   * @param a_coins number of coins in the solution
	   * @return penalty for the fitness value base on the number of coins
	   *
	   * @author John Serri
	   * @since 2.2
	   */
	 /* protected double computeCoinNumberPenalty(double a_maxFitness, int a_coins) {
	    if (a_coins == 1) {
	      // we know the solution cannot have less than one coin
	      return 0;
	    }
	    else {
	      // The more coins the more penalty, but not more than the maximum fitness
	      // value possible. Let's avoid linear behavior and use
	      // exponential penalty calculation instead
	      return (Math.min(a_maxFitness, a_coins * a_coins));
	    }
	  }*/

	  
	  public static double getParameterValue(IChromosome a_potentialSolution,
	                                           int a_position) {
	    Double value =
	        (Double) a_potentialSolution.getGene(a_position).getAllele();
	    return value.doubleValue();
	  }

}

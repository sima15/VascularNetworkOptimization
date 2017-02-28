package vnoptimization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import idyno.Idynomics;
import simulation.MyController;
import vnoptimization.utility.WriteToFile;


public class VNFitnessFunction extends FitnessFunction {
	  /** String containing the CVS revision. Read out via reflection!*/
	  private final static String CVS_REVISION = "$Revision: 1.0 $";
	  private int evolutionIndex =0;
	  private final int m_targetAmount;

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
		  
		  
		  //get values of the chromosome
		  Map<String,Double> map=new HashMap<String,Double>();
		  Double value = (Double) a_subject.getGene(0).getAllele();
		  
		  //updating chemotactic Strength with attract
		  double parameterValue0 =value.doubleValue();
		  WriteToFile.write("-----------------------------------------");
		  WriteToFile.write("Evolution number "+ ++evolutionIndex);
		  WriteToFile.write("Parameter values");
		  WriteToFile.write("chemotactic Strength With Attract: "+parameterValue0);
		  map.put("chemotactic Strength With Attract", parameterValue0 );
		
		  //updating chemotactic Strength with gradient
		  double parameterValue1 = getParameterValue(a_subject,1);
		  WriteToFile.write("chemotactic Strength With Gradient: "+parameterValue1);
		  map.put("chemotactic Strength With Gradient", parameterValue1 );

		  //updating 
		  double parameterValue2 = getParameterValue(a_subject,2);
		  WriteToFile.write("Vessel muMax: "+parameterValue2);
		  map.put("Vessel muMax",(double) parameterValue2);
		  
		  //updating 
		  double parameterValue3 = getParameterValue(a_subject,3);
		  WriteToFile.write("Pipe muMax: "+parameterValue3);
		  map.put("Pipe muMax",(double) parameterValue3);
		  
		  //updating 
		  double parameterValue4 = getParameterValue(a_subject,4);
		  WriteToFile.write("Vessel Beta: "+parameterValue4);
		  map.put("Vessel Beta",(double) parameterValue4);
		  
		  //updating 
		  double parameterValue5 = getParameterValue(a_subject,5);
		  WriteToFile.write("Pipe Beta: "+parameterValue5);
		  map.put("Pipe Beta",(double) parameterValue5);
		  
		//updating 
		  double parameterValue6 = getParameterValue(a_subject,6);
		  WriteToFile.write("Vessel K: "+parameterValue6);
		  map.put("Vessel K",(double) parameterValue6);
		  
		  //update protocol xml of iDynomica with new values in chromosome
		  XMLUpdater.updateParameter(map);
		  
		  //run idynomics
		  System.out.println("Runnig iDynomics in VNOptimization project");
		  Idynomics id =new Idynomics();
		  try {
			id.func();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String folderName = "";
		try {
			folderName = LatestModifiedFileReader.getLastFolderName();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		  MyController imgController = new MyController(folderName);
		  try {
			imgController.start();
		} catch (Exception e) {
			System.out.println("Exception caught in GA:");
			e.printStackTrace();
			if(imgController.getNodeMergerTileSize() <24){
				imgController.setNodeMergerOptimizer(true);
				imgController.setNodeMergerTileSize(imgController.getNodeMergerTileSize() + 1);
				System.out.println("Starting phase one with nodeMergerTileSize = "+ imgController.getNodeMergerTileSize());
				try{
					imgController.verifyConditions();
				}catch(Exception e2){
					System.out.println("Exception e2 caught in GA:");
					e2.printStackTrace();
				}
			}
		} 

		  //find Set Complexity
	    int numCycles = MyController.getNumCycles();
//	    double product = imgController.getProduct();
//		WriteToFile.write("product amount for this Chromosome is " + product);
	    WriteToFile.write("Number of cycles for this Chromosome is " + numCycles);
//	    return product;
	    return numCycles;
//			try {
//				 setComplexity=SetComplexityFinder.calcuate();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
		  
		
//		  Double value = (Double) a_subject.getGene(0).getAllele();
//		  System.out.println("Gene value = "+ value);
//		  Double d = Math.random();
//		  System.out.println("Random value = "+ d );
//		  return value+d;
		  
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

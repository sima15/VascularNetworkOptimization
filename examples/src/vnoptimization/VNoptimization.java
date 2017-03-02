package vnoptimization;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;

import utils.ImgProcLog;

public class VNoptimization {
	/**
	 * Usage of JGAP to optimize vascular cell's production.
	 *
	 * @author Sima Mehri
	 */

	  /** String containing the CVS revision. Read out via reflection!*/
	  private static final String CVS_REVISION = "$Revision: 2.0 $";

	  /**
	   * Starts the program.
	   * @param args if optional first argument provided, it represents the number
	   * of bits to use, but no more than 32
	   */
	  public static void main(String[] args) {
		int numEvolutions = 30;
		Configuration gaConf = new DefaultConfiguration();
		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(true);
		Genotype genotype = null;
//		int chromeSize = 2;
		int chromeSize = 7;
		int targetAmount = 2;
		Gene chemStrenWAttract;
		Gene chemStrenWGradient;
		Gene vesselMu;
		Gene pipeMu;
		Gene vesselBeta;
		Gene pipeBeta;
		Gene vesselK;
		Gene attachmentFactor;
		Gene detachmentFactor;
	    
	   try {
//	    	Gene[] gene = new Gene[chromeSize];
//	    	chemStrenWAttract = gene[0] = new DoubleGene(gaConf, 0.1, 5);
//	    	chemStrenWGradient = gene[1] = new DoubleGene(gaConf, 0.1, 5);
//	    	vesselMu = gene[2] = new DoubleGene(gaConf, 0.2, 7 );
//	    	pipeMu = gene[3] = new DoubleGene(gaConf, 0.2, 7 );
//	    	vesselBeta = gene[4] = new DoubleGene(gaConf, 0.00001, 0.9 );
//	    	pipeBeta = gene[5] = new DoubleGene(gaConf, 0.0000000001, 0.9 );
//	    	vesselK = gene[6] = new DoubleGene(gaConf, 0.0000000001, 0.9);
//	    	attachmentFactor = gene[6] = new DoubleGene(gaConf, 0.0000000001, 0.9);
//	    	detachmentFactor = gene[6] = new DoubleGene(gaConf, 0.0000000001, 0.9);
	    	
	    	Gene[] gene = new Gene[chromeSize];
	    	gene[0] = new DoubleGene(gaConf, 1, 6);
	    	gene[1] = new DoubleGene(gaConf, 1, 6);
	    	gene[2] = new DoubleGene(gaConf, 1, 7 );
	    	gene[3] = new DoubleGene(gaConf, 1, 7 );
	    	gene[4] = new DoubleGene(gaConf, 0.00001, 0.1 );
	    	gene[5] = new DoubleGene(gaConf, 0.0000000001, 0.001 );
	    	gene[6] = new DoubleGene(gaConf, 0.0000000001, 0.01);
	    	
	      IChromosome sampleChromosome = new Chromosome(gaConf,
	          gene);
	      gaConf.setSampleChromosome(sampleChromosome);
	      gaConf.setPopulationSize(2);
	      gaConf.setFitnessFunction(new VNFitnessFunction(targetAmount));
	      genotype = Genotype.randomInitialGenotype(gaConf);
	    }
	    catch (InvalidConfigurationException e) {
	      e.printStackTrace();
	      System.exit( -2);
	    }

	    for( int i = 0; i <numEvolutions; i++ )
		  {
			  IChromosome bestSolutionSoFar = genotype.getFittestChromosome();
			  ImgProcLog.write("Best so far ... "+ bestSolutionSoFar.getFitnessValue());

		  		genotype.evolve();
		  }
	    
	    // Print summary.
	    // --------------
	    IChromosome fittest = genotype.getFittestChromosome();
	    ImgProcLog.write("Fittest Chromosome has fitness " +
	                       fittest.getFitnessValue());
	  }
	
}

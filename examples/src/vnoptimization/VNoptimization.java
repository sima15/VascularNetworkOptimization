package vnoptimization;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;

import com.google.common.base.Throwables;

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
	   */
	  public static void main(String[] args) {
		final int numEvolutions = 5000;
		final int POPULATION = 3;
		Configuration gaConf = new DefaultConfiguration();
		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(true);
		Genotype genotype = null;
		int chromeSize = 7;
	    
	   try {
		   /*
	    	Gene[] gene = new Gene[chromeSize];
	    	chemStrenWAttract = gene[0] = new DoubleGene(gaConf, 1, 6);
	    	chemStrenWGradient = gene[1] = new DoubleGene(gaConf, 1, 6);
	    	vesselMu = gene[2] = new DoubleGene(gaConf, 1, 7 );
	    	pipeMu = gene[3] = new DoubleGene(gaConf, 1, 7 );
	    	vesselBeta = gene[4] = new DoubleGene(gaConf, 0.00001, 0.1 );
	    	pipeBeta = gene[5] = new DoubleGene(gaConf, 0.0000000001, 0.001 );
	    	vesselK = gene[6] = new DoubleGene(gaConf, 0.0000000001, 0.01);
	    	attachmentFactor = gene[6] = new DoubleGene(gaConf, 0.0000000001, 0.9);
	    	detachmentFactor = gene[6] = new DoubleGene(gaConf, 0.0000000001, 0.9);
	    	*/
	    	Gene[] gene = new Gene[chromeSize];
	    	gene[0] = new DoubleGene(gaConf, 1, 6);
	    	gene[1] = new DoubleGene(gaConf, 1, 6);
	    	gene[2] = new DoubleGene(gaConf, 1, 7 );
	    	gene[3] = new DoubleGene(gaConf, 1, 7 );
	    	gene[4] = new DoubleGene(gaConf, 0.00001, 0.1 );
	    	gene[5] = new DoubleGene(gaConf, 0.0000000001, 0.001 );
	    	gene[6] = new DoubleGene(gaConf, 0.0000000001, 0.01);
	    	
	    	IChromosome sampleChromosome = new Chromosome(gaConf, gene);
	    	gaConf.setSampleChromosome(sampleChromosome);
	    	gaConf.setPopulationSize(POPULATION);
	    	gaConf.setFitnessFunction(new VNFitnessFunction());
	    	genotype = Genotype.randomInitialGenotype(gaConf);
	    }
	    catch (InvalidConfigurationException e) {
	    	String exception = Throwables.getStackTraceAsString(e);
	    	ImgProcLog.write(exception);
	    	e.printStackTrace();
	    	System.exit( -2);
	    }

	   DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	   Date start = new Date();
	   ImgProcLog.write("Start Date/Time: "+ dateFormat.format(start));
	   ImgProcLog.write("Population size = "+ POPULATION);
	   ImgProcLog.write("Number of evolutions = "+ numEvolutions);
	   
	   for( int i = 1; i <= numEvolutions; i++ ){
	    	genotype.evolve();
	    	IChromosome bestSolutionSoFar = genotype.getFittestChromosome();
	    	ImgProcLog.write("Evolution "+ i+ ", Best solution so far = "+ bestSolutionSoFar.getFitnessValue());
	   }
	    
	    // Print summary.
	    // --------------
	    IChromosome fittest = genotype.getFittestChromosome();
	    ImgProcLog.write("Fittest Chromosome has fitness " + fittest.getFitnessValue());
	  }
	
}

package vnoptimization;

import java.io.File;
import java.io.IOException;
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

import iDynoOptimizer.MOEAFramework26.src.org.moeaframework.util.io.FileUtils;
import utils.ImgProcLog;

public class VNoptimization {
	/**
	 * Usage of JGAP to maximize cell culture production.
	 *
	 * @author Sima Mehri
	 */

	  /** String containing the CVS revision. Read out via reflection!*/
	  private static final String CVS_REVISION = "$Revision: 2.0 $";

	  /**
	   * Starts the program.
	   */
	  public static void main(String[] args) {
		  
		  DateFormat folderNameFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");  
		  Date start = new Date();
		  String resultDirName = folderNameFormat.format(start);
		  String RESULT_PATH = "E:\\Bio research\\GA\\new runs\\"+ resultDirName;
		  new File(RESULT_PATH).mkdir();
		  RESULT_PATH += File.separator;
		  VNFitnessFunction.setResultdirName(RESULT_PATH);
		  final String PROTOCOL = "Vasc30-quartSize-short.xml";
		  File source = new File("E:\\Bio research\\GA\\protocols\\experiments\\" + PROTOCOL);
		  File destination = new File(RESULT_PATH + PROTOCOL);
		  try {
			FileUtils.copy(source, destination);
		} catch (IOException e) {
			e.printStackTrace();
			String exception = Throwables.getStackTraceAsString(e);
			ImgProcLog.write(RESULT_PATH, exception);
		}
		  VNFitnessFunction.setProtocolName(PROTOCOL);
		  VNFitnessFunction.setProtocolPath(RESULT_PATH + PROTOCOL);
		  
		  
		final int numEvolutions = 5000;
		final int POPULATION = 5;
		Configuration gaConf = new DefaultConfiguration();
		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(true);
		Genotype genotype = null;
		int chromeSize = 10;
	    
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
	    	gene[7] = new DoubleGene(gaConf, 0, 6);
	    	gene[8] = new DoubleGene(gaConf, 0, 6);
	    	gene[9] = new DoubleGene(gaConf, 0, 6 );
	    	
	    	IChromosome sampleChromosome = new Chromosome(gaConf, gene);
	    	gaConf.setSampleChromosome(sampleChromosome);
	    	gaConf.setPopulationSize(POPULATION);
	    	gaConf.setFitnessFunction(new VNFitnessFunction());
	    	genotype = Genotype.randomInitialGenotype(gaConf);
	    }
	    catch (InvalidConfigurationException e) {
	    	String exception = Throwables.getStackTraceAsString(e);
	    	ImgProcLog.write(RESULT_PATH, exception);
	    	e.printStackTrace();
	    	System.exit( -2);
	    }

	   
	   DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	   ImgProcLog.write(RESULT_PATH, "Start Date/Time: "+ dateFormat.format(start));
	   ImgProcLog.write(RESULT_PATH, "Population size = "+ POPULATION);
	   ImgProcLog.write(RESULT_PATH, "Number of evolutions = "+ numEvolutions);
	   
	   for( int i = 1; i <= numEvolutions; i++ ){
	    	genotype.evolve();
	    	IChromosome bestSolutionSoFar = genotype.getFittestChromosome();
	    	ImgProcLog.write(RESULT_PATH, "Evolution "+ i+ ", Best solution so far = "+ bestSolutionSoFar.getFitnessValue());
	   }
	    
	    // Print summary.
	    // --------------
	    IChromosome fittest = genotype.getFittestChromosome();
	    ImgProcLog.write(RESULT_PATH, "Fittest Chromosome has fitness " + fittest.getFitnessValue());
	  }
	
}

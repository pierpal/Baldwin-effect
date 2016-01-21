package baldwineffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Pier Palamara <ppalama@hsph.harvard.edu>
 */
public class BaldwinEffect {

    static long seed = System.currentTimeMillis();

    public static void main(String[] args) {

        Random random = new Random(seed);
        int numberOfSamples = Integer.parseInt(args[0]);
        int len = Integer.parseInt(args[1]);
        int fixed = Integer.parseInt(args[2]);
        
        Individual.setNumberOfLearningAttemps(numberOfSamples);

        ArrayList<Individual> inds = new ArrayList<Individual>();
        for (int i = 0; i < numberOfSamples; i++) {
            inds.add(new Individual(i, len, fixed, random));
        }

        int generation = 0;
        double[] populationStats = Individual.getGenomeProb(inds);

        while (!Individual.isFixated(inds)) {
            System.out.println("GENERATION " + generation + "\t\t0 --> " + populationStats[0] + "\t1 --> " + populationStats[1] + "\t? --> " + populationStats[2]);
            HashMap<Individual, Double> largeFitness = new HashMap<Individual, Double>();
            ArrayList<Individual> fitnessOne = new ArrayList<Individual>();
            double totFitnessGreaterThanOne = 0.;
            for (Individual ind : inds) {
                double fitness = ind.liveLearnAndReturnFitness(random);
                if (fitness > 1) {
                    largeFitness.put(ind, fitness);
                    totFitnessGreaterThanOne += fitness;
                } else {
                    fitnessOne.add(ind);
                }
            }

            ArrayList<Individual> newGen = new ArrayList<Individual>();
            for (int i = 0; i < numberOfSamples; i++) {
                Individual samp1 = Individual.sampleIndividualPropToFitness(random, largeFitness, fitnessOne, totFitnessGreaterThanOne);
                Individual samp2 = Individual.sampleIndividualPropToFitness(random, largeFitness, fitnessOne, totFitnessGreaterThanOne);
                Individual newSamp = Individual.mate(i, samp1, samp2, random);
                newGen.add(newSamp);
            }
            inds = newGen;
            populationStats = Individual.getGenomeProb(inds);
            generation++;
        }
        System.out.println("Population is now fixated. Stats:\t" + Individual.sequenceToString(inds.get(0).getSequence())
                + "\t" + populationStats[0] + "\t" + populationStats[1] + "\t" + populationStats[2]);
    }

}

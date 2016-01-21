package baldwineffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Pier Palamara <ppalama@hsph.harvard.edu>
 */
public class Individual {

    private static int numberLearningAttempts = 1000;
    
    private final boolean fixGenomeWhenSuccess = false;
    private final double fitnessBoost = 19.;

    private int[] genome;
    private int ID;
    
    public static void setNumberOfLearningAttemps(int numberLearningAttempts) {
        Individual.numberLearningAttempts = numberLearningAttempts;
    }

    public Individual(int ID, int[] genome) {
        this.ID = ID;
        this.genome = genome;
    }

    public Individual(int ID, int size, int fixedElements, Random random) {
        this.ID = ID;
        genome = new int[size];
        ArrayList<Integer> positions = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            genome[i] = 2; // init as ?
            positions.add(i);
        }
        Collections.shuffle(positions, random);
        for (int i = 0; i < fixedElements; i++) {
            int pos = positions.get(i);
            genome[pos] = random.nextInt(2);
        }
    }

    @Override
    public String toString() {
        if (genome == null) {
            return "";
        }
        StringBuilder s = new StringBuilder();
        s.append(ID);
        s.append('\t');
        s.append(sequenceToString(genome));
        return s.toString();
    }

    public void setSequence(int[] genome) {
        this.genome = genome;
    }

    public int[] getSequence() {
        return this.genome;
    }

    public static String sequenceToString(int[] genome) {
        if (genome == null) {
            return "";
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < genome.length; i++) {
            if (genome[i] == 2) {
                s.append('?');
            } else {
                s.append(genome[i]);
            }
        }
        return s.toString();
    }

    public static Individual mate(int ID, Individual ind1, Individual ind2, Random rand) {
        int recPos = 1 + rand.nextInt(ind1.genome.length - 1);
        int[] genomeNew = new int[ind1.genome.length];
        for (int i = 0; i < recPos; i++) {
            genomeNew[i] = ind1.genome[i];
        }
        for (int i = recPos; i < genomeNew.length; i++) {
            genomeNew[i] = ind2.genome[i];
        }
        return new Individual(ID, genomeNew);
    }

    public static int[] getCounts(int[] genome) {
        int[] cnts = new int[3];
        for (int i = 0; i < genome.length; i++) {
            cnts[genome[i]]++;
        }
        return cnts;
    }

    public double liveLearnAndReturnFitness(Random random) {
        int len = genome.length;
        if (getCounts(genome)[1] == len) {
            // this individual has correct sequence, no learning will happen
            return 1 + fitnessBoost;
        } else if (getCounts(genome)[0] > 0) {
            // this individual has 0s in the sequence, will not learn right solution
            return 1;
        } else {
            // do learning
            int[] sequence = genome.clone();
            ArrayList<Integer> positionOfQuestionMarks = new ArrayList<Integer>();
            for (int i = 0; i < sequence.length; i++) {
                if (sequence[i] == 2) {
                    positionOfQuestionMarks.add(i);
                }
            }
            int attempts = 1;
            for (; attempts <= numberLearningAttempts; attempts++) {
                int[] learnedBits = getRandomBinaryString(positionOfQuestionMarks.size(), random);
                for (int i = 0; i < positionOfQuestionMarks.size(); i++) {
                    sequence[positionOfQuestionMarks.get(i)] = learnedBits[i];
                }
                if (getCounts(sequence)[1] == len) {
                    if (fixGenomeWhenSuccess) {
                        genome = sequence;
                    }
                    return 1 + fitnessBoost * (numberLearningAttempts - attempts) / 1000.;
                }
            }
            // could not learn
            return 1;
        }
    }

    public static int[] getRandomBinaryString(int len, Random random) {
        int[] s = new int[len];
        for (int i = 0; i < len; i++) {
            s[i] = random.nextBoolean() ? 1 : 0;
        }
        return s;
    }

    public static Individual sampleIndividualPropToFitness(Random random, HashMap<Individual, Double> largeFitness, ArrayList<Individual> fitnessOne, double totFitnessGreaterThanOne) {
        double totFitness = fitnessOne.size() + totFitnessGreaterThanOne;
        if (random.nextDouble() < fitnessOne.size() / totFitness) {
            // sample from those with fitness = 1
            return fitnessOne.get(random.nextInt(fitnessOne.size()));
        } else {
            // sample from those ith fitness > 1
            double fitnessMark = random.nextDouble() * totFitnessGreaterThanOne;
            double cumulativeFitness = 0.;
            Individual sampledInd = null;
            for (Individual ind : largeFitness.keySet()) {
                cumulativeFitness += largeFitness.get(ind);
                if (cumulativeFitness >= fitnessMark) {
                    sampledInd = ind;
                    break;
                }
            }
            return sampledInd;
        }
    }

    public static boolean isFixated(ArrayList<Individual> inds) {
        int[] firstGenome = inds.get(0).genome;
        for (Individual ind : inds) {
            if (!Arrays.equals(firstGenome, ind.genome)) {
                return false;
            }
        }
        return true;
    }

    public static double[] getGenomeProb(Collection<Individual> inds) {
        double[] res = new double[3];
        for (Individual ind : inds) {
            int[] thisInd = getCounts(ind.genome);
            res[0] += thisInd[0];
            res[1] += thisInd[1];
            res[2] += thisInd[2];
        }
        double sum = res[0] + res[1] + res[2];
        res[0] /= sum;
        res[1] /= sum;
        res[2] /= sum;
        return res;
    }

}

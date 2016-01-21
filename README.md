# Baldwin effect simulation experiment

Code to run Hinton and Nowlan's experiment on the Baldwin effect from the paper ["How learning can guide evolution", Complex Systems, 1987](http://www.complex-systems.com/pdf/01-3-6.pdf). To use it, run:

    java -jar BaldwinEffect.jar numberOfSamples lenghtOfGenome numberOfNonLearningAlleles

where "numberOfSamples" is the number of individuals in the population, "lenghtOfGenome" is the length of the genome (number of alleles) "numberOfNonLearningAlleles" is the number of alleles with value 0 or 1 that will be initialized randomly in each individual. The remaining alleles will be set to "?", i.e. the "learning" alleles. Each individual runs 1000 learning attempts before potentially reproducing. The individual's reproductive fitness is 1+19n/1000 when finding the correct neural net setup with n attemps left, 1 otherwise. The correct neural net setup corresponds to a genome where all values are 1. Mating occurs by recombining two individuals that are chosen (with replacement) with probability proportional to their fitness. Recombination occurs at one uniformaly chosen position along the genome. There is no mutation. Simulation ends when all alleles drift to fixation. The output shows the current generation and the fraction of 0's, 1's and ?'s in the population. Running

    java -jar BaldwinEffect.jar 1000 20 10

should give you results that are similar to Figure 2 of the original paper.

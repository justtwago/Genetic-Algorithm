import utils.ArrayUtils;
import utils.DataManager;

import java.util.*;

public class GeneticAlgorithm {
    private int population[][];
    private int populationSize = 500;
    private int generationSize = 100;
    private int tournamentSize = 5;
    private int individualSize;
    private double crossoverProbabilityPercent = 0.7;
    private double mutationProbabilityPercent = 0.03;

    private int currentGeneration = 0;

    private DataManager dataManager;
    private Random random;

    private int bestResults[];
    private int worstResults[];
    private int averageResults[];
    private int bestResult;

    GeneticAlgorithm(DataManager dataManager) {
        this.dataManager = dataManager;
        this.random = new Random();
        this.bestResults = new int[generationSize];
        this.worstResults = new int[generationSize];
        this.averageResults = new int[generationSize];
    }

    public int runGeneticAlgorithm() {
        initialize();
        evaluate();
        while (currentGeneration < generationSize) {
            printGenerationNumber();
            createNewPopulation();
            evaluate();
            currentGeneration++;
        }
        outputData();
        return bestResult;
    }

    public int runRandomAlgorithm() {
        do {
            initialize();
            evaluate();
            currentGeneration++;
        }
        while (currentGeneration < generationSize);

        return bestResult;
    }

    public int runGreedyAlgorithm() {
        individualSize = dataManager.getMatrixSize();
        ArrayList<Integer> stock = new ArrayList<>();
        ArrayList<Integer> individual = new ArrayList<>();

        for (int i = 1; i <= individualSize; i++) {
            stock.add(i);
        }

        int pivot = random.nextInt(individualSize);
        individual.add(stock.get(pivot));
        stock.remove(pivot);
        do {
            for (int i = 1; i < individualSize; i++) {
                int bestPivot = 0;
                int bestResult = -1;

                for (int j = 0; j < stock.size(); j++) {
                    individual.add(stock.get(j));
                    int[] arr = individual.stream().mapToInt(p -> p).toArray();

                    int result = countCosts(arr);
                    if (result < bestResult || bestResult == -1) {
                        bestResult = result;
                        bestPivot = j;
                    }

                    individual.remove(individual.size() - 1);
                }

                individual.add(stock.get(bestPivot));
                stock.remove(bestPivot);
            }
        }
        while (stock.size() != 0);

        return countCosts(individual.stream().mapToInt(p -> p).toArray());
    }

    private void initialize() {
        individualSize = dataManager.getMatrixSize();
        population = new int[populationSize][individualSize];
        for (int i = 0; i < populationSize; i++) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            while (arrayList.size() < individualSize) {
                int temp = random.nextInt(individualSize) + 1;
                if (!arrayList.contains(temp)) {
                    arrayList.add(temp);
                }
            }
            population[i] = arrayList.stream().mapToInt(j -> j).toArray();
        }
    }

    private void evaluate() {
        Integer[] results = new Integer[populationSize];
        for (int i = 0; i < populationSize; i++) {
            results[i] = countCosts(population[i]);
        }
        saveResults(results);
    }

    private void createNewPopulation() {
        ArrayList<int[]> selectionPopulation = new ArrayList<>();
        for (int i = 0; i < populationSize / 2; i++) {
            int[][] ints = rouletteSelection();
            ArrayList<int[]> selectedMembers = selectMembers();
            ArrayList<int[]> crossedMembers = crossover(selectedMembers);
            ArrayList<int[]> mutatedMembers = mutation(crossedMembers);


            selectionPopulation.add(mutatedMembers.get(0));
            selectionPopulation.add(mutatedMembers.get(1));
        }
        for (int i = 0; i < populationSize; i++) {
            population[i] = selectionPopulation.get(i).clone();
        }
    }

    private ArrayList<int[]> selectMembers() {
        ArrayList<int[]> members = new ArrayList<>();
        members.add(getBestMemberFromTournament());
        members.add(getBestMemberFromTournament());
        return members;
    }

    private int[][] rouletteSelection() {
        int totalCost = 0;
        int worstIndividualCost = countCosts(population[0]);
        int selectedIndividuals[][] = new int[2][individualSize];
        Sector sectors[] = new Sector[populationSize];

        for (int i = 0; i < populationSize; i++) {
            int popMemberCost = countCosts(population[i]);
            if (popMemberCost > worstIndividualCost) {
                worstIndividualCost = popMemberCost;
            }
        }

        for (int i = 0; i < populationSize; i++) {
            double startFitness = 0;
            if (i > 0) {
                startFitness = sectors[i - 1].getEnd();
            }
            double fitness = (worstIndividualCost - countCosts(population[i]) + 1);
            sectors[i] = new Sector(startFitness, startFitness + fitness);
            totalCost += fitness;
        }

        int rand = random.nextInt(totalCost);
        int rand2 = random.nextInt(totalCost);
        for (int i = 0; i < populationSize; i++) {
            if (sectors[i].contains(rand)) {
                selectedIndividuals[0] = population[i].clone();
            }
            if (sectors[i].contains(rand2)) {
                selectedIndividuals[1] = population[i].clone();
            }
        }
        return selectedIndividuals;
    }

    private ArrayList<int[]> crossover(ArrayList<int[]> members) {
        if (crossoverProbabilityPercent >= random.nextDouble()) {
            return cross(members);
        }
        return members;
    }

    private ArrayList<int[]> cross(ArrayList<int[]> individuals) {
        int pivot = random.nextInt(individualSize + 2) + 1;

        int[] parentA = individuals.get(0);
        int[] parentB = individuals.get(1);

        int[] childA = new int[individualSize];
        int[] childB = new int[individualSize];

        for (int i = 0; i < individualSize; i++) {
            if (i < pivot) {
                childA[i] = parentB[i];
                childB[i] = parentA[i];
            } else {
                childA[i] = parentA[i];
                childB[i] = parentB[i];
            }
        }

        ArrayList<Integer> indexA = new ArrayList<>();
        ArrayList<Integer> indexB = new ArrayList<>();

        for (int i = 0; i < individualSize; i++) {
            if (i < pivot && !ArrayUtils.isArrayContainsValue(childB, childA[i])) {
                indexA.add(i);
            }
            if (i < individualSize - pivot && !ArrayUtils.isArrayContainsValue(childA, childB[individualSize - i - 1])) {
                indexB.add(individualSize - i - 1);
            }
        }

        for (int i = 0; i < indexA.size(); i++) {
            int temp = childA[indexA.get(i)];
            childA[indexA.get(i)] = childB[indexB.get(i)];
            childB[indexB.get(i)] = temp;
        }

        individuals.set(0, childA);
        individuals.set(1, childB);
        return individuals;
    }

    private int[] getBestMemberFromTournament() {
        int members[][] = getRandomMembersForTournament();
        return getBestMember(members);
    }

    private int[][] getRandomMembersForTournament() {
        int members[][] = new int[tournamentSize][individualSize];
        for (int i = 0; i < tournamentSize; i++) {
            int randomMemberIndex = random.nextInt(populationSize);
            members[i] = population[randomMemberIndex].clone();
        }
        return members;
    }

    private int[] getBestMember(int[][] members) {
        int[] bestMember = members[0];
        for (int i = 0; i < tournamentSize; i++) {
            if (countCosts(bestMember) > countCosts(members[i])) {
                bestMember = members[i];
            }
        }

        return bestMember;
    }

    private ArrayList<int[]> mutation(ArrayList<int[]> members) {
        ArrayList<int[]> mutatedMembers = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            mutatedMembers.add(mutate(members.get(i)));
        }
        return mutatedMembers;
    }

    private int[] mutate(int[] newIndividual) {
        for (int i = 0; i < individualSize; i++) {
            if (mutationProbabilityPercent >= random.nextDouble()) {
                int pivotToMutate;
                do {
                    pivotToMutate = random.nextInt(individualSize);
                } while (pivotToMutate == i);

                int temp = newIndividual[pivotToMutate];
                newIndividual[pivotToMutate] = newIndividual[i];
                newIndividual[i] = temp;
            }
        }
        return newIndividual;
    }

    private int countCosts(int[] input) {
        int result = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                result += (dataManager.getMatrixDist()[i][j] * dataManager.getMatrixFlow()[input[i] - 1][input[j] - 1]);
            }
        }
        return result;
    }

    private void printGenerationNumber() {
        System.out.println("GENERATION: " + currentGeneration);
    }

    private void outputData() {
        dataManager.outputDataToFile(getOutputFileName(),
                bestResults, averageResults, worstResults);
        System.out.println(bestResults[generationSize - 1]);
    }

    private void saveResults(Integer[] results) {
        bestResult = Collections.min(Arrays.asList(results));
        bestResults[currentGeneration] = bestResult;
        Integer worstResult = Collections.max(Arrays.asList(results));
        worstResults[currentGeneration] = worstResult;
        Integer averageResult = (bestResult + worstResult) / 2;
        averageResults[currentGeneration] = averageResult;
    }

    private String getOutputFileName() {
        return "/Users/artyomvlasov/Desktop/Dataset/Had20/pop_" + populationSize
                + "_gen_" + generationSize
                + "_tour_" + tournamentSize
                + "_cross_" + crossoverProbabilityPercent
                + "_mut_" + mutationProbabilityPercent + ".csv";
    }

}

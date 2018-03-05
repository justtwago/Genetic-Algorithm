import java.util.*;

public class GeneticAlgorithm {
    private int population[][];
    private int selectedPopulation[][];
    private int populationSize = 1000;
    private int generationSize = 1000;
    private int tournamentSize = 5;
    private int individualSize;
    private double crossoverProbabilityPercent = 0;
    private double mutationProbabilityPercent = 0.1;

    private int currentGeneration = 0;

    private DataManager dataManager;
    private Random random;

    private int bestMember[];

    private int bestResults[];
    private int worstResults[];
    private int averageResults[];

    public GeneticAlgorithm(DataManager dataManager) {
        this.dataManager = dataManager;
        this.random = new Random();
        this.bestResults = new int[generationSize];
        this.worstResults = new int[generationSize];
        this.averageResults = new int[generationSize];
    }

    public void run() {
        initialize();
        evaluate();
        while (currentGeneration < generationSize) {
            System.out.println("GENERATION: " + currentGeneration);
            selection();
            crossover();
            mutation();
            population = selectedPopulation;
            evaluate();
            currentGeneration++;
        }
        outputData();
    }

    private void initialize() {
        individualSize = dataManager.getMatrixSize();
        population = new int[populationSize][individualSize];
        selectedPopulation = new int[populationSize][individualSize];
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
            results[i] = costs(population[i]);
        }
        Integer bestResult = Collections.min(Arrays.asList(results));
        bestResults[currentGeneration] = bestResult;
        Integer worstResult = Collections.max(Arrays.asList(results));
        worstResults[currentGeneration] = worstResult;
        Integer averageResult = (bestResult + worstResult) / 2;
        averageResults[currentGeneration] = averageResult;
    }

    private void selection() {
        for (int i = 0; i < populationSize; i++) {
            selectedPopulation[i] = getBestMemberFromTournament();
        }
    }

    private int[] getBestMemberFromTournament() {
        int members[][] = getRandomMembersForTournament();
        return fitness(members);
    }

    private int[][] getRandomMembersForTournament() {
        int members[][] = new int[tournamentSize][individualSize];
        for (int i = 0; i < tournamentSize; i++) {
            int randomMemberIndex = random.nextInt(populationSize);
            members[i] = population[randomMemberIndex];
        }
        return members;
    }

    private void mutation() {
        for (int i = 0; i < populationSize; i++) {
            mutate(selectedPopulation[i]);
        }
    }

    private void crossover() {
        for (int i = 0; i < populationSize; i++) {
            if (crossoverProbabilityPercent >= random.nextDouble() * 101) {
                int randomX = random.nextInt(populationSize);
                int randomY = random.nextInt(populationSize);
                cross(randomX, randomY);
            }
        }
    }

    private void cross(int randomX, int randomY) {
        int parentA[] = selectedPopulation[randomX];
        int parentB[] = selectedPopulation[randomY];
        int pivot = random.nextInt(individualSize + 2) + 1;

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

        selectedPopulation[randomX] = childA;
        selectedPopulation[randomY] = childB;
    }


    private int[] fitness(int[][] members) {
        int[] bestMember = members[0];
        for (int i = 0; i < tournamentSize; i++) {
            if (costs(bestMember) > costs(members[i])) {
                bestMember = members[i];
            }
        }
        this.bestMember = bestMember;

        return bestMember;
    }

    private int[] mutate(int[] newIndividual) {
        for (int i = 0; i < individualSize; i++) {
            if (mutationProbabilityPercent >= random.nextDouble() * 101) {
                int pivotStart, pivotEnd;
                do {
                    pivotStart = random.nextInt(individualSize);
                    pivotEnd = random.nextInt(individualSize);
                } while (pivotStart == pivotEnd);

                int temp = newIndividual[pivotStart];
                newIndividual[pivotStart] = newIndividual[pivotEnd];
                newIndividual[pivotEnd] = temp;
            }
        }
        return newIndividual;
    }

    private int costs(int[] input) {
        int result = 0;
        for (int i = 0; i < dataManager.getMatrixSize(); i++) {
            for (int j = 0; j < dataManager.getMatrixSize(); j++) {
                result += (dataManager.getMatrixDist()[i][j] * dataManager.getMatrixFlow()[input[i] - 1][input[j] - 1]);
            }
        }
        return result;
    }

    private void outputData() {
        dataManager.outputDataToFile(
                "/Users/artyomvlasov/Desktop/test_data.csv",
                bestResults, averageResults, worstResults);
        System.out.println(costs(bestMember));
        for (int i = 0; i < individualSize; i++){
            System.out.print(bestMember[i] + " ");
        }
    }

}

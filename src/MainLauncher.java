import utils.DataManager;

public class MainLauncher {

    public static void main(String[] args) {
//        int bestResults[] = new int[10];
//        for (int i = 0; i < 10; i++) {
//            bestResults[i] = new GeneticAlgorithm(new DataManager("data/Had20.rtf")).runGreedyAlgorithm();
//        }
//
//        for (int i = 0; i < 10; i++) {
//            System.out.print(bestResults[i] + ",");
//        }

        new GeneticAlgorithm(new DataManager("data/Had20.rtf")).runGeneticAlgorithm();
    }
}

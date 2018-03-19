package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DataManager {
    private int[][] matrixDist;
    private int[][] matrixFlow;
    private int matrixSize;

    public DataManager(String inputFile) {
        initDataBuilder(inputFile);
    }

    private void initDataBuilder(String inputFile) {
        try {
            readInputData(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void outputDataToFile(String outputFile, int[] bestResults, int[] averageResults, int[] worstResults) {
        try {
            writeResultData(outputFile, bestResults, averageResults, worstResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readInputData(String inputFile) throws FileNotFoundException {
        File source = new File(inputFile);

        Scanner scanner = new Scanner(source);
        matrixSize = scanner.nextInt();

        matrixDist = new int[matrixSize][matrixSize];
        matrixFlow = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++)
            for (int j = 0; j < matrixSize; j++)
                matrixDist[i][j] = scanner.nextInt();

        for (int i = 0; i < matrixSize; i++)
            for (int j = 0; j < matrixSize; j++)
                matrixFlow[i][j] = scanner.nextInt();

        scanner.close();
    }

    private void writeResultData(String outputFile, int[] bestResults, int[] averageResults, int[] worstResults) throws IOException {
        FileWriter writer = new FileWriter(outputFile);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Generation").append(",");
        stringBuffer.append("Best result").append(",");
        stringBuffer.append("Average result").append(",");
        stringBuffer.append("Worst result");
        stringBuffer.append("\n");
        for (int i = 0; i < bestResults.length; i++) {
            stringBuffer.append(i).append(",");
            stringBuffer.append(bestResults[i]).append(",");
            stringBuffer.append(averageResults[i]).append(",");
            stringBuffer.append(worstResults[i]);
            stringBuffer.append("\n");
        }
        writer.append(stringBuffer);
        writer.flush();
        writer.close();
    }

    public int getMatrixSize() {
        return matrixSize;
    }

    public int[][] getMatrixDist() {
        return matrixDist;
    }

    public int[][] getMatrixFlow() {
        return matrixFlow;
    }
}

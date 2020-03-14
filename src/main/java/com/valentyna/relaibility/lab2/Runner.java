package com.valentyna.relaibility.lab2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.valentyna.relaibility.util.FileReader.getMatrixFromFile;

public class Runner {

    private static final int[][] matrix = getMatrixFromFile("src/main/resources/scheme.csv");
    private static final double[] probabilities = new double[]{0.5, 0.6, 0.7, 0.8, 0.85, 0.9, 0.92, 0.94};
    private static List<List<Integer>> possiblePaths = new ArrayList<>();

    public static void main(String[] args) {
        findPossiblePaths();

        List<List<Integer>> possibleSystemOperatingStates = getPossibleSystemOperatingStates();
        Map<List<Integer>, Double> stateProbabilities = new HashMap<>();

        possibleSystemOperatingStates.stream()
                .filter(state -> possiblePaths.stream().anyMatch(state::containsAll))
                .forEach(state -> stateProbabilities.put(state, calculateStateProbability(state)));

        printSystemAbleStatesTable(stateProbabilities);

        double systemProbability = stateProbabilities.values().stream().reduce(0.0, Double::sum);
        System.out.println("P(10) = " + systemProbability);

        double failureRate = -Math.log(systemProbability) / 10;
        System.out.println("λ = " + failureRate);

        double timeToFailure = 1 / failureRate;
        System.out.println("T ндв = " + timeToFailure);
    }

    private static Double calculateStateProbability(List<Integer> state) {
        double stateProbability = 1.0;
        for (int i = 0; i < probabilities.length; i++) {
            if (state.contains(i + 1)) {
                stateProbability *= probabilities[i];
            } else {
                stateProbability *= 1 - probabilities[i];
            }
        }
        return stateProbability;
    }

    private static List<List<Integer>> getPossibleSystemOperatingStates() {
        List<List<Integer>> elementsTruthTable = new ArrayList<>();

        for (List<Integer> value : getTruthTable(matrix.length)) {

            List<Integer> row = new ArrayList<>();
            while (value.stream().anyMatch(v -> v == 1)) {
                row.add(value.indexOf(1) + 1);
                value.set(value.indexOf(1), 2);
            }
            elementsTruthTable.add(row);
        }
        return elementsTruthTable;
    }

    private static void findPossiblePaths() {
        boolean[] isVisited = new boolean[matrix.length];
        List<Integer> startPath = new ArrayList<>();
        startPath.add(1);
        dfs(0, isVisited, startPath);
    }

    private static void dfs(int current, boolean[] isVisited, List<Integer> path) {
        isVisited[current] = true;
        for (int i = 0; i < matrix.length; i++) {
            if (isUnvisitedAdjElement(current, i, isVisited)) {
                path.add(i + 1);
                dfs(i, Arrays.copyOf(isVisited, isVisited.length), new ArrayList<>(path));
                if (isEndElement(i)) {
                    possiblePaths.add(new ArrayList<>(path));
                }
                path.remove(path.size() - 1);
            }
        }
    }

    private static boolean isEndElement(int current) {
        return Arrays.stream(matrix[current]).noneMatch(i -> i == 1);
    }

    private static boolean isUnvisitedAdjElement(int current, int adjElement, boolean[] isVisited) {
        return matrix[current][adjElement] == 1 && !isVisited[adjElement];
    }

    private static List<List<Integer>> getTruthTable(int n) {
        List<List<Integer>> truthTable = new ArrayList<>();

        int rows = (int) Math.pow(2, n);
        for (int i = 0; i < rows; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = n - 1; j >= 0; j--) {
                row.add(((i / (int) Math.pow(2, j)) % 2));
            }
            truthTable.add(row);
        }
        return truthTable;
    }

    private static void printSystemAbleStatesTable(Map<List<Integer>, Double> stateProbabilities) {
        System.out.println("_____________Table of able states of the system_____________\n");
        for (Map.Entry<List<Integer>, Double> entry : stateProbabilities.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println("______________________________________________________________\n");
    }
}

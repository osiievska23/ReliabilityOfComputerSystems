package com.valentyna.relaibility.util;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.IntStream;

public class FileReader {

    public static double[] getSampleFromFile(String fileName) {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/" + fileName);
            String[] array = IOUtils.toString(fis, StandardCharsets.UTF_8.name()).split("\n");
            return Arrays.stream(array).mapToDouble(Double::valueOf).toArray();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return new double[]{};
    }

    public static int[][] getMatrixFromFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            String[] array = IOUtils.toString(fis, StandardCharsets.UTF_8.name()).split("\n");
            int[][] result = new int[array.length][];
            IntStream.range(0, array.length)
                    .forEach(i -> result[i] = Arrays.stream(array[i].split(", ")).mapToInt(Integer::valueOf).toArray());
            return result;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return new int[][]{};
    }
}

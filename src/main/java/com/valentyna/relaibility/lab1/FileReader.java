package com.valentyna.relaibility.lab1;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.distribution.GammaDistribution;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FileReader {

    public static double[] getContentFromFile(String fileName) {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/" + fileName + ".txt");
            String[] array = IOUtils.toString(fis, StandardCharsets.UTF_8.name()).split("\n");
            return Arrays.stream(array).mapToDouble(Double::valueOf).toArray();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return new double[]{};
    }
}

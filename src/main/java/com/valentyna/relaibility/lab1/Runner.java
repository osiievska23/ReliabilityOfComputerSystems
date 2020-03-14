package com.valentyna.relaibility.lab1;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.valentyna.relaibility.lab1.GraphicBuilder.printBarGraphic;
import static com.valentyna.relaibility.util.FileReader.getSampleFromFile;
import static java.lang.Math.pow;

public class Runner {

    private static final String EXPONENTIAL_FILE_NAME = "exponential.txt";
    private static final String GAMMA_FILE_NAME = "gamma.txt";
    private static final String NORMAL_FILE_NAME = "normal.txt";
    private static final String UNIFORM_FILE_NAME = "uniform.txt";

    private static double[] sample = getSampleFromFile(NORMAL_FILE_NAME);
    private static List<double[]> ranges;

    public static void main(String[] args) {
        DescriptiveStatistics statistics = new DescriptiveStatistics();
        Arrays.stream(sample).forEach(statistics::addValue);

        // gather sample statistic
        double mean = statistics.getMean();
        double variance = statistics.getVariance();
        double standardDeviation = statistics.getStandardDeviation();

        System.out.println("T ндв = " + mean);
        System.out.println("D = " + variance);
        System.out.println("σ = " + standardDeviation);

        double[] statisticDensity = calculateStatisticDensity();
        double[] statisticProbabilityOfFailure = ranges.stream()
                .mapToDouble(r -> getSortedCopyOfArray().indexOf(r[1]) / (double) sample.length)
                .toArray();
        double[] statisticProbabilityOfTroubleFreeOperation = calculateFailureFreeProbability(statisticProbabilityOfFailure);

        printBarGraphic("f*(t)", "", getRanges(), statisticDensity);
        printBarGraphic("Q*(t)", "", getRanges(), statisticProbabilityOfFailure);
        printBarGraphic("P*(t)", "", getRanges(), statisticProbabilityOfTroubleFreeOperation);

        AbstractRealDistribution exponentialDistribution = new ExponentialDistribution(mean);
        AbstractRealDistribution normalDistribution = new NormalDistribution(mean, standardDeviation);
        AbstractRealDistribution uniformRealDistribution = new UniformRealDistribution(statistics.getMin(), statistics.getMax());
        AbstractRealDistribution gammaDistribution = new GammaDistribution(Math.pow(mean / standardDeviation, 2), variance / mean);

        // start chi-square tests
        Map<Double, String> chiSquares = new HashMap<>();

        chiSquares.put(calculateChiSquare(calculateFailureProbability(exponentialDistribution), statisticProbabilityOfFailure), "exponential");
        chiSquares.put(calculateChiSquare(calculateFailureProbability(normalDistribution), statisticProbabilityOfFailure), "normal");
        chiSquares.put(calculateChiSquare(calculateFailureProbability(uniformRealDistribution), statisticProbabilityOfFailure), "uniform");
        chiSquares.put(calculateChiSquare(calculateFailureProbability(gammaDistribution), statisticProbabilityOfFailure), "gamma");

        chiSquares.entrySet().forEach(e -> System.out.println("ChiSquare ("  + e.getValue() + ") = " + e.getKey()));

        Map.Entry<Double, String> minChiSquare = chiSquares.entrySet().stream().min(Map.Entry.comparingByKey()).get();
        switch (minChiSquare.getValue()) {
            case "exponential" :
                buildDistributionProbabilityGraph(exponentialDistribution, minChiSquare.getValue());
                return;
            case "normal" :
                buildDistributionProbabilityGraph(normalDistribution, minChiSquare.getValue());
                return;
            case "uniform" :
                buildDistributionProbabilityGraph(uniformRealDistribution, minChiSquare.getValue());
                return;
            case "gamma" :
                buildDistributionProbabilityGraph(gammaDistribution, minChiSquare.getValue());
        }
    }

    private static void buildDistributionProbabilityGraph(AbstractRealDistribution distribution, String distributionName) {
        double[] probabilityOfFailure = calculateFailureProbability(distribution);
        double[] failureFreeProbability = calculateFailureFreeProbability(probabilityOfFailure);

        double[] density = ranges.stream()
                .mapToDouble(r -> distribution.density(r[1]))
                .toArray();

        double[] intensity = IntStream.range(0, density.length - 1)
                .mapToDouble(i -> density[i] / failureFreeProbability[i])
                .toArray();

        printBarGraphic("Q(t)", distributionName, getRanges(), probabilityOfFailure);
        printBarGraphic("P(t)", distributionName, getRanges(), failureFreeProbability);
        printBarGraphic("λ(t)", distributionName, getRanges(), intensity);
    }

    private static double[] calculateFailureProbability(AbstractRealDistribution distribution) {
        return ranges.stream()
                .mapToDouble(r -> distribution.cumulativeProbability(r[1]))
                .toArray();
    }

    private static double[] calculateFailureFreeProbability(double[] failureProbability) {
        return Arrays.stream(failureProbability)
                .map(t -> 1 - t)
                .toArray();
    }

    private static double calculateChiSquare(double[] expected, double[] observed) {
        double chiSquare = 0;

        for (int i = 0; i < expected.length; i++) {
            chiSquare += pow(expected[i] - observed[i], 2) / expected[i];
        }
        return chiSquare;
    }

    private static double[] calculateStatisticDensity() {
        int k = 10;
        double h = calculateSampleSize(k);
        List<double[]> density = getDensityRanges(k, h);
        ranges = getRangesBounds(density);
        return density.stream().mapToDouble(t -> t.length / (sample.length * h)).toArray();
    }

    private static double calculateSampleSize(int k) {
        double max = Arrays.stream(sample).max().getAsDouble();
        double min = Arrays.stream(sample).min().getAsDouble();
        return  (max - min) / (double) k;
    }

    private static List<double[]> getDensityRanges(int k, double h) {
        List<double[]> density = new ArrayList<>();
        double lower = Arrays.stream(sample).min().getAsDouble();
        while (density.size() != k) {
            double finalLower = lower;
            double[] range = Arrays.stream(sample)
                    .filter(t -> t >= finalLower && t < finalLower + h)
                    .toArray();

            density.add(range);
            lower += h;
        }

        return density;
    }

    private static List<double[]> getRangesBounds(List<double[]> density) {
        List<double[]> ranges = new ArrayList<>();
        density.forEach(Arrays::sort);

        IntStream.range(0, density.size())
                .forEach(i -> ranges.add(
                        density.get(i).length == 0 ? new double[]{density.get(i - 1)[density.get(i - 1).length - 1], density.get(i - 1)[density.get(i - 1).length - 1]}
                            : new double[]{density.get(i)[0], density.get(i)[density.get(i).length - 1]}));
        return ranges;
    }

    private static double[] getRanges() {
        int k = ranges.size();
        double h = calculateSampleSize(k);

        List<Double> ranges = new ArrayList<>();
        ranges.add(Arrays.stream(sample).min().getAsDouble());
        IntStream.range(1, k).forEach(l -> ranges.add(ranges.get(ranges.size() - 1) + h));
        return ranges.stream().mapToDouble(r -> r).toArray();
    }

    private static List<Double> getSortedCopyOfArray(){
        double[] copy = Arrays.copyOf(sample, sample.length);
        Arrays.sort(copy);
        return Arrays.stream(copy).boxed().collect(Collectors.toList());
    }
}
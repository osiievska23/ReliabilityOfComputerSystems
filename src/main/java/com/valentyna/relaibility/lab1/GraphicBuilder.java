package com.valentyna.relaibility.lab1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class GraphicBuilder {

    public static void printBarGraphic(String title, String distributionName, double[] x, double[] y) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < y.length; i++) {
            dataset.addValue(Double.valueOf(y[i]), distributionName, Double.valueOf(x[i]));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title, "x", "y", dataset,
                PlotOrientation.VERTICAL, true, true, true
        );

        createFrame(chart);
    }

    public static void createFrame(JFreeChart chart) {
        JFrame frame = new JFrame("ReliabilityOfComputerSystems");
        frame.getContentPane().add(new ChartPanel(chart));
        frame.setSize(500,500);
        frame.show();
    }
}

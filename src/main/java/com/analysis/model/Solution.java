package com.analysis.model;

import java.util.Arrays;

public class Solution {
    private final double[] values;
    private final double[] xPoints;
    private final int iterations;
    private final double residual;
    
    public Solution(double[] values, double[] xPoints, int iterations, double residual) {
        this.values = Arrays.copyOf(values, values.length);
        this.xPoints = Arrays.copyOf(xPoints, xPoints.length);
        this.iterations = iterations;
        this.residual = residual;
    }
    
    public double[] getValues() {
        return Arrays.copyOf(values, values.length);
    }
    
    public double[] getXPoints() {
        return Arrays.copyOf(xPoints, xPoints.length);
    }
    
    public int getIterations() {
        return iterations;
    }
    
    public double getResidual() {
        return residual;
    }
    
    public double getValue(int index) {
        return values[index];
    }
    
    public int size() {
        return values.length;
    }
    
    @Override
    public String toString() {
        return String.format("Solution[size=%d, iterations=%d, residual=%.2e]", 
                           values.length, iterations, residual);
    }
}
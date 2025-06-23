package com.analysis.model;

import com.analysis.function.MathFunction;

public class TestData<T> {
    private final String scenario;
    private final T inputData;
    private final MathFunction functionToTest;
    private final MathFunction testFunction;
    private final T expectedResult;
    private final double tolerance;
    private final String norm;
    
    public TestData(String scenario, T inputData, MathFunction functionToTest, 
                   MathFunction testFunction, T expectedResult, double tolerance, String norm) {
        this.scenario = scenario;
        this.inputData = inputData;
        this.functionToTest = functionToTest;
        this.testFunction = testFunction;
        this.expectedResult = expectedResult;
        this.tolerance = tolerance;
        this.norm = norm;
    }
    
    // Getters
    public String getScenario() { return scenario; }
    public T getInputData() { return inputData; }
    public MathFunction getFunctionToTest() { return functionToTest; }
    public MathFunction getTestFunction() { return testFunction; }
    public T getExpectedResult() { return expectedResult; }
    public double getTolerance() { return tolerance; }
    public String getNorm() { return norm; }
    
    @Override
    public String toString() {
        return String.format("TestData[%s, tol=%.2e]", scenario, tolerance);
    }
}
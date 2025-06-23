package com.analysis.model;

public class BoundaryConditions {
    private final double u0; // u(0)
    private final double u1; // u(1)
    
    public BoundaryConditions(double u0, double u1) {
        this.u0 = u0;
        this.u1 = u1;
    }
    
    public double getU0() {
        return u0;
    }
    
    public double getU1() {
        return u1;
    }
    
    @Override
    public String toString() {
        return String.format("BC(u(0)=%.3f, u(1)=%.3f)", u0, u1);
    }
}
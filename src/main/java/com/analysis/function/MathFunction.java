package com.analysis.function;

@FunctionalInterface
public interface MathFunction {
    double apply(double x);
    
    // Fonctions utilitaires
    static MathFunction constant(double c) {
        return x -> c;
    }
    
    static MathFunction linear(double a, double b) {
        return x -> a * x + b;
    }
    
    static MathFunction quadratic(double a, double b, double c) {
        return x -> a * x * x + b * x + c;
    }
    
    static MathFunction sin() {
        return Math::sin;
    }
    
    static MathFunction cos() {
        return Math::cos;
    }
    
    static MathFunction exp() {
        return Math::exp;
    }
}
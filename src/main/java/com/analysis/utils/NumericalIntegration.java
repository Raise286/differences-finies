package com.analysis.utils;

import com.analysis.function.MathFunction;

public class NumericalIntegration {
    
    // Intégration par la règle des trapèzes
    public static double trapezoidalRule(MathFunction f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0.5 * (f.apply(a) + f.apply(b));
        
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            sum += f.apply(x);
        }
        
        return h * sum;
    }
    
    // Intégration par la règle de Simpson
    public static double simpsonRule(MathFunction f, double a, double b, int n) {
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n doit être pair pour la règle de Simpson");
        }
        
        double h = (b - a) / n;
        double sum = f.apply(a) + f.apply(b);
        
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            if (i % 2 == 0) {
                sum += 2 * f.apply(x);
            } else {
                sum += 4 * f.apply(x);
            }
        }
        
        return (h / 3) * sum;
    }
    
    // Calcul de la norme L2 d'une fonction
    public static double l2Norm(MathFunction f, double a, double b, int n) {
        MathFunction f2 = x -> f.apply(x) * f.apply(x);
        return Math.sqrt(trapezoidalRule(f2, a, b, n));
    }
}
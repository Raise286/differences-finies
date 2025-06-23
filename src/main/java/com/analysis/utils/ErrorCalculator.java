package com.analysis.utils;

import com.analysis.function.MathFunction;
import com.analysis.model.Solution;

public class ErrorCalculator {
    
    // Calcul de l'erreur en norme L2 par intégration numérique
    public static double calculateL2Error(Solution numericalSolution, MathFunction exactSolution) {
        double[] x = numericalSolution.getXPoints();
        double[] u_num = numericalSolution.getValues();
        
        double error = 0.0;
        double h = x[1] - x[0];
        
        // Intégration par la règle des trapèzes
        for (int i = 0; i < x.length - 1; i++) {
            double x1 = x[i];
            double x2 = x[i + 1];
            double u_exact1 = exactSolution.apply(x1);
            double u_exact2 = exactSolution.apply(x2);
            
            double diff1 = u_num[i] - u_exact1;
            double diff2 = u_num[i + 1] - u_exact2;
            
            error += 0.5 * h * (diff1 * diff1 + diff2 * diff2);
        }
        
        return Math.sqrt(error);
    }
    
    // Calcul de l'erreur en norme infinie
    public static double calculateMaxError(Solution numericalSolution, MathFunction exactSolution) {
        double[] x = numericalSolution.getXPoints();
        double[] u_num = numericalSolution.getValues();
        
        double maxError = 0.0;
        for (int i = 0; i < x.length; i++) {
            double error = Math.abs(u_num[i] - exactSolution.apply(x[i]));
            maxError = Math.max(maxError, error);
        }
        
        return maxError;
    }
    
    // Calcul de l'ordre de convergence numérique
    public static double calculateConvergenceOrder(double[] errors, double[] meshSizes) {
        if (errors.length != meshSizes.length || errors.length < 2) {
            throw new IllegalArgumentException("Besoin d'au moins 2 points pour calculer l'ordre");
        }
        
        double sumLogRatio = 0.0;
        double sumLogMeshRatio = 0.0;
        
        for (int i = 1; i < errors.length; i++) {
            if (errors[i] <= 0 || errors[i-1] <= 0) continue;
            if (meshSizes[i] <= 0 || meshSizes[i-1] <= 0) continue;
            
            double logErrorRatio = Math.log(errors[i] / errors[i-1]);
            double logMeshRatio = Math.log(meshSizes[i] / meshSizes[i-1]);
            
            sumLogRatio += logErrorRatio;
            sumLogMeshRatio += logMeshRatio;
        }
        
        return sumLogRatio / sumLogMeshRatio;
    }
}
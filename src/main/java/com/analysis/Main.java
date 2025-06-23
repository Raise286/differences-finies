package com.analysis;


import com.analysis.function.MathFunction;
import com.analysis.model.BoundaryConditions;
import com.analysis.model.Solution;
import com.analysis.solver.DirectSolver;
import com.analysis.solver.GaussSeidelSolver;
import com.analysis.utils.ErrorCalculator;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Solveur d'Équations Différentielles par Différences Finies ===\n");
        
        // Exemple: -u'' = 4π²sin(2πx) avec u(0) = 0, u(1) = 0
        // Solution exacte: u(x) = sin(2πx)
        MathFunction f = x -> 4 * Math.PI * Math.PI * Math.sin(2 * Math.PI * x);
        MathFunction exactSolution = x -> Math.sin(2 * Math.PI * x);
        BoundaryConditions bc = new BoundaryConditions(0.0, 0.0);
        
        int n = 50; // nombre de points intérieurs
        
        // Résolution directe
        System.out.println("=== Méthode Directe ===");
        DirectSolver directSolver = new DirectSolver(n, f, bc);
        Solution directSolution = directSolver.solve();
        System.out.println("Solution directe: " + directSolution);
        
        double directError = ErrorCalculator.calculateL2Error(directSolution, exactSolution);
        System.out.printf("Erreur L2: %.6e\n\n", directError);
        
        // Résolution par Gauss-Seidel
        System.out.println("=== Méthode Gauss-Seidel ===");
        GaussSeidelSolver gsSolver = new GaussSeidelSolver(n, f, bc);
        Solution gsSolution = gsSolver.solve();
        System.out.println("Solution Gauss-Seidel: " + gsSolution);
        
        double gsError = ErrorCalculator.calculateL2Error(gsSolution, exactSolution);
        System.out.printf("Erreur L2: %.6e\n\n", gsError);
        
        // Test de convergence
        System.out.println("=== Test de Convergence ===");
        testConvergence(f, exactSolution, bc);
    }
    
    private static void testConvergence(MathFunction f, MathFunction exactSolution, BoundaryConditions bc) {
        int[] nValues = {10, 20, 40, 80, 160};
        double[] errors = new double[nValues.length];
        double[] meshSizes = new double[nValues.length];
        
        System.out.println("n\th\tErreur L2\tOrdre");
        System.out.println("----------------------------------------");
        
        for (int i = 0; i < nValues.length; i++) {
            int n = nValues[i];
            DirectSolver solver = new DirectSolver(n, f, bc);
            Solution solution = solver.solve();
            
            double error = ErrorCalculator.calculateL2Error(solution, exactSolution);
            double h = 1.0 / (n + 1);
            
            errors[i] = error;
            meshSizes[i] = h;
            
            if (i > 0) {
                double order = Math.log(errors[i] / errors[i-1]) / Math.log(meshSizes[i] / meshSizes[i-1]);
                System.out.printf("%d\t%.4f\t%.2e\t%.2f\n", n, h, error, order);
            } else {
                System.out.printf("%d\t%.4f\t%.2e\t-\n", n, h, error);
            }
        }
        
        if (errors.length >= 2) {
            double overallOrder = ErrorCalculator.calculateConvergenceOrder(errors, meshSizes);
            System.out.printf("\nOrdre de convergence global: %.2f\n", overallOrder);
        }
    }
}
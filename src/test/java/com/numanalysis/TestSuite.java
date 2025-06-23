package com.numanalysis;


import com.analysis.function.MathFunction;
import com.analysis.model.BoundaryConditions;
import com.analysis.model.Solution;
import com.analysis.model.TestData;
import com.analysis.solver.DirectSolver;
import com.analysis.solver.GaussSeidelSolver;
import com.analysis.utils.ErrorCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suite de Tests Complète")
class TestSuite {
    
    private List<TestData<Double>> testDataList;
    
    @BeforeEach
    void setUp() {
        testDataList = new ArrayList<>();
        initializeTestData();
    }
    
    private void initializeTestData() {
        // Test Data 1: Fonction constante
        TestData<Double> td1 = new TestData<Double>(
            "Fonction constante f=2",
            2.0, // nombre de points
            MathFunction.constant(2.0),
            x -> x * (1 - x), // solution exacte approchée
             0.01, // erreur attendue
            1e-3,
            "L2"
        );
        testDataList.add(td1);
        
        // Test Data 2: Fonction sinusoïdale
        TestData<Double> td2 = new TestData<Double>(
            "Fonction sinusoïdale",
            50.0,
            x -> 4 * Math.PI * Math.PI * Math.sin(2 * Math.PI * x),
            x -> Math.sin(2 * Math.PI * x),
            1e-4,
            1e-3,
            "L2"
        );
        testDataList.add(td2);
        
        // Test Data 3: Fonction polynomiale
        TestData<Double> td3 = new TestData<Double>(
            "Fonction polynomiale f=6x",
            30.0,
            MathFunction.linear(6, 0),
            x -> x * x * x - x, // solution exacte pour f = 6x avec BC(0,0)
            1e-6,
            1e-5,
            "Max"
        );
        testDataList.add(td3);
    }
    
    @Test
    @DisplayName("Test Suite Complète - Solveur Direct")
    void testDirectSolverSuite() {
        System.out.println("\n=== Tests Suite - Solveur Direct ===");
        
        for (TestData<Double> testData : testDataList) {
            System.out.println("Test: " + testData.getScenario());
            
            BoundaryConditions bc = new BoundaryConditions(0.0, 0.0);
            DirectSolver solver = new DirectSolver(
                (int)Math.round(testData.getInputData()), 
                testData.getFunctionToTest(), 
                bc
            );
            
            Solution solution = solver.solve();
            
            double error;
            if ("L2".equals(testData.getNorm())) {
                error = ErrorCalculator.calculateL2Error(solution, testData.getTestFunction());
            } else {
                error = ErrorCalculator.calculateMaxError(solution, testData.getTestFunction());
            }
            
            System.out.printf("  Erreur calculée: %.2e (seuil: %.2e)\n", 
                            error, testData.getTolerance());
            
            assertTrue(error < testData.getTolerance(), 
                      "Test " + testData.getScenario() + " échoué");
        }
    }
    
    @Test
    @DisplayName("Test Suite Complète - Solveur Gauss-Seidel")
    void testGaussSeidelSolverSuite() {
        System.out.println("\n=== Tests Suite - Solveur Gauss-Seidel ===");
        
        for (TestData<Double> testData : testDataList) {
            System.out.println("Test: " + testData.getScenario());
            
            BoundaryConditions bc = new BoundaryConditions(0.0, 0.0);
            GaussSeidelSolver solver = new GaussSeidelSolver(
                (int)Math.round(testData.getInputData()), 
                testData.getFunctionToTest(), 
                bc,
                1e-10, 5000, 1.0
            );
            
            Solution solution = solver.solve();
            
            double error;
            if ("L2".equals(testData.getNorm())) {
                error = ErrorCalculator.calculateL2Error(solution, testData.getTestFunction());
            } else {
                error = ErrorCalculator.calculateMaxError(solution, testData.getTestFunction());
            }
            
            System.out.printf("  Erreur calculée: %.2e (seuil: %.2e), Itérations: %d\n", 
                            error, testData.getTolerance(), solution.getIterations());
            
            assertTrue(error < testData.getTolerance() * 10, // Plus de tolérance pour itératif
                      "Test " + testData.getScenario() + " échoué");
            assertTrue(solution.getIterations() < 5000, "Trop d'itérations");
        }
    }
    
    @Test
    @DisplayName("Test Comparaison Solveurs")
    void testSolverComparison() {
        System.out.println("\n=== Comparaison des Solveurs ===");
        
        MathFunction f = x -> Math.PI * Math.PI * Math.sin(Math.PI * x);
        MathFunction exactSol = x -> Math.sin(Math.PI * x);
        BoundaryConditions bc = new BoundaryConditions(0.0, 0.0);
        int n = 40;
        
        // Solveur direct
        DirectSolver directSolver = new DirectSolver(n, f, bc);
        Solution directSolution = directSolver.solve();
        double directError = ErrorCalculator.calculateL2Error(directSolution, exactSol);
        
        // Solveur Gauss-Seidel
        GaussSeidelSolver gsSolver = new GaussSeidelSolver(n, f, bc);
        Solution gsSolution = gsSolver.solve();
        double gsError = ErrorCalculator.calculateL2Error(gsSolution, exactSol);
        
        System.out.printf("Erreur Direct: %.2e\n", directError);
        System.out.printf("Erreur Gauss-Seidel: %.2e (Itérations: %d)\n", 
                         gsError, gsSolution.getIterations());
        
        // Les deux méthodes devraient donner des résultats similaires
        assertTrue(Math.abs(directError - gsError) < 1e-4, 
                  "Les deux solveurs devraient donner des résultats similaires");
    }
    
    @Test
    @DisplayName("Test Ordre de Convergence Global")
    void testGlobalConvergenceOrder() {
        System.out.println("\n=== Test Ordre de Convergence ===");
        
        MathFunction f = x -> 2.0;
        MathFunction exactSol = x -> x * (1 - x); // solution analytique pour f=2
        BoundaryConditions bc = new BoundaryConditions(0.0, 0.0);
        
        int[] nValues = {10, 20, 40, 80};
        double[] errors = new double[nValues.length];
        double[] meshSizes = new double[nValues.length];
        
        for (int i = 0; i < nValues.length; i++) {
            DirectSolver solver = new DirectSolver(nValues[i], f, bc);
            Solution solution = solver.solve();
            
            errors[i] = ErrorCalculator.calculateL2Error(solution, exactSol);
            meshSizes[i] = 1.0 / (nValues[i] + 1);
            
            System.out.printf("n=%d, h=%.4f, erreur=%.2e\n", 
                            nValues[i], meshSizes[i], errors[i]);
        }
        
        double order = ErrorCalculator.calculateConvergenceOrder(errors, meshSizes);
        System.out.printf("Ordre de convergence: %.2f\n", order);
        
        // Pour les différences finies d'ordre 2, on s'attend à un ordre proche de 2
        assertTrue(order > 1.5 && order < 2.5, 
                  "L'ordre de convergence devrait être proche de 2");
    }
    
    @Test
    @DisplayName("Test Performance et Robustesse")
    void testPerformanceAndRobustness() {
        System.out.println("\n=== Test Performance et Robustesse ===");
        
        // Test avec différentes tailles de problème
        int[] problemSizes = {50, 100, 200};
        
        for (int n : problemSizes) {
            long startTime = System.nanoTime();
            
            MathFunction f = MathFunction.constant(1.0);
            BoundaryConditions bc = new BoundaryConditions(0.0, 1.0);
            DirectSolver solver = new DirectSolver(n, f, bc);
            Solution solution = solver.solve();
            
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1e6; // en millisecondes
            
            System.out.printf("n=%d: temps=%.2f ms, résidu=%.2e\n", 
                            n, duration, solution.getResidual());
            
            assertNotNull(solution);
            assertTrue(solution.getResidual() < 1e-10);
            assertTrue(duration < 1000, "Le calcul ne devrait pas prendre plus d'1 seconde");
        }
    }
}

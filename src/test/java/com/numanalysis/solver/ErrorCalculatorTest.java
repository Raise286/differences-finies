package com.numanalysis.solver;


import com.analysis.function.MathFunction;
import com.analysis.model.Solution;
import com.analysis.utils.ErrorCalculator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ErrorCalculatorTest {
    
    @Test
    @DisplayName("Test calcul erreur L2 avec solution exacte")
    void testL2ErrorWithExactSolution() {
        // Solution numérique = solution exacte
        double[] values = {0.0, 0.5, 1.0, 0.5, 0.0};
        double[] xPoints = {0.0, 0.25, 0.5, 0.75, 1.0};
        Solution solution = new Solution(values, xPoints, 1, 0.0);
        
        MathFunction exactSolution = x -> {
            if (x <= 0.5) return 2 * x;
            else return 2 * (1 - x);
        };
        
        double error = ErrorCalculator.calculateL2Error(solution, exactSolution);
        assertTrue(error < 1e-10, "L'erreur devrait être très petite pour une solution exacte");
    }
    
    @Test
    @DisplayName("Test calcul erreur maximale")
    void testMaxError() {
        double[] values = {0.0, 0.1, 0.2, 0.1, 0.0};
        double[] xPoints = {0.0, 0.25, 0.5, 0.75, 1.0};
        Solution solution = new Solution(values, xPoints, 1, 0.0);
        
        MathFunction exactSolution = MathFunction.constant(0.0);
        
        double maxError = ErrorCalculator.calculateMaxError(solution, exactSolution);
        assertEquals(0.2, maxError, 1e-10);
    }
    
    @Test
    @DisplayName("Test calcul ordre de convergence")
    void testConvergenceOrder() {
        // Erreurs simulées pour ordre 2 (h² convergence)
        double[] errors = {0.04, 0.01, 0.0025, 0.000625};
        double[] meshSizes = {0.2, 0.1, 0.05, 0.025};
        
        double order = ErrorCalculator.calculateConvergenceOrder(errors, meshSizes);
        assertEquals(2.0, order, 0.1, "L'ordre devrait être proche de 2");
    }
    
    @Test
    @DisplayName("Test ordre de convergence avec données insuffisantes")
    void testConvergenceOrderInsufficientData() {
        double[] errors = {0.1};
        double[] meshSizes = {0.1};
        
        assertThrows(IllegalArgumentException.class, () -> {
            ErrorCalculator.calculateConvergenceOrder(errors, meshSizes);
        });
    }
    
    @Test
    @DisplayName("Test robustesse avec valeurs nulles")
    void testRobustnessWithZeroValues() {
        double[] errors = {0.1, 0.0, 0.01};
        double[] meshSizes = {0.2, 0.1, 0.05};
        
        // Ne devrait pas lancer d'exception
        assertDoesNotThrow(() -> {
            ErrorCalculator.calculateConvergenceOrder(errors, meshSizes);
        });
    }
}
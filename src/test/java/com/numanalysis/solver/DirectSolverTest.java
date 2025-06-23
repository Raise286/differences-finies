package com.numanalysis.solver;


import com.analysis.function.MathFunction;
import com.analysis.model.BoundaryConditions;
import com.analysis.model.Solution;
import com.analysis.solver.DirectSolver;
import com.analysis.utils.ErrorCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DirectSolverTest {
    
    @Test
    @DisplayName("Test solution exacte pour f=0")
    void testZeroFunction() {
        MathFunction f = MathFunction.constant(0);
        BoundaryConditions bc = new BoundaryConditions(1.0, 2.0);
        DirectSolver solver = new DirectSolver(10, f, bc);
        
        Solution solution = solver.solve();
        
        assertNotNull(solution);
        assertEquals(1.0, solution.getValue(0), 1e-10);
        assertEquals(2.0, solution.getValue(solution.size() - 1), 1e-10);
        
        // Pour f=0, la solution est linéaire
        double[] x = solution.getXPoints();
        double[] u = solution.getValues();
        for (int i = 0; i < u.length; i++) {
            double expected = 1.0 + (2.0 - 1.0) * x[i];
            assertEquals(expected, u[i], 1e-10);
        }
    }
    
    @Test
    @DisplayName("Test solution analytique connue")
    void testKnownAnalyticalSolution() {
        // -u'' = 4π²sin(2πx), u(0) = 0, u(1) = 0
        // Solution exacte: u(x) = sin(2πx)
        MathFunction f = x -> 4 * Math.PI * Math.PI * Math.sin(2 * Math.PI * x);
        MathFunction exactSolution = x -> Math.sin(2 * Math.PI * x);
        BoundaryConditions bc = new BoundaryConditions(0.0, 0.0);
        
        DirectSolver solver = new DirectSolver(50, f, bc);
        Solution solution = solver.solve();
        
        double error = ErrorCalculator.calculateL2Error(solution, exactSolution);
        assertTrue(error < 1e-3, "L'erreur L2 devrait être inférieure à 1e-3");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {5, 10, 20, 50})
    @DisplayName("Test convergence avec différentes tailles de maillage")
    void testConvergenceWithDifferentMeshSizes(int n) {
        MathFunction f = MathFunction.quadratic(2, 0, 0); // f(x) = 2x²
        MathFunction exactSolution = x -> x * x * (1 - x) / 3; // solution exacte calculée
        BoundaryConditions bc = new BoundaryConditions(0.0, 0.0);
        
        DirectSolver solver = new DirectSolver(n, f, bc);
        Solution solution = solver.solve();
        
        assertNotNull(solution);
        assertTrue(solution.size() > 0);
        
        double error = ErrorCalculator.calculateMaxError(solution, exactSolution);
        assertTrue(error < 0.1, "L'erreur maximale devrait être raisonnable");
    }
    
    @Test
    @DisplayName("Test conditions aux limites non nulles")
    void testNonZeroBoundaryConditions() {
        MathFunction f = MathFunction.constant(2.0);
        BoundaryConditions bc = new BoundaryConditions(1.0, 3.0);
        DirectSolver solver = new DirectSolver(20, f, bc);
        
        Solution solution = solver.solve();
        
        assertEquals(1.0, solution.getValue(0), 1e-10);
        assertEquals(3.0, solution.getValue(solution.size() - 1), 1e-10);
        assertTrue(solution.getResidual() < 1e-10);
    }
}
package com.numanalysis.solver;

import com.analysis.function.MathFunction;
import com.analysis.model.BoundaryConditions;
import com.analysis.model.Solution;
import com.analysis.solver.DirectSolver;
import com.analysis.solver.GaussSeidelSolver;
import com.analysis.utils.ErrorCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class GaussSeidelSolverTest {
    
    @Test
    @DisplayName("Test convergence Gauss-Seidel pour problème simple")
    void testSimpleConvergence() {
        MathFunction f = MathFunction.constant(2.0);
        BoundaryConditions bc = new BoundaryConditions(0.0, 0.0);
        GaussSeidelSolver solver = new GaussSeidelSolver(20, f, bc, 1e-8, 1000, 1.0);
        
        Solution solution = solver.solve();
        
        assertNotNull(solution);
        assertEquals(0.0, solution.getValue(0), 1e-10);
        assertEquals(0.0, solution.getValue(solution.size() - 1), 1e-10);
        assertTrue(solution.getIterations() > 0);
        assertTrue(solution.getResidual() < 1e-6);
    }
    
    @Test
    @DisplayName("Test comparaison avec solution directe")
    void testComparisonWithDirectSolver() {
        MathFunction f = x -> 4 * Math.PI * Math.PI * Math.sin(2 * Math.PI * x);
        BoundaryConditions bc = new BoundaryConditions(0.0, 0.0);
        
        DirectSolver directSolver = new DirectSolver(30, f, bc);
        GaussSeidelSolver gsSolver = new GaussSeidelSolver(30, f, bc, 1e-10, 5000, 1.0);
        
        Solution directSolution = directSolver.solve();
        Solution gsSolution = gsSolver.solve();
        
        // Comparer les solutions
        double[] directValues = directSolution.getValues();
        double[] gsValues = gsSolution.getValues();
        
        for (int i = 0; i < directValues.length; i++) {
            assertEquals(directValues[i], gsValues[i], 1e-6, 
                        "Solutions should be similar at point " + i);
        }
    }
    
    @Test
    @DisplayName("Test avec facteur de relaxation SOR")
    void testSORRelaxation() {
        MathFunction f = MathFunction.constant(1.0);
        BoundaryConditions bc = new BoundaryConditions(0.0, 1.0);
        
        // Test avec différents facteurs de relaxation
        double[] omegas = {0.8, 1.0, 1.2, 1.5};
        
        for (double omega : omegas) {
            GaussSeidelSolver solver = GaussSeidelSolver.withSOR(20, f, bc, omega);
            Solution solution = solver.solve();
            
            assertNotNull(solution);
            assertTrue(solution.getIterations() > 0);
            assertTrue(solution.getResidual() < 1e-6);
        }
    }
    
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @DisplayName("Test que l'algorithme converge en temps raisonnable")
    void testConvergenceTime() {
        MathFunction f = MathFunction.quadratic(1, 0, 0);
        BoundaryConditions bc = new BoundaryConditions(0.0, 0.0);
        GaussSeidelSolver solver = new GaussSeidelSolver(50, f, bc);
        
        Solution solution = solver.solve();
        
        assertNotNull(solution);
        assertTrue(solution.getIterations() < 10000, "Trop d'itérations nécessaires");
    }
    
    @Test
    @DisplayName("Test robustesse avec conditions aux limites extrêmes")
    void testExtremeConditions() {
        MathFunction f = MathFunction.constant(100.0);
        BoundaryConditions bc = new BoundaryConditions(-50.0, 50.0);
        GaussSeidelSolver solver = new GaussSeidelSolver(15, f, bc);
        
        Solution solution = solver.solve();
        
        assertNotNull(solution);
        assertEquals(-50.0, solution.getValue(0), 1e-10);
        assertEquals(50.0, solution.getValue(solution.size() - 1), 1e-10);
        assertTrue(solution.getResidual() < 1e-5);
    }
}
package com.analysis.solver;

import com.analysis.function.MathFunction;
import com.analysis.model.BoundaryConditions;
import com.analysis.model.Solution;

public class GaussSeidelSolver extends FiniteDifferenceSolver {
    private final double tolerance;
    private final int maxIterations;
    private final double relaxationFactor;
    
    public GaussSeidelSolver(int n, MathFunction f, BoundaryConditions bc) {
        this(n, f, bc, 1e-10, 10000, 1.0);
    }
    
    public GaussSeidelSolver(int n, MathFunction f, BoundaryConditions bc, 
                           double tolerance, int maxIterations, double relaxationFactor) {
        super(n, f, bc);
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
        this.relaxationFactor = relaxationFactor;
    }
    
    @Override
    public Solution solve() {
        double[] u = new double[n + 2];
        u[0] = bc.getU0();
        u[n + 1] = bc.getU1();
        
        // Initialisation avec interpolation linéaire
        for (int i = 1; i <= n; i++) {
            u[i] = bc.getU0() + (bc.getU1() - bc.getU0()) * i * h;
        }
        
        double[] b = createRightHandSide();
        int iterations = 0;
        double error = Double.MAX_VALUE;
        
        while (error > tolerance && iterations < maxIterations) {
            double maxChange = 0.0;
            
            for (int i = 1; i <= n; i++) {
                double oldValue = u[i];
                double newValue = 0.5 * (u[i - 1] + u[i + 1] - h * h * f.apply(i * h));
                
                // Relaxation
                u[i] = (1 - relaxationFactor) * oldValue + relaxationFactor * newValue;
                
                double change = Math.abs(u[i] - oldValue);
                maxChange = Math.max(maxChange, change);
            }
            
            error = maxChange;
            iterations++;
        }
        
        double[] x = createXPoints();
        double residual = calculateResidual(u);
        
        return new Solution(u, x, iterations, residual);
    }
    
    // Version avec relaxation successive (SOR)
    public static GaussSeidelSolver withSOR(int n, MathFunction f, BoundaryConditions bc, 
                                           double omega) {
        return new GaussSeidelSolver(n, f, bc, 1e-10, 10000, omega);
    }
}
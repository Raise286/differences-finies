package com.analysis.solver;

import com.analysis.function.MathFunction;
import com.analysis.model.BoundaryConditions;
import com.analysis.model.Solution;

public abstract class FiniteDifferenceSolver {
    protected final int n; // nombre de points intérieurs
    protected final double h; // pas de discrétisation
    protected final MathFunction f; // fonction f dans -u'' = f
    protected final BoundaryConditions bc;
    
    public FiniteDifferenceSolver(int n, MathFunction f, BoundaryConditions bc) {
        this.n = n;
        this.h = 1.0 / (n + 1);
        this.f = f;
        this.bc = bc;
    }
    
    public abstract Solution solve();
    
    protected double[] createXPoints() {
        double[] x = new double[n + 2];
        for (int i = 0; i <= n + 1; i++) {
            x[i] = i * h;
        }
        return x;
    }
    
    protected double[] createRightHandSide() {
        double[] b = new double[n];
        for (int i = 0; i < n; i++) {
            double xi = (i + 1) * h;
            b[i] = h * h * f.apply(xi);
        }
        // Conditions aux limites
        b[0] += bc.getU0();
        b[n - 1] += bc.getU1();
        return b;
    }
    
    protected double calculateResidual(double[] u) {
        double maxResidual = 0.0;
        for (int i = 1; i < u.length - 1; i++) {
            double xi = i * h;
            double residual = Math.abs(-(u[i - 1] - 2 * u[i] + u[i + 1]) / (h * h) - f.apply(xi));
            maxResidual = Math.max(maxResidual, residual);
        }
        return maxResidual;
    }
}
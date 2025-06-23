package com.analysis.solver;

import com.analysis.function.MathFunction;
import com.analysis.model.BoundaryConditions;
import com.analysis.model.Solution;

public class DirectSolver extends FiniteDifferenceSolver {
    
    public DirectSolver(int n, MathFunction f, BoundaryConditions bc) {
        super(n, f, bc);
    }
    
    @Override
    public Solution solve() {
        // Matrice tridiagonale: -1, 2, -1
        double[] diagonal = new double[n];
        double[] upperDiagonal = new double[n - 1];
        double[] lowerDiagonal = new double[n - 1];
        double[] b = createRightHandSide();
        
        // Remplir la matrice tridiagonale
        for (int i = 0; i < n; i++) {
            diagonal[i] = 2.0;
        }
        for (int i = 0; i < n - 1; i++) {
            upperDiagonal[i] = -1.0;
            lowerDiagonal[i] = -1.0;
        }
        
        // Résolution par algorithme de Thomas
        double[] u_interior = solveTridiagonal(lowerDiagonal, diagonal, upperDiagonal, b);
        
        // Construire la solution complète avec conditions aux limites
        double[] u = new double[n + 2];
        u[0] = bc.getU0();
        System.arraycopy(u_interior, 0, u, 1, n);
        u[n + 1] = bc.getU1();
        
        double[] x = createXPoints();
        double residual = calculateResidual(u);
        
        return new Solution(u, x, 1, residual);
    }
    
    private double[] solveTridiagonal(double[] a, double[] b, double[] c, double[] d) {
        int n = d.length;
        double[] x = new double[n];
        double[] cp = new double[n - 1];
        double[] dp = new double[n];
        
        // Forward sweep
        dp[0] = d[0] / b[0];
        for (int i = 0; i < n - 1; i++) {
            cp[i] = c[i] / b[i];
            b[i + 1] = b[i + 1] - a[i] * cp[i];
            dp[i + 1] = (d[i + 1] - a[i] * dp[i]) / b[i + 1];
        }
        
        // Back substitution
        x[n - 1] = dp[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            x[i] = dp[i] - cp[i] * x[i + 1];
        }
        
        return x;
    }
}
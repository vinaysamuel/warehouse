
	/*************************************************************************
	 *  Compilation:  javac Simplex.java
	 *  Execution:    java Simplex
	 *
	 *  Given an M-by-N matrix A, an M-length vector b, and an
	 *  N-length vector c, solve the  LP { min cx : Ax <= b, x >= 0 }.
	 *  Assumes that b >= 0 so that x = 0 is a basic feasible solution.
	 *
	 *  Creates an (M+1)-by-(N+M+1) simplex tableaux with the 
	 *  RHS in column M+N, the objective function in row M, and
	 *  slack variables in columns M through M+N-1.
	 *
	 *************************************************************************/

public class SimplexSolver {

	    private static final float EPSILON = 1.0E-10f;
	    private float[][] a;   // Tableau
	    private int M;          // number of constraints
	    private int N;          // number of original variables

	    private int[] basis;    // basis[i] = basic variable corresponding to row i
	                            // only needed to print out solution, not book
	    private float [] primal;
	    private float value;

	    // sets up the simplex tableau
	    public SimplexSolver(float[][] A, float[] b, float[] c) {
	        M = b.length;
	        N = c.length;
	        a = new float[M+1][N+M+1];
	        for (int i = 0; i < M; i++)
	            for (int j = 0; j < N; j++)
	                a[i][j] = A[i][j];
	        A = null;
	        for (int i = 0; i < M; i++) a[i][N+i] = 1.0f;
	        for (int j = 0; j < N; j++) a[M][j]   = c[j];
	        c = null;
	        for (int i = 0; i < M; i++) a[i][M+N] = b[i];
	        b = null;
	        basis = new int[M];
	        for (int i = 0; i < M; i++) basis[i] = N + i;

	        solve();
	        
	        primal = primal_soln();
	        value = value_soln();
	        // check optimality conditions
	        //assert check(A, b, c);
	        a = null;
	    }

	    // run simplex algorithm starting from initial BFS
	    private void solve() {
	        while (true) {

	            // find entering column q
	            int q = dantzig();
	            if (q == -1) break;  // optimal

	            // find leaving row p
	            int p = minRatioRule(q);
	            if (p == -1) throw new ArithmeticException("Linear program is unbounded");

	            // pivot
	            pivot(p, q);

	            // update basis
	            basis[p] = q;
	        }
	    }

	    // lowest index of a non-basic column with a negative cost
	    private int bland() {
	        for (int j = 0; j < M + N; j++)
	            if (a[M][j] < 0) return j;
	        return -1;  // optimal
	    }

	   // index of a non-basic column with most negative cost
	    private int dantzig() {
	        int q = 0;
	        for (int j = 1; j < M + N; j++)
	            if (a[M][j] < a[M][q]) q = j;

	        if (a[M][q] >= 0) return -1;  // optimal
	        else return q;
	    }

	    // find row p using min ratio rule (-1 if no such row)
	    private int minRatioRule(int q) {
	        int p = -1;
	        for (int i = 0; i < M; i++) {
	            if (a[i][q] <= 0) continue;
	            else if (p == -1) p = i;
	            else if ((a[i][M+N] / a[i][q]) < (a[p][M+N] / a[p][q])) p = i;
	        }
	        return p;
	    }

	    // pivot on entry (p, q) using Gauss-Jordan elimination
	    private void pivot(int p, int q) {

	        // everything but row p and column q
	        for (int i = 0; i <= M; i++)
	            for (int j = 0; j <= M + N; j++)
	                if (i != p && j != q) a[i][j] -= a[p][j] * a[i][q] / a[p][q];

	        // zero out column q
	        for (int i = 0; i <= M; i++)
	            if (i != p) a[i][q] = 0.0f;

	        // scale row p
	        for (int j = 0; j <= M + N; j++)
	            if (j != q) a[p][j] /= a[p][q];
	        a[p][q] = 1.0f;
	    }

	    // return optimal objective value
	    public float value_soln() {
	        return a[M][M+N];
	    }
	    public float value() {
	        return value;
	    }
	    
	    // return primal solution vector
	    public float[] primal_soln() {
	        float[] x = new float[N];
	        for (int i = 0; i < M; i++)
	            if (basis[i] < N) x[basis[i]] = a[i][M+N];
	        return x;
	    }

	    public float[] primal() {
	        return primal;
	    }
	    
	    // return dual solution vector
	    public float[] dual() {
	        float[] y = new float[M];
	        for (int i = 0; i < M; i++)
	            y[i] = -a[M][N+i];
	        return y;
	    }


	    // is the solution primal feasible?
	    private boolean isPrimalFeasible(float[][] A, float[] b) {
	        float[] x = primal();

	        // check that x >= 0
	        for (int j = 0; j < x.length; j++) {
	            if (x[j] < 0.0) {
	                System.out.println("x[" + j + "] = " + x[j] + " is negative");
	                return false;
	            }
	        }

	        // check that Ax <= b
	        for (int i = 0; i < M; i++) {
	            float sum = 0.0f;
	            for (int j = 0; j < N; j++) {
	                sum += A[i][j] * x[j];
	            }
	            if (sum > b[i] + EPSILON) {
	                System.out.println("not primal feasible");
	                System.out.println("b[" + i + "] = " + b[i] + ", sum = " + sum);
	                return false;
	            }
	        }
	        return true;
	    }

	    // is the solution dual feasible?
	    private boolean isDualFeasible(float[][] A, float[] c) {
	        float[] y = dual();

	        // check that y >= 0
	        for (int i = 0; i < y.length; i++) {
	            if (y[i] < 0.0) {
	                System.out.println("y[" + i + "] = " + y[i] + " is negative");
	                return false;
	            }
	        }

	        // check that yA >= c
	        for (int j = 0; j < N; j++) {
	            float sum = 0.0f;
	            for (int i = 0; i < M; i++) {
	                sum += A[i][j] * y[i];
	            }
	            if (sum < c[j] - EPSILON) {
	                System.out.println("not dual feasible");
	                System.out.println("c[" + j + "] = " + c[j] + ", sum = " + sum);
	                return false;
	            }
	        }
	        return true;
	    }

	    // check that optimal value = cx = yb
	    private boolean isOptimal(float[] b, float[] c) {
	        float[] x = primal();
	        float[] y = dual();
	        float value = value();

	        // check that value = cx = yb
	        float value1 = 0.0f;
	        for (int j = 0; j < x.length; j++)
	            value1 += c[j] * x[j];
	        float value2 = 0.0f;
	        for (int i = 0; i < y.length; i++)
	            value2 += y[i] * b[i];
	        if (Math.abs(value - value1) > EPSILON || Math.abs(value - value2) > EPSILON) {
	            System.out.println("value = " + value + ", cx = " + value1 + ", yb = " + value2);
	            return false;
	        }

	        return true;
	    }

	    private boolean check(float[][]A, float[] b, float[] c) {
	        return isPrimalFeasible(A, b) && isDualFeasible(A, c) && isOptimal(b, c);
	    }

	    // print tableaux
	    public void show() {
	        System.out.println("M = " + M);
	        System.out.println("N = " + N);
	        for (int i = 0; i <= M; i++) {
	            for (int j = 0; j <= M + N; j++) {
	                System.out.printf("%7.2f ", a[i][j]);
	            }
	            System.out.println();
	        }
	        System.out.println("value = " + value());
	        for (int i = 0; i < M; i++)
	            if (basis[i] < N) System.out.println("x_" + basis[i] + " = " + a[i][M+N]);
	        System.out.println();
	    }


	    public static void test(float[][] A, float[] b, float[] c) {
	        SimplexSolver lp = new SimplexSolver(A, b, c);
	        System.out.println("value = " + lp.value());
	        float[] x = lp.primal();
	        for (int i = 0; i < x.length; i++)
	            System.out.println("x[" + i + "] = " + x[i]);
	        float[] y = lp.dual();
	        for (int j = 0; j < y.length; j++)
	            System.out.println("y[" + j + "] = " + y[j]);
	    }

	    public static void test1() {
	        float[][] A = {
	            { 4,  5,  8, 3 },
	            { 1,  0,  0, 0 },
	            { 0,  1,  0, 0 },
	            { 0,  0,  1, 0 },
	            { 0,  0,  0, 1 },
	        };
	        float[] c = { -8, -10, -15, -4 };
	        float[] b = { 11, 1, 1, 1, 1 };
	        test(A, b, c);
	    }

/*
	    // x0 = 12, x1 = 28, opt = 800
	    public static void test2() {
	        float[] c = {  13.0f,  23.0f };
	        float[] b = { 480.0f, 160.0f, 1190.0f };
	        float[][] A = {
	            {  5.0f, 15.0f },
	            {  4.0,  4.0 },
	            { 35.0, 20.0 },
	        };
	        test(A, b, c);
	    }

	    // unbounded
	    public static void test3() {
	        float[] c = { 2.0, 3.0, -1.0, -12.0 };
	        float[] b = {  3.0,   2.0 };
	        float[][] A = {
	            { -2.0, -9.0,  1.0,  9.0 },
	            {  1.0,  1.0, -1.0, -2.0 },
	        };
	        test(A, b, c);
	    }

	    // degenerate - cycles if you choose most positive objective function coefficient
	    public static void test4() {
	        float[] c = { 10.0, -57.0, -9.0, -24.0 };
	        float[] b = {  0.0,   0.0,  1.0 };
	        float[][] A = {
	            { 0.5, -5.5, -2.5, 9.0 },
	            { 0.5, -1.5, -0.5, 1.0 },
	            { 1.0,  0.0,  0.0, 0.0 },
	        };
	        test(A, b, c);
	    }


*/
	    // test client
/*	    
	    public static void main(String[] args) {

	        try                           { test1();             }
	        catch (ArithmeticException e) { e.printStackTrace(); }
	        System.out.println("--------------------------------");

	        int M = Integer.parseInt(args[0]);
	        int N = Integer.parseInt(args[1]);
	        float[] c = new float[N];
	        float[] b = new float[M];
	        float[][] A = new float[M][N];
	        for (int j = 0; j < N; j++)
	            c[j] = StdRandom.uniform(1000);
	        for (int i = 0; i < M; i++)
	            b[i] = StdRandom.uniform(1000);
	        for (int i = 0; i < M; i++)
	            for (int j = 0; j < N; j++)
	                A[i][j] = StdRandom.uniform(100);
	        SimplexSolver lp = new SimplexSolver(A, b, c);
	        System.out.println(lp.value());
	    }
*/
}

package com.carterza.universe.generator;

import com.badlogic.gdx.math.MathUtils;

/**
 * Java implementation of the Navier-Stokes-Solver from
 http://www.openprocessing.org/sketch/27653
 *
 */
public class NavierStokesSolver {
    final int width, height;
    final static int N = 100;
    final static int SIZE = (N + 2) * (N + 2);
    double[] u = new double[SIZE];
    double[] v = new double[SIZE];
    double[] u_prev = new double[SIZE];
    double[] v_prev = new double[SIZE];
    double[] dense = new double[SIZE];
    double[] dense_prev = new double[SIZE];

    public NavierStokesSolver(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public double getDx(int x, int y) {
        return u[INDEX(x+1, y+1)];
    }

    public double getDy(int x, int y) {
        return v[INDEX(x+1, y+1)];
    }

    public double getDx(float x, float y, float width, float height){
        final float cellWidth = width / N;
        final float cellHeight = height / N;
        int cellX = (int)(x/cellWidth);
        int cellY = (int)(y/cellHeight);
        // get dx,dy for cell, but this isn't good enough.
        float dx = (float) getDx(cellX, cellY);
        float dy = (float) getDy(cellX, cellY);
        // get x,y if 0,0 was center of the cell.
        float lX = x - cellX * cellWidth - cellWidth / 2;
        float lY = y - cellY * cellHeight - cellHeight / 2;

        int v, h, vf, hf;
        // find out which neighbor cell we want to lerp with
        // if point is in right half of cell, lerp with cell below
        if (lX > 0) {
            v = Math.min(N, cellX + 1);
            vf = 1;
        }
        // else lerp with cell above
        else {
            v = Math.min(N, cellX - 1);
            vf = -1;
        }
        // if point is in lower half, lerp with cell to the right
        if (lY > 0) {
            h = Math.min(N, cellY + 1);
            hf = 1;
        }
        // else lerp with cell to the left
        else {
            h = Math.min(N, cellY - 1);
            hf = -1;
        }
        // get the dx values of three cells adjacent to the quadrant of the sample point x,y
        float dxv = (float) getDx(v, cellY);
        float dxh = (float) getDx(cellX, h);
        float dxvh = (float) getDx(v, h);


        // lerp dx of the sampled cell with the vertically adjecent cell's dx according to placement along the cell,
        // lerp dx of the horizontally adjacent cell with dx of the diagonally adjacent cell according to the sample's
        // closeness to the cell wall.
        // lerp these values together based on the closeness of the sample to the cell wall.
        dx = MathUtils.lerp(MathUtils.lerp(dx, dxv, hf * lY / cellWidth), MathUtils.lerp(dxh, dxvh, hf * lY / cellWidth), vf * lX / cellHeight);

        return dx;
    }

    public double getDy(float x, float y, float width, float height){
        final float cellWidth = width / N;
        final float cellHeight = height / N;
        int cellX = (int)(x/cellWidth);
        int cellY = (int)(y/cellHeight);
        // get dx,dy for cell, but this isn't good enough.
        float dx = (float) getDx(cellX, cellY);
        float dy = (float) getDy(cellX, cellY);
        // get x,y if 0,0 was center of the cell.
        float lX = x - cellX * cellWidth - cellWidth / 2;
        float lY = y - cellY * cellHeight - cellHeight / 2;

        int v, h, vf, hf;
        // find out which neighbor cell we want to lerp with
        // if point is in lower half of cell, lerp with cell below
        if (lX > 0) {
            v = Math.min(N, cellX + 1);
            vf = 1;
        }
        // else lerp with cell above
        else {
            v = Math.min(N, cellX - 1);
            vf = -1;
        }
        // if point is in right half, lerp with cell to the right
        if (lY > 0) {
            h = Math.min(N, cellY + 1);
            hf = 1;
        }
        // else lerp with cell to the left
        else {
            h = Math.min(N, cellY - 1);
            hf = -1;
        }
        // get the dy values of the three cells adjacent to the quadrant of the sample point x,y
        float dyv = (float) getDy(v, cellY);
        float dyh = (float) getDy(cellX, h);
        float dyvh = (float) getDy(v, h);   // lerp the dy of the sampled cell with the vertically adjacent cell's dy according to the placement along the cell,
        // lerp the dy of the horizontally adjacent cell with the dy of the diagonally adjacent cell according to the sample's
        // closeness to the cell wall.
        // lerp these calues together based on the closeness of the sample to the cell wall.
        dy = MathUtils.lerp(MathUtils.lerp(dy, dyv, hf * lY / cellWidth), MathUtils.lerp(dyh, dyvh, hf * lY / cellWidth), vf * lX / cellHeight);
        return dy;
    }

    public void applyForce(int cellX, int cellY, double vx, double vy) {
        cellX += 1;
        cellY += 1;
        double dx = u[INDEX(cellX, cellY)];
        double dy = v[INDEX(cellX, cellY)];

        u[INDEX(cellX, cellY)] = (vx != 0) ? MathUtils.lerp((float) vx,
                (float) dx, 0.85f) : dx;
        v[INDEX(cellX, cellY)] = (vy != 0) ? MathUtils.lerp((float) vy,
                (float) dy, 0.85f) : dy;

    }

    void tick(double dt, double visc, double diff) {
        vel_step(u, v, u_prev, v_prev, visc, dt);
        dens_step(dense, dense_prev, u, v, diff, dt);
    }

    // method used to be 'static' since this class is not a top level type
    final int INDEX(int i, int j) {
        return i + (N + 2) * j;
    }

    double[] tmp = new double[SIZE];
    // same applies to the swap operation ^^
    final void SWAP(double[] x0, double[] x) {
        System.arraycopy(x0, 0, tmp, 0, SIZE);
        System.arraycopy(x, 0, x0, 0, SIZE);
        System.arraycopy(tmp, 0, x, 0, SIZE);
    }

    void add_source(double[] x, double[] s, double dt) {
        int i, size = (N + 2) * (N + 2);
        for (i = 0; i < size; i++)
            x[i] += dt * s[i];
    }

    void diffuse(int b, double[] x, double[] x0, double diff, double dt) {
        int i, j, k;
        double a = dt * diff * N * N;
        for (k = 0; k < 20; k++) {
            for (i = 1; i <= N; i++) {
                for (j = 1; j <= N; j++) {
                    x[INDEX(i, j)] = (x0[INDEX(i, j)] + a
                            * (x[INDEX(i - 1, j)] + x[INDEX(i + 1, j)] + x[INDEX(i, j - 1)] + x[INDEX(
                            i, j + 1)]))
                            / (1 + 4 * a);
                }
            }
            set_bnd(b, x);
        }
    }

    void advect(int b, double[] d, double[] d0, double[] u, double[] v, double dt) {
        int i, j, i0, j0, i1, j1;
        double x, y, s0, t0, s1, t1, dt0;
        dt0 = dt * N;
        for (i = 1; i <= N; i++) {
            for (j = 1; j <= N; j++) {
                x = i - dt0 * u[INDEX(i, j)];
                y = j - dt0 * v[INDEX(i, j)];
                if (x < 0.5)
                    x = 0.5;
                if (x > N + 0.5)
                    x = N + 0.5;
                i0 = (int) x;
                i1 = i0 + 1;
                if (y < 0.5)
                    y = 0.5;
                if (y > N + 0.5)
                    y = N + 0.5;
                j0 = (int) y;
                j1 = j0 + 1;
                s1 = x - i0;
                s0 = 1 - s1;
                t1 = y - j0;
                t0 = 1 - t1;
                d[INDEX(i, j)] = s0 * (t0 * d0[INDEX(i0, j0)] + t1 * d0[INDEX(i0, j1)])
                        + s1 * (t0 * d0[INDEX(i1, j0)] + t1 * d0[INDEX(i1, j1)]);
            }
        }
        set_bnd(b, d);
    }

    void set_bnd(int b, double[] x) {
        int i;
        for (i = 1; i <= N; i++) {
            x[INDEX(0, i)] = (b == 1) ? -x[INDEX(1, i)] : x[INDEX(1, i)];
            x[INDEX(N + 1, i)] = b == 1 ? -x[INDEX(N, i)] : x[INDEX(N, i)];
            x[INDEX(i, 0)] = b == 2 ? -x[INDEX(i, 1)] : x[INDEX(i, 1)];
            x[INDEX(i, N + 1)] = b == 2 ? -x[INDEX(i, N)] : x[INDEX(i, N)];
        }
        x[INDEX(0, 0)] = 0.5 * (x[INDEX(1, 0)] + x[INDEX(0, 1)]);
        x[INDEX(0, N + 1)] = 0.5 * (x[INDEX(1, N + 1)] + x[INDEX(0, N)]);
        x[INDEX(N + 1, 0)] = 0.5 * (x[INDEX(N, 0)] + x[INDEX(N + 1, 1)]);
        x[INDEX(N + 1, N + 1)] = 0.5 * (x[INDEX(N, N + 1)] + x[INDEX(N + 1, N)]);
    }

    void dens_step(double[] x, double[] x0, double[] u, double[] v, double diff,
                   double dt) {
        add_source(x, x0, dt);
        SWAP(x0, x);
        diffuse(0, x, x0, diff, dt);
        SWAP(x0, x);
        advect(0, x, x0, u, v, dt);
    }

    void vel_step(double[] u, double[] v, double[] u0, double[] v0, double visc,
                  double dt) {
        add_source(u, u0, dt);
        add_source(v, v0, dt);
        SWAP(u0, u);
        diffuse(1, u, u0, visc, dt);
        SWAP(v0, v);
        diffuse(2, v, v0, visc, dt);
        project(u, v, u0, v0);
        SWAP(u0, u);
        SWAP(v0, v);
        advect(1, u, u0, u0, v0, dt);
        advect(2, v, v0, u0, v0, dt);
        project(u, v, u0, v0);
    }

    void project(double[] u, double[] v, double[] p, double[] div) {
        int i, j, k;
        double h;
        h = 1.0 / N;
        for (i = 1; i <= N; i++) {
            for (j = 1; j <= N; j++) {
                div[INDEX(i, j)] = -0.5
                        * h
                        * (u[INDEX(i + 1, j)] - u[INDEX(i - 1, j)] + v[INDEX(i, j + 1)] - v[INDEX(
                        i, j - 1)]);
                p[INDEX(i, j)] = 0;
            }
        }
        set_bnd(0, div);
        set_bnd(0, p);
        for (k = 0; k < 20; k++) {
            for (i = 1; i <= N; i++) {
                for (j = 1; j <= N; j++) {
                    p[INDEX(i, j)] = (div[INDEX(i, j)] + p[INDEX(i - 1, j)]
                            + p[INDEX(i + 1, j)] + p[INDEX(i, j - 1)] + p[INDEX(i, j + 1)]) / 4;
                }
            }
            set_bnd(0, p);
        }
        for (i = 1; i <= N; i++) {
            for (j = 1; j <= N; j++) {
                u[INDEX(i, j)] -= 0.5 * (p[INDEX(i + 1, j)] - p[INDEX(i - 1, j)]) / h;
                v[INDEX(i, j)] -= 0.5 * (p[INDEX(i, j + 1)] - p[INDEX(i, j - 1)]) / h;
            }
        }
        set_bnd(1, u);
        set_bnd(2, v);
    }
}


package com.carterza;

import com.badlogic.gdx.math.Vector3;

import java.util.stream.IntStream;

public class PlanetShadingTest {

    static char[] textShades;

    public static void main(String[] args) {
        double[] light = normalize(new double[]{30.0,30.0,-50.0});
        textShades = new char[]{'.',':','!','*','o','e','&','#','%','@'};

        int r = 20;
        double k = 4;
        double ambient = 0.1;

        buildCircleMask(r, k, ambient, light);
    }

    private static void buildCircleMask(int r, double k, double ambient, double[] light) {
        IntStream.range((int)Math.floor(-r), (int)Math.ceil(r)+1).forEachOrdered(i -> {
            double x = i + 0.5;
            StringBuilder sb = new StringBuilder();
            IntStream.range((int)Math.floor(-2*r), (int)Math.ceil(2*r)+1).forEachOrdered(j -> {
                double y = j/2 + 0.5;
                if (x*x + y*y <= r*r) {
                    double[] vec = normalize(new double[]{x,y,Math.sqrt(r*r - x*x - y*y)});
                    double b = Math.pow(dot(light, vec), k) + ambient;
                    int intensity = (int)((1-b)*(textShades.length-1));
                    if(0 <= intensity && intensity < textShades.length) {
                        sb.append(textShades[intensity]);
                    } else {
                        sb.append(textShades[0]);
                    }
                } else {
                    sb.append(' ');
                }
            });
            System.out.println(sb.toString());
        });
    }

    private static double[] normalize(double[] v) {
        double len = Math.sqrt(Math.pow(v[0],2) + Math.pow(v[1],2) + Math.pow(v[2],2));
        return new double[]{v[0]/len, v[1]/len, v[2]/len};
    }

    private static double dot (double[] v1, double[] v2) {
        double d =  v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
        if(d < 0) return -d;
        return 0;
    }
}
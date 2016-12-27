package com.carterza.universe.generator;

import com.carterza.common.CommonRNG;

/**
 * Created by zachcarter on 12/9/16.
 */
public class GeneratorUtils {
    // Convert float [0,1] to integer [0,255]
    public static int convertNoise(double value) {
        double f2 = Math.max(0.0, Math.min(1.0, value));
        return (int) Math.floor(f2 == 1.0 ? 255 : f2 * 256.0);
    }

    public static double expFilter(double value, double coverage, double sharpness) {
        double c = (value - (1.0 - coverage)) * 10000;
        value = 10000 - (Math.pow(sharpness, c < 0 ? 0 : c) * 10000);
        return value / 10000;
    }

    public static int clampColor(int x) {
        return x < 0 ? 0 : (x > 255 ? 255 : (x|0));
    }


    public static char randchar(char[] str) {
        return str[CommonRNG.getRng().between(0, str.length)];
    }


    public static float blendMul(int a, int b) {
        return (a * b) >> 8;
    }

    public static double blend(float a, float b, double f) {
        return a*f + b*(1.0-f);
    }


    public static boolean between(double x, double a, double b) {
        return (x < a || x > b) ? false : true;
    }
}

package com.carterza.math;

import java.util.Random;

/**
 * Created by zachcarter on 12/11/16.
 */
public class PerlinNoise {
    static final int PERLIN_YWRAPB = 4;
    static final int PERLIN_YWRAP = 16;
    static final int PERLIN_ZWRAPB = 8;
    static final int PERLIN_ZWRAP = 256;
    static final int PERLIN_SIZE = 4095;
    int perlin_octaves = 4;
    float perlin_amp_falloff = 0.5F;
    int perlin_TWOPI;
    int perlin_PI;
    float[] perlin_cosTable;
    float[] perlin;
    Random perlinRandom;

    protected static final float[] cosLUT = new float[720];

    public float noise(float var1, float var2) {
        return this.noise(var1, var2, 0.0F);
    }

    public float noise(float var1, float var2, float var3) {
        int var4;
        if(this.perlin == null) {
            if(this.perlinRandom == null) {
                this.perlinRandom = new Random();
            }

            this.perlin = new float[4096];

            for(var4 = 0; var4 < 4096; ++var4) {
                this.perlin[var4] = this.perlinRandom.nextFloat();
            }

            this.perlin_cosTable = cosLUT;
            this.perlin_TWOPI = this.perlin_PI = 720;
            this.perlin_PI >>= 1;
        }

        if(var1 < 0.0F) {
            var1 = -var1;
        }

        if(var2 < 0.0F) {
            var2 = -var2;
        }

        if(var3 < 0.0F) {
            var3 = -var3;
        }

        var4 = (int)var1;
        int var5 = (int)var2;
        int var6 = (int)var3;
        float var7 = var1 - (float)var4;
        float var8 = var2 - (float)var5;
        float var9 = var3 - (float)var6;
        float var12 = 0.0F;
        float var13 = 0.5F;

        for(int var17 = 0; var17 < this.perlin_octaves; ++var17) {
            int var18 = var4 + (var5 << 4) + (var6 << 8);
            float var10 = this.noise_fsc(var7);
            float var11 = this.noise_fsc(var8);
            float var14 = this.perlin[var18 & 4095];
            var14 += var10 * (this.perlin[var18 + 1 & 4095] - var14);
            float var15 = this.perlin[var18 + 16 & 4095];
            var15 += var10 * (this.perlin[var18 + 16 + 1 & 4095] - var15);
            var14 += var11 * (var15 - var14);
            var18 += 256;
            var15 = this.perlin[var18 & 4095];
            var15 += var10 * (this.perlin[var18 + 1 & 4095] - var15);
            float var16 = this.perlin[var18 + 16 & 4095];
            var16 += var10 * (this.perlin[var18 + 16 + 1 & 4095] - var16);
            var15 += var11 * (var16 - var15);
            var14 += this.noise_fsc(var9) * (var15 - var14);
            var12 += var14 * var13;
            var13 *= this.perlin_amp_falloff;
            var4 <<= 1;
            var7 *= 2.0F;
            var5 <<= 1;
            var8 *= 2.0F;
            var6 <<= 1;
            var9 *= 2.0F;
            if(var7 >= 1.0F) {
                ++var4;
                --var7;
            }

            if(var8 >= 1.0F) {
                ++var5;
                --var8;
            }

            if(var9 >= 1.0F) {
                ++var6;
                --var9;
            }
        }

        return var12;
    }

    private float noise_fsc(float var1) {
        return 0.5F * (1.0F - this.perlin_cosTable[(int)(var1 * (float)this.perlin_PI) % this.perlin_TWOPI]);
    }

    public void noiseDetail(int var1) {
        if(var1 > 0) {
            this.perlin_octaves = var1;
        }

    }

    public void noiseDetail(int var1, float var2) {
        if(var1 > 0) {
            this.perlin_octaves = var1;
        }

        if(var2 > 0.0F) {
            this.perlin_amp_falloff = var2;
        }

    }

    public void noiseSeed(long var1) {
        if(this.perlinRandom == null) {
            this.perlinRandom = new Random();
        }

        this.perlinRandom.setSeed(var1);
        this.perlin = null;
    }
}

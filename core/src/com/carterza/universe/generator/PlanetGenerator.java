package com.carterza.universe.generator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.carterza.common.CommonRNG;
import com.carterza.universe.*;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SColorFactory;
import squidpony.squidgrid.mapping.SpillWorldMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zachcarter on 12/10/16.
 */
public class PlanetGenerator {
    private final Universe universe;
    private final Galaxy galaxy;
    private final StarSystem starSystem;
    private final SolarSystem solarSystem;
    private final Planet planet;
    private final ArrayList<Planet> dungeons;
    /*private final ArrayList<Sun> cities;*/
    GroundTextures groundTextures = new GroundTextures();
    VegetationTextures vegetationTextures = new VegetationTextures();
    List<HeightTexture> heightTextures = new ArrayList<HeightTexture>();
    double waterLevel;
    HeightTexture[][] buffer;

    SpillWorldMap worldMap;

    double[] shades;

    Map<Integer, Color> colorMap;

    int diameter = 30;
    int planetWidth = diameter;
    int planetHeight;

    int detailWidth = planetWidth * 2;

    int noiseOctaves;
    double noiseZoom;

    int heightmapWidth, heightmapHeight;
    int detailHeightmapWidth, detailHeightmapHeight;

    SColorFactory sColorFactory;

    List<List<Color>> sprite;
    List<List<Color>> detailSprite;

    List<List<Double>> circleMask;
    List<List<Double>> detailCircleMask;

    float[][] detailHeightmap;
    float[][] detailAtmosphere;

    public PlanetGenerator(Planet planet, SolarSystem solarSystem, StarSystem starSystem, Galaxy galaxy, Universe universe) {
        this.universe = universe;
        this.galaxy = galaxy;
        this.starSystem = starSystem;
        this.solarSystem = solarSystem;
        this.planet = planet;
        /*this.cities = new ArrayList<Sun>();*/
        this.dungeons = new ArrayList<Planet>();

        sColorFactory = new SColorFactory();

        shades = new double[22];

        int ii = 0;
        for(int i = 20; i > -1; i--) {
            shades[ii] = i/20.0;
            ii++;
        }

        colorMap = new HashMap<Integer, Color>();

        switch (planet.getPlanetType()) {
            case OCEAN:
                for(int i = 0; i<=39; i++) {
                    colorMap.put(i,new SColor(13, 44, 53));
                }
                for(int i = 40; i<=79; i++) {
                    colorMap.put(i,new SColor(18, 51,  57));
                }
                for(int i = 80; i<=129; i++) {
                    colorMap.put(i,new SColor(16,  70,  63));
                }
                for(int i = 130; i<=149; i++) {
                    colorMap.put(i,new SColor(31,  106,  100));
                }
                for(int i = 150; i<=159; i++) {
                    colorMap.put(i,new SColor(92,  198,  169));
                }
                for(int i = 160; i<=179; i++) {
                    colorMap.put(i,new SColor(170,   211,   142));
                }
                for(int i = 180; i<=199; i++) {
                    colorMap.put(i,new SColor(78,  144,  72));
                }
                colorMap.put(200,new SColor(24,  55,  23));

                noiseOctaves = 6;
                noiseZoom = 1;
                break;
            case JUNGLE:
                for(int i = 0; i<=59; i++) {
                    colorMap.put(i,new SColor(29,  53,  112));
                }
                for(int i = 60; i<=79; i++) {
                    colorMap.put(i,new SColor(69,  137, 200));
                }
                for(int i = 80; i<=109; i++) {
                    colorMap.put(i,new SColor(61,  86,  34));
                }
                for(int i = 110; i<=149; i++) {
                    colorMap.put(i,new SColor(42,  72,  38));
                }
                for(int i = 150; i<=204; i++) {
                    colorMap.put(i,new SColor(52,  103, 35));
                }
                for(int i = 205; i<=234; i++) {
                    colorMap.put(i,new SColor(29,  47,  18));
                }
                for(int i = 235; i<=254; i++) {
                    colorMap.put(i,new SColor(75,  118, 33));
                }
                colorMap.put(255,new SColor(100, 173, 22));

                noiseOctaves = 6;
                noiseZoom = 3;
                break;
            case LAVA:
                for(int i = 0; i<=59; i++) {
                    colorMap.put(i,new SColor(255, 151, 19));
                }
                for(int i = 60; i<=79; i++) {
                    colorMap.put(i,new SColor(255, 52,  0));
                }
                for(int i = 80; i<=109; i++) {
                    colorMap.put(i,new SColor(91,  31,  12));
                }
                for(int i = 110; i<=149; i++) {
                    colorMap.put(i,new SColor(31,  21,  11));
                }
                for(int i = 150; i<=204; i++) {
                    colorMap.put(i,new SColor(56,  44,  21));
                }
                for(int i = 205; i<=234; i++) {
                    colorMap.put(i,new SColor(0,   0,   0));
                }
                for(int i = 235; i<=254; i++) {
                    colorMap.put(i,new SColor(62,  19,  15));
                }
                colorMap.put(255,new SColor(94,  65,  35));

                noiseOctaves = 6;
                noiseZoom = 6;
                break;
            case TUNDRA:
                for(int i = 0; i<=59; i++) {
                    colorMap.put(i,new SColor(121, 183, 170));
                }
                for(int i = 60; i<=74; i++) {
                    colorMap.put(i,new SColor(167, 206, 174));
                }
                for(int i = 75; i<=104; i++) {
                    colorMap.put(i,new SColor(145, 150, 117));
                }
                for(int i = 105; i<=139; i++) {
                    colorMap.put(i,new SColor(110, 116, 93));
                }
                for(int i = 140; i<=199; i++) {
                    colorMap.put(i,new SColor(167, 157, 109));
                }
                for(int i = 200; i<=234; i++) {
                    colorMap.put(i,new SColor(86,  111, 95));
                }
                for(int i = 235; i<=254; i++) {
                    colorMap.put(i,new SColor(210, 199, 132));
                }
                colorMap.put(255,new SColor(255, 255, 200));

                noiseOctaves = 6;
                noiseZoom = 2;
                break;
            case ARID:
                for(int i = 0; i<=59; i++) {
                    colorMap.put(i,new SColor(121, 183, 170));
                }
                for(int i = 60; i<=74; i++) {
                    colorMap.put(i,new SColor(167, 206, 174));
                }
                for(int i = 75; i<=89; i++) {
                    colorMap.put(i,new SColor(126, 86,  36));
                }
                for(int i = 90; i<=139; i++) {
                    colorMap.put(i,new SColor(163, 94,  45));
                }
                for(int i = 140; i<=179; i++) {
                    colorMap.put(i,new SColor(235, 131, 44));
                }
                for(int i = 180; i<=224; i++) {
                    colorMap.put(i,new SColor(174, 115, 29));
                }
                for(int i = 225; i<=254; i++) {
                    colorMap.put(i,new SColor(112, 69,  35));
                }
                colorMap.put(255,new SColor(41,  30,  20));

                noiseOctaves = 6;
                noiseZoom = 3;
                break;
            case DESERT:
                for(int i = 0; i<=29; i++) {
                    colorMap.put(i,new SColor(255, 178, 58));
                }
                for(int i = 30; i<=39; i++) {
                    colorMap.put(i,new SColor(229, 163, 78));
                }
                for(int i = 40; i<=79; i++) {
                    colorMap.put(i,new SColor(255, 150, 61));
                }
                for(int i = 80; i<=139; i++) {
                    colorMap.put(i,new SColor(235, 131, 44));
                }
                for(int i = 140; i<=199; i++) {
                    colorMap.put(i,new SColor(174, 115, 29));
                }
                for(int i = 200; i<=243; i++) {
                    colorMap.put(i,new SColor(163, 94,  45));
                }
                for(int i = 244; i<=254; i++) {
                    colorMap.put(i,new SColor(112, 69,  35));
                }
                colorMap.put(255,new SColor(68,  47,  49));

                noiseOctaves = 6;
                noiseZoom = 3;
                break;
            case ARTIC:
                for(int i = 0; i<=59; i++) {
                    colorMap.put(i,new SColor(255, 255, 240));
                }
                for(int i = 60; i<=99; i++) {
                    colorMap.put(i,new SColor(221, 245, 193));
                }
                for(int i = 100; i<=119; i++) {
                    colorMap.put(i,new SColor(157, 198, 160));
                }
                for(int i = 120; i<=139; i++) {
                    colorMap.put(i,new SColor(134, 152, 113));
                }
                for(int i = 140; i<=169; i++) {
                    colorMap.put(i,new SColor(154, 166, 116));
                }
                for(int i = 170; i<=174; i++) {
                    colorMap.put(i,new SColor(208, 211, 156));
                }
                for(int i = 175; i<=254; i++) {
                    colorMap.put(i,new SColor(172, 198, 155));
                }
                colorMap.put(255,new SColor(255, 255, 255));

                noiseOctaves = 6;
                noiseZoom = 1;
                break;
            case BARREN:
                for(int i = 0; i<=29; i++) {
                    colorMap.put(i,new SColor(39,  41,  44));
                }
                for(int i = 30; i<=59; i++) {
                    colorMap.put(i,new SColor(88,  93,  67));
                }
                for(int i = 60; i<=84; i++) {
                    colorMap.put(i,new SColor(111, 109, 78));
                }
                for(int i = 85; i<=99; i++) {
                    colorMap.put(i,new SColor(151, 152, 113));
                }
                for(int i = 100; i<=119; i++) {
                    colorMap.put(i,new SColor(151, 141, 101));
                }
                for(int i = 120; i<=179; i++) {
                    colorMap.put(i,new SColor(198, 189, 133));
                }
                for(int i = 180; i<=254; i++) {
                    colorMap.put(i,new SColor(100, 101, 81));
                }
                colorMap.put(255,new SColor(247, 236, 177));

                noiseOctaves = 6;
                noiseZoom = 2.5;
                break;
            case GAS:
                for(int i = 0; i<=39; i++) {
                    colorMap.put(i,new SColor(62,  99,  120));
                }
                for(int i = 40; i<=79; i++) {
                    colorMap.put(i,new SColor(86,  137, 173));
                }
                for(int i = 80; i<=99; i++) {
                    colorMap.put(i,new SColor(112, 199, 242));
                }
                for(int i = 100; i<=119; i++) {
                    colorMap.put(i,new SColor(115, 214, 255));
                }
                for(int i = 120; i<=189; i++) {
                    colorMap.put(i,new SColor(162, 212, 234));
                }
                for(int i = 190; i<=209; i++) {
                    colorMap.put(i,new SColor(237, 236, 255));
                }
                for(int i = 210; i<=254; i++) {
                    colorMap.put(i,new SColor(222, 255, 255));
                }
                colorMap.put(255,new SColor(255, 255, 255));

                noiseOctaves = 4;
                noiseZoom = 3;
                break;
            default:
                // Terran Planet
                for(int i = 0; i<=14; i++) {
                    colorMap.put(i,new SColor(39,  62,  90));
                }
                for(int i = 15; i<=69; i++) {
                    colorMap.put(i,new SColor(50,  72,  88));
                }
                for(int i = 70; i<=79; i++) {
                    colorMap.put(i,new SColor(116, 184, 164));
                }
                for(int i = 80; i<=89; i++) {
                    colorMap.put(i,new SColor(142, 163, 164));
                }
                for(int i = 90; i<=199; i++) {
                    colorMap.put(i,new SColor(71,  97,  81));
                }
                for(int i = 200; i<=232; i++) {
                    colorMap.put(i,new SColor(149, 138, 115));
                }
                for(int i = 233; i<=254; i++) {
                    colorMap.put(i,new SColor(199, 197, 150));
                }
                colorMap.put(255,new SColor(220, 197, 173));

                noiseOctaves = 6;
                noiseZoom = 1;
        }

        if ((planetWidth % 2) != 0)
            planetWidth += 1;

        if ((detailWidth % 2) != 0)
            detailWidth += 1;

        planetHeight = planetWidth;

        heightmapWidth = planetWidth * 2;
        heightmapHeight = planetHeight;

        detailHeightmapWidth = detailWidth*2;
        detailHeightmapHeight = detailWidth;

    }

    public void generate() {
        System.out.println("Generating planet!");

        double[] light = normalize(new double[]{ 30, 30, -50});

        double ambient = 0.1;

        detailCircleMask = buildCircleMask(detailWidth/2, 4, light, ambient);

        detailHeightmap = createHeightmap(detailHeightmapWidth, detailHeightmapHeight);

        detailAtmosphere = createAtmosphere(detailHeightmapWidth, detailHeightmapHeight);
    }

    private Color blendColors(Color c1, Color c2, float alpha) {
        return new Color(
                (alpha * c1.r + (1-alpha) * c2.r)
                ,(alpha * c1.g + (1-alpha) * c2.g)
                ,(alpha * c1.b + (1-alpha) * c2.b)
                , 1
        );
    }

    public Color blendLayers(int x, int y, int terrainRotation, int atmosphereRotation, List<List<Double>> cm, float[][] height, float[][] atmos, int w) {

        int terrainColor = (int)height[((x+terrainRotation) % w)][y];
        if (terrainColor > 255)
            terrainColor = 255;

        Color color = colorMap.get(terrainColor);

        if(atmos != null) {
            float cloudCover = atmos[((x + atmosphereRotation) % w)][y];
            color = blendColors(color, Color.WHITE, cloudCover);
        }

        if(cm.get(x).get(y) < 1) {
            color = blendColors(color, Color.BLACK, cm.get(x).get(y).floatValue());
        }

        return color;
    }

    private float[][] createAtmosphere(int hmw, int hmh) {
        float[][] atmosphere = null;
        switch(planet.getPlanetType()) {
            case TERRAN:
            case OCEAN:
            case JUNGLE:
            case TUNDRA:
            case ARTIC:
                 atmosphere = sphericalNoise(
                        10.0
                        , 10.0
                        , 10.0
                        , 4
                        , 2
                        , 0.5
                        , 2.0
                        , hmw
                        , hmh
                        , CommonRNG.getRng().nextLong()
                );

                atmosphere = normalizeHeightmap(atmosphere, 0,1);
                atmosphere = heightmapAdd(atmosphere, 0.30f);
                atmosphere = heightmapClamp(atmosphere, 0.4,1.0);
                break;
            case DESERT:
            case ARID:
                atmosphere = sphericalNoise(
                        10.0
                        , 10.0
                        , 10.0
                        , 4
                        , 2
                        , 0.5
                        , 2.0
                        , hmw
                        , hmh
                        , CommonRNG.getRng().nextLong()
                );

                atmosphere = normalizeHeightmap(atmosphere, 0,1);
                atmosphere = heightmapAdd(atmosphere, 0.7f);
                atmosphere = heightmapClamp(atmosphere, 0.8,1.0);
                break;
            default:
        }
        return  atmosphere;
    }

    private List<List<Double>> buildCircleMask(int r, double k, double[] light, double ambient) {
        List<List<Double>> circleMask = new ArrayList<List<Double>>();
        for(int i = (int)Math.floor(-r); i <= (int)Math.ceil(r)-1; i++) {
            double xx = i + 0.5;
            List<Double> col = new ArrayList<Double>();
            for(int j = (int)Math.floor(-1*r); j <= (int)Math.ceil(1*r)-1; j++) {

                double yy = j + 0.5;

                if (xx*xx + yy*yy <= r*r) {
                    double[] vector3 = normalize(new double[]{xx,yy, Math.sqrt(r*r - xx*xx - yy*yy)});
                    double b = Math.pow(dot(light, vector3), k) + ambient;
                    int intensity = (int)((1-b)*(shades.length-1));
                    col.add((0 <= intensity && intensity < shades.length) ? shades[intensity] : shades[0]);
                } else {
                    col.add(0.0);
                }
            }
            circleMask.add(col);
        }
        return circleMask;
    }

    private float[][] createHeightmap(int hmw, int hmh) {
        float[][] hm = sphericalNoise(
                0.0
                , 0.0
                , 0.0
                , noiseOctaves
                , noiseZoom
                , 0.5
                , 2.0
                , hmw
                , hmh
                , CommonRNG.getRng().nextLong()
        );

        switch (planet.getPlanetType()) {
            case TERRAN:
                hm = normalizeHeightmap(hm, 0, 1);
                hm = heightmapAdd(hm, -0.40f);
                hm = heightmapClamp(hm, 0.0, 1.0);
                hm = heightmapRainErosion(hm, 1000, 0.46f, 0.12f);
                hm = normalizeHeightmap(hm, 0, 255);
                break;
            case OCEAN:
                hm = normalizeHeightmap(hm, 0, 1);
                hm = heightmapAdd(hm, -0.40f);
                hm = heightmapClamp(hm, 0.0, 1.0);
                hm = heightmapRainErosion(hm, 3000, 0.46f, 0.12f);
                hm = normalizeHeightmap(hm, 0, 200);
                break;
            case JUNGLE:
                hm = normalizeHeightmap(hm, 0, 1);
                hm = heightmapAdd(hm, .2f);
                hm = heightmapClamp(hm, 0.0, 1.0);
                hm = heightmapRainErosion(hm, 3000, 0.25f, 0.5f);
                hm = normalizeHeightmap(hm, 0, 255);
            case LAVA:
                hm = normalizeHeightmap(hm, 0, 1);
                hm = heightmapRainErosion(hm, 1000, 0.65f, 0.05f);
                hm = normalizeHeightmap(hm, 0, 255);
                break;
            case TUNDRA:
                hm = normalizeHeightmap(hm, 0, 1);
                hm = heightmapRainErosion(hm, 2000, 0.45f, 0.05f);
                hm = normalizeHeightmap(hm, 0, 255);
                break;
            case ARID:
                hm = normalizeHeightmap(hm, 0, 1);
                hm = heightmapAdd(hm, .15f);
                hm = heightmapClamp(hm, 0.0, 1.0);
                hm = heightmapRainErosion(hm, 1000, 0.10f, 0.10f);
                hm = normalizeHeightmap(hm, 0, 255);
                break;
            case ARTIC:
                hm = normalizeHeightmap(hm, 0, 1);
                hm = heightmapAdd(hm, .40f);
                hm = heightmapClamp(hm, 0.0, 1.0);
                hm = heightmapRainErosion(hm, 1000, 0.45f, 0.05f);
                hm = normalizeHeightmap(hm, 0, 255);
                break;
            case BARREN:
                hm = normalizeHeightmap(hm, 0, 1);
                hm = heightmapRainErosion(hm, 2000, 0.45f, 0.05f);
                hm = normalizeHeightmap(hm, 0, 255);
                break;
            case GAS:
                hm = normalizeHeightmap(hm, 0, 1);
                int smoothKernelSize = 9;
                int[] smoothKernelDx = new int[]{
                        - 1, 0, 1,
                        -1, 0, 1,
                        -1, 0, 1
                };

                int[] smoothKernelDy = new int[]{
                        0,  0,  0,
                        0,  0,  0,
                        0,  0,  0,
                };

                float[] smoothKernelWeight = new float[]{
                        1.0f, 2.0f,  1.0f,
                        4.0f, 20.0f, 4.0f,
                        1.0f, 2.0f,  1.0f
                };

                for(int i = 20; i > -1; i--) {
                    hm = heightmapKernelTransform(hm, smoothKernelSize,smoothKernelDx,smoothKernelDy,smoothKernelWeight,0,1.0f);
                }

                hm = normalizeHeightmap(hm, 0,255);
                break;
        }

        return hm;
    }

    private float[][] sphericalNoise(
            double noiseDx
            , double noiseDy
            , double noiseDz
            , long noiseOctaves
            , double noiseZoom
            , double noiseHurst
            , double noiseLacunarity
            , int width
            , int height
            , long seed
    ) {
        ModuleFractal gen = new ModuleFractal();
        gen.setAllSourceBasisTypes(ModuleBasisFunction.BasisType.SIMPLEX);
        gen.setAllSourceInterpolationTypes(ModuleBasisFunction.InterpolationType.CUBIC);
        gen.setNumOctaves(noiseOctaves);
        gen.setFrequency(2.34);
        gen.setH(noiseHurst);
        gen.setLacunarity(noiseLacunarity);
        gen.setType(ModuleFractal.FractalType.FBM);
        gen.setSeed(seed);

        ModuleAutoCorrect ac = new ModuleAutoCorrect();
        ac.setSource(gen); // set source (can usually be either another Module or a
        // double value; see specific module for details)
        ac.setRange(0.0f, 1.0f); // set the range to auto-correct to
        ac.setSamples(10000); // set how many samples to take
        ac.calculate(); // perform the caclulations

        float[][] hm = new float[width+1][height+1];

        noiseDx += 0.01;
        noiseDy += 0.01;
        noiseDz += 0.01;

        float pi_times_two = (float) (2 * Math.PI);
        float pi_div_two = (float) (Math.PI / 2.0f);

        float theta = 0.0f;
        float phi = pi_div_two * -1.0f;
        int x = 0;
        int y = 0;


        while(phi <= pi_div_two) {
            while (theta <= pi_times_two) {
                double val = ac.get(
                        noiseZoom * Math.cos(phi) * Math.cos(theta)
                        , noiseZoom * Math.cos(phi) * Math.sin(theta)
                        , noiseZoom * Math.sin(phi)
                );

                hm[x][y] = (float) val;

                theta += (pi_times_two / width);
                x += 1;
            }
            phi += (Math.PI / (height-1));
            y += 1;
            x = 0;
            theta = 0.0f;
        }



        return hm;
    }

    private float[][] heightmapClamp(float[][] hm, double min, double max) {
        for(int x = 0; x < hm.length; x++) {
            for (int y = 0; y < hm[0].length; y++) {
                hm[x][y] = (float) MathUtils.clamp(hm[x][y], min, max);
            }
        }
        return hm;
    }

    private float[][] heightmapAdd(float[][] hm, float v) {
        for(int x = 0; x < hm.length; x++) {
            for(int y = 0; y < hm[0].length; y++) {
                hm[x][y] += v;
            }
        }
        return hm;
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

    float[] heightmapGetMinMax(float[][] hm, float min, float max) {
        float curmax = hm[0][0];
        float curmin= hm[0][0];
        int x,y;
	/* get max and min height */
        for (y=0; y < hm[0].length; y++) {
            for (x=0; x < hm.length; x++) {
                float val= (float) hm[x][y];
                if ( val > curmax ) curmax = val;
                else if ( val < curmin ) curmin = val;
            }
        }
        min= (float) curmin;
        max= (float) curmax;
        return new float[]{min, max};
    }


    float[][] normalizeHeightmap(float[][] hm, float min, float max) {
        float curmin = Float.MAX_VALUE,curmax = Float.MIN_VALUE;
        int x,y;
        float invmax;
        float[] minMax = heightmapGetMinMax(hm, curmin, curmax);
        curmin = minMax[0];
        curmax = minMax[1];
        if (curmax - curmin == 0.0f) invmax=0.0f;
        else invmax = (max-min) / (curmax-curmin);
	/* normalize */
        for (y=0; y < hm[0].length; y++) {
            for (x=0; x < hm.length; x++) {
                hm[x][y] = min + (hm[x][y] - curmin) * invmax;
            }
        }
        return hm;
    }

    float[][] heightmapKernelTransform(float[][] hm, int kernelSize, int[] dx, int[] dy, float[] weight, float minLevel, float maxLevel) {
        int n = hm.length*hm[0].length;
        float[] hm1d = new float[n];

        int k = 0;
        for (int i =0; i<hm.length; i++) {
            for (int j =0; j<hm[0].length; j++) {
                hm1d[k++] = hm[i][j];
            }
        }


        int x,y;
        for (x=0; x < hm.length; x++) {
            int offset=x;
            for (y=0; y < hm[0].length; y++) {
                if ( hm1d[offset] >= minLevel && hm1d[offset] <= maxLevel) {
                    float val=0.0f;
                    float totalWeight=0.0f;
                    int i;
                    for (i=0; i < kernelSize; i++ ) {
                        int nx=x+dx[i];
                        int ny=y+dy[i];
                        if ( nx >= 0 && nx < hm.length && ny >= 0 && ny < hm[0].length ) {
                            val+=weight[i]*hm[nx][ny];
                            totalWeight+=weight[i];
                        }
                    }
                    hm1d[offset]=val/totalWeight;
                }
                offset+=hm.length;
            }
        }
        return hm;
    }


    float[][] heightmapRainErosion(float[][] hm, int nbDrops,float erosionCoef,float agregationCoef) {
        while ( nbDrops > 0 ) {
            int curx = CommonRNG.getRng().between(0, hm.length-1);
            int cury = CommonRNG.getRng().between(0, hm[0].length-1);
            int dx[]={-1,0,1,-1,1,-1,0,1};
            int dy[]={-1,-1,-1,0,0,1,1,1};
            float slope=0.0f;
            float sediment=0.0f;
            do {
                int nextx=0,nexty=0,i;
                float v= hm[curx][cury];
			/* calculate slope at x,y */
                slope=0.0f;
                for (i=0; i < 8; i++ ) {
                    int nx=curx+dx[i];
                    int ny=cury+dy[i];
                    if ( nx >= 0 && nx < hm.length && ny >= 0 && ny < hm[0].length ) {
                        float nslope=v-hm[nx][ny];
                        if ( nslope > slope ) {
                            slope=nslope;
                            nextx=nx;
                            nexty=ny;
                        }
                    }
                }
                if ( slope > 0.0f ) {
/*				GET_VALUE(hm,curx,cury) *= 1.0f - (erosionCoef * slope); */
                    hm[curx][cury] -= erosionCoef * slope;
                    curx=nextx;
                    cury=nexty;
                    sediment+=slope;
                } else {
/*				GET_VALUE(hm,curx,cury) *= 1.0f + (agregationCoef*sediment); */
                    hm[curx][cury] += agregationCoef*sediment;
                }
            } while ( slope > 0.0f );
            nbDrops--;
        }
        return hm;
    }

    private int c(int lo, int hi) {
        return CommonRNG.getRng().between(lo, hi);
    }

    private int avg(double a, double b) {
        return (int) Math.floor((a+b)/2);
    }

    public SpillWorldMap getWorldMap() {
        return worldMap;
    }

    public List<List<Double>> getDetailCircleMask() {
        return detailCircleMask;
    }

    public int getDetailWidth() {
        return detailWidth;
    }

    public float[][] getDetailHeightMap() {
        return detailHeightmap;
    }

    public float[][] getDetailAtmosphere() {
        return detailAtmosphere;
    }

    public int getDetailHeightmapWidth() {
        return detailHeightmapWidth;
    }

    public void dispose() {
    }
}

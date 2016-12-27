package com.carterza.universe.generator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.carterza.common.CommonRNG;
import com.carterza.math.Vec2I;
import com.carterza.universe.Galaxy;
import com.carterza.universe.StarSystem;
import com.carterza.universe.Universe;
import squidpony.squidmath.Coord;
import squidpony.squidmath.CrossHash;
import squidpony.squidmath.PerlinNoise;

/**
 * Created by zachcarter on 12/9/16.
 */
public class GalaxyGenerator implements IGenerator {
    // Galaxy Generation Constants
    private final int NUMHUB   = 2000; // Number of stars in the core (Example: 2000)
    private final int NUMDISK  = 4000; // Number of stars in the disk (Example: 4000)
    private final int NUMHUBDISK = NUMHUB + NUMDISK;
    private double DISKRAD  = 90.0; // Radius of the disk (Example: 90.0)
    private double HUBRAD   = 45.0; // Radius of the hub (Example: 45.0)
    private int NUMARMS  = 2; // Number of arms (Example: 3)
    private double ARMROTS  = 1.0; // 0.45 // Tightness of winding (Example: 0.5)
    private double ARMWIDTH = 60.0; // 15.0 // Arm width in degrees (Not affected by number of arms or rotations)
    private double FUZZ     = 25.0; // 25.0 // Maximum outlier distance from arms (Example: 25.0)

    public final static char[] STARS = {' ', '*', '*', '*', '*', '*', '*', '*'};

    private Vec2I[] starPositions;
    private int[][] map;
    private StarSystem[][] buffer;

    Universe universe;
    Galaxy galaxy;

    public GalaxyGenerator(Galaxy galaxy, Universe universe) {
        this.galaxy = galaxy;
        this.universe = universe;
        map = new int[Galaxy.SIZE][Galaxy.SIZE];
        buffer = new StarSystem[Galaxy.SIZE][Galaxy.SIZE];
        for(int x = 0; x < Galaxy.SIZE; x++) {
            for(int y = 0; y < Galaxy.SIZE; y++) {
                map[x][y] = -1;
                buffer[x][y] = new StarSystem();
            }
        }

        galaxy.setHash(Double.valueOf(Math.floor(CommonRNG.getRng().nextDouble() * 100000000000.0)).toString(16) + "gal");
    }

    private void generateGalaxy() {
        int i;
        float angle;
        double armSeparation, dist, maxim = 0;
        starPositions = new Vec2I[NUMHUBDISK];
        for (i = 0; i < NUMHUBDISK; ++i) {
            starPositions[i] = new Vec2I(0, 0);
        }

        // Arms
        armSeparation = 360.0 / NUMARMS;
        for (i = 0; i < NUMDISK; ++i) {
            dist = HUBRAD + CommonRNG.getRng().nextDouble() * DISKRAD;
            double armSeparationFactor = CommonRNG.getRng().nextDouble() * NUMARMS;
            armSeparationFactor = armSeparationFactor > 0 ? Math.floor(armSeparationFactor) : Math.ceil(armSeparationFactor);
            angle = (float) ((360.0 * ARMROTS * (dist / DISKRAD)) +
                    CommonRNG.getRng().nextDouble() * ARMWIDTH + // move the point further around by a random factor up to ARMWIDTH
                    (armSeparation * (armSeparationFactor + 1)) + // multiply the angle by a factor of armSeparation, putting the point into one of the arms
                    CommonRNG.getRng().nextDouble() * FUZZ * 2.0 - FUZZ); // add a further random factor, fuzzing the edge of the arms
            //  Convert to cartesian
            starPositions[i] = new Vec2I(
                    (int) (MathUtils.cosDeg(angle) * dist)
                    , (int) (MathUtils.sinDeg(angle) * dist)
            );
            maxim = Math.max(maxim, dist);
        }

        // Center
        for (i = NUMDISK; i < NUMHUBDISK; ++i) {
            dist = CommonRNG.getRng().nextDouble() * HUBRAD;
            angle = (float) (CommonRNG.getRng().nextDouble() * 360);
            starPositions[i] = new Vec2I(
                    (int) (MathUtils.cosDeg(angle) * dist)
                    , (int) (MathUtils.sinDeg(angle) * dist)
            );
            maxim = Math.max(maxim, dist);
        }

        // Fit the galaxy to the requested size
        int sx, sy;
        double factor = Galaxy.SIZE / (maxim * 2);
        for (i = 0; i < NUMHUBDISK; ++i) {
            sx = mapRange(starPositions[i].x, -maxim, maxim, 0, Galaxy.SIZE-1);
            sx = (int) (sx > 0 ? Math.floor(sx) : Math.ceil(sx));
            sy = mapRange(starPositions[i].y, -maxim, maxim, 0, Galaxy.SIZE-1);
            sy = (int) (sy > 0 ? Math.floor(sy) : Math.ceil(sy));
            map[sy][sx] = (int) Math.min(255, Math.floor(map[sy][sx])+1);
        }

        for(int x = 0; x < Galaxy.SIZE; x++) {
            for(int y = 0; y < Galaxy.SIZE; y++) {
                float bg = (float) PerlinNoise.noise(x*.8,y*.8, CrossHash.hash("galaxy_noise"));
                float star = Math.min(100 + map[x][y] * 20, 255);
                if(Double.isNaN(star)) {
                    map[x][y] = 0;
                    buffer[x][y] = new StarSystem(
                            galaxy
                            , universe
                            , ' '
                            , new Color(star/255, star/255, star/255, 1)
                            , new Color(bg, bg, bg, 1)
                            , Coord.get(x, y)
                    );
                } else {
                    int starIndex = mapRange((int) star, 100, 255, 0, STARS.length-1);
                    starIndex = (int) (starIndex > 0 ? Math.floor(starIndex) : Math.ceil(starIndex));
                    map[x][y] = STARS[starIndex] == ' ' ? 0 : 1;
                    buffer[x][y] = new StarSystem(
                            galaxy
                            , universe
                            , STARS[starIndex]
                            , new Color(star/255, star/255, star/255, 1)
                            , new Color(bg,bg,bg,1)
                            , Coord.get(x, y)
                    );
                }
            }
        }
    }

    private int mapRange(int x, double in_min, double in_max, int out_min, int out_max) {
        return (int) ((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }

    public StarSystem[][] getBuffer() {
        return buffer;
    }

    public int[][] getMap() { return map; }

    @Override
    public void generate() {
        generateGalaxy();
    }
}

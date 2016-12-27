package com.carterza.universe.generator;

import com.badlogic.gdx.graphics.Color;
import com.carterza.common.CommonRNG;
import com.carterza.universe.*;
import squidpony.squidmath.Coord;
import squidpony.squidmath.CrossHash;
import squidpony.squidmath.PerlinNoise;

/**
 * Created by zachcarter on 12/9/16.
 */
public class StarSystemGenerator implements IGenerator {

    Universe universe;
    Galaxy galaxy;
    StarSystem starSystem;

    final char[] STARS = new char[]{'*', '*', '*', '*', '*', '*', '*'};

    SolarSystem[][] buffer;

    public StarSystemGenerator(StarSystem starSystem, Galaxy galaxy, Universe universe) {
        this.universe = universe;
        this.galaxy = galaxy;
        this.starSystem = starSystem;
        buffer = new SolarSystem[StarSystem.STARSYSTEM_WIDTH][StarSystem.STARSYSTEM_HEIGHT];
        for(int x = 0; x < StarSystem.STARSYSTEM_WIDTH; x++) {
            for(int y = 0; y < StarSystem.STARSYSTEM_HEIGHT; y++) {
                buffer[x][y] = new SolarSystem();
            }
        }
    }

    @Override
    public void generate() {
        generateStarSystem();
    }

    private void generateStarSystem() {
        CommonRNG.setSeed("starsystem" + starSystem.getPosition().x + starSystem.getPosition().y + galaxy.getHash());

        StarSystem neighbor = (StarSystem) galaxy.neighbors(0,0);
        double bright = neighbor.getSymbol() == ' ' ? 0 : neighbor.getFg().r;
        double starThreshold = 0.95 - 0.2 * bright;
        double fogfactor = (neighbor.getBg().r * 255.0f) / galaxy.NEBULA_FADE / 255.0f;
        double coverage = 0.3 + 0.5 * fogfactor;
        double nebulascale = 0.02 + 0.02 * fogfactor;
        double colorscale = 0.03;

        final String hash = Double.valueOf(Math.floor(CommonRNG.getRng().nextDouble() * 100000000000.0)).toString(16) + "sys";
        starSystem.setHash(hash);

        for(int x = 0; x < StarSystem.STARSYSTEM_WIDTH; x++) {
            for(int y = 0; y < StarSystem.STARSYSTEM_HEIGHT; y++) {
                float star = (float) PerlinNoise.noise(
                        x*10
                        , y*10
                        , CrossHash.hash("starsystem_star")
                                + starSystem.getPosition().x
                                + starSystem.getPosition().y
                                + CrossHash.hash(hash) * Math.PI
                );

                char symbol = ' ';
                String description = "";

                if (star > starThreshold) {
                    star = GeneratorUtils.convertNoise(PerlinNoise.noise(
                            x*100
                            ,y*100
                            , CrossHash.hash("starsystem_startype")
                                    + starSystem.getPosition().x
                                    + starSystem.getPosition().y
                                    + CrossHash.hash(hash)
                    ));
                    symbol = STARS[(int) (star / 256 * STARS.length)];
                    star = Math.min(star+30, 255);
                    description = "Solar system";
                } else if (star > starThreshold * 0.9) {
                    symbol = '·';
                    star = 30;
                }

                double mask = PerlinNoise.noise(
                        x*nebulascale
                        ,y*nebulascale
                        , CrossHash.hash("starsystem_exp")
                                + starSystem.getPosition().x
                                + starSystem.getPosition().y
                                + CrossHash.hash(hash)
                );
                mask = GeneratorUtils.expFilter(mask, coverage, 0.9999);
                double i = x*colorscale;
                double j = y*colorscale;

                float br = GeneratorUtils.convertNoise(
                        PerlinNoise.noise(
                                i
                                ,j
                                , CrossHash.hash("starsystem_r")
                                        + starSystem.getPosition().x
                                        + starSystem.getPosition().y
                                        + CrossHash.hash(hash)) * mask);
                float bg = GeneratorUtils.convertNoise(
                        PerlinNoise.noise(
                                i
                                ,j
                                , CrossHash.hash("starsystem_g")
                                        + starSystem.getPosition().x
                                        + starSystem.getPosition().y
                                        + CrossHash.hash(hash)) * mask);
                float bb = GeneratorUtils.convertNoise(
                        PerlinNoise.noise(
                                i
                                ,j
                                , CrossHash.hash("starsystem_bb")
                                        + starSystem.getPosition().x
                                        + starSystem.getPosition().y
                                        + CrossHash.hash(hash)) * mask);
                float minNeb = Math.max(Math.max(br, bg), bb);
                star = Math.min(star + minNeb, 255);

                SolarSystem solarSystem = new SolarSystem(
                        starSystem
                        , galaxy
                        , universe
                        , symbol
                        , new Color(star/255, star/255, star/255, 1)
                        , new Color(br/255, bg/255, bb/255, 1)
                        , Coord.get(x, y)
                );
                if (mask > 0.1) solarSystem.setNebula(true);
                if (solarSystem.isNebula() && description.length() == 0) solarSystem.setDescription("Nebula");
                else if (description.length() > 0) solarSystem.setDescription(description);
                else solarSystem.setDescription("Vast empty space");
                buffer[x][y] = solarSystem;
            }
        }
    }

    public SolarSystem[][] getBuffer() {
        return buffer;
    }
}

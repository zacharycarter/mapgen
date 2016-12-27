package com.carterza.universe.generator;

import com.carterza.universe.*;
import squidpony.squidgrid.mapping.MetsaMapFactory;
import squidpony.squidgrid.mapping.SpillWorldMap;
import squidpony.squidmath.Coord;

import java.util.List;

/**
 * Created by zachcarter on 12/10/16.
 */
public class PlanetGeneratorBak implements IGenerator {

    private PlanetMapGenerator mapFactory;

    private double highn = 0;
    private int[][] biomeMap;
    private double[][] map;
    private List<Coord> cities;
    private List<Coord> dungeons;

    public PlanetGeneratorBak(Planet planet, SolarSystem solarSystem, StarSystem starSystem, Galaxy galaxy, Universe universe) {
        mapFactory = new PlanetMapGenerator(Planet.PLANET_WIDTH, Planet.PLANET_HEIGHT);
    }

    @Override
    public void generate() {
        System.out.println("Generating planet!");
        map = mapFactory.getHeightMap();
        biomeMap = mapFactory.makeBiomeMap();
        mapFactory.makeWeightedMap();
        highn = mapFactory.getMaxPeak();
        cities = mapFactory.getCities();
        //dungeons = mapFactory.getDungeons();
    }

    public double getHighn() {
        return highn;
    }

    public int[][] getBiomeMap() {
        return biomeMap;
    }

    public double[][] getMap() {
        return map;
    }

    public List<Coord> getCities() {
        return cities;
    }

    public PlanetMapGenerator getMapFactory() {
        return mapFactory;
    }

    public List<Coord> getDungeons() {
        return dungeons;
    }
}

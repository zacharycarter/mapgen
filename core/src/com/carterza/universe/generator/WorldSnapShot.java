package com.carterza.universe.generator;

import com.badlogic.gdx.math.MathUtils;

import java.util.List;

public class WorldSnapShot
{
    // Height of world

    float[][] world;
    // Height of water
    float[][] water;
    // Height of snow.
    float[][] snow;
    // Mass of water vapor.
    float[][] waterVapor;
    // Amount of vegetation
    float[][] vegetation;
    // People in the world
    List<Citizen> citizens;
    // Cities of the world
    List<City> cities;

    public WorldSnapShot
            (
                    final float[][] world,
                    final float[][] water,
                    final float[][] snow,
                    final float[][] waterVapor,
                    float[][] vegetation,
                    List<Citizen> citizens,
                    List<City> cities
            )
    {
        this.world = world;
        this.water = water;
        this.snow = snow;
        this.waterVapor = waterVapor;
        this.vegetation = vegetation;
        this.citizens = citizens;
        this.cities = cities;
    }

    public float[][] getWorldData()
    {
        return world;
    }

    public float getWorld(float x, float y)
    {
        return world[(int) (x * world[0].length)][(int) (y * world.length)];
    }

    public float[][] getWater()
    {
        return water;
    }

    public float getWater(float x, float y)
    {
        return water[(int) (x * water[0].length)][(int) (y * water.length)];
    }

    public float[][] getSnow()
    {
        return snow;
    }

    public float getSnow(float x, float y)
    {
        return snow[(int) (x * snow[0].length)][(int) (y * snow.length)];
    }

    public float[][] getWaterVapor()
    {
        return waterVapor;
    }

    public float getWaterVapor(float x, float y)
    {
        return waterVapor[(int) (x * waterVapor[0].length)][(int) (y * waterVapor.length)];
    }

    public float[][] getVegetation()
    {
        return vegetation;
    }

    public float getVegetation(float u, float v)
    {
        int x = (int) (u * vegetation[0].length);
        int y = (int) (v * vegetation.length);
        float vo = vegetation[x][y];
        float ve = vegetation[MathUtils.clamp(x + 1, 0, vegetation[0].length - 1)][y];
        float vw = vegetation[MathUtils.clamp(x - 1, 0, vegetation[0].length - 1)][y];
        float vs = vegetation[x][MathUtils.clamp(y + 1, 0, vegetation.length - 1)];
        float vn = vegetation[x][MathUtils.clamp(y - 1, 0, vegetation.length - 1)];
        return (vo + ve + vw + vs + vn) / 5;
    }

    public List<Citizen> getCitizens()
    {
        return citizens;
    }

    public List<City> getCities()
    {
        return cities;
    }
}

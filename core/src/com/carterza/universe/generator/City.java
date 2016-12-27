package com.carterza.universe.generator;

class City
{
    int x, y, population;
    Culture culture;

    public City(int x, int y, int population, final Culture culture)
    {
        this.x = x;
        this.y = y;
        this.population = population;
        this.culture = culture;
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public int getPopulation()
    {
        return population;
    }
    public void setPopulation(int population)
    {
        this.population = population;
    }
    public Culture getCulture()
    {
        return culture;
    }
}

package com.carterza.universe.generator;

/**
 * Created by zachcarter on 12/11/16.
 */
class Citizen
{
    int x,y;
    Culture culture;

    public Citizen(int x, int y, final Culture culture)
    {
        this.x = x;
        this.y = y;
        this.culture = culture;
    }
    public int getX()
    {
        return x;
    }
    public void setX(int x)
    {
        this.x = x;
    }
    public int getY()
    {
        return y;
    }
    public void setY(int y)
    {
        this.y = y;
    }
    public Culture getCulture()
    {
        return culture;
    }
}

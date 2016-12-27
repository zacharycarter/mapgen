package com.carterza.universe;

import com.badlogic.gdx.graphics.Color;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

/**
 * Created by zachcarter on 12/10/16.
 */
public class CelestialBody extends BaseRoom {
    char symbol;
    Color color;
    Color bgColor;
    String description;
    Coord position;

    public CelestialBody(char symbol, Color color, String description) {
        this.symbol = symbol;
        this.color = color;
        this.description = description;
    }

    public CelestialBody(char symbol, Color color, String description, Universe universe) {
        super(universe);
        this.symbol = symbol;
        this.color = color;
        this.description = description;
    }

    public CelestialBody(char symbol, Color color, Color bgColor, String description, Universe universe) {
        super(universe);
        this.symbol = symbol;
        this.color = color;
        this.bgColor = bgColor;
        this.description = description;
    }

    public CelestialBody() {

    }

    public void setPosition(Coord position) {
        this.position = position;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public char getSymbol() {
        return symbol;
    }

    public Color getColor() {
        return color;
    }

    public Coord getPosition() {
        return position;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public Color getBgColor() {
        return bgColor;
    }
}

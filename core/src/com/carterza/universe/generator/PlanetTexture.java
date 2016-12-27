package com.carterza.universe.generator;

import com.badlogic.gdx.graphics.Color;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Created by zachcarter on 12/10/16.
 */
public class PlanetTexture {
    char symbol;
    SColor fg;
    SColor bg;
    String description;

    public PlanetTexture(char symbol, SColor fg, String description) {
        this.symbol = symbol;
        this.fg = fg;
        this.description = description;
        this.bg = new SColor(0,0,0,1);
    }

    public PlanetTexture() {

    }

    public char getSymbol() {
        return symbol;
    }

    public SColor getFg() {
        return fg;
    }

    public SColor getBg() {
        return bg;
    }
}

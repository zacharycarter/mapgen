package com.carterza.universe.generator;

/**
 * Created by zachcarter on 12/10/16.
 */
public class HeightTexture {
    PlanetTexture planetTexture;
    double minH;

    public HeightTexture(PlanetTexture planetTexture, double minH) {
        this.planetTexture = planetTexture;
        this.minH = minH;
    }

    public HeightTexture(HeightTexture heightTexture) {
        this.planetTexture = heightTexture.planetTexture;
        this.minH = heightTexture.minH;
    }

    public HeightTexture() {

    }

    public HeightTexture(PlanetTexture planetTexture) {
        this.planetTexture = planetTexture;
    }

    public void setDescription(final String description) {
        planetTexture.description = description;
    }

    public PlanetTexture getPlanetTexture() {
        return planetTexture;
    }
}

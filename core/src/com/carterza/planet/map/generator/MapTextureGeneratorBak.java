/*
package com.carterza.planet.map.generator;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.carterza.planet.map.*;
import squidpony.squidgrid.gui.gdx.SquidColorCenter;

import java.util.List;

*/
/**
 * Created by zachcarter on 12/15/16.
 *//*

public class MapTextureGeneratorBak {
    private static SquidColorCenter squidColorCenter = new SquidColorCenter();

    // Heightmap colors
    private static Color DeepColor = new Color(0, 0, 0.5f, 1);
    private static Color ShallowColor = new Color(25/255f, 25/255f, 150/255f, 1);
    private static Color RiverColor = new Color(30/255f, 120/255f, 200/255f, 1);
    private static Color SandColor = new Color(240 / 255f, 240 / 255f, 64 / 255f, 1);
    private static Color GrassColor = new Color(50 / 255f, 220 / 255f, 20 / 255f, 1);
    private static Color ForestColor = new Color(16 / 255f, 160 / 255f, 0, 1);
    private static Color RockColor = new Color(0.5f, 0.5f, 0.5f, 1);
    private static Color SnowColor = new Color(1, 1, 1, 1);

    private static Color IceWater = new Color (210/255f, 255/255f, 252/255f, 1);
    private static Color ColdWater = new Color (119/255f, 156/255f, 213/255f, 1);
    private static Color RiverWater = new Color (65/255f, 110/255f, 179/255f, 1);

    // Heat map colors
    private static Color Coldest = new Color(0, 1, 1, 1);
    private static Color Colder = new Color(170/255f, 1, 1, 1);
    private static Color Cold = new Color(0, 229/255f, 133/255f, 1);
    private static Color Warm = new Color(1, 1, 100/255f, 1);
    private static Color Warmer = new Color(1, 100/255f, 0, 1);
    private static Color Warmest = new Color(241/255f, 12/255f, 0, 1);

    // Moisture map colors
    private static Color Dryest = new Color(255/255f, 139/255f, 17/255f, 1);
    private static Color Dryer = new Color(245/255f, 245/255f, 23/255f, 1);
    private static Color Dry = new Color(80/255f, 255/255f, 0/255f, 1);
    private static Color Wet = new Color(85/255f, 255/255f, 255/255f, 1);
    private static Color Wetter = new Color(20/255f, 70/255f, 255/255f, 1);
    private static Color Wettest = new Color(0/255f, 0/255f, 100/255f, 1);

    // Biome map colors
    private static Color Ice = Color.WHITE;
    private static Color Desert = new Color(238/255f, 218/255f, 130/255f, 1);
    private static Color Savanna = new Color(177/255f, 209/255f, 110/255f, 1);
    private static Color TropicalRainforest = new Color(66/255f, 123/255f, 25/255f, 1);
    private static Color Tundra = new Color(96/255f, 131/255f, 112/255f, 1);
    private static Color TemperateRainforest = new Color(29/255f, 73/255f, 40/255f, 1);
    private static Color Grassland = new Color(164/255f, 225/255f, 99/255f, 1);
    private static Color SeasonalForest = new Color(73/255f, 100/255f, 35/255f, 1);
    private static Color BorealForest = new Color(95/255f, 115/255f, 62/255f, 1);
    private static Color Woodland = new Color(139/255f, 175/255f, 90/255f, 1);

    public static Texture generateWaterMapTexture(int width, int height, List<TileGroup> waters, List<TileGroup> lands) {
        Pixmap pm = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Texture texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        for(TileGroup tileGroup : waters) {
            for(Tile tile : tileGroup.tiles) {
                switch (tile.heightType) {
                    default:
                        pm.drawPixel(tile.x, tile.y, Color.rgba8888(DeepColor));
                        break;
                }
            }
        }

        for(TileGroup tileGroup : lands) {
            for(Tile tile : tileGroup.tiles) {
                switch (tile.heightType) {
                    default:
                        pm.drawPixel(tile.x, tile.y, Color.rgba8888(GrassColor));
                        break;
                }
            }
        }

        PixmapIO.writePNG(Gdx.files.getFileHandle("watermap.png", Files.FileType.Local), pm);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pm, 0, 0);
        return texture;
    }

    public static Texture generateBiomeMapTexture(int width, int height, Tile[][] tiles, float coldest, float colder, float cold) {
        Pixmap pm = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Texture texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        int color, tgt;
        float degree = 0.35f, inv = 1f - degree;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                BiomeType value = tiles[x][y].biomeType;

                if(value != null) {

                    switch (value) {
                        case Ice:
                            pm.drawPixel(x, y, Color.rgba8888(Ice));
                            break;
                        case BorealForest:
                            pm.drawPixel(x, y, Color.rgba8888(BorealForest));
                            break;
                        case Desert:
                            pm.drawPixel(x, y, Color.rgba8888(Desert));
                            break;
                        case Grassland:
                            pm.drawPixel(x, y, Color.rgba8888(Grassland));
                            break;
                        case SeasonalForest:
                            pm.drawPixel(x, y, Color.rgba8888(SeasonalForest));
                            break;
                        case Tundra:
                            pm.drawPixel(x, y, Color.rgba8888(Tundra));
                            break;
                        case Savanna:
                            pm.drawPixel(x, y, Color.rgba8888(Savanna));
                            break;
                        case TemperateRainforest:
                            pm.drawPixel(x, y, Color.rgba8888(TemperateRainforest));
                            break;
                        case TropicalRainforest:
                            pm.drawPixel(x, y, Color.rgba8888(TropicalRainforest));
                            break;
                        case Woodland:
                            pm.drawPixel(x, y, Color.rgba8888(Woodland));
                            break;
                    }
                }

                // Water tiles
                if (tiles[x][y].heightType == HeightType.DeepWater) {
                    pm.drawPixel(x, y, Color.rgba8888(DeepColor));
                }
                else if (tiles[x][y].heightType == HeightType.ShallowWater) {
                    pm.drawPixel(x, y, Color.rgba8888(ShallowColor));
                }

                // draw riverPaths
                if (tiles[x][y].heightType == HeightType.River)
                {
                    float heatValue = tiles[x][y].heatValue;

                    if (tiles[x][y].heatType == HeatType.Coldest)
                        pm.drawPixel(x,y, Color.rgba8888(IceWater.cpy().lerp(ColdWater, (heatValue) / (coldest))));
                    else if (tiles[x][y].heatType == HeatType.Colder)
                        pm.drawPixel(x,y, Color.rgba8888(ColdWater.cpy().lerp(RiverWater, (heatValue - coldest) / (colder - coldest))));
                    else if (tiles[x][y].heatType == HeatType.Cold)
                        pm.drawPixel(x,y, Color.rgba8888(RiverWater.cpy().lerp(ShallowColor, (heatValue - colder) / (cold - colder))));
                    else
                        pm.drawPixel(x, y, Color.rgba8888(ShallowColor));
                }


                // add a outline
                if (tiles[x][y].heightType.getNumVal() >= HeightType.Shore.getNumVal() && tiles[x][y].heightType != HeightType.River)
                {
                    if (tiles[x][y].biomeBitmask != 15) {
                        color = pm.getPixel(x,y);
                        tgt = 0xff;
                        pm.drawPixel(x, y, ((int)((color >>> 24) * inv + (tgt >>> 24) * degree) << 24) | ((int)((color >>> 16 & 0xff) * inv + (tgt >>> 16 & 0xff) * degree) << 16) | ((int)((color >>> 8 & 0xff) * inv + (tgt >>> 8 & 0xff) * degree) << 8) | 0xff);
                        */
/*pm.drawPixel(x, y, Color.rgba8888(new Color(pm.getPixel(x, y)).lerp(Color.BLACK, 0.35f)));*//*

                    }

                }
            }
        }
        PixmapIO.writePNG(Gdx.files.getFileHandle("biomemap.png", Files.FileType.Local), pm);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pm, 0, 0);
        return texture;
    }

    public static Texture generateMoistureMapTexture(int width, int height, Tile[][] tiles) {
        Pixmap pm = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Texture texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                switch (tiles[x][y].moistureType)
                {
                    case Dryest:
                        pm.drawPixel(x, y, Color.rgba8888(Dryest));
                        break;
                    case Dryer:
                        pm.drawPixel(x, y, Color.rgba8888(Dryer));
                        break;
                    case Dry:
                        pm.drawPixel(x, y, Color.rgba8888(Dry));
                        break;
                    case Wet:
                        pm.drawPixel(x, y, Color.rgba8888(Wet));
                        break;
                    case Wetter:
                        pm.drawPixel(x, y, Color.rgba8888(Wetter));
                        break;
                    default:
                        pm.drawPixel(x, y, Color.rgba8888(Wettest));
                        break;
                }
            }
        }
        PixmapIO.writePNG(Gdx.files.getFileHandle("moisturemap.png", Files.FileType.Local), pm);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pm, 0, 0);
        return texture;
    }

    public static Texture generateHeatMapTexture(int width, int height, Tile[][] tiles) {
        Pixmap pm = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Texture texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
               switch (tiles[x][y].heatType)
                {
                    case Coldest:
                        pm.drawPixel(x, y, Color.rgba8888(Coldest));
                        break;
                    case Colder:
                        pm.drawPixel(x, y, Color.rgba8888(Colder));
                        break;
                    case Cold:
                        pm.drawPixel(x, y, Color.rgba8888(Cold));
                        break;
                    case Warm:
                        pm.drawPixel(x, y, Color.rgba8888(Warm));
                        break;
                    case Warmer:
                        pm.drawPixel(x, y, Color.rgba8888(Warmer));
                        break;
                    case Warmest:
                        pm.drawPixel(x, y, Color.rgba8888(Warmest));
                        break;
                }

                if (tiles[x][y].heightType.getNumVal() > 2 && tiles[x][y].bitmask != 15)
                    pm.drawPixel(x, y, Color.rgba8888(new Color(pm.getPixel(x,y)).lerp(Color.BLACK, 0.4f)));
            }

        }
        PixmapIO.writePNG(Gdx.files.getFileHandle("heatmap.png", Files.FileType.Local), pm);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pm, 0, 0);
        return texture;
    }


    public static Texture generateHeightMapTexture(int width, int height, Tile[][] tiles)
    {
        Pixmap pm = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Texture texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {

                switch (tiles[x][y].heightType)
                {
                    case DeepWater:
                        pm.drawPixel(x, y, Color.rgba8888(DeepColor));
                        break;
                    case ShallowWater:
                        pm.drawPixel(x, y, Color.rgba8888(ShallowColor));
                        break;
                    case Sand:
                        pm.drawPixel(x, y, Color.rgba8888(SandColor));
                        break;
                    case Grass:
                        pm.drawPixel(x, y, Color.rgba8888(GrassColor));
                        break;
                    case Forest:
                        pm.drawPixel(x, y, Color.rgba8888(ForestColor));
                        break;
                    case Rock:
                        pm.drawPixel(x, y, Color.rgba8888(RockColor));
                        break;
                    case Snow:
                        pm.drawPixel(x, y, Color.rgba8888(SnowColor));
                        break;
                    case River:
                        pm.drawPixel(x, y, Color.rgba8888(RiverColor));
                        break;
                }

                //darken the color if a edge tile
                if (tiles[x][y].heightType.getNumVal() > 2 && tiles[x][y].bitmask != 15)
                    pm.drawPixel(x,y, Color.rgba8888(new Color(pm.getPixel(x,y)).lerp(Color.BLACK, 0.4f)));


            }
        }

        PixmapIO.writePNG(Gdx.files.getFileHandle("heightmap.png", Files.FileType.Local), pm);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pm, 0, 0);
        return texture;
    }
}
*/

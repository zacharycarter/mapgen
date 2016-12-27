package com.carterza.planet.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachcarter on 12/15/16.
 */


public class TileGroup  {

    public enum TileGroupType
    {
        Water,
        Land
    }

    public TileGroupType type;
    public List<Tile> tiles;

    public TileGroup()
    {
        tiles = new ArrayList<>();
    }

    public boolean containsTile(Tile tile) {
        for(Tile t : tiles) {
            if(t.equals(tile)) return true;
        }

        return false;
    }
}

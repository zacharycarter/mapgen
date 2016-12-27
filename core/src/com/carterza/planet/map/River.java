package com.carterza.planet.map;

import com.carterza.common.Direction;
import squidpony.squidmath.Coord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachcarter on 12/15/16.
 */
public class River {
    public int length;
    public List<Coord> path;
    public int id;

    public int intersections;
    public float turns;
    public Direction currentDirection;

    public River(int id)
    {
        id = id;
        path = new ArrayList<>();
    }

    public River(int id, List<Coord> path)
    {
        this.id = id;
        this.path = path;
    }

    public void addTile(Tile tile)
    {
        tile.setRiverPath(this);
        path.add (Coord.get(tile.x, tile.y));
    }
}

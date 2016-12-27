package com.carterza.planet.map;

import com.carterza.common.Direction;
import com.carterza.planet.map.generator.MapGenerator;
import squidpony.squidmath.Coord;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    public HeightType heightType;
    public double heightValue;
    public double originalHeightValue;

    public HeatType heatType;
    public double heatValue;

    public MoistureType moistureType;
    public double moistureValue;

    public BiomeType biomeType;

    public int x, y;
    public int bitmask, biomeBitmask;

    public Tile left;
    public Tile right;
    public Tile top;
    public Tile bottom;


    public boolean collidable;
    public boolean floodFilled;

    public List<River> rivers = new ArrayList<>();

    public int riverSize;

    public Tile()
    {
    }

    public void updateBitmask()
    {
        int count = 0;

        if (top.heightType == heightType)
            count += 1;
        if (right.heightType == heightType)
            count += 2;
        if (bottom.heightType == heightType)
            count += 4;
        if (left.heightType == heightType)
            count += 8;

        bitmask = count;
    }

    public void updateBiomeBitmask()
    {
        int count = 0;

        if (collidable && top != null && top.biomeType == biomeType)
            count += 1;
        if (collidable && bottom != null && bottom.biomeType == biomeType)
            count += 4;
        if (collidable && left != null && left.biomeType == biomeType)
            count += 8;
        if (collidable && right != null && right.biomeType == biomeType)
            count += 2;

        biomeBitmask = count;
    }

    public int getRiverNeighborCount(River river)
    {
        int count = 0;
        if (left.rivers.size() > 0 && left.rivers.contains (river))
            count++;
        if (right.rivers.size() > 0 && right.rivers.contains (river))
            count++;
        if (top.rivers.size() > 0 && top.rivers.contains (river))
            count++;
        if (bottom.rivers.size() > 0 && bottom.rivers.contains (river))
            count++;
        return count;
    }

    public Direction getLowestNeighbor(MapGenerator generator)
    {
        double l = generator.getHeightValue(left);
        double r = generator.getHeightValue(right);
        double b = generator.getHeightValue(bottom);
        double t = generator.getHeightValue(top);

        if (l < r && l < t && l < b)
            return Direction.W;
        else if (r < l && r < t && r < b)
            return Direction.E;
        else if (t < l && t < r && t < b)
            return Direction.N;
        else if (b < t && b < r && b < l)
            return Direction.S;
        else
            return Direction.O;

    }

    public void setRiverPath(River river)
    {
        if (!collidable)
            return;

        if (!rivers.contains (river)) {
            rivers.add (river);
        }
    }

    private void setRiverTile(River river)
    {
        setRiverPath (river);
        heightType = HeightType.River;
        collidable = false;
    }

    // This function got messy.  Sorry.
    public void digRiver(River river, int size)
    {
        setRiverTile (river);
        riverSize = size;

        if (size == 1) {
            if (bottom != null)
            {
                bottom.setRiverTile (river);
                if (bottom.right != null) bottom.right.setRiverTile (river);
            }
            if (right != null) right.setRiverTile (river);
        }

        if (size == 2) {
            if (bottom != null) {
                bottom.setRiverTile (river);
                if (bottom.right != null) bottom.right.setRiverTile (river);
            }
            if (right != null) {
                right.setRiverTile (river);
            }
            if (top != null) {
                top.setRiverTile (river);
                if (top.left != null) top.left.setRiverTile (river);
                if (top.right != null)top.right.setRiverTile (river);
            }
            if (left != null) {
                left.setRiverTile (river);
                if (left.bottom != null) left.bottom.setRiverTile (river);
            }
        }

        if (size == 3) {
            if (bottom != null) {
                bottom.setRiverTile (river);
                if (bottom.right != null) bottom.right.setRiverTile (river);
                if (bottom.bottom != null)
                {
                    bottom.bottom.setRiverTile (river);
                    if (bottom.bottom.right != null) bottom.bottom.right.setRiverTile (river);
                }
            }
            if (right != null) {
                right.setRiverTile (river);
                if (right.right != null)
                {
                    right.right.setRiverTile (river);
                    if (right.right.bottom != null) right.right.bottom.setRiverTile (river);
                }
            }
            if (top != null) {
                top.setRiverTile (river);
                if (top.left != null) top.left.setRiverTile (river);
                if (top.right != null)top.right.setRiverTile (river);
            }
            if (left != null) {
                left.setRiverTile (river);
                if (left.bottom != null) left.bottom.setRiverTile (river);
            }
        }

        if (size == 4) {

            if (bottom != null) {
                bottom.setRiverTile (river);
                if (bottom.right != null) bottom.right.setRiverTile (river);
                if (bottom.bottom != null)
                {
                    bottom.bottom.setRiverTile (river);
                    if (bottom.bottom.right != null) bottom.bottom.right.setRiverTile (river);
                }
            }
            if (right != null) {
                right.setRiverTile (river);
                if (right.right != null)
                {
                    right.right.setRiverTile (river);
                    if (right.right.bottom != null) right.right.bottom.setRiverTile (river);
                }
            }
            if (top != null) {
                top.setRiverTile (river);
                if (top.right != null) {
                    top.right.setRiverTile (river);
                    if (top.right.right != null) top.right.right.setRiverTile (river);
                }
                if (top.top != null)
                {
                    top.top.setRiverTile (river);
                    if (top.top.right != null) top.top.right.setRiverTile (river);
                }
            }
            if (left != null) {
                left.setRiverTile (river);
                if (left.bottom != null) {
                    left.bottom.setRiverTile (river);
                    if (left.bottom.bottom != null) left.bottom.bottom.setRiverTile (river);
                }

                if (left.left != null) {
                    left.left.setRiverTile (river);
                    if (left.left.bottom != null) left.left.bottom.setRiverTile (river);
                    if (left.left.top != null) left.left.top.setRiverTile (river);
                }

                if (left.top != null)
                {
                    left.top.setRiverTile (river);
                    if (left.top.top != null) left.top.top.setRiverTile (river);
                }
            }
        }
    }

    public Tile[] getNeighbors() {
        return new Tile[]{left, right, top, bottom};
    }

    public Tile getNeighbor(Direction directionOfNeighbor) {
        switch (directionOfNeighbor) {
            case N:
                return top;
            case E:
                return right;
            case W:
                return left;
            case S:
                return bottom;
            case O:
                return this;
        }
        return null;
    }

    public Coord getCoord() {
        return Coord.get(x,y);
    }

    public Direction getLowestNeighborNextTo(Direction lowestNeighbor, MapGenerator generator) {
        double l = generator.getHeightValue(left);
        double r = generator.getHeightValue(right);
        double b = generator.getHeightValue(bottom);
        double t = generator.getHeightValue(top);

        switch(lowestNeighbor) {
            case N:
                if (l < r && l < t && l < b)
                    return Direction.W;
                else if (r < l && r < t && r < b)
                    return Direction.E;
                else if (b < t && b < r && b < l)
                    return Direction.S;
                else
                    return Direction.O;
            case S:
                if (l < r && l < t && l < b)
                    return Direction.W;
                else if (r < l && r < t && r < b)
                    return Direction.E;
                else if (t < l && t < r && t < b)
                    return Direction.N;
                else
                    return Direction.O;
            case E:
                if (l < r && l < t && l < b)
                    return Direction.W;
                else if (t < l && t < r && t < b)
                    return Direction.N;
                else if (b < t && b < r && b < l)
                    return Direction.S;
                else
                    return Direction.O;
            case W:
                if (r < l && r < t && r < b)
                    return Direction.E;
                else if (t < l && t < r && t < b)
                    return Direction.N;
                else if (b < t && b < r && b < l)
                    return Direction.S;
                else
                    return Direction.O;
            case O:
                if (l < r && l < t && l < b)
                    return Direction.W;
                else if (r < l && r < t && r < b)
                    return Direction.E;
                else if (t < l && t < r && t < b)
                    return Direction.N;
                else
                    return Direction.S;

        }
        return null;
    }

    public Tile getHighestNeighbor(MapGenerator generator) {
        double l = generator.getHeightValue(left);
        double r = generator.getHeightValue(right);
        double b = generator.getHeightValue(bottom);
        double t = generator.getHeightValue(top);

        if (l > r && l > t && l > b)
            return left;
        else if (r > l && r > t && r > b)
            return right;
        else if (t > l && t > r && t > b)
            return top;
        else if (b > t && b > r && b > l)
            return bottom;
        else
            return null;
    }
}

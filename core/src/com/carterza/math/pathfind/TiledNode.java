package com.carterza.math.pathfind;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;
import squidpony.squidmath.Coord;

public abstract class TiledNode<N extends TiledNode<N>> {

    public static final int TILE_EMPTY = 0;

    public static final int TILE_FLOOR = 1;

    public static final int TILE_WALL = 2;

    public Coord coord;

    public final int type;

    protected Array<Connection<N>> connections;

    public TiledNode (Coord coord, int type, Array<Connection<N>> connections) {
        this.coord = coord;
        this.type = type;
        this.connections = connections;
    }

    public abstract int getIndex ();

    public Array<Connection<N>> getConnections () {
        return this.connections;
    }

}

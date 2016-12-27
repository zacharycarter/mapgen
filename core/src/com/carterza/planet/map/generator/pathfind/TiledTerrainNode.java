package com.carterza.planet.map.generator.pathfind;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;
import com.carterza.math.pathfind.TiledNode;
import squidpony.squidmath.Coord;

public class TiledTerrainNode extends TiledNode<TiledTerrainNode> {

    final double height;

    public TiledTerrainNode(Coord coord, int type, int connectionCapacity, double height) {
        super(coord, type, new Array<Connection<TiledTerrainNode>>(connectionCapacity));
        this.height = height;
    }

    @Override
    public int getIndex() {
        return coord.x * TiledTerrainGraph.height + coord.y;
    }
}

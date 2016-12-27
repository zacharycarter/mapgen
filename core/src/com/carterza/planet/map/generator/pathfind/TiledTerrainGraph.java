package com.carterza.planet.map.generator.pathfind;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;
import com.carterza.math.pathfind.TiledGraph;
import com.carterza.planet.map.MapData;
import com.carterza.planet.map.generator.MapGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.carterza.math.pathfind.TiledNode.TILE_EMPTY;
import static com.carterza.math.pathfind.TiledNode.TILE_FLOOR;
import static com.carterza.math.pathfind.TiledNode.TILE_WALL;

/**
 * Created by zachcarter on 12/20/16.
 */
public class TiledTerrainGraph implements TiledGraph<TiledTerrainNode> {

    public static int width;
    public static int height;

    MapData heightData;
    GreasedRegion obstacles;

    protected Array<TiledTerrainNode> nodes;

    public boolean diagonal;
    public TiledTerrainNode startNode;


    public TiledTerrainGraph(final int width, final int height, MapData heightData, GreasedRegion obstacles) {
        this.width = width;
        this.height = height;
        this.heightData = heightData;
        this.obstacles = obstacles;
        this.nodes = new Array<TiledTerrainNode>(width * height);
        this.diagonal = false;
        this.startNode = null;
    }


    @Override
    public void init() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int nodeType;

                // if((heightData.data[x][y] >= MapGenerator.Sand) && !obstacles.contains(x,y))
                if((heightData.data[x][y] >= MapGenerator.ShallowWater) && !obstacles.contains(x,y))
                    nodeType = TILE_FLOOR;
                else
                    nodeType = TILE_WALL;


                nodes.add(new TiledTerrainNode(Coord.get(x,y), nodeType, 4, heightData.data[x][y]));
            }
        }

        for (int x = 0; x < width; x++) {
            int idx = x * height;
            for (int y = 0; y < height; y++) {
                TiledTerrainNode n = nodes.get(idx + y);
                if (x > 0) addConnection(n, -1, 0); // W
                // if (x > 0 && y > 0) addConnection(n, -1, -1); // SW
                if (y > 0) addConnection(n, 0, -1); // S
                // if (x < width -1 && y > 0) addConnection(n, 1, -1); // SE
                if (x < width - 1) addConnection(n, 1, 0); // E
                // if (x < width - 1 && y < height - 1) addConnection(n, 1, 1); // NE
                if (y < height - 1) addConnection(n, 0, 1); // N
                // if (x > 0 && y < height - 1) addConnection(n, -1, 1); // NW
            }
        }
    }

    @Override
    public TiledTerrainNode getNode (int x, int y) {
        return nodes.get(x * height + y);
    }

    @Override
    public TiledTerrainNode getNode (int index) {
        return nodes.get(index);
    }

    @Override
    public int getIndex(TiledTerrainNode node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount () {
        return nodes.size;
    }


    private void addConnection (TiledTerrainNode n, int xOffset, int yOffset) {
        TiledTerrainNode target = getNode(n.coord.x + xOffset, n.coord.y + yOffset);
        if(target.type == TILE_FLOOR)
            n.getConnections().add(new TiledTerrainConnection(this, n, target));
    }

    @Override
    public Array<Connection<TiledTerrainNode>> getConnections(TiledTerrainNode fromNode) {
        return fromNode.getConnections();
    }
}

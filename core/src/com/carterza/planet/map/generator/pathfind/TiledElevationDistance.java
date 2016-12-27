package com.carterza.planet.map.generator.pathfind;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * Created by zachcarter on 12/20/16.
 */
public class TiledElevationDistance<N extends TiledTerrainNode> implements Heuristic<N> {

    @Override
    public float estimate(N node, N endNode) {
        /*double minHeight = Double.MAX_VALUE;
        for(Connection<TiledTerrainNode> nodeConnection : node.getConnections()) {
            minHeight = Math.min(nodeConnection.getToNode().height, minHeight);
        }
        int dx = Math.abs(node.coord.x - endNode.coord.x);
        int dy = Math.abs(node.coord.y - endNode.coord.y);
        // return ((float)minHeight * (dx + dy <= 1 ? 1f : 1.4142135f));
        return ((float)minHeight * (dx + dy));*/
        return Math.abs(endNode.coord.x - node.coord.x) + Math.abs(endNode.coord.y - node.coord.y);
    }
}
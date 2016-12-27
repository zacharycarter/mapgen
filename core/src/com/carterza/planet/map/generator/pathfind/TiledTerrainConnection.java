package com.carterza.planet.map.generator.pathfind;

import com.badlogic.gdx.ai.pfa.DefaultConnection;

/** A connection for a {@link TiledTerrainGraph}.
 *
 * @author davebaol */
public class TiledTerrainConnection extends DefaultConnection<TiledTerrainNode> {

    TiledTerrainGraph worldMap;

    public TiledTerrainConnection (TiledTerrainGraph worldMap, TiledTerrainNode fromNode, TiledTerrainNode toNode) {
        super(fromNode, toNode);
        this.worldMap = worldMap;
    }

    @Override
    public float getCost () {
        if(getToNode().height >= getFromNode().height)
            return 2;
        return 1;
    }
}
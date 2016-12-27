package com.carterza.math.pathfind;

import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;

public interface TiledGraph<N extends TiledNode<N>> extends IndexedGraph<N> {

    public void init ();

    public N getNode (int x, int y);

    public N getNode (int index);

}

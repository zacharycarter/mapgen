package com.carterza.game.action;

import java.util.LinkedList;
import java.util.Queue;

public class ActionManager {
    private static Queue<Action> actionQueue;

    static {
        actionQueue = new LinkedList<Action>();
    }

    public static Queue<Action> getActionQueue() {
        return actionQueue;
    }
}

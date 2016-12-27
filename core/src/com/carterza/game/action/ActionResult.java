package com.carterza.game.action;

public class ActionResult {
    private final Action action;
    final Action alternative;
    final boolean succeeded;

    public ActionResult(Action action, boolean succeeded) {
        this.action = action;
        this.succeeded = succeeded;
        this.alternative = null;
    }

    public ActionResult(Action alternative) {
        this.succeeded = false;
        this.alternative = alternative;
        this.action = null;
    }

    public ActionResult(boolean succeeded) {
        this.succeeded = succeeded;
        this.action = null;
        this.alternative = null;
    }

    public Action getAction() {
        return action;
    }
}

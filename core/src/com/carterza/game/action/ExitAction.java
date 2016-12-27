package com.carterza.game.action;

import com.carterza.game.Actor;
import com.carterza.game.Character;
import com.carterza.universe.Room;
import com.carterza.universe.Universe;

/**
 * Created by zachcarter on 12/10/16.
 */
public class ExitAction extends Action {
    Character character;

    public ExitAction(Actor actor) {
        super(actor, 100);
        if(actor instanceof Character)
            this.character = (Character)actor;
    }

    @Override
    public ActionResult perform() {
        if(character != null) {
            Universe universe = character.getUniverse();
            universe.getCurrentRoom().exit();
            return new ExitAction.ExitActionResult(this, true);
        }
        return null;
    }

    class ExitActionResult extends ActionResult {

        public ExitActionResult(Action action, boolean succeeded) {
            super(action, succeeded);
        }
    }
}

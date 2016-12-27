package com.carterza.game.action;

import com.carterza.game.Actor;
import com.carterza.game.Character;
import com.carterza.universe.Room;
import com.carterza.universe.Universe;

/**
 * Created by zachcarter on 12/9/16.
 */
public class InteractAction extends Action {

    Character character;


    public InteractAction(Actor actor) {
        super(actor, 100);
        if(actor instanceof Character)
            this.character = (Character)actor;
    }

    @Override
    public ActionResult perform() {
        if(character != null) {
            Universe universe = character.getUniverse();
            Room currentRoom = universe.getCurrentRoom();
            Room roomToEnter = currentRoom.getRoomAt(character.getPosition());
            roomToEnter.enter();
            return new InteractActionResult(this, true);
        }
        return null;
    }

    class InteractActionResult extends ActionResult {

        public InteractActionResult(Action action, boolean succeeded) {
            super(action, succeeded);
        }
    }
}

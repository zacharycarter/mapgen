package com.carterza.game.action;

import com.carterza.common.Direction;
import com.carterza.game.Actor;
import com.carterza.game.Character;
import com.carterza.game.Player;
import com.carterza.universe.Universe;
import squidpony.squidmath.Coord;

/**
 * Created by zachcarter on 12/9/16.
 */
public class MoveAction extends Action {
    Direction moveDirection;
    Character character;


    public MoveAction(Actor actor, Direction moveDirection) {
        super(actor, 100);
        if(actor instanceof Character)
            this.character = (Character)actor;
        this.moveDirection = moveDirection;
    }

    @Override
    public ActionResult perform() {
        if(this.character != null) {
            Universe universe = character.getUniverse();
            Coord currentPosition = character.getPosition();
            Coord nextPosition = Coord.get(currentPosition.x + moveDirection.x, currentPosition.y + moveDirection.y);
            if(universe.getBounds().contains(nextPosition.x, nextPosition.y)) {
                this.character.setPosition(nextPosition);
                return new MoveActionResult(this, true, nextPosition);
            }
            return new MoveActionResult(this, false, nextPosition);
        }
        return null;
    }

    class MoveActionResult extends ActionResult {
        private Coord positionMovedTo;

        public MoveActionResult(Action action, boolean succeeded, Coord positionMovedTo) {
            super(action, succeeded);
            this.positionMovedTo = positionMovedTo;
        }


        public Coord getPositionMovedTo() {
            return positionMovedTo;
        }
    }
}

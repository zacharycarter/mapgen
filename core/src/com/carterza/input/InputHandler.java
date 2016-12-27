package com.carterza.input;

import com.badlogic.gdx.Gdx;
import com.carterza.Derelict;
import com.carterza.common.Direction;
import com.carterza.game.Player;
import com.carterza.game.action.*;
import com.carterza.universe.Universe;
import squidpony.squidgrid.gui.gdx.SquidInput;

/**
 * Created by zachcarter on 12/9/16.
 */
public class InputHandler implements SquidInput.KeyHandler {

    Player player;

    public InputHandler() {

    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        switch (key)
        {
            case SquidInput.UP_ARROW:
            case 'k':
            case 'w':
            case 'K':
            case 'W':
            {
                //-1 is up on the screen
                if(player != null)
                    setNextPlayerAction(new MoveAction(player, Direction.N));
                break;
            }
            case SquidInput.DOWN_ARROW:
            case 'j':
            case 's':
            case 'J':
            case 'S':
            {
                //+1 is down on the screen
                if(player != null)
                    setNextPlayerAction(new MoveAction(player, Direction.S));
                break;
            }
            case SquidInput.LEFT_ARROW:
            case 'h':
            case 'a':
            case 'H':
            case 'A':
            {
                if(player != null)
                    setNextPlayerAction(new MoveAction(player, Direction.W));
                break;
            }
            case SquidInput.RIGHT_ARROW:
            case 'l':
            case 'd':
            case 'L':
            case 'D':
            {
                if(player != null)
                    setNextPlayerAction(new MoveAction(player, Direction.E));
                break;
            }
            case 'Q':
            case 'q':
            case SquidInput.ESCAPE:
            {
                Gdx.app.exit();
                break;
            }
            case SquidInput.ENTER:
            {
                if(player != null) {
                    setNextPlayerAction(new InteractAction(player));
                }
                break;
            }
            case SquidInput.BACKSPACE:
            {
                if(player != null) {
                    setNextPlayerAction(new ExitAction(player));
                }
                break;
            }
        }
    }

    private void setNextPlayerAction(Action action) {
        player.setNextAction(action);
        ActionManager.getActionQueue().add(action);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}

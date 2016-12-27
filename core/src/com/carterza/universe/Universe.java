package com.carterza.universe;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.carterza.game.Actor;
import com.carterza.game.ActorTurn;
import com.carterza.game.Player;
import com.carterza.game.action.Action;
import com.carterza.game.action.ActionManager;
import com.carterza.game.action.ActionResult;
import com.carterza.input.InputHandler;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidmath.Coord;

import java.util.*;

/**
 * Created by zachcarter on 12/9/16.
 */
public class Universe {

    private SpriteBatch spriteBatch;
    Galaxy galaxy;
    Player player;
    private Room currentRoom;
    private SquidInput squidInput;
    InputHandler inputHandler;
    long currentActorId = 0;
    long counter = 0;
    Coord lastPlayerPos;


    private final List<Actor> actors;
    private final Queue<ActorTurn> actorTurnQueue;


    public Universe(SpriteBatch spriteBatch, SquidInput squidInput, InputHandler inputHandler) {
        this.spriteBatch = spriteBatch;
        this.squidInput = squidInput;
        this.inputHandler = inputHandler;
        this.actors = new ArrayList<Actor>();
        this.actorTurnQueue = new PriorityQueue<ActorTurn>();
        initialize();
    }

    private void initialize() {
        currentRoom = galaxy = new Galaxy(this, spriteBatch, squidInput);
        createPlayer();
        currentRoom.addActor(player);
        inputHandler.setPlayer(player);
    }

    private void createPlayer() {
        player = new Player(currentActorId, 1, this, galaxy.getCurrentStarSystem().position);
        actorTurnQueue.add(new ActorTurn(player, player.actionDelay(), counter));
        counter++;
        currentActorId++;
    }


    public UniverseUpdate update() {
        if(squidInput.hasNext())
            squidInput.next();

        UniverseUpdate universeUpdate = new UniverseUpdate();

        ActorTurn actorTurn = actorTurnQueue.remove();

        Actor actor = actorTurn.getActor();

        if(actor instanceof Player) {
            if (((Player) actor).needsInput()) {
                actorTurnQueue.add(actorTurn);
                return universeUpdate;
            }
        }
        universeUpdate.universeProgressed = true;

        updateTurnQueue(-actorTurn.getTime());

        int actionCost = actor.act();

        actorTurnQueue.add(new ActorTurn(actor, actor.actionDelay(actionCost), counter));
        counter++;

        Action actionToPerform = null;
        ActionResult actionResult = null;
        Iterator<Action> actionIterator = ActionManager.getActionQueue().iterator();
        while(actionIterator.hasNext()) {
            actionToPerform = actionIterator.next();
            actionResult = actionToPerform.perform();
            // game.updateGUI(actionResult);
            actionIterator.remove();
        }

        return universeUpdate;
    }

    private void updateTurnQueue(double time) {
        Iterator<ActorTurn> turnQueueIterator = actorTurnQueue.iterator();
        while(turnQueueIterator.hasNext()) {
            ActorTurn actorTurn = turnQueueIterator.next();
            actorTurn.setTime(actorTurn.getTime()+time);
        }
    }

    public void render() {
        currentRoom.render();
    }

    public Player getPlayer() {
        return player;
    }

    public void setInputHandler(SquidInput squidInput) {
        this.squidInput = squidInput;
    }

    public Rectangle getBounds() {
        return currentRoom.getBounds();
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public SpriteBatch getSpriteBatch() { return spriteBatch; }

    public SquidInput getSquidInput() {
         return squidInput;
    }

    public void setLastPlayerPos(Coord lastPlayerPos) {
        this.lastPlayerPos = lastPlayerPos;
    }

    public void dispose() {
        galaxy.dispose();
    }
}

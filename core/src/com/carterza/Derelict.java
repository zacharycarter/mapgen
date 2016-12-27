package com.carterza;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.carterza.screen.GameScreen;
import com.carterza.universe.Universe;

public class Derelict extends Game {

	GameScreen gameScreen;
	private Universe universe;

	@Override
	public void create () {
		gameScreen = new GameScreen(this);
		setScreen(gameScreen);
	}

	@Override
	public void dispose () {
		gameScreen.dispose();
	}

	public void setUniverse(Universe universe) {
		this.universe = universe;
	}

	public Universe getUniverse() {
		return universe;
	}
}

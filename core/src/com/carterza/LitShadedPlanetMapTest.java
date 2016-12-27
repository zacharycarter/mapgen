package com.carterza;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.carterza.planet.map.generator.MapGenerator;

public class LitShadedPlanetMapTest implements ApplicationListener {

    Texture tex;
    SpriteBatch batch;
    OrthographicCamera cam;
    MapGenerator mapGenerator;


    @Override
    public void create() {
        mapGenerator = new MapGenerator(512,512);

        tex = mapGenerator.generate();

        batch = new SpriteBatch(1000);

        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.setToOrtho(false);

        //handle mouse wheel
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean scrolled(int delta) {
                //LibGDX mouse wheel is inverted compared to lwjgl-basics
                cam.zoom += delta * .01;
                return true;
            }
        });

    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false, width, height);
        batch.setProjectionMatrix(cam.combined);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();



        batch.draw(tex, 0, 0);

        batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        tex.dispose();
        mapGenerator.dispose();
    }

}

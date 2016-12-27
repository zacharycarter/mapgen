package com.carterza;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import squidpony.ArrayTools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ShapeRendererTest extends ApplicationAdapter {

    private static final int WIDTH = 128, HEIGHT = 128;
    ShapeRenderer renderer;
    OrthographicCamera cam;
    OrthoCamController controller;
    SpriteBatch batch;
    BitmapFont font;
    double[][] heightMap;
    int[][] rivers;
    /*CA test;*/

    public void create () {
        final double waterShedCutoff = 0.10;


        renderer = new ShapeRenderer();
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.setToOrtho(false);
        controller = new OrthoCamController(cam);
        Gdx.input.setInputProcessor(controller);
        batch = new SpriteBatch();
        font = new BitmapFont();
        heightMap = new double[WIDTH][HEIGHT];
        rivers = new int[WIDTH][HEIGHT];
        ArrayTools.fill(rivers, 0);

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("heights.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] heightValues = properties.getProperty("HEIGHTS").split(",");

        int hx = 0, hy = 0;
        for(String height : heightValues) {
            heightMap[hx][hy] = Double.parseDouble(height);
            hy++;
            if(hy == heightMap.length) {
                hx++;
                hy = 0;
            }
        }

        int[][] initialData = new int[WIDTH][HEIGHT];
        ArrayTools.fill(initialData, -999);

        CellularAutomata automata = new CellularAutomata(initialData, new CellularAutomata.CAStep()
        {
            @Override
            public int[][] work(int[][] grid)
            {
                for(int r=1; r < grid.length - 1; r++)
                {
                    for(int c=1; c < grid[0].length - 1; c++)
                    {
                        // if the threshold was crossed, progress the watershed
                        double dval = heightMap[r][c];
                        if(dval > waterShedCutoff)
                        {
                            int val = 1;
                            grid[r-1][c] = val;
                            grid[r+1][c] = val;
                            grid[r][c] = val;
                            grid[r][c-1] = val;
                            grid[r][c+1] = val;
                        }
                        else
                        {
                            grid[r][c] = -1;
                        }
                    }
                }
                return grid;
            }
        });

        // progress some steps so that the watersheds have time to form
        int steps = 10;
        automata.step(steps);

        // store the result in the initial data for the next automata
        initialData = automata.result();

        // create the cellular automata that computes the initial watersheds
        CellularAutomata automataExpand = new CellularAutomata(initialData, new CellularAutomata.CAStep()
        {
            @Override
            public int[][] work(int[][] grid)
            {
                for(int r=0; r < grid.length; r++)
                {
                    for(int c=0; c < grid[0].length; c++)
                    {
                        int val = grid[r][c];
                        double cheight = heightMap[r][c];
                        // if a watershed is present, extend it in an appropriate direction
                        if(val > -100 && cheight > -0.1)
                        {
                            for(int hr=-1; hr<=1; hr++)
                            {
                                for(int hc=-1; hc<=1; hc++)
                                {
                                    // if the height is decreasing, extend the watershed
                                    if(isInvalid(r+hr, c+hc))
                                    {
                                        double h = heightMap[r+hr][c+hc];
                                        if(h < cheight)
                                        {
                                            grid[r+hr][c+hc] = (int)(h*100);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return grid;
            }
        });

        // progress some steps so that the watersheds have time to form
        steps = 15;
        automataExpand.step(steps);

        rivers = automataExpand.result();
        System.out.println(rivers);
    }

    private boolean isInvalid(int x, int y) {
        return(x > 0 && x < WIDTH && y > 0 && y < HEIGHT);
    }

    public void render () {
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.update();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        renderer.setProjectionMatrix(cam.combined);



        renderer.begin(ShapeRenderer.ShapeType.Filled);

        for(int x = 0; x < rivers.length; x++) {
            for(int y = 0; y < rivers[0].length; y++) {
                if (rivers[x][y] == 1)
                    renderer.setColor(Color.BLACK);
                else
                    renderer.setColor(Color.WHITE);
                renderer.rect(x*8, y*8, 8, 8);
            }
        }


        /*renderer.begin(ShapeRenderer.ShapeType.Point);

        renderer.setColor(Color.PINK);
        for (int i = 0; i < 100; i++)
            renderer.point(MathUtils.random(0.0f, 1024), MathUtils.random(0.0f, 768), 0);*/

        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Line);

        renderer.setColor(Color.BLACK);
        for(int x = 0; x < WIDTH; x++) {
            for(int y = 0; y < HEIGHT; y++) {

                renderer.rect(x*8, y*8, 8, 8);
            }
        }

        renderer.end();

        /*test.generate();*/

        batch.begin();
        font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 0, 20);
        batch.end();
    }

    @Override
    public void dispose () {
        batch.dispose();
        font.dispose();
        renderer.dispose();
    }
}

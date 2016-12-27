package com.carterza;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.carterza.common.CommonRNG;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.RNG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetTest extends ApplicationAdapter {
    SpriteBatch batch;

    private RNG rng;
    private SquidLayers display;
    private DungeonGenerator dungeonGen;
    private char[][] decoDungeon, bareDungeon, lineDungeon;
    private int[][] colorIndices, bgColorIndices;
    /** In number of cells */
    private int gridWidth;
    /** In number of cells */
    private int gridHeight;
    /** The pixel width of a cell */
    private int cellWidth;
    /** The pixel height of a cell */
    private int cellHeight;
    private SquidInput input;
    private Color bgColor;
    private Stage stage;
    private DijkstraMap playerToCursor;
    private Coord cursor, player;
    private ArrayList<Coord> toCursor;
    private ArrayList<Coord> awaitedMoves;
    private float secondsWithoutMoves;
    private String[] lang;
    private int langIndex = 0;

    double sx = 0, sy = 0;

    double[] shades;

    int diameter = 60;
    int planetWidth = diameter*2;
    int planetHeight;

    int detailWidth = planetWidth * 2;

    int noiseOctaves;
    int noiseZoom;

    int solarSystemViewWidth = 6;

    int heightmapWidth, heightmapHeight;
    int detailHeightmapWidth, detailHeightmapHeight;

    Map<Integer, SColor> colorMap;

    SColorFactory sColorFactor;

    @Override
    public void create () {

        shades = new double[22];

        int ii = 0;
        for(int i = 20; i > -1; i--) {
            shades[ii] = i/20.0;
            ii++;
        }

        sColorFactor = new SColorFactory();

        colorMap = new HashMap<Integer, SColor>();

        // Terrestrial Planet
        for(int i = 0; i<=14; i++) {
            colorMap.put(i,new SColor(39,  62,  90));
        }
        for(int i = 15; i<=69; i++) {
            colorMap.put(i,new SColor(50,  72,  88));
        }
        for(int i = 70; i<=79; i++) {
            colorMap.put(i,new SColor(116, 184, 164));
        }
        for(int i = 80; i<=89; i++) {
            colorMap.put(i,new SColor(142, 163, 164));
        }
        for(int i = 90; i<=199; i++) {
            colorMap.put(i,new SColor(71,  97,  81));
        }
        for(int i = 200; i<=232; i++) {
            colorMap.put(i,new SColor(149, 138, 115));
        }
        for(int i = 233; i<=254; i++) {
            colorMap.put(i,new SColor(199, 197, 150));
        }
        colorMap.put(255,new SColor(220, 197, 173));

        noiseOctaves = 6;
        noiseZoom = 1;

        /*for(int i = 0; i<=59; i++) {
            colorMap.put(i,new SColor(255, 151, 19));
        }
        for(int i = 60; i<=79; i++) {
            colorMap.put(i,new SColor(255, 52,  0));
        }
        for(int i = 80; i<=109; i++) {
            colorMap.put(i,new SColor(91,  31,  12));
        }
        for(int i = 110; i<=149; i++) {
            colorMap.put(i,new SColor(31,  21,  11));
        }
        for(int i = 150; i<=204; i++) {
            colorMap.put(i,new SColor(56,  44,  21));
        }
        for(int i = 205; i<=234; i++) {
            colorMap.put(i,new SColor(0,   0,   0));
        }
        for(int i = 235; i<=254; i++) {
            colorMap.put(i,new SColor(62,  19,  15));
        }
        colorMap.put(255,new SColor(94,  65,  35));

        noiseOctaves = 6;
        noiseZoom = 6;*/

        gridWidth = 360;
        gridHeight = 120;
        cellWidth = 14;
        cellHeight = 21;

        // gotta have a random number generator. We can seed an RNG with any long we want, or even a String.
        rng = new RNG("SquidLib!");

        //Some classes in SquidLib need access to a batch to render certain things, so it's a good idea to have one.
        batch = new SpriteBatch();
        //Here we make sure our Stage, which holds any text-based grids we make, uses our Batch.
        stage = new Stage(new StretchViewport(gridWidth * cellWidth, (gridHeight + 8) * cellHeight), batch);
        // the font will try to load CM-Custom as an embedded bitmap font with a distance field effect.
        // this font is covered under the SIL Open Font License (fully free), so there's no reason it can't be used.
        display = new SquidLayers(gridWidth, gridHeight + 8, cellWidth, cellHeight,
                DefaultResources.getStretchableTypewriterFont());
        // a bit of a hack to increase the text height slightly without changing the size of the cells they're in.
        // this causes a tiny bit of overlap between cells, which gets rid of an annoying gap between vertical lines.
        // if you use '#' for walls instead of box drawing chars, you don't need this.
        display.setTextSize(cellWidth, cellHeight + 1);

        // this makes animations very fast, which is good for multi-cell movement but bad for attack animations.
        display.setAnimationDuration(0.03f);

        //These need to have their positions set before adding any entities if there is an offset involved.
        //There is no offset used here, but it's still a good practice here to set positions early on.
        display.setPosition(0, 0);
        //Setting the InputProcessor is ABSOLUTELY NEEDED TO HANDLE INPUT
        /*Gdx.input.setInputProcessor(new InputMultiplexer(stage, input));*/
        //You might be able to get by with the next line instead of the above line, but the former is preferred.
        //Gdx.input.setInputProcessor(input);
        // and then add display, our one visual component, to the list of things that act in Stage.
        stage.addActor(display);


        if ((planetWidth % 2) != 0)
            planetWidth += 1;

        planetHeight = planetWidth;

        heightmapWidth = planetWidth * 2;
        heightmapHeight = planetHeight;

        detailHeightmapWidth = detailWidth*2;
        detailHeightmapHeight = detailWidth;
    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the player.
     */
    public void putMap()
    {
        /*sx = 5; sy = 5;*/
        double d = 0.05 * Math.sqrt(Math.pow(sx, 2) + Math.pow(sy,2));
        double x = d * sx;
        double y = -1.0 * d * sy;
        /*Vector3 light = new Vector3((float)x,(float)y,(float)(-1.0 * 750.0 * d)).nor();*/

        double[] light = normalize(new double[]{ x, y, (-1.0 * .7500 * d)});


        int r = diameter/2;
        double k = 4;
        double ambient = 0.1;


        List<List<Double>> circleMask = buildCircleMask(r, 4, light, 0.1);
        /*List<List<Double>> detailCircleMask = buildCircleMask(detailWidth, light, ambient);*/
        // List<List<Double>> solarSystemViewCircleMask = buildCircleMask(solarSystemViewWidth, light, ambient);


        // Draw circle mask
        /*int cmx = 0, cmy = 0;
        for(int ix = sx - circleMask.size()/2; ix < (sx + circleMask.size()/2); ix++) {
            for(int iy = sy - circleMask.get(cmx).size()/2; iy < (sy + circleMask.get(cmx).size()/2); iy++) {
                if(circleMask.get(cmx).get(cmy) > 0) {
                    display.put(ix,iy,' ', Color.WHITE, new Color(1,1,1, circleMask.get(cmx).get(cmy).floatValue()));
                }
                cmy++;
            }
            cmy = 0;
            cmx++;
        }*/

        // Draw detail circle mask
        /*int cmx = 0, cmy = 0;
        for(int ix = sx - detailCircleMask.size()/2; ix < (sx + detailCircleMask.size()/2); ix++) {
            for(int iy = sy - detailCircleMask.get(cmx).size()/2; iy < (sy + detailCircleMask.get(cmx).size()/2); iy++) {
                if(detailCircleMask.get(cmx).get(cmy) > 0) {
                    display.put(ix,iy,' ', Color.WHITE, new Color(1,1,1, detailCircleMask.get(cmx).get(cmy).floatValue()));
                }
                cmy++;
            }
            cmy = 0;
            cmx++;
        }*/



/*        int cmx = 0, cmy = 0;
        for(int ix = sx - solarSystemViewCircleMask.size()/2; ix < (sx + solarSystemViewCircleMask.size()/2); ix++) {
            for(int iy = sy - solarSystemViewCircleMask.get(cmx).size()/2; iy < (sy + solarSystemViewCircleMask.get(cmx).size()/2); iy++) {
                if(solarSystemViewCircleMask.get(cmx).get(cmy) > 0) {
                    display.put(ix,iy,' ', Color.WHITE, new Color(1,1,1, solarSystemViewCircleMask.get(cmx).get(cmy).floatValue()));
                }
                cmy++;
            }
            cmy = 0;
            cmx++;
        }*/


        float[][] hm = createHeightmap(heightmapWidth, heightmapHeight);
        float[][] detailHeightmap = createHeightmap(detailHeightmapWidth, detailHeightmapHeight);
        float[][] solarSystemViewHeightmap = createHeightmap(solarSystemViewWidth*2, solarSystemViewWidth*2);

        // Print Heightmap
        /*for(int i = 0; i < hm.length; i++)
        {
            for(int j = 0; j < hm[0].length; j++)
            {
                System.out.printf("%f ", hm[i][j]);
                display.put(i,j,' ', Color.WHITE, new Color(1,1,1, (float)hm[i][j]/255));
            }

        }*/

        // Print Detail Heightmap
        /*for(int i = 0; i < detailHeightmap.length; i++)
        {
            for(int j = 0; j < detailHeightmap[0].length; j++)
            {
                System.out.printf("%f ", detailHeightmap[i][j]);
                display.put(i,j,' ', Color.WHITE, new Color(1,1,1, (float)detailHeightmap[i][j]/255));
            }

        }*/

        // Print Solar System View Heightmap
        /*for(int i = 0; i < solarSystemViewHeightmap.length; i++)
        {
            for(int j = 0; j < solarSystemViewHeightmap[0].length; j++)
            {
                System.out.printf("%f ", solarSystemViewHeightmap[i][j]);
                display.put(i,j,' ', Color.WHITE, new Color(1,1,1, (float)solarSystemViewHeightmap[i][j]/255));
            }

        }*/

        float[][] atmosphere = createAtmosphere(heightmapWidth, heightmapHeight);
        float[][] detailAtmosphere = createAtmosphere(detailHeightmapWidth, detailHeightmapHeight);
        float[][] solarSystemViewAtmosphere = createAtmosphere(solarSystemViewWidth*2, solarSystemViewWidth*2);



        /*for(int i = 0; i < atmosphere.length; i++)
        {
            for(int j = 0; j < atmosphere[0].length; j++)
            {
                System.out.printf("%f ", atmosphere[i][j]);
                display.put(i,j,' ', Color.WHITE, new Color(1,1,1, atmosphere[i][j]));
            }

        }*/


        /*for(int i = 0; i < atmosphere.length; i++)
        {
            for(int j = 0; j < atmosphere[0].length; j++)
            {
                System.out.printf("%f ", atmosphere[i][j]);
                display.put(i,j,' ', Color.WHITE, new Color(1,1,1, atmosphere[i][j]));
            }

        }*/

        /*for(int i = 0; i < solarSystemViewAtmosphere.length; i++)
        {
            for(int j = 0; j < solarSystemViewAtmosphere[0].length; j++)
            {
                System.out.printf("%f ", solarSystemViewAtmosphere[i][j]);
                display.put(i,j,' ', Color.WHITE, new Color(1,1,1, solarSystemViewAtmosphere[i][j]));
            }

        }*/

        List<List<SColor>> sprite = createSprite(circleMask, hm, atmosphere, planetWidth);
        /*List<List<SColor>> detailSprite = createDetailSprite(detailCircleMask, detailHeightmap, detailAtmosphere, detailWidth);*/
        //List<List<SColor>> solarSystemViewSprite = createSolarSystemSprite(solarSystemViewWidth*2, solarSystemViewCircleMask, solarSystemViewHeightmap, solarSystemViewAtmosphere, solarSystemViewWidth);



        for(int i = 0; i < sprite.size(); i++)
        {
            for(int j = 0; j < sprite.get(0).size(); j++)
            {
                System.out.printf("%f ", atmosphere[i][j]);
                if(circleMask.get(i).get(j) != 0 && circleMask.get(i).get(j) != 1)
                    display.put(i,j,' ', Color.WHITE, sprite.get(i).get(j));
            }

        }

       /* for(int i = 0; i < detailSprite.size(); i++)
        {
            for(int j = 0; j < detailSprite.get(0).size(); j++)
            {
                if(detailCircleMask.get(i).get(j) != 0 && detailCircleMask.get(i).get(j) != 1)
                    display.put(i,j,' ', Color.WHITE, detailSprite.get(i).get(j));
            }

        }*/

        /*for(int i = 0; i < solarSystemViewWidth*2; i++)
        {
            for(int j = 0; j < solarSystemViewWidth*2; j++)
            {
                if(solarSystemViewCircleMask.get(i).get(j) != 0 && solarSystemViewCircleMask.get(i).get(j) != 1) {
                    display.put(i, j, ' ', null, solarSystemViewSprite.get(i).get(j));
                }
            }

        }*/
        sx += .1;
        sy += .1;
        if(sx >= 180) sx = 0;
        if(sy >= 180) sy = 0;
    }

    private List<List<SColor>> createSolarSystemSprite(int systemViewWidth, List<List<Double>> solarSystemViewCircleMask, float[][] solarSystemViewHeightmap, float[][] solarSystemViewAtmosphere, int solarSystemViewWidth) {
        List<List<SColor>> sprite = new ArrayList<List<SColor>>();
        for(int x = 0; x <= systemViewWidth-1; x++) {
            List<SColor> column = new ArrayList<SColor>();
            for(int y = 0; y <= systemViewWidth-1; y++) {
                column.add(blendLayers(x, y, solarSystemViewCircleMask, solarSystemViewHeightmap, solarSystemViewAtmosphere, solarSystemViewWidth));
            }
            sprite.add(column);
        }
        return sprite;
    }

    private List<List<SColor>> createDetailSprite(List<List<Double>> detailCircleMask, float[][] detailHeightmap, float[][] detailAtmosphere, int detailWidth) {
        List<List<SColor>> sprite = new ArrayList<List<SColor>>();
        for(int x = 0; x <= detailWidth-1; x++) {
            List<SColor> column = new ArrayList<SColor>();
            for(int y = 0; y <= detailWidth-1; y++) {
                column.add(blendLayers(x, y, detailCircleMask, detailHeightmap, detailAtmosphere, detailWidth));
            }
            sprite.add(column);
        }
        return sprite;
    }

    private List<List<Double>> buildCircleMask(int r, double k, double[] light, double ambient) {
        List<List<Double>> circleMask = new ArrayList<List<Double>>();
        for(int i = (int)Math.floor(-r); i <= (int)Math.ceil(r)-1; i++) {
            double xx = i + 0.5;
            List<Double> col = new ArrayList<Double>();
            for(int j = (int)Math.floor(-1*r); j <= (int)Math.ceil(1*r)-1; j++) {

                double yy = j + 0.5;

                if (xx*xx + yy*yy <= r*r) {
                    double[] vector3 = normalize(new double[]{xx,yy, Math.sqrt(r*r - xx*xx - yy*yy)});
                    double b = Math.pow(dot(light, vector3), k) + ambient;
                    int intensity = (int)((1-b)*(shades.length-1));
                    col.add((0 <= intensity && intensity < shades.length) ? shades[intensity] : shades[0]);
                } else {
                    col.add(0.0);
                }
            }
            circleMask.add(col);
        }
        return circleMask;
    }

    private float[][] sphericalNoise(
            double noiseDx
            , double noiseDy
            , double noiseDz
            , long noiseOctaves
            , double noiseZoom
            , double noiseHurst
            , double noiseLacunarity
            , int width
            , int height
            , long seed
    ) {
         /*
     * Start with a fractal generator...
     */
        ModuleFractal gen = new ModuleFractal();
        gen.setAllSourceBasisTypes(ModuleBasisFunction.BasisType.SIMPLEX);
        gen.setAllSourceInterpolationTypes(ModuleBasisFunction.InterpolationType.CUBIC);
        gen.setNumOctaves(noiseOctaves);
        gen.setFrequency(2.34);
        gen.setH(noiseHurst);
        gen.setLacunarity(noiseLacunarity);
        gen.setType(ModuleFractal.FractalType.FBM);
        gen.setSeed(seed);

    /*
     * ... route it through an autocorrection module...
     *
     * This module will sample it's source multiple times and attempt to
     * auto-correct the output to the range specified.
     */
        ModuleAutoCorrect ac = new ModuleAutoCorrect();
        ac.setSource(gen); // set source (can usually be either another Module or a
        // double value; see specific module for details)
        ac.setRange(0.0f, 1.0f); // set the range to auto-correct to
        ac.setSamples(10000); // set how many samples to take
        ac.calculate(); // perform the caclulations

        float[][] hm = new float[width+1][height+1];

        noiseDx += 0.01;
        noiseDy += 0.01;
        noiseDz += 0.01;

        float pi_times_two = (float) (2 * Math.PI);
        float pi_div_two = (float) (Math.PI / 2.0f);

        float theta = 0.0f;
        float phi = pi_div_two * -1.0f;
        int x = 0;
        int y = 0;


        while(phi <= pi_div_two) {
            while (theta <= pi_times_two) {
                double val = ac.get(
                        noiseZoom * Math.cos(phi) * Math.cos(theta)
                        , noiseZoom * Math.cos(phi) * Math.sin(theta)
                        , noiseZoom * Math.sin(phi)
                );

                hm[x][y] = (float) val;

                theta += (pi_times_two / width);
                x += 1;
            }
            phi += (Math.PI / (height-1));
            y += 1;
            x = 0;
            theta = 0.0f;
        }



        return hm;
    }

    private List<List<SColor>> createSprite(List<List<Double>> circleMask, float[][] hm, float[][] atmosphere, int width) {
        List<List<SColor>> sprite = new ArrayList<List<SColor>>();
        for(int x = 0; x <= diameter-1; x++) {
            List<SColor> column = new ArrayList<SColor>();
            for(int y = 0; y <= diameter-1; y++) {
                column.add(blendLayers(x, y, circleMask, hm, atmosphere, width));
            }
            sprite.add(column);
        }
        return sprite;
    }

    private SColor blendLayers(int x, int y, List<List<Double>> cm, float[][] height, float[][] atmos, int w) {
        int terrainRotation = 0;
        int atmosphereRotation = 0;

        int terrainColor = (int)height[((x+terrainRotation) % w)][y];
        if (terrainColor > 255)
            terrainColor = 255;

        SColor color = colorMap.get(terrainColor);

        float cloudCover = atmos[((x+atmosphereRotation) % w)][y];
        color = sColorFactor.blend(color, SColor.WHITE, cloudCover*.5);

        if(cm.get(x).get(y) < 1) {
            color = sColorFactor.blend(color, SColor.BLACK, cm.get(x).get(y));
        }

        return color;
    }

    private float[][] createAtmosphere(int hmw, int hmh) {
        float[][] atmosphere = sphericalNoise(
                10.0
                , 10.0
                , 10.0
                , noiseOctaves
                , noiseZoom
                , 0.5
                , 2.0
                , hmw
                , hmh
                , CommonRNG.getRng().nextLong()
        );

        atmosphere = normalizeHeightmap(atmosphere, 0,1);
        atmosphere = heightmapAdd(atmosphere, 0.30f);
        atmosphere = heightmapClamp(atmosphere, 0.4,1.0);
        return atmosphere;
    }

    private float[][] createHeightmap(int hmw, int hmh) {
        float[][] hm = sphericalNoise(
                0.0
                , 0.0
                , 0.0
                , noiseOctaves
                , noiseZoom
                , 0.5
                , 2.0
                , hmw
                , hmh
                , CommonRNG.getRng().nextLong()
        );

        // Terran
        /*hm = normalizeHeightmap(hm, 0, 1);
        hm = heightmapAdd(hm, -0.40f);
        hm = heightmapClamp(hm, 0.0, 1.0);
        hm = heightmapRainErosion(hm, 1000,0.46f, 0.12f);
        hm = normalizeHeightmap(hm, 0, 255);*/

        //Lava
        hm = normalizeHeightmap(hm, 0, 1);
        hm = heightmapClamp(hm, 0.0, 1.0);
        hm = heightmapRainErosion(hm, planetWidth == this.detailHeightmapWidth ? 1000 : 500,0.65f, 0.05f);
        hm = normalizeHeightmap(hm, 0, 255);

        return hm;
    }

    private float[][] heightmapClamp(float[][] hm, double min, double max) {
        for(int x = 0; x < hm.length; x++) {
            for (int y = 0; y < hm[0].length; y++) {
                hm[x][y] = (float) MathUtils.clamp(hm[x][y], min, max);
            }
        }
        return hm;
    }

    private float[][] heightmapAdd(float[][] hm, float v) {
        for(int x = 0; x < hm.length; x++) {
            for(int y = 0; y < hm[0].length; y++) {
                hm[x][y] += v;
            }
        }
        return hm;
    }


    @Override
    public void render () {
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // need to display the map every frame, since we clear the screen to avoid artifacts.
        putMap();


        // stage has its own batch and must be explicitly told to draw(). this also causes it to act().
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        //very important to have the mouse behave correctly if the user fullscreens or resizes the game!
		/*input.getMouse().reinitialize((float) width / this.gridWidth, (float)height / (this.gridHeight + 8), this.gridWidth, this.gridHeight, 0, 0);*/
    }

    private static double[] normalize(double[] v) {
        double len = Math.sqrt(Math.pow(v[0],2) + Math.pow(v[1],2) + Math.pow(v[2],2));
        return new double[]{v[0]/len, v[1]/len, v[2]/len};
    }

    private static double dot (double[] v1, double[] v2) {
        double d =  v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
        if(d < 0) return -d;
        return 0;
    }

    float[] heightmapGetMinMax(float[][] hm, float min, float max) {
        float curmax = hm[0][0];
        float curmin= hm[0][0];
        int x,y;
	/* get max and min height */
        for (y=0; y < hm[0].length; y++) {
            for (x=0; x < hm.length; x++) {
                float val= (float) hm[x][y];
                if ( val > curmax ) curmax = val;
                else if ( val < curmin ) curmin = val;
            }
        }
        min= (float) curmin;
        max= (float) curmax;
        return new float[]{min, max};
    }


    float[][] normalizeHeightmap(float[][] hm, float min, float max) {
        float curmin = Float.MAX_VALUE,curmax = Float.MIN_VALUE;
        int x,y;
        float invmax;
        float[] minMax = heightmapGetMinMax(hm, curmin, curmax);
        curmin = minMax[0];
        curmax = minMax[1];
        if (curmax - curmin == 0.0f) invmax=0.0f;
        else invmax = (max-min) / (curmax-curmin);
	/* normalize */
        for (y=0; y < hm[0].length; y++) {
            for (x=0; x < hm.length; x++) {
                hm[x][y] = min + (hm[x][y] - curmin) * invmax;
            }
        }
        return hm;
    }


    float[][] heightmapRainErosion(float[][] hm, int nbDrops,float erosionCoef,float agregationCoef) {
        while ( nbDrops > 0 ) {
            int curx = CommonRNG.getRng().between(0, hm.length-1);
            int cury = CommonRNG.getRng().between(0, hm[0].length-1);
            int dx[]={-1,0,1,-1,1,-1,0,1};
            int dy[]={-1,-1,-1,0,0,1,1,1};
            float slope=0.0f;
            float sediment=0.0f;
            do {
                int nextx=0,nexty=0,i;
                float v= hm[curx][cury];
			/* calculate slope at x,y */
                slope=0.0f;
                for (i=0; i < 8; i++ ) {
                    int nx=curx+dx[i];
                    int ny=cury+dy[i];
                    if ( nx >= 0 && nx < hm.length && ny >= 0 && ny < hm[0].length ) {
                        float nslope=v-hm[nx][ny];
                        if ( nslope > slope ) {
                            slope=nslope;
                            nextx=nx;
                            nexty=ny;
                        }
                    }
                }
                if ( slope > 0.0f ) {
/*				GET_VALUE(hm,curx,cury) *= 1.0f - (erosionCoef * slope); */
                    hm[curx][cury] -= erosionCoef * slope;
                    curx=nextx;
                    cury=nexty;
                    sediment+=slope;
                } else {
/*				GET_VALUE(hm,curx,cury) *= 1.0f + (agregationCoef*sediment); */
                    hm[curx][cury] += agregationCoef*sediment;
                }
            } while ( slope > 0.0f );
            nbDrops--;
        }
        return hm;
    }

}


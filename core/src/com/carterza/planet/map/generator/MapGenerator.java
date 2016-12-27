package com.carterza.planet.map.generator;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.carterza.common.CommonRNG;
import com.carterza.math.pathfind.TiledSmoothableGraphPath;
import com.carterza.planet.map.*;
import com.carterza.planet.map.generator.pathfind.TiledElevationDistance;
import com.carterza.planet.map.generator.pathfind.TiledTerrainGraph;
import com.carterza.planet.map.generator.pathfind.TiledTerrainNode;
import com.sudoplay.joise.module.*;
import squidpony.ArrayTools;
import squidpony.Thesaurus;
import squidpony.squidgrid.MultiSpill;
import squidpony.squidgrid.Spill;
import squidpony.squidgrid.Splash;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.OrderedMap;

import java.util.*;

import static com.carterza.common.Direction.O;
import static com.carterza.planet.map.BiomeType.Grassland;
import static squidpony.squidgrid.mapping.SpillWorldMap.letters;

/**
 * Created by zachcarter on 12/15/16.
 */
public class MapGenerator {

    public static int WIDTH;
    public static int HEIGHT;

    public static final double DeepWater = 0.2;
    public static final double MediumWater = 0.3;
    public static final double ShallowWater = 0.4;
    public static final double CoastalWater = 0.48;
    public static final double Sand = 0.5;
    public static final double Grass = 0.7;
    public static final double Forest = 0.8;
    public static final double Rock = 0.9;


    double ColdestValue = 0.05;
    double ColderValue = 0.18;
    double ColdValue = 0.4;
    double WarmValue = 0.6;
    double WarmerValue = 0.8;

    double DryerValue = 0.27;
    double DryValue = 0.4;
    double WetValue = 0.6;
    double WetterValue = 0.8;
    double WettestValue = 0.9;

    int riverCount = 0;

    // Nosie generator modules
    ModuleAutoCorrect heightmap;
    ModuleCombiner heatmap;
    ModuleAutoCorrect moisturemap;

    protected List<River> riverPaths;
    protected List<RiverGroup> riverGroups;

    MapData heightData;
    MapData heatData;
    MapData moistureData;
    double[][] initialHeatData;

    Tile[][] tiles;
    char[][] politicalMap;
    char[][] waterLandMap;


    int terrainOctaves = 6;
    int terrainRidgeOctaves = 6;
    double terrainFrequency = 1.25;

    int heatOctaves = 4;
    double heatFrequency = 3.0;

    int moistureOctaves = 4;
    double moistureFrequency = 3.0;

    double terrainNoiseScale = 1;
    double heatNoiseScale = 1;
    double moistureNoiseScale = 1;


    List<TileGroup> waters = new ArrayList<>();
    List<TileGroup> lands = new ArrayList<> ();
    List<Coord> ocean;
    List<List<Coord>> lakes;
    GreasedRegion coastline;

    protected final static BiomeType[][] BIOME_TABLE = new BiomeType[][] {
            //COLDEST        //COLDER          //COLD                  //HOT                          //HOTTER                       //HOTTEST
            { BiomeType.Ice, BiomeType.Tundra, Grassland,    BiomeType.Desert,              BiomeType.Desert,              BiomeType.Desert },              //DRYEST
            { BiomeType.Ice, BiomeType.Tundra, Grassland,    BiomeType.Desert,              BiomeType.Desert,              BiomeType.Desert },              //DRYER
            { BiomeType.Ice, BiomeType.Tundra, BiomeType.Woodland,     BiomeType.Woodland,            BiomeType.Savanna,             BiomeType.Savanna },             //DRY
            { BiomeType.Ice, BiomeType.Tundra, BiomeType.BorealForest, BiomeType.Woodland,            BiomeType.Savanna,             BiomeType.Savanna },             //WET
            { BiomeType.Ice, BiomeType.Tundra, BiomeType.BorealForest, BiomeType.SeasonalForest,      BiomeType.TropicalRainforest,  BiomeType.TropicalRainforest },  //WETTER
            { BiomeType.Ice, BiomeType.Tundra, BiomeType.BorealForest, BiomeType.TemperateRainforest, BiomeType.TropicalRainforest,  BiomeType.TropicalRainforest }   //WETTEST
    };

    public final OrderedMap<Character, String> atlas = new OrderedMap<>(16);

    TiledTerrainGraph graph;
    TiledSmoothableGraphPath<TiledTerrainNode> aStarPath;
    TiledElevationDistance<TiledTerrainNode> heuristic;
    IndexedAStarPathFinder<TiledTerrainNode> riverPathFinder;

    GreasedRegion temp;
    GreasedRegion reuse;

    private int riverPathCount = 0;
    GreasedRegion heights;
    GreasedRegion riverBlockages;
    GreasedRegion rivers;

    public MapGenerator(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
    }

    public Pixmap generateGameMap() {
        initialize();

        getData();
        loadTiles();
        updateNeighbors();
        // fillDepressions();

        refreshTiles(true);

        createWaterLandMaps();

        generateRivers();
        // buildRiverGroups();
        // digRiverGroups();

        Coord c;
        for(River r : riverPaths) {
            // cleanUpRiverFlow(r);
            // erodeRiverBed(r);
            Tile riverOrigin = tiles[r.path.get(0).x][r.path.get(0).y];
            Tile riverDestination = tiles[r.path.get(r.path.size()-1).x][r.path.get(r.path.size()-1).y];
            double oldRange = (riverOrigin.heightValue - riverDestination.heightValue);
            for(int i = 0; i < r.path.size(); i++) {
                c = r.path.get(i);
                tiles[c.x][c.y].originalHeightValue = tiles[c.x][c.y].heightValue;
                tiles[c.x][c.y].heightValue = 0;
                // tiles[c.x][c.y].heightValue = MathUtils.lerp((float)riverOrigin.heightValue, (float)riverDestination.heightValue, (float) ((tiles[c.x][c.y].heightValue - riverDestination.heightValue) / oldRange));
                rivers.add(c);
            }

        }

        for(Coord r : rivers) {
            if(tiles[r.x][r.y].originalHeightValue == 0) {
                tiles[r.x][r.y].originalHeightValue = tiles[r.x][r.y].heightValue;
                tiles[r.x][r.y].heightValue = .5;
            }
            tiles[r.x][r.y].heightType = HeightType.River;
        }


        adjustMoistureMap();

        // refreshTiles(false);

        generateBiomeMap ();

        int numFactions = CommonRNG.getRng().between(6, 15);

        generateFactionMap(numFactions, CommonRNG.getRng().between(.5, 1));

        generateCities();

        // MapTextureGenerator.generateHeightMapTexture(WIDTH, HEIGHT, tiles, true);
        // MapTextureGenerator.generateWaterMapTexture(WIDTH, HEIGHT, waterLandMap);
        // MapTextureGenerator.generateHeatMapTexture(WIDTH, HEIGHT, tiles);
        // MapTextureGenerator.generateMoistureMapTexture(WIDTH, HEIGHT, tiles);
        // MapTextureGenerator.generatePoliticalMapTexture(WIDTH, HEIGHT, atlas, politicalMap, tiles, ColdestValue, ColderValue, ColdValue);
        return MapTextureGenerator.generateGameMap(WIDTH, HEIGHT, tiles, ColdestValue, ColderValue, ColdValue, WarmValue);
    }

    private void generateCities() {

        GreasedRegion factionTerritory = new GreasedRegion(WIDTH,HEIGHT);
        for(char factionSymbol : atlas.keySet()) {
            if(!Character.isLetter(factionSymbol))
                continue;

            factionTerritory.refill(politicalMap, factionSymbol);

            DungeonUtility.debugPrint(factionTerritory.toChars());
        }
    }

    public Texture generate() {
        initialize();

        getData();
        loadTiles();
        updateNeighbors();
        // fillDepressions();

        refreshTiles(true);

        createWaterLandMaps();

        generateRivers();
        // buildRiverGroups();
        // digRiverGroups();

        Coord c;
        for(River r : riverPaths) {
            // cleanUpRiverFlow(r);
            // erodeRiverBed(r);
            Tile riverOrigin = tiles[r.path.get(0).x][r.path.get(0).y];
            Tile riverDestination = tiles[r.path.get(r.path.size()-1).x][r.path.get(r.path.size()-1).y];
            double oldRange = (riverOrigin.heightValue - riverDestination.heightValue);
            for(int i = 0; i < r.path.size(); i++) {
                c = r.path.get(i);
                tiles[c.x][c.y].originalHeightValue = tiles[c.x][c.y].heightValue;
                tiles[c.x][c.y].heightValue = 0;
                // tiles[c.x][c.y].heightValue = MathUtils.lerp((float)riverOrigin.heightValue, (float)riverDestination.heightValue, (float) ((tiles[c.x][c.y].heightValue - riverDestination.heightValue) / oldRange));
                rivers.add(c);
            }

        }

        rivers.expand(2).retract8way();

        for(Coord r : rivers) {
            if(tiles[r.x][r.y].originalHeightValue == 0) {
                tiles[r.x][r.y].originalHeightValue = tiles[r.x][r.y].heightValue;
                tiles[r.x][r.y].heightValue = .5;
            }
            tiles[r.x][r.y].heightType = HeightType.River;
        }


        adjustMoistureMap();

        // refreshTiles(false);

        generateBiomeMap ();

        generateFactionMap(15, 1);

        MapTextureGenerator.generateHeightMapTexture(WIDTH, HEIGHT, tiles, true);
        MapTextureGenerator.generateWaterMapTexture(WIDTH, HEIGHT, waterLandMap);
        MapTextureGenerator.generateHeatMapTexture(WIDTH, HEIGHT, tiles);
        MapTextureGenerator.generateMoistureMapTexture(WIDTH, HEIGHT, tiles);
        MapTextureGenerator.generatePoliticalMapTexture(WIDTH, HEIGHT, atlas, politicalMap, tiles, ColdestValue, ColderValue, ColdValue);
        return MapTextureGenerator.generateBiomeMapTexture(WIDTH, HEIGHT, tiles, ColdestValue, ColderValue, ColdValue, WarmValue);
    }

    private void buildRiverGroups() {
        //loop each tile, checking if it belongs to multiple riverPaths
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Tile t = tiles[x][y];

                if (t.rivers.size() > 1)
                {
                    // multiple riverPaths == intersection
                    RiverGroup group = null;

                    // Does a rivergroup already exist for this group?
                    for (int n=0; n < t.rivers.size(); n++)
                    {
                        River tileriver = t.rivers.get(n);
                        for (int i = 0; i < riverGroups.size(); i++)
                        {
                            for (int j = 0; j < riverGroups.get(i).rivers.size(); j++)
                            {
                                River river = riverGroups.get(i).rivers.get(j);
                                if (river.id == tileriver.id)
                                {
                                    group = riverGroups.get(i);
                                }
                                if (group != null) break;
                            }
                            if (group != null) break;
                        }
                        if (group != null) break;
                    }

                    // existing group found -- add to it
                    if (group != null)
                    {
                        for (int n=0; n < t.rivers.size(); n++)
                        {
                            if (!group.rivers.contains(t.rivers.get(n)))
                                group.rivers.add(t.rivers.get(n));
                        }
                    }
                    else   //No existing group found - create a new one
                    {
                        group = new RiverGroup();
                        for (int n=0; n < t.rivers.size(); n++)
                        {
                            group.rivers.add(t.rivers.get(n));
                        }
                        riverGroups.add (group);
                    }
                }
            }
        }
    }

    private void generateRivers() {
        rivers = new GreasedRegion(WIDTH, HEIGHT);
        riverPaths = new ArrayList<>();
        riverGroups = new ArrayList<>();

        Coord[] riverSources = generateRiverSources();
        riverBlockages = generateRiverBlockages(riverSources);

        graph = new TiledTerrainGraph(WIDTH, HEIGHT, heightData, riverBlockages);
        graph.init();
        aStarPath = new TiledSmoothableGraphPath<>();
        heuristic = new TiledElevationDistance<>();
        riverPathFinder = new IndexedAStarPathFinder<>(graph);
        heights = new GreasedRegion(WIDTH, HEIGHT);
        temp = new GreasedRegion(WIDTH, HEIGHT);
        reuse = new GreasedRegion(WIDTH, HEIGHT);

        Tile riverSourceTile = null;
        int riversToGenerate = CommonRNG.getRng().between(40,Math.max(WIDTH,HEIGHT)/2);

        double startTime = System.currentTimeMillis();
        double endTime = startTime + 2000;
        for(Coord riverSource : riverSources) {

            riverSourceTile = tiles[riverSource.x][riverSource.y];
            River river = new River(riverCount);

            river.currentDirection = riverSourceTile.getLowestNeighbor(this);
            if (river.currentDirection == O)
                continue;

            Tile destination = findPathToWater(riverSourceTile);
            if (destination == null) {
                continue;
            }
            if (destination.equals(riverSourceTile)) {
                continue;
            }

            aStarPath.clear();
            TiledTerrainNode startNode = graph.getNode(riverSource.x, riverSource.y);
            graph.startNode = startNode;
            riverPathFinder.searchNodePath(
                    startNode
                    , graph.getNode(destination.x, destination.y)
                    , heuristic
                    , aStarPath
            );
            if (aStarPath.nodes.size > 20) {
                for (TiledTerrainNode node : aStarPath.nodes) {
                    //river.addTile(tiles[node.coord.x][node.coord.y]);
                    if(tiles[node.coord.x][node.coord.y].heightValue < CoastalWater)
                        break;
                    river.path.add(node.coord);
                }

                riverPaths.add(river);
                riverCount++;
                System.out.println(riverCount);
            } else {
                // Form lake
                continue;
            }
        }
    }

    private Tile findPathToWater(Tile riverSourceTile) {
        Tile currentTile, nextTile;
        Coord newDestination = null, lastDestination =  riverSourceTile.getCoord(), currentCoord = null;
        List<Coord> coordsToIgnore = new ArrayList<>();

        currentTile = riverSourceTile;

        outerloop:
        while(currentTile.heightValue >= CoastalWater) {

            for(Tile neighbor : currentTile.getNeighbors()) {
                currentCoord = currentTile.getCoord();
                for(River river1 : riverPaths) {
                    if(river1.path.contains(neighbor)) {
                        return neighbor;
                    }
                }
            }

            nextTile = currentTile.getNeighbor(currentTile.getLowestNeighbor(this));

            if(nextTile != null) {
                if(
                        !nextTile.equals(currentTile)
                                && !cyclicFlow(currentTile, nextTile)
                                && !cyclicFlow(currentTile, nextTile.getNeighbor(nextTile.getLowestNeighbor(this)))) {
                    currentTile = nextTile;
                    continue;
                }
            }

            if(newDestination != null)
                lastDestination = newDestination;

            newDestination = findLowerElevation(currentCoord, coordsToIgnore, riverBlockages, reuse, temp);

            int attempts = 0;
            while(lastDestination.equals(newDestination) || newDestination == null) {
                attempts++;
                if(attempts >= 5)
                    break outerloop;
                coordsToIgnore.add(newDestination);
                newDestination = findLowerElevation(currentCoord, coordsToIgnore, riverBlockages, reuse, temp);
            }

            coordsToIgnore.clear();;

            currentTile = tiles[newDestination.x][newDestination.y];
            continue;
        }
        return currentTile;
    }

    private boolean cyclicFlow(Tile currentTile, Tile other) {
        Tile othersLowestNeighbor = other.getNeighbor(other.getLowestNeighbor(this));
        return othersLowestNeighbor.x == currentTile.x && othersLowestNeighbor.y == currentTile.y;
    }

    private void fillDepressions() {
        Queue<CoordinateElevation> open = new PriorityQueue<>();
        boolean[][] closed = new boolean[WIDTH][HEIGHT];

        // Adding perimeter cells to the priority queue
        for(int x = 0; x < heightData.data.length; x++) {
            open.add(new CoordinateElevation(Coord.get(x, 0), Double.valueOf(heightData.data[x][0])));
            open.add(new CoordinateElevation(Coord.get(x, HEIGHT-1), heightData.data[x][HEIGHT-1]));
            closed[x][0] = true;
            closed[x][HEIGHT-1] = true;
        }
        for(int y = 0; y < heightData.data[0].length; y++) {
            open.add(new CoordinateElevation(Coord.get(0, y), Double.valueOf(heightData.data[0][y])));
            open.add(new CoordinateElevation(Coord.get(WIDTH-1, y), heightData.data[WIDTH-1][y]));
            closed[0][y] = true;
            closed[WIDTH-1][y] = true;
        }

        // Performing priority flood
        Tile t;
        while(open.size() > 0) {
            CoordinateElevation coordinateElevation = open.peek();
            open.poll();

            Coord coordinate = coordinateElevation.coord;
            t = tiles[coordinate.x][coordinate.y];
            int tx = t.x, ty = t.y;
            for(Tile neighbor : t.getNeighbors()) {
                int nx = neighbor.x, ny = neighbor.y;
                if(closed[nx][ny])
                    continue;

                closed[nx][ny] = true;

                heightData.data[nx][ny] = Math.max(heightData.data[nx][ny], heightData.data[tx][ty]);
                open.add(new CoordinateElevation(Coord.get(neighbor.x, neighbor.y), heightData.data[nx][ny]));
            }
        }
    }

    private void digRiverGroups() {
        {
            for (int i = 0; i < riverGroups.size(); i++) {

                RiverGroup group = riverGroups.get(i);
                River longest = null;

                //Find longest river in this group
                for (int j = 0; j < group.rivers.size(); j++) {
                    River river = group.rivers.get(j);
                    if (longest == null)
                        longest = river;
                    else if (longest.path.size() < river.path.size())
                        longest = river;
                }

                if (longest != null) {
                    //Dig out longest path first
                    digRiver(longest);

                    for (int j = 0; j < group.rivers.size(); j++) {
                        River river = group.rivers.get(j);
                        if (river != longest) {
                            digRiver(river, longest);
                        }
                    }
                }
            }
        }
    }

    private void digRiver(River river, River parent) {
        int intersectionID = 0;
        int intersectionSize = 0;

        // determine point of intersection
        for (int i = 0; i < river.path.size(); i++) {
            Tile t1 = tiles[river.path.get(i).x][river.path.get(i).y];
            for (int j = 0; j < parent.path.size(); j++) {
                Tile t2 = tiles[parent.path.get(j).x][parent.path.get(j).y];
                if (t1 == t2)
                {
                    intersectionID = i;
                    intersectionSize = t2.riverSize;
                }
            }
        }

        int counter = 0;
        int intersectionCount = river.path.size() - intersectionID;
        int size = CommonRNG.getRng().between(intersectionSize,5);
        river.length = river.path.size();

        // randomize size change
        int two = river.length / 2;
        int three = two / 2;
        int four = three / 2;
        int five = four / 2;

        int twomin = two / 3;
        int threemin = three / 3;
        int fourmin = four / 3;
        int fivemin = five / 3;

        // randomize length of each size
        int count1 = CommonRNG.getRng().between (fivemin, five);
        if (size < 4) {
            count1 = 0;
        }
        int count2 = count1 + CommonRNG.getRng().between(fourmin, four);
        if (size < 3) {
            count2 = 0;
            count1 = 0;
        }
        int count3 = count2 + CommonRNG.getRng().between(threemin, three);
        if (size < 2) {
            count3 = 0;
            count2 = 0;
            count1 = 0;
        }
        int count4 = count3 + CommonRNG.getRng().between (twomin, two);

        // Make sure we are not digging past the river path
        if (count4 > river.path.size()) {
            int extra = count4 - river.path.size();
            while (extra > 0)
            {
                if (count1 > 0) { count1--; count2--; count3--; count4--; extra--; }
                else if (count2 > 0) { count2--; count3--; count4--; extra--; }
                else if (count3 > 0) { count3--; count4--; extra--; }
                else if (count4 > 0) { count4--; extra--; }
            }
        }

        // adjust size of river at intersection point
        if (intersectionSize == 1) {
            count4 = intersectionCount;
            count1 = 0;
            count2 = 0;
            count3 = 0;
        } else if (intersectionSize == 2) {
            count3 = intersectionCount;
            count1 = 0;
            count2 = 0;
        } else if (intersectionSize == 3) {
            count2 = intersectionCount;
            count1 = 0;
        } else if (intersectionSize == 4) {
            count1 = intersectionCount;
        } else {
            count1 = 0;
            count2 = 0;
            count3 = 0;
            count4 = 0;
        }

        // dig out the river
        for (int i = river.path.size() - 1; i >= 0; i--) {

            Tile t = tiles[river.path.get(i).x][river.path.get(i).y];

            if (counter < count1) {
                t.digRiver (river, 4);
            } else if (counter < count2) {
                t.digRiver (river, 3);
            } else if (counter < count3) {
                t.digRiver (river, 2);
            }
            else if ( counter < count4) {
                t.digRiver (river, 1);
            }
            else {
                t.digRiver (river, 0);
            }
            counter++;
        }
    }

    private void digRiver(River river) {
        int counter = 0;

        // How wide are we digging this river?
        int size = CommonRNG.getRng().between(1,5);
        river.length = river.path.size();

        // randomize size change
        int two = river.length / 2;
        int three = two / 2;
        int four = three / 2;
        int five = four / 2;

        int twomin = two / 3;
        int threemin = three / 3;
        int fourmin = four / 3;
        int fivemin = five / 3;

        // randomize lenght of each size
        int count1 = CommonRNG.getRng().between (fivemin, five);
        if (size < 4) {
            count1 = 0;
        }
        int count2 = count1 + CommonRNG.getRng().between(fourmin, four);
        if (size < 3) {
            count2 = 0;
            count1 = 0;
        }
        int count3 = count2 + CommonRNG.getRng().between(threemin, three);
        if (size < 2) {
            count3 = 0;
            count2 = 0;
            count1 = 0;
        }
        int count4 = count3 + CommonRNG.getRng().between (twomin, two);

        // Make sure we are not digging past the river path
        if (count4 > river.path.size()) {
            int extra = count4 - river.path.size();
            while (extra > 0)
            {
                if (count1 > 0) { count1--; count2--; count3--; count4--; extra--; }
                else if (count2 > 0) { count2--; count3--; count4--; extra--; }
                else if (count3 > 0) { count3--; count4--; extra--; }
                else if (count4 > 0) { count4--; extra--; }
            }
        }

        // Dig it out
        for (int i = river.path.size() - 1; i >= 0 ; i--)
        {
            Tile t = tiles[river.path.get(i).x][river.path.get(i).y];

            if (counter < count1) {
                t.digRiver (river, 4);
            }
            else if (counter < count2) {
                t.digRiver (river, 3);
            }
            else if (counter < count3) {
                t.digRiver (river, 2);
            }
            else if ( counter < count4) {
                t.digRiver (river, 1);
            }
            else {
                t.digRiver(river, 0);
            }
            counter++;
        }
    }

    /*private void simulateErosion() {
        for(River river : riverPaths) {
            erodeRiverBed(river);
        }
    }*/

    private void erodeRiverBed(River river) {
        for(Coord r : river.path) {
            int rx = r.x, ry = r.y;
            int radius = 3;

            double curve;
            for(int x = (rx-radius); x < (rx+radius); x++) {
                for(int y = (ry - radius); y < (ry + radius); y++) {
                    if(!contains(x,y))
                        continue;

                    curve = 1.0;
                    if(x == 0 && y == 0)
                        continue;
                    if(river.path.contains(Coord.get(x,y)))
                        continue;
                    if(heightData.data[x][y] <= heightData.data[rx][ry])
                        continue;
                    if(!inCircle(radius, rx, ry, x, y))
                        continue;

                    double adx = Math.abs(rx -x), ady = Math.abs(ry -y);
                    if(adx == 1 || ady == 1) {
                        curve = 0.2;
                    } else if(adx == 2 || ady == 2) {
                        curve = 0.05;
                    }

                    double heightDiff = heightData.data[rx][ry] - heightData.data[x][y];
                    double newElevation = heightData.data[x][y] + (heightDiff * curve);
                    if(newElevation <= heightData.data[rx][ry]) {
                        System.out.println("Erosion problem!");
                    }
                    heightData.data[x][y] = newElevation;
                }
            }
        }
    }

    private void refreshTiles(boolean setMoistureBasedOnHeight) {
        resetHeatValues();
        for (int x = 0; x < WIDTH; x++)
        {
            for (int y = 0; y < HEIGHT; y++)
            {
                Tile t = tiles[x][y];

                double value = heightData.data[x][y];
                value = (value - heightData.min) / (heightData.max - heightData.min);

                t.heightValue = value;

                //HeightMap Analyze
                if (t.heightType == HeightType.River) {
                    t.collidable = false;
                }
                else if(t.heightType == HeightType.DebugDestination || t.heightType == HeightType.DebugSource || t.heightType == HeightType.DebugCoastline) {
                    t.collidable = false;
                }
                else if (value < DeepWater)  {
                    t.heightType = HeightType.DeepWater;
                    t.collidable = false;
                }
                else if (value < ShallowWater)  {
                    t.heightType = HeightType.ShallowWater;
                    t.collidable = false;
                }
                else if (value < MediumWater)  {
                    t.heightType = HeightType.MediumWater;
                    t.collidable = false;
                }
                else if (value < CoastalWater)  {
                    t.heightType = HeightType.CoastalWater;
                    t.collidable = false;
                }
                else if (value < Sand) {
                    t.heightType = HeightType.Sand;
                    t.collidable = true;
                }
                else if (value < Grass) {
                    t.heightType = HeightType.Grass;
                    t.collidable = true;
                }
                else if (value < Forest) {
                    t.heightType = HeightType.Forest;
                    t.collidable = true;
                }
                else if (value < Rock) {
                    t.heightType = HeightType.Rock;
                    t.collidable = true;
                }
                else  {
                    t.heightType = HeightType.Snow;
                    t.collidable = true;
                }



                //adjust moisture based on height
                if(setMoistureBasedOnHeight) {
                    if (t.heightType == HeightType.DeepWater) {
                        moistureData.data[t.x][t.y] += 8f * t.heightValue;
                    } else if (t.heightType == HeightType.MediumWater) {
                        moistureData.data[t.x][t.y] += 5f * t.heightValue;
                    } else if (t.heightType == HeightType.ShallowWater) {
                        moistureData.data[t.x][t.y] += 3f * t.heightValue;
                    } else if (t.heightType == HeightType.CoastalWater) {
                        moistureData.data[t.x][t.y] += 2f * t.heightValue;
                    } else if (t.heightType == HeightType.Shore) {
                        moistureData.data[t.x][t.y] += 1f * t.heightValue;
                    } else if (t.heightType == HeightType.Sand) {
                        moistureData.data[t.x][t.y] += 0.2f * t.heightValue;
                    }
                }

                //Moisture Map Analyze
                double moistureValue = moistureData.data[x][y];
                moistureValue = (moistureValue - moistureData.min) / (moistureData.max - moistureData.min);
                t.moistureValue = moistureValue;

                //set moisture type
                if (moistureValue < DryerValue) t.moistureType = MoistureType.Dryest;
                else if (moistureValue < DryValue) t.moistureType = MoistureType.Dryer;
                else if (moistureValue < WetValue) t.moistureType = MoistureType.Dry;
                else if (moistureValue < WetterValue) t.moistureType = MoistureType.Wet;
                else if (moistureValue < WettestValue) t.moistureType = MoistureType.Wetter;
                else t.moistureType = MoistureType.Wettest;

                // Adjust Heat Map based on Height - Higher == colder
                if (t.heightType == HeightType.Forest) {
                    heatData.data[t.x][t.y] -= 0.1f * t.heightValue;
                }
                else if (t.heightType == HeightType.Rock) {
                    heatData.data[t.x][t.y] -= 0.25f * t.heightValue;
                }
                else if (t.heightType == HeightType.Snow) {
                    heatData.data[t.x][t.y] -= 0.4f * t.heightValue;
                }
                else {
                    heatData.data[t.x][t.y] += 0.01f * t.heightValue;
                }

                // Set heat value
                double heatValue = heatData.data[x][y];
                heatValue = (heatValue - heatData.min) / (heatData.max - heatData.min);
                t.heatValue = heatValue;

                // set heat type
                if (heatValue < ColdestValue) t.heatType = HeatType.Coldest;
                else if (heatValue < ColderValue) t.heatType = HeatType.Colder;
                else if (heatValue < ColdValue) t.heatType = HeatType.Cold;
                else if (heatValue < WarmValue) t.heatType = HeatType.Warm;
                else if (heatValue < WarmerValue) t.heatType = HeatType.Warmer;
                else t.heatType = HeatType.Warmest;

                tiles[x][y] = t;
            }
        }
    }

    private void resetHeatValues() {
        heatData.data = initialHeatData;
    }

    private void createWaterLandMaps() {
        waterLandMap = ArrayTools.fill('#', WIDTH, HEIGHT);
        for(int x = 0; x < WIDTH; x++) {
            for(int y = 0; y < HEIGHT; y++) {
                if(tiles[x][y].heightType.getNumVal() > 5) {
                    waterLandMap[x][y] = '.';
                }
            }
        }

        List<List<Coord>> waterBodies = new ArrayList<>();
        char[][] tempWaterMap = ArrayTools.copy(waterLandMap);

        GreasedRegion waterRegion = new GreasedRegion(waterLandMap, '#');
        char[][] water = waterRegion.toChars();


        Coord spreadSource;
        List<Coord> spill;
        Splash spreader = new Splash();
        while(waterRegion.size() > 0) {
            water = waterRegion.toChars();
            spreadSource = waterRegion.singleRandom(CommonRNG.getRng());
            spill = spreader.spill(CommonRNG.getRng(), water, spreadSource, WIDTH*HEIGHT, 0);
            waterBodies.add(spill);
            for(Coord c: spill) {
                tempWaterMap[c.x][c.y] = '#';
            }
            tempWaterMap[spreadSource.x][spreadSource.y] = '#';
            /*DungeonUtility.debugPrint(tempWaterMap);*/
            waterRegion.removeAll(spill);
            /*System.out.println();*/
        }
        /*System.out.println();*/

        waterBodies.sort(new Comparator<List<Coord>>() {
            @Override
            public int compare(List<Coord> o1, List<Coord> o2) {
                return Integer.valueOf(o1.size()).compareTo(o2.size());
            }
        });

        ocean = waterBodies.get(waterBodies.size()-1);
        waterBodies.remove(ocean);
        lakes = waterBodies;
        coastline = new GreasedRegion(waterLandMap, '.').fringe();

        /*Tile t;
        for(Coord c : ocean) {
            t = tiles[c.x][c.y];
            t.heightValue = 0;
            t.heightType = HeightType.River;
        }*/
    }

    private boolean contains(int tx, int ty) {
        if(tx >= 0 && tx < WIDTH && ty >= 0 && ty < HEIGHT) return true;
        return false;
    }

    private void generateFactionMap(final int factionCount, double controlledFraction) {
        MultiSpill spreader = new MultiSpill(new short[WIDTH][HEIGHT], Spill.Measurement.MANHATTAN, CommonRNG.getRng());
        OrderedMap<Coord, Double> entries = new OrderedMap<>();

        short[][] regionMap = new short[WIDTH][HEIGHT];

        for(int x = 0; x < WIDTH; x++) {
            for(int y = 0; y < HEIGHT; y++) {
                if(tiles[x][y].heightValue >= Sand)
                {
                    regionMap[x][y] = 0;
                }
                else
                {
                    regionMap[x][y] = -1;
                }
            }
        }

        GreasedRegion factionMap = new GreasedRegion(waterLandMap, '.');
        Coord[] centers = factionMap.randomSeparated(0.1, CommonRNG.getRng(), factionCount);
        int controlled = (int) (factionMap.size() * Math.max(0.0, Math.min(1.0, controlledFraction)));

        Set<Coord> impassable = new squidpony.squidmath.OrderedSet<>(factionMap.not().asCoords());

        spreader.initialize(regionMap);
        entries.put(Coord.get(-1, -1), 0.0);
        for (int i = 0; i < factionCount; i++) {
            entries.put(centers[i], CommonRNG.getRng().nextDouble());
        }
        spreader.start(entries, controlled, impassable);
        regionMap = spreader.spillMap;



        politicalMap = new char[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                politicalMap[x][y] = (regionMap[x][y] == -1) ? '~' : (regionMap[x][y] == 0) ? '%' : letters[(regionMap[x][y] - 1) & 255];
            }
        }
        atlas.clear();
        atlas.put('~', "Water");
        atlas.put('%', "Wilderness");
        if(factionCount > 0) {
            Thesaurus th = new Thesaurus(CommonRNG.getRng().nextLong());
            th.addKnownCategories();
            for (int i = 0; i < factionCount && i < 256; i++) {
                atlas.put(letters[i], th.makeNationName());
            }
        }

        // DungeonUtility.debugPrint(politicalMap);
    }

    private void initialize() {
        Coord.expandPoolTo(WIDTH+3, HEIGHT+3);

        // Initialize the heightmap generator
        ModuleFractal heightFractal = new ModuleFractal (ModuleFractal.FractalType.FBM,
                ModuleBasisFunction.BasisType.GRADIENT,
                ModuleBasisFunction.InterpolationType.QUINTIC);
        heightFractal.setNumOctaves(terrainOctaves);
        heightFractal.setFrequency(terrainFrequency);
        heightFractal.setSeed(CommonRNG.getRng().between(0, Integer.MAX_VALUE));

        ModuleFractal ridgedHeightFractal = new ModuleFractal (ModuleFractal.FractalType.RIDGEMULTI,
                ModuleBasisFunction.BasisType.SIMPLEX,
                ModuleBasisFunction.InterpolationType.QUINTIC);
        ridgedHeightFractal.setNumOctaves(terrainRidgeOctaves);
        ridgedHeightFractal.setFrequency(terrainFrequency);
        ridgedHeightFractal.setSeed(CommonRNG.getRng().between(0, Integer.MAX_VALUE));

        ModuleTranslateDomain heightTranslateDomain = new ModuleTranslateDomain();
        heightTranslateDomain.setSource(heightFractal);
        heightTranslateDomain.setAxisXSource(ridgedHeightFractal);

        heightmap = new ModuleAutoCorrect();
        heightmap.setSamples(1000);
        heightmap.setSource(heightTranslateDomain);
        heightmap.calculate();


        // Initialize the heat map generator
        ModuleGradient heatmapGradient = new ModuleGradient();
        heatmapGradient.setGradient(0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);

        ModuleFractal heatFractal = new ModuleFractal (ModuleFractal.FractalType.MULTI,
                ModuleBasisFunction.BasisType.SIMPLEX,
                ModuleBasisFunction.InterpolationType.QUINTIC);
        heatFractal.setNumOctaves(heatOctaves);
        heatFractal.setFrequency(heatFrequency);
        heatFractal.setSeed(CommonRNG.getRng().between(0, Integer.MAX_VALUE));

        heatmap = new ModuleCombiner(ModuleCombiner.CombinerType.MULT);
        heatmap.setSource(0, heatmapGradient);
        heatmap.setSource(1, heatFractal);


        // Initialize the moisture map generator
        ModuleFractal moistureFractal = new ModuleFractal (ModuleFractal.FractalType.MULTI,
                ModuleBasisFunction.BasisType.SIMPLEX,
                ModuleBasisFunction.InterpolationType.QUINTIC);
        moistureFractal.setNumOctaves(moistureOctaves);
        moistureFractal.setFrequency(moistureFrequency);
        moistureFractal.setSeed(CommonRNG.getRng().between(0, Integer.MAX_VALUE));

        moisturemap = new ModuleAutoCorrect();
        moisturemap.setSamples(1000);
        moisturemap.setSource(moistureFractal);
        moisturemap.calculate();
    }

    // Extract data from a noise module
    private void getData() {
        heightData = new MapData(WIDTH, HEIGHT);
        heatData = new MapData(WIDTH, HEIGHT);
        moistureData = new MapData(WIDTH, HEIGHT);

        // loop through each x,y point - get height value
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {

                // Noise range
                float x1 = 0, x2 = 2;
                float y1 = 0, y2 = 2;
                float dx = x2 - x1;
                float dy = y2 - y1;

                // Sample noise at smaller intervals
                float s = x / (float) WIDTH;
                float t = y / (float) HEIGHT;


                // Calculate our 4D coordinates
                float nx = x1 + MathUtils.cos(s * 2 * MathUtils.PI) * dx / (2 * MathUtils.PI);
                float ny = y1 + MathUtils.cos(t * 2 * MathUtils.PI) * dy / (2 * MathUtils.PI);
                float nz = x1 + MathUtils.sin(s * 2 * MathUtils.PI) * dx / (2 * MathUtils.PI);
                float nw = y1 + MathUtils.sin(t * 2 * MathUtils.PI) * dy / (2 * MathUtils.PI);

                float heightValue = (float) heightmap.get(nx * terrainNoiseScale, ny * terrainNoiseScale, nz * terrainNoiseScale, nw * terrainNoiseScale);
                float heatValue = (float) heatmap.get(nx * heatNoiseScale, ny * heatNoiseScale, nz * heatNoiseScale, nw * heatNoiseScale);
                float moistureValue = (float) moisturemap.get(nx * moistureNoiseScale, ny * moistureNoiseScale, nz * moistureNoiseScale, nw * moistureNoiseScale);

                // keep track of the max and min values found
                if (heightValue > heightData.max) heightData.max = heightValue;
                if (heightValue < heightData.min) heightData.min = heightValue;

                if (heatValue > heatData.max) heatData.max = heatValue;
                if (heatValue < heatData.min) heatData.min = heatValue;

                if (moistureValue > moistureData.max) moistureData.max = moistureValue;
                if (moistureValue < moistureData.min) moistureData.min = moistureValue;

                heightData.data[x][y] = heightValue;
                heatData.data[x][y] = heatValue;
                moistureData.data[x][y] = moistureValue;

                initialHeatData = heatData.data;
            }
        }
    }

    // Build a Tile array from our data
    private void loadTiles() {
        tiles = new Tile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x++)
        {
            for (int y = 0; y < HEIGHT; y++)
            {
                Tile t = new Tile();
                t.x = x;
                t.y = y;

                double value = heightData.data[x][y];
                value = (value - heightData.min) / (heightData.max - heightData.min);

                t.heightValue = value;

                //HeightMap Analyze
                if (value < DeepWater)  {
                    t.heightType = HeightType.DeepWater;
                    t.collidable = false;
                }
                else if (value < ShallowWater)  {
                    t.heightType = HeightType.ShallowWater;
                    t.collidable = false;
                }
                else if (value < MediumWater)  {
                    t.heightType = HeightType.MediumWater;
                    t.collidable = false;
                }
                else if (value < CoastalWater)  {
                    t.heightType = HeightType.CoastalWater;
                    t.collidable = false;
                }
                else if (value < Sand) {
                    t.heightType = HeightType.Sand;
                    t.collidable = true;
                }
                else if (value < Grass) {
                    t.heightType = HeightType.Grass;
                    t.collidable = true;
                }
                else if (value < Forest) {
                    t.heightType = HeightType.Forest;
                    t.collidable = true;
                }
                else if (value < Rock) {
                    t.heightType = HeightType.Rock;
                    t.collidable = true;
                }
                else  {
                    t.heightType = HeightType.Snow;
                    t.collidable = true;
                }

                // Adjust Heat Map based on Height - Higher == colder
                if (t.heightType == HeightType.Forest) {
                    heatData.data[t.x][t.y] -= 0.1f * t.heightValue;
                }
                else if (t.heightType == HeightType.Rock) {
                    heatData.data[t.x][t.y] -= 0.25f * t.heightValue;
                }
                else if (t.heightType == HeightType.Snow) {
                    heatData.data[t.x][t.y] -= 0.4f * t.heightValue;
                }
                else {
                    heatData.data[t.x][t.y] += 0.01f * t.heightValue;
                }

                // Set heat value
                double heatValue = heatData.data[x][y];
                heatValue = (heatValue - heatData.min) / (heatData.max - heatData.min);
                t.heatValue = heatValue;

                // set heat type
                if (heatValue < ColdestValue) t.heatType = HeatType.Coldest;
                else if (heatValue < ColderValue) t.heatType = HeatType.Colder;
                else if (heatValue < ColdValue) t.heatType = HeatType.Cold;
                else if (heatValue < WarmValue) t.heatType = HeatType.Warm;
                else if (heatValue < WarmerValue) t.heatType = HeatType.Warmer;
                else t.heatType = HeatType.Warmest;

                tiles[x][y] = t;
            }
        }
    }

    private void floodFill(Tile tile, TileGroup tiles, Stack<Tile> stack) {
        // Validate
        if (tile.floodFilled)
            return;
        if (tiles.type == TileGroup.TileGroupType.Land && !tile.collidable)
            return;
        if (tiles.type == TileGroup.TileGroupType.Water && tile.collidable)
            return;

        // Add to TileGroup
        tiles.tiles.add (tile);
        tile.floodFilled = true;

        // floodfill into neighbors
        Tile t = getTop (tile);
        if (!t.floodFilled && tile.collidable == t.collidable)
            stack.push (t);
        t = getBottom (tile);
        if (!t.floodFilled && tile.collidable == t.collidable)
            stack.push (t);
        t = getLeft (tile);
        if (!t.floodFilled && tile.collidable == t.collidable)
            stack.push (t);
        t = getRight (tile);
        if (!t.floodFilled && tile.collidable == t.collidable)
            stack.push (t);
    }

    private void updateNeighbors() {
        for (int x = 0; x < WIDTH; x++)
        {
            for (int y = 0; y < HEIGHT; y++)
            {
                Tile t = tiles[x][y];

                t.top = getTop(t);
                t.bottom = getBottom(t);
                t.left = getLeft(t);
                t.right = getRight(t);

                tiles[x][y] = t;
            }
        }
    }

    private void updateBitmasks() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles [x][y].updateBitmask ();
            }
        }
    }

    private void updateBiomeBitmask() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles [x][y].updateBiomeBitmask ();
            }
        }
    }

    public BiomeType getBiomeType(Tile tile) {
        BiomeType biomeType = BIOME_TABLE [tile.moistureType.getNumVal()][tile.heatType.getNumVal()];
        return biomeType;
    }

    private void generateBiomeMap() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {

                if (!tiles[x][y].collidable) continue;

                Tile t = tiles[x][y];
                t.biomeType = getBiomeType(t);
                tiles[x][y] = t;
            }
        }
    }

    private void addMoisture(Tile t, int radius) {
        int startx = MathHelper.mod (t.x - radius, WIDTH);
        int endx = MathHelper.mod (t.x + radius, WIDTH);
        Vector2 center = new Vector2(t.x, t.y);
        int curr = radius;

        while (curr > 0) {

            int x1 = MathHelper.mod (t.x - curr, WIDTH);
            int x2 = MathHelper.mod (t.x + curr, WIDTH);
            int y = t.y;

            addMoisture(tiles[x1][y], 0.025f / (center.sub(new Vector2(x1, y))).len());

            for (int i = 0; i < curr; i++)
            {
                addMoisture (tiles[x1][MathHelper.mod (y + i + 1, HEIGHT)], 0.025f / (center.sub(new Vector2(x1, MathHelper.mod (y + i + 1, HEIGHT)))).len());
                addMoisture (tiles[x1][MathHelper.mod (y - (i + 1), HEIGHT)], 0.025f / (center.sub(new Vector2(x1, MathHelper.mod (y - (i + 1), HEIGHT)))).len());

                addMoisture (tiles[x2][MathHelper.mod (y + i + 1, HEIGHT)], 0.025f / (center.sub(new Vector2(x2, MathHelper.mod (y + i + 1, HEIGHT)))).len());
                addMoisture (tiles[x2][MathHelper.mod (y - (i + 1), HEIGHT)], 0.025f / (center.sub(new Vector2(x2, MathHelper.mod (y - (i + 1), HEIGHT)))).len());
            }
            curr--;
        }
    }

    private void addMoisture(Tile t, float amount) {
        moistureData.data[t.x][t.y] += amount;
        t.moistureValue += amount;
        if (t.moistureValue > 1)
            t.moistureValue = 1;

        //set moisture type
        if (t.moistureValue < DryerValue) t.moistureType = MoistureType.Dryest;
        else if (t.moistureValue < DryValue) t.moistureType = MoistureType.Dryer;
        else if (t.moistureValue < WetValue) t.moistureType = MoistureType.Dry;
        else if (t.moistureValue < WetterValue) t.moistureType = MoistureType.Wet;
        else if (t.moistureValue < WettestValue) t.moistureType = MoistureType.Wetter;
        else t.moistureType = MoistureType.Wettest;
    }

    private void adjustMoistureMap() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {

                Tile t = tiles[x][y];
                if (t.heightType == HeightType.River)
                {
                    addMoisture (t, (int)60);
                }
            }
        }
    }

    private boolean inCircle(int radius, int centerX, int centerY, int x, int y) {
        return Coord.get(centerX,centerY).distanceSq(Coord.get(x,y)) <= Math.pow(radius, 2);
    }

    private boolean lakeContains(Coord coord) {
        for(List<Coord> lake : lakes) {
            if (lake.contains(coord))
                return true;
        }
        return false;
    }

    private GreasedRegion generateRiverBlockages(Coord[] riverSources) {
        ArrayList<Coord> sources = new ArrayList(Arrays.asList(riverSources));
        char[][] sourceMap = new char[WIDTH][HEIGHT];
        for(int x = 0; x < WIDTH; x++) {
            for(int y = 0;  y < HEIGHT; y++) {
                sourceMap[x][y] = sources.contains(Coord.get(x,y)) ? '#' : '.';
            }
        }

        // DungeonUtility.debugPrint(sourceMap);

        // System.out.println();

        GreasedRegion blockages = new GreasedRegion(CommonRNG.getRng(), .4, WIDTH, HEIGHT).andNot(new GreasedRegion(sourceMap, '#'));

        return blockages;
    }

    private Coord[] generateRiverSources() {
        List<Coord> riverSources;

        GreasedRegion potentialRiverSources = new GreasedRegion(heightData.data, Grass, Rock);

        riverSources = new ArrayList<>(Arrays.asList(potentialRiverSources.quasiRandomSeparated(.1, 50)));

        return riverSources.toArray(new Coord[riverSources.size()]);
    }

    private void cleanUpRiverFlow(River river) {
        Tile r, pr;
        for(int i = 0; i < river.path.size(); i++) {
            r = tiles[river.path.get(i).x][river.path.get(i).y];
            r.originalHeightValue = r.heightValue;
            r.heightValue *= .25;
            if(i > 0) {
                pr = tiles[river.path.get(i - 1).x][river.path.get(i - 1).y];

                if(r.heightValue >= pr.heightValue) {
                    r.heightValue = pr.heightValue - 0.01;
                }
            }
        }
    }

    private Coord findLowerElevation(Coord currentLocation, List<Coord> coordsToIgnore, GreasedRegion riverBlockages,
                                     GreasedRegion reuse, GreasedRegion temp) {
        heights.refill(heightData.data, Sand, heightData.data[currentLocation.x][currentLocation.y])
                .andNot(riverBlockages);

        heights.removeAll(coordsToIgnore);

        reuse.clear();
        reuse.insert(currentLocation);
        temp.remake(reuse).fringe();
        int searchDistance = Math.max(WIDTH, HEIGHT);
        for(int d = 1; d < searchDistance; d++)
        {
            if(temp.intersects(heights))
            {
                return temp.singleRandom(CommonRNG.getRng());
            }
            temp.remake(reuse.expand()).fringe();
        }
        return null;
    }

    public double getHeightValue(Tile tile) {
        if (tile == null)
            return Integer.MAX_VALUE;
        else
            return tile.heightValue;
    }


    private Tile getTop(Tile t)
    {
        return tiles [t.x][MathHelper.mod (t.y - 1, HEIGHT)];
    }
    private Tile getBottom(Tile t)
    {
        return tiles [t.x][MathHelper.mod (t.y + 1, HEIGHT)];
    }
    private Tile getLeft(Tile t)
    {
        return tiles [MathHelper.mod(t.x - 1, WIDTH)][t.y];
    }
    private Tile getRight(Tile t)
    {
        return tiles [MathHelper.mod (t.x + 1, WIDTH)][t.y];
    }
    private Tile getTopLeft(Tile t) {
        return tiles [MathHelper.mod(t.x-1, WIDTH)][MathHelper.mod(t.y+1, WIDTH)];
    }
    private Tile getTopRight(Tile t) {
        return tiles [MathHelper.mod(t.x+1, WIDTH)][MathHelper.mod(t.y+1, WIDTH)];
    }
    private Tile getBottomLeft(Tile t) {
        return tiles [MathHelper.mod(t.x-1, WIDTH)][MathHelper.mod(t.y-1, WIDTH)];
    }
    private Tile getBottomRight(Tile t) {
        return tiles [MathHelper.mod(t.x+1, WIDTH)][MathHelper.mod(t.y-1, WIDTH)];
    }

    public void dispose() {
        heightmap = null;
        heatmap = null;
        moisturemap = null;

        heightData = null;
        heatData = null;
        moistureData = null;

        tiles = null;
        politicalMap = null;
        waterLandMap = null;

        aStarPath = null;
        heuristic = null;
        riverPathFinder = null;
        MapTextureGenerator.dispose();

        waters = null;
        lands = null;
        ocean = null;
        lakes = null;
    }

    public Tile[][] getTiles() {
        return tiles;
    }
}

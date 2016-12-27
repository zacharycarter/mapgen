package com.carterza.universe.generator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.carterza.common.CommonRNG;
import com.carterza.math.Vec2I;
import com.carterza.universe.*;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SColorFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.CrossHash;
import squidpony.squidmath.PerlinNoise;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachcarter on 12/10/16.
 */
public class SolarSystemGenerator implements IGenerator {

    private final int HALF_SIZE_X = SolarSystem.SOLAR_SYSTEM_WIDTH/4;
    private final int HALF_SIZE_Y = SolarSystem.SOLAR_SYSTEM_HEIGHT/4;

    Universe universe;
    Galaxy galaxy;
    StarSystem starSystem;
    SolarSystem solarSystem;
    List<Sun> suns;
    List<Planet> planets;

    CelestialBody[][] buffer;

    SColorFactory sColorFactory;


    final int[] starMultiples = new int[]{
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 2, 2, 2, 2, 2, 2,
            2, 3, 3, 3, 3, 4, 4, 5
    };

    final int[] planetMultiples = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 1, 2, 2, 2, 3, 3,
            4, 4, 5, 6, 7, 8, 9, 10
    };

    public CelestialBody[][] getBuffer() {
        return buffer;
    }

    public List<Sun> getSuns() {
        return suns;
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    enum StarTypes {

        CLASS_O_STAR(new Sun('*', Color.BLUE, 40, 1, "Class O Star"))
        , CLASS_B_STAR(new Sun('*', Color.CYAN, 24, 100, "Class B Star"))
        , CLASS_A_STAR(new Sun('*', Color.SKY, 12, 100, "Class A Star"))
        , CLASS_F_STAR(new Sun('*', Color.WHITE, 8, 500, "Class F Star"))
        , CLASS_G_STAR(new Sun('*', Color.YELLOW, 6, 1500, "Class G Star"))
        , CLASS_K_STAR(new Sun('*', Color.ORANGE, 6, 2200, "Class K Star"))
        , CLASS_M_STAR(new Sun('*', Color.RED, 4, 4500, "Class M Star"))
        , CLASS_D_STAR(new Sun('*', Color.WHITE, 8, 100, "Class D Star"));

        StarTypes(Sun star) {
            this.star = star;
        }

        private Sun star;

        Sun getStar() {
            return star;
        }
    }

    public enum PlanetTypes {

        TERRAN(new Planet('●', new SColor(50,72,200), "Terran"))
        , OCEAN(new Planet('●', new SColor(27, 75, 174), "Ocean"))
        , JUNGLE(new Planet('●', new SColor(100, 173, 22), "Jungle"))
        , LAVA(new Planet('●', new SColor(255, 52, 0), "Lava"))
        , TUNDRA(new Planet('●', new SColor(167, 157, 109), "Tundra"))
        , ARID(new Planet('●', new SColor(235, 131, 44), "Arid"))
        , DESERT(new Planet('●', new SColor(255, 178, 58), "Desert"))
        , ARTIC(new Planet('●', new SColor(255, 255, 240), "Artic"))
        , BARREN(new Planet('●', new SColor(151, 152, 113), "Barren"))
        , GAS(new Planet('●', new SColor(112, 199, 242), "Gas Giant"));

        PlanetTypes(Planet planet) {
            this.planet = planet;
        }

        private Planet planet;

        Planet getPlanet() {
            return planet;
        }
    }

    public SolarSystemGenerator(SolarSystem solarSystem, StarSystem starSystem, Galaxy galaxy, Universe universe) {
        this.universe = universe;
        this.galaxy = galaxy;
        this.starSystem = starSystem;
        this.solarSystem = solarSystem;
        this.suns = new ArrayList<Sun>();
        this.planets = new ArrayList<Planet>();
        sColorFactory = new SColorFactory();

        buffer = new CelestialBody[SolarSystem.SOLAR_SYSTEM_WIDTH][SolarSystem.SOLAR_SYSTEM_HEIGHT];
        for(int x = 0; x < SolarSystem.SOLAR_SYSTEM_WIDTH; x++) {
            for(int y = 0; y < SolarSystem.SOLAR_SYSTEM_HEIGHT; y++) {
                buffer[x][y] = new CelestialBody();
            }
        }
    }

    @Override
    public void generate() {
        System.out.println("Generating solar system!");
        generateSolarSystem();
    }

    private void generateSolarSystem() {
        CommonRNG.setSeed("solarsystem" + solarSystem.getPosition().x + solarSystem.getPosition().y + galaxy.getHash() +  starSystem.getHash());

        SolarSystem neighbor = (SolarSystem) starSystem.neighbors(0,0);
        Color nebColor = neighbor.getBg();
        System.out.println(nebColor.toString());
        final String hash = Double.valueOf(Math.floor(CommonRNG.getRng().nextDouble() * 100000000000.0)).toString(16) + "sol";

        int starCount = starMultiples[CommonRNG.getRng().between(0, starMultiples.length)];
        int planetCount = planetMultiples[CommonRNG.getRng().between(0, planetMultiples.length)];


        int i;
        int j;
        double x, y;

        float ang = CommonRNG.getRng().nextFloat() * 360;
        for (i = 0; i < starCount; ++i) {
            int starTypeChooser = CommonRNG.getRng().between(0, StarTypes.values()[StarTypes.values().length-2].getStar().getFrequency());
            Sun starProto = null;
            for(j = 0; j < StarTypes.values().length; ++j)
                if (starTypeChooser < StarTypes.values()[j].getStar().getFrequency()) { starProto = StarTypes.values()[j].getStar(); break; }

            suns.add(new Sun(starProto));
            ang += i * (360.0 / starCount);
            double minDistX = Math.min(starProto.getRadius()+10, HALF_SIZE_X-5);
            double maxDistX = Math.min(starProto.getRadius()+30, HALF_SIZE_X);
            double minDistY = Math.min(starProto.getRadius()+10, HALF_SIZE_Y-5);
            double maxDistY = Math.min(starProto.getRadius()+30, HALF_SIZE_Y);

            x = HALF_SIZE_X + MathUtils.cosDeg(ang) * CommonRNG.getRng().between(minDistX, maxDistX);
            y = HALF_SIZE_Y - MathUtils.sinDeg(ang) * CommonRNG.getRng().between(minDistY, maxDistY);

            x = x > 0 ? Math.floor(x) : Math.ceil(x);
            y = y > 0 ? Math.floor(y) : Math.ceil(y);

            suns.get(i).setPosition(Coord.get((int)x+SolarSystem.SOLAR_SYSTEM_WIDTH/4,(int)y+SolarSystem.SOLAR_SYSTEM_WIDTH/12));
        }

        int minDimension = Math.min(SolarSystem.SOLAR_SYSTEM_WIDTH, SolarSystem.SOLAR_SYSTEM_HEIGHT);
        int radius = minDimension/2;
        for (i = 0; i < planetCount; ++i) {

            PlanetTypes planetProto = PlanetTypes.values()[(int) Math.floor(CommonRNG.getRng().nextDouble()*PlanetTypes.values().length)];
            planets.add(
                    new Planet(
                            planetProto.getPlanet().getSymbol()
                            , planetProto.getPlanet().getColor()
                            , planetProto.getPlanet().getDescription()
                            , planetProto
                            , solarSystem
                            , starSystem
                            , galaxy
                            , universe
                            , Coord.get(0,0)
                    )
            );
            if(planetProto == PlanetTypes.GAS)
                planets.get(i).setGasType(CommonRNG.getRng().between(0,5));

            double dHalfwidthEllipse = 30;       // a
            double dHalfheightEllipse = 15;      // b
            Coord origin = Coord.get(SolarSystem.SOLAR_SYSTEM_WIDTH/2, SolarSystem.SOLAR_SYSTEM_HEIGHT/2); // Origin

            double t = CommonRNG.getRng().between(-180, 180);

            Coord point = Coord.get(
                    (int) (origin.x + dHalfwidthEllipse * Math.cos(t * Math.PI/180.0))
                    ,(int)(origin.y + dHalfheightEllipse * Math.sin(t * Math.PI/180.0))
            );

            while(planetAlreadyOccupies(planets.get(i), point) != false) {
                t = CommonRNG.getRng().between(-180, 180);

                point = Coord.get(
                        (int) (origin.x + dHalfwidthEllipse * Math.cos(t * Math.PI/180.0))
                        ,(int)(origin.y + dHalfheightEllipse * Math.sin(t * Math.PI/180.0))
                );
            }

            /*double a = CommonRNG.getRng().nextDouble() * Math.PI * 2;

            x = MathUtils.cos((float) a) * radius;
            y = MathUtils.sin((float) a) * radius;*/

            /*Coord planetPosition = Coord.get((int)x + SolarSystem.SOLAR_SYSTEM_WIDTH/2, (int)y + SolarSystem.SOLAR_SYSTEM_HEIGHT/2);*/

            /**/


            planets.get(i).setPosition(point);
            /*planets.get(i).generateSolarSystemSprite();*/
        }

        for(i = 0; i < SolarSystem.SOLAR_SYSTEM_WIDTH; i++) {
            for(j = 0; j < SolarSystem.SOLAR_SYSTEM_HEIGHT; j++) {
                String description = "Empty space";
                int star = GeneratorUtils.convertNoise(PerlinNoise.noise(
                        i*10
                        , j*10
                        , CrossHash.hash("solarsystem_bgstars")
                                + solarSystem.getPosition().x
                                + solarSystem.getPosition().y
                                + CrossHash.hash(hash)
                ));
                char symbol = ' ';

                if (star % 10 == 0) {
                    symbol = '·';
                    star = Math.min(star+50, 255);
                }






                // Suns
                float sunR = 0, sunG = 0, sunB = 0, mask = 0;
                for (int s = 0; s < starCount; ++s) {
                    Sun sun = suns.get(s);
                    double distSquared = (i-sun.getPosition().x)*(i-sun.getPosition().x) + (j-sun.getPosition().y)*(j-sun.getPosition().y);
                    if (distSquared < sun.getRadius() * sun.getRadius()) {
                        double dist2 = Math.sqrt(distSquared) / sun.getRadius() * 256.0;
                        mask = (float) (256-dist2); //expFilter(dst2, 0, .99)
                        //temp = (Perlin(x,y,worldW,worldH,2,2) - 128.0) / 8.0;
                        //mask = Max( Min(mask+temp, 255), 0 );
                        float mask2 = mask / 256.0f;
                        if (mask2 > 1.0f) mask2 = 1.0f;
                        sunR = GeneratorUtils.clampColor((int) (sun.getColor().r*255.0f + CommonRNG.getRng().between(-20, 20)));
                        sunB = GeneratorUtils.clampColor((int) (sun.getColor().b*255.0f + CommonRNG.getRng().between(-20, 20)));
                        sunG = GeneratorUtils.clampColor((int) (sun.getColor().g*255.0f + CommonRNG.getRng().between(-20, 20)));
                        symbol = ' ';
                        description = sun.getDescription();
                        break;
                    }
                }

                // Nebula
                double neb = PerlinNoise.noise(
                        i*.25
                        ,j*.25
                        , CrossHash.hash("solarsystem_neb")
                                + starSystem.getPosition().x
                                + starSystem.getPosition().y
                                + CrossHash.hash(hash)
                );

                SColor color = sColorFactory.blend(SColor.BLACK, new SColor(nebColor), neb);

                float r = color.r*255.0f;
                float g = color.g*255.0f;
                float b = color.b*255.0f;

                /*float r = GeneratorUtils.blendMul((int)nebColor.r*255, (int)neb);
                float g = GeneratorUtils.blendMul((int)nebColor.g*255, (int)neb);
                float b = GeneratorUtils.blendMul((int)nebColor.b*255, (int)neb);*/

                mask = Math.min(mask*2, 255.0f);

                r = (float) Math.floor(GeneratorUtils.blend(sunR, r, mask / 255.0));
                g = (float) Math.floor(GeneratorUtils.blend(sunG, g, mask / 255.0));
                b = (float) Math.floor(GeneratorUtils.blend(sunB, b, mask / 255.0));

                buffer[i][j] = new CelestialBody(symbol, new Color(star/255.0f,star/255.0f,star/255.0f,1), new Color(r/255.0f,g/255.0f,b/255.0f,1), description, universe);

                // Planets
                for (int p = 0; p < planetCount; ++p) {
                    Planet planet = planets.get(p);
                    if (i == planet.getPosition().x && j == planet.getPosition().y) {
                        planet.setBgColor(buffer[i][j].getBgColor());
                        buffer[i][j] = planet;
                        break;
                    }
                }
            }
        }
    }

    private Sun findSunContainingPoint(int i, int j) {
        for(Sun sun : suns) {
            Rectangle sunBounds = new Rectangle(sun.getPosition().x-sun.getRadius(), sun.getPosition().y-sun.getRadius(), sun.getRadius()*2, sun.getRadius()*2);
            if(sunBounds.contains(i,j)) return sun;
        }
        return null;
    }

    private boolean planetAlreadyOccupies(Planet planet, Coord planetPosition) {
        for(Planet p : planets) {
            if (p.equals(planet)) continue;
            if(p.getPosition() != null) {
                if(p.getPosition().x == planetPosition.x && p.getPosition().y == planetPosition.y) return true;
            }
        }
        return false;
    }
}

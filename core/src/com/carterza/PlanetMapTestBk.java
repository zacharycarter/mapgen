package com.carterza;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.carterza.planet.map.generator.MapGenerator;

public class PlanetMapTestBk implements ApplicationListener {

    Texture tex, nm;
    SpriteBatch batch;
    OrthographicCamera cam;
    ShaderProgram shader;
    MapGenerator mapGenerator;

    float globalTime = 0;

    //our constants...
    public static final float DEFAULT_LIGHT_Z = .075f;
    public static final float AMBIENT_INTENSITY = .75f;
    public static final float LIGHT_INTENSITY = 1f;

    public static final Vector3 LIGHT_POS = new Vector3(0f,0f,DEFAULT_LIGHT_Z);

    //Light RGB and intensity (alpha)
    public static final Vector3 LIGHT_COLOR = new Vector3(1f, 0.8f, 0.6f);

    //Ambient RGB and intensity (alpha)
    public static final Vector3 AMBIENT_COLOR = new Vector3(0.6f, 0.6f, 1f);

    //Attenuation coefficients for light falloff
    public static final Vector3 FALLOFF = new Vector3(.4f, 3f, 20f);

    final String VERT =
            "attribute vec4 "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
                    "attribute vec4 "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
                    "attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +

                    "uniform mat4 u_projTrans;\n" +
                    " \n" +
                    "varying vec4 vColor;\n" +
                    "varying vec2 vTexCoord;\n" +

                    "void main() {\n" +
                    "	vColor = "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
                    "	vTexCoord = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
                    "	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                    "}";

    //no changes except for LOWP for color values
    //we would store this in a file for increased readability
    final String FRAG =
            //GL ES specific stuff
            "#ifdef GL_ES\n" +
                    " #define LOWP lowp\n" +
                    " precision mediump float;\n" +
                    " #else\n" +
                    " #define LOWP\n" +
                    " #endif\n" +
                    " \n" +
                    "#define M_PI 3.1415926535897932384626433832795\n" +
                    " \n" +
                    "//attributes from vertex shader \n" +
                    "varying LOWP vec4 vColor; \n" +
                    "varying vec2 vTexCoord; \n" +
                    " \n" +
                    "//our texture samplers \n" +
                    "uniform sampler2D u_texture;   //diffuse map \n" +
                    "uniform sampler2D u_normals;   //normal map \n" +
                    "uniform sampler2D u_height;   //height map \n" +
                    " \n" +
                    "//values used for shading algorithm... \n" +
                    "uniform vec2 Resolution;         //resolution of screen \n" +
                    "uniform float LightAmount;           //light amount \n" +
                    "uniform LOWP vec4 LightColor;    //light RGBA -- alpha is intensity \n" +
                    "uniform LOWP vec4 AmbientColor;  //ambient RGBA -- alpha is intensity  \n" +
                    " \n" +
                    "const float LightAngleZ = 1.15;\n" +
                    "const float TextureStep = 0.005; // distance to move towards the light with each tested step\n" +
                    "\n" +
                    "vec2 PointOnLine(vec2 start, float angle, float length) {\n" +
                    "    float x = length * cos(angle);\n" +
                    "    float y = length * sin(angle);\n" +
                    "\n" +
                    "    return vec2(start.x + x, start.y + y);\n" +
                    "}\n" +
                    "\n" +
                    "float PixelHeightAtPoint(vec2 texCoord, float LightAngleXY, float distance,\n" +
                    "    sampler2D heightMap) {\n" +
                    "\n" +
                    "    vec2 newTexCoord = PointOnLine(texCoord, LightAngleXY, distance);\n" +
                    "    return texture2D(heightMap, newTexCoord).r;\n" +
                    "}\n" +
                    "\n" +
                    "float GetRayHeightAtPoint(float height, float LightAngleZ, float distance) {\n" +
                    "    return distance * tan(LightAngleZ) + height;\n" +
                    "}\n" +
                    "\n" +
                    "float TraceLight(float LightAngleXY, float LightAngleZ,\n" +
                    "    vec2 texCoord, float step) {\n" +
                    "\n" +
                    "    float distance; // current distance along the line from current heightmap pixel towards the light\n" +
                    "    float currentHeight; // value of currently tested heightmap pixel\n" +
                    "    float newHeight; // values of heightmap pixels lying somewhere on the line towards the light from current position\n" +
                    "    float rayHeight; // height of a ray drawn from currentHeight along the light Z angle, sampled at a certain position\n" +
                    "\t\n" +
                    "    currentHeight = texture2D(u_height, texCoord).a;\n" +
                    "\t\n" +
                    "    for (int i = 0; i < 100; ++i) {\n" +
                    "        distance = step * float(i);\n" +
                    "        newHeight = PixelHeightAtPoint(texCoord, LightAngleXY, distance, u_height);\n" +
                    "\n" +
                    "        if (newHeight > currentHeight) { // there's a higher point on the line from current pixel to light\n" +
                    "            rayHeight = GetRayHeightAtPoint(currentHeight, LightAngleZ, distance);\n" +
                    "            if (rayHeight <= newHeight) { // the higher point also blocks the direct visibility from light to current pixel,  current pixel is in shadow\n" +
                    "                return 0.0 + (distance * 5.0);\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    return 1.0; // pixel is not occluded\n" +
                    "}\n" +
                    "\n" +
                    "void main() {\n" +
                    "\tvec2 texCoord = gl_FragCoord.xy / Resolution.xy;\n" +
                    "\n" +
                    "\t\n" +
                    "\t//RGBA of our diffuse color \n" +
                    "\tvec4 DiffuseColor = texture2D(u_texture, vTexCoord); \n" +
                    "\t \n" +
                    "\t//RGB of our normal map \n" +
                    "\tvec3 NormalMap = texture2D(u_normals, vTexCoord).rgb; \n" +
                    "\t \n" +
                    "\t//The delta position of light \n" +
                    "\t// vec3 LightDir = vec3(LightPos.xy - (gl_FragCoord.xy / Resolution.xy), LightPos.z); \n" +
                    "\tvec3 LightDir = vec3(1,0,LightAmount);\n" +
                    "\n" +
                    "\t// float LightAngleXY = 22.0;\n" +
                    "\tfloat LightAngleXY = atan(LightDir.x, LightDir.y)*180.0/M_PI;\n" +
                    "\t// float lightLevel = TraceLight(LightAngleXY, LightDir.z, texCoord, TextureStep);\n" +
                    "\t\n" +
                    "\t \n" +
                    "\t//normalize our vectors \n" +
                    "\tvec3 N = normalize(NormalMap * 2.0 - 1.0); \n" +
                    "\tvec3 L = normalize(LightDir);\n" +
                    "\n" +
                    "\tfloat NdotL = max( dot(N, L), 0.0);\n" +
                    "\t \n" +
                    "\t//Pre-multiply light color with intensity \n" +
                    "\t//Then perform  dot L\\ to determine our diffuse term \n" +
                    "\tvec3 Diffuse = (LightColor.rgb * LightColor.a) * max(dot(N, L), 0.0); \n" +
                    " \n" +
                    "\t//pre-multiply ambient color with intensity \n" +
                    "\tvec3 Ambient = AmbientColor.rgb * AmbientColor.a; \n" +
                    "\t \n" +
                    "\t//the calculation which brings it all together \n" +
                    "\tvec3 Intensity = Ambient + Diffuse; \n" +
                    "\tvec3 FinalColor = DiffuseColor.rgb * Intensity; \n" +
                    "\n" +
                    "\tgl_FragColor = vColor * vec4(FinalColor, DiffuseColor.a); \n" +
                    "\t// gl_FragColor *= lightLevel;\n" +
                    "}";
    private float LIGHT_AMOUNT = 0;


    @Override
    public void create() {
        mapGenerator = new MapGenerator(512,512);
        tex = mapGenerator.generate();
        nm = new Texture(Gdx.files.internal("normalmap.png"));

        ShaderProgram.pedantic = false;

        shader = new ShaderProgram(VERT, FRAG);
        //ensure it compiled
        if (!shader.isCompiled())
            throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());
        //print any warnings
        if (shader.getLog().length()!=0)
            System.out.println(shader.getLog());

        //setup default uniforms
        shader.begin();

        //our normal map
        shader.setUniformi("u_normals", 1); //GL_TEXTURE1

        //light/ambient colors
        //LibGDX doesn't have Vector4 class at the moment, so we pass them individually...
        shader.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
        shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
        shader.setUniformf("Falloff", FALLOFF);

        //LibGDX likes us to end the shader program
        shader.end();

        batch = new SpriteBatch(1000, shader);
        batch.setShader(shader);

        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.setToOrtho(false);

        //handle mouse wheel
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean scrolled(int delta) {
                //LibGDX mouse wheel is inverted compared to lwjgl-basics
                LIGHT_AMOUNT += delta * .01;
                return true;
            }
        });

    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false, width, height);
        batch.setProjectionMatrix(cam.combined);

        shader.begin();
        shader.setUniformf("Resolution", width, height);
        shader.end();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();

        globalTime = (globalTime + Gdx.graphics.getDeltaTime()) % MathUtils.PI2;

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        //shader will now be in use...

        //update light position, normalized to screen resolution

        float x = Gdx.input.getX() / (float)Gdx.graphics.getWidth();
        float y = Gdx.input.getY() / (float)Gdx.graphics.getHeight();

        LIGHT_POS.x += .001f;
        LIGHT_POS.y += .001f;

        if(LIGHT_POS.x >= 1) LIGHT_POS.x = 0;
        if(LIGHT_POS.y >= 1) LIGHT_POS.y = 0;

        //send a Vector4f to GLSL
        shader.setUniformf("LightAmount", LIGHT_AMOUNT);

        //bind normal map to texture unit 1
        nm.bind(1);

        //bind diffuse color to texture unit 0
        //important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
        tex.bind(0);

        //draw the texture unit 0 with our shader effect applied
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
        shader.dispose();
        tex.dispose();
        mapGenerator.dispose();
    }

}

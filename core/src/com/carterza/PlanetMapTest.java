package com.carterza;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.carterza.planet.map.generator.MapGenerator;
import com.carterza.shader.ShadowShader;

import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.Keys.*;

public class PlanetMapTest implements ApplicationListener {

    Texture tex, nm, hm;
    SpriteBatch batch;
    OrthographicCamera cam;
    ShaderProgram shader;
    Shader shadowShader;
    MapGenerator mapGenerator;

    ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;

    private static final float Z_OFFSET = -0.01f;
    Color planeColor = new Color(1,1,1,1f);


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
            "attribute vec3 a_position;\n" +
                    "attribute vec3 a_normal;\n" +
                    "attribute vec4 a_color;\n" +
                    "attribute vec2 a_texCoord0;\n" +
                    "attribute vec3 a_tangent;\n" +
                    "\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "uniform mat4 u_normalMatrix;\n" +
                    "\n" +
                    "varying vec4 vColor;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "\n" +
                    "// varying vec3 vToLightInTangentSpace;\n" +
                    "// varying vec3 vToCameraInTangentSpace;\n" +
                    "\n" +
                    "void main() {\n" +
                    "\tvec3 worldDirectionToLight = vec3(0,0,1);\n" +
                    "\tvec3 worldDirectionToCamera = vec3(0,0,-1);\n" +
                    "\t\n" +
                    "\t// vec3 worldBitangent = cross(a_normal, a_tangent);\n" +
                    "\t\n" +
                    "\t//vToLightInTangentSpace = vec3(\n" +
                    "\t//\tdot(worldDirectionToLight, a_tangent)\n" +
                    "\t//\t, dot(worldDirectionToLight, worldBitangent)\n" +
                    "\t//\t, dot(worldDirectionToLight, a_normal)\n" +
                    "\t//);\n" +
                    "\t\n" +
                    "\t//vToCameraInTangentSpace = vec3(\n" +
                    "\t//\tdot(worldDirectionToCamera, a_tangent)\n" +
                    "\t//\t, dot(worldDirectionToCamera, worldBitangent)\n" +
                    "\t//\t, dot(worldDirectionToCamera, a_normal)\n" +
                    "\t//);\n" +
                    "\t\n" +
                    "\tvColor = a_color;\n" +
                    "\tvTexCoord = a_texCoord0;\n" +
                    "\tgl_Position = u_projTrans * vec4(a_position, 1);\n" +
                    "}";

    //no changes except for LOWP for color values
    //we would store this in a file for increased readability
    final String FRAG = "#ifdef GL_ES\n" +
            "            \n" +
            "             #define LOWP lowp \n" +
            "             precision mediump float; \n" +
            "             #else \n" +
            "             #define LOWP \n" +
            "             #endif \n" +
            "              \n" +
            "            #define M_PI 3.1415926535897932384626433832795 \n" +
            "              \n" +
            "            //attributes from vertex shader  \n" +
            "            varying LOWP vec4 vColor;  \n" +
            "            varying vec2 vTexCoord;  \n" +
            "              \n" +
            "            //our texture samplers  \n" +
            "            uniform sampler2D u_texture;   //diffuse map  \n" +
            "            uniform sampler2D u_normals;   //normal map  \n" +
            "            uniform sampler2D u_height;   //height map  \n" +
            "              \n" +
            "            //values used for shading algorithm...  \n" +
            "            uniform vec2 Resolution;         //resolution of screen  \n" +
            "            uniform float LightAmount;           //light amount  \n" +
            "            uniform LOWP vec4 LightColor;    //light RGBA -- alpha is intensity  \n" +
            "            uniform LOWP vec4 AmbientColor;  //ambient RGBA -- alpha is intensity\n" +
            "            \n" +
            "            uniform float globalTime;\n" +
            "            uniform vec3 LightPosition; // position of light\n" +
            "              \n" +
            "            const float LightAngleZ = 1.15; \n" +
            "            const float TextureStep = 0.005; // distance to move towards the light with each tested step \n" +
            "            \n" +
            "            vec2 PointOnLine(vec2 start, float angle, float length) { \n" +
            "                float x = length * cos(angle); \n" +
            "                float y = length * sin(angle); \n" +
            "            \n" +
            "                return vec2(start.x + x, start.y + y); \n" +
            "            } \n" +
            "            \n" +
            "            float PixelHeightAtPoint(vec2 texCoord, float LightAngleXY, float distance, \n" +
            "                sampler2D heightMap) { \n" +
            "            \n" +
            "                vec2 newTexCoord = PointOnLine(texCoord, LightAngleXY, distance); \n" +
            "                return texture2D(heightMap, newTexCoord).r; \n" +
            "            } \n" +
            "            \n" +
            "            float GetRayHeightAtPoint(float height, float LightAngleZ, float distance) { \n" +
            "                return distance * tan(LightAngleZ) + height; \n" +
            "            } \n" +
            "            \n" +
            "            float TraceLight(float LightAngleXY, float LightAngleZ, \n" +
            "                vec2 texCoord, float step) { \n" +
            "            \n" +
            "                float distance; // current distance along the line from current heightmap pixel towards the light \n" +
            "                float currentHeight; // value of currently tested heightmap pixel \n" +
            "                float newHeight; // values of heightmap pixels lying somewhere on the line towards the light from current position \n" +
            "                float rayHeight; // height of a ray drawn from currentHeight along the light Z angle, sampled at a certain position \n" +
            "            \n" +
            "                currentHeight = texture2D(u_height, texCoord).r; \n" +
            "            \n" +
            "                for (int i = 0; i < 100; ++i) { \n" +
            "                    distance = step * float(i); \n" +
            "                    newHeight = PixelHeightAtPoint(texCoord, LightAngleXY, distance, u_height); \n" +
            "            \n" +
            "                    if (newHeight > currentHeight) { // there's a higher point on the line from current pixel to light \n" +
            "                        rayHeight = GetRayHeightAtPoint(currentHeight, LightAngleZ, distance); \n" +
            "                        if (rayHeight <= newHeight) { // the higher point also blocks the direct visibility from light to current pixel,  current pixel is in shadow \n" +
            "                            return 0.0 + (distance * 5.0); \n" +
            "                        } \n" +
            "                    } \n" +
            "                } \n" +
            "            \n" +
            "                return 1.0; // pixel is not occluded \n" +
            "            } \n" +
            "            \n" +
            "            void main() {\n" +
            "            vec2 texCoord = gl_FragCoord.xy / Resolution.xy;\n" +
            "             \n" +
            "            //RGBA of our diffuse color\n" +
            "            vec4 DiffuseColor = texture2D(u_texture, vTexCoord);\n" +
            "             \n" +
            "            //RGB of our normal map\n" +
            "            vec3 NormalMap = texture2D(u_normals, vTexCoord).rgb;\n" +
            "            \n" +
            "            vec3 surfaceToLight = normalize(LightPosition);\n" +
            "             \n" +
            "            //float LightAngleXY = atan(LightDir.x, LightDir.y)*180.0/M_PI;\n" +
            "            //float lightLevel = TraceLight(LightAngleXY, LightDir.z, texCoord, TextureStep);\n" +
            "             \n" +
            "            //normalize our vectors\n" +
            "            vec2 newLightPos = LightPosition.xy + sin(globalTime);\n" +
            "            vec3 N = normalize(NormalMap * 2.0 - 1.0);\n" +
            "            vec3 L = normalize(vec3(-newLightPos.xy, LightPosition.z));\n" +
            "            \n" +
            "            float lightAngleXY = atan(L.x, L.y) * 180.0/M_PI;\n" +
            "            float lightLevel = TraceLight(lightAngleXY, L.z, texCoord, TextureStep);\n" +
            "             \n" +
            "            float NdotL = max( dot(N, L), 0.0);\n" +
            "            \n" +
            "            //Pre-multiply light color with intensity\n" +
            "            //Then perform  dot L\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ to determine our diffuse term\n" +
            "            vec3 Diffuse = (LightColor.rgb * LightColor.a) * max(dot(N, L), 0.0);\n" +
            "             \n" +
            "            //pre-multiply ambient color with intensity \n" +
            "            vec3 Ambient = AmbientColor.rgb * AmbientColor.a;\n" +
            "             \n" +
            "            //the calculation which brings it all together\n" +
            "            vec3 Intensity = Ambient + Diffuse;\n" +
            "            vec3 FinalColor = DiffuseColor.rgb * Intensity;\n" +
            "             \n" +
            "            gl_FragColor = vColor * vec4(FinalColor, DiffuseColor.a);\n" +
            "            // gl_FragColor *= lightLevel;\n" +
            "            }";

    private float LIGHT_AMOUNT = 0;


    @Override
    public void create() {
        mapGenerator = new MapGenerator(120,40);

        tex = mapGenerator.generate();
        nm = new Texture(Gdx.files.internal("normalmap.png"));
        hm = new Texture(Gdx.files.internal("noisemap.png"));

        ShaderProgram.pedantic = false;

        shader = new ShaderProgram(VERT, FRAG);
        /*shader = new ShaderProgram(Gdx.files.internal("shader/default/default.vertex.glsl"), Gdx.files.internal("shader/default/default.fragment.glsl"));*/
        //ensure it compiled
        if (!shader.isCompiled())
            throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());
        //print any warnings
        if (shader.getLog().length()!=0)
            System.out.println(shader.getLog());



        //setup default uniforms
        shader.begin();

        //our texture
        shader.setUniformi("u_texture", 0); //GL_TEXTURE1
        //our normal map
        shader.setUniformi("u_normals", 1); //GL_TEXTURE1
        // our height map
        shader.setUniformi("u_height", 2); //GL_TEXTURE1

        //light/ambient colors
        //LibGDX doesn't have Vector4 class at the moment, so we pass them individually...
        shader.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
        shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);

        //LibGDX likes us to end the shader program
        shader.end();

        batch = new SpriteBatch(1000, shader);


        shadowShader = new ShadowShader(hm);
        shadowShader.init();

        modelBatch = new ModelBatch();
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        model = modelBuilder.createRect(0, 0, Z_OFFSET,
                512, 0, Z_OFFSET,
                512, 512, Z_OFFSET,
                0, 512, Z_OFFSET,
                0, 0, 1,
                GL20.GL_TRIANGLES,
                new Material(
                        new ColorAttribute(
                                ColorAttribute.createDiffuse(planeColor)),
                        new BlendingAttribute(
                                GL20.GL_SRC_ALPHA,
                                GL20.GL_ONE_MINUS_SRC_ALPHA)),
                VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.TextureCoordinates);
        instance = new ModelInstance(model);
        instance.transform.setToTranslation(0,0,-5);

        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.setToOrtho(false);
        /*cam.translate(0,0,10)*/;

        //handle mouse wheel
        input.setInputProcessor(new InputAdapter() {
            public boolean scrolled(int delta) {
                //LibGDX mouse wheel is inverted compared to lwjgl-basics
                LIGHT_AMOUNT += delta * .01;
                return true;
            }

            public boolean keyDown (int keycode) {
                switch(keycode) {
                    case Z:
                        cam.zoom += .2;
                        break;
                    case C:
                        cam.zoom -= .2;
                        break;
                    case W:
                        cam.translate(0, 2);
                        break;
                    case S:
                        cam.translate(0, -2);
                        break;
                    case A:
                        cam.translate(-2, 0);
                        break;
                    case D:
                        cam.translate(2, 0);
                        break;
                }
                return false;
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        cam.update();

        globalTime = (globalTime + Gdx.graphics.getDeltaTime()) % MathUtils.PI2;

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        //shader will now be in use...

        //update light position, normalized to screen resolution

        float x = input.getX() / (float)Gdx.graphics.getWidth();
        float y = input.getY() / (float)Gdx.graphics.getHeight();

        LIGHT_POS.x += .001f;
        LIGHT_POS.y += .001f;

        if(LIGHT_POS.x >= 1) LIGHT_POS.x = 0;
        if(LIGHT_POS.y >= 1) LIGHT_POS.y = 0;

        //send a Vector4f to GLSL
        shader.setUniformf("LightAmount", LIGHT_AMOUNT);
        // light direction
        shader.setUniformf("LightPosition", new Vector3(1,0,LIGHT_AMOUNT)); //GL_TEXTURE1
        shader.setUniformf("globalTime", globalTime);


        //bind normal map to texture unit 1
        nm.bind(1);
        // bind height map to texture unit 2
        hm.bind(2);

        //bind diffuse color to texture unit 0
        //important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
        tex.bind(0);

        //draw the texture unit 0 with our shader effect applied
        batch.draw(tex, 0, 0);

        batch.end();

        /*modelBatch.begin(cam);
        modelBatch.render(instance, shadowShader);
        modelBatch.end();*/
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
        modelBatch.dispose();
        tex.dispose();
        mapGenerator.dispose();
    }

}

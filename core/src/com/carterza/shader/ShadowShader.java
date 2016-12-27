package com.carterza.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by zachcarter on 12/22/16.
 */
public class ShadowShader implements Shader {
    ShaderProgram program;
    Camera camera;
    RenderContext context;
    Texture heightmap;

    public ShadowShader(Texture heightmap) {
        this.heightmap = heightmap;
    }

    @Override
    public void init() {
        String vert = Gdx.files.internal("shader/default/default.vertex.glsl").readString();
        String frag = Gdx.files.internal("shader/default/default.fragment.glsl").readString();

        program = new ShaderProgram(vert, frag);
        //ensure it compiled
        if (!program.isCompiled())
            throw new GdxRuntimeException("Could not compile shader: "+ program.getLog());
        //print any warnings
        if (program.getLog().length()!=0)
            System.out.println(program.getLog());
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return false;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        program.begin();
        this.context = context;
        program.begin();
        program.setUniformMatrix("u_projViewTrans", camera.combined);
        // program.setUniformf("iResolution", new Vector3(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0));
        // program.setUniformi("u_texture", 0); //GL_TEXTURE1
    }

    @Override
    public void render(Renderable renderable) {
        // heightmap.bind(0);
        program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
        renderable.meshPart.render(program);
    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
        heightmap.dispose();
    }
}

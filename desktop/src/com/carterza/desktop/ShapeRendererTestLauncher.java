package com.carterza.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.carterza.PlanetTest;
import com.carterza.ShapeRendererTest;

public class ShapeRendererTestLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "SquidLib GDX Basic Demo";
        config.width = 1024;
        config.height = 768;
        new LwjglApplication(new ShapeRendererTest(), config);
    }
}

package com.carterza.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.carterza.PlanetMapTest;
import com.carterza.PlanetMapTestBk;

public class PlanetMapTestLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "SquidLib GDX Basic Demo";
        config.width = 512;
        config.height = 512;
        new LwjglApplication(new PlanetMapTest(), config);
    }
}

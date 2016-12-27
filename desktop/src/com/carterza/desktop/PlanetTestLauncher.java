package com.carterza.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.carterza.PlanetTest;

public class PlanetTestLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "SquidLib GDX Basic Demo";
        config.width = 80 * 14;
        config.height = 32 * 21;
        new LwjglApplication(new PlanetTest(), config);
    }
}

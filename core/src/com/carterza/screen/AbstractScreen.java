package com.carterza.screen;

import com.badlogic.gdx.Screen;
import com.carterza.Derelict;

/**
 * Created by zachcarter on 12/9/16.
 */
public abstract class AbstractScreen implements Screen {
    Derelict derelict;

    public AbstractScreen(Derelict derelict) {
        this.derelict = derelict;
    }
}

package com.wavetrick.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

/**
 * Created by furukawa on 15/05/13.
 */
public abstract class MyScreenAdapter implements Screen {
    private static final String LOG_TAG = MyScreenAdapter.class.getSimpleName();
    public WavetrickGame game;

    public MyScreenAdapter (WavetrickGame game) {
        this.game = game;
    }

    @Override
    public void resize (int width, int height) {
        Gdx.app.log(LOG_TAG, "resize(" + width + "," + height + ")");
    }

    @Override
    public void show () {
        Gdx.app.log(LOG_TAG, "show");
    }

    @Override
    public void hide () {
        Gdx.app.log(LOG_TAG, "hide");
    }

    @Override
    public void pause () {
        Gdx.app.log(LOG_TAG, "pause");
    }

    @Override
    public void resume () {
        Gdx.app.log(LOG_TAG, "resume");
    }

    @Override
    public void dispose () {
        Gdx.app.log(LOG_TAG, "dispose");
    }

}

package com.wavetrick.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import java.util.Set;
import java.util.HashSet;

public class WavetrickGame extends Game {
	public static final String LOG_TAG = WavetrickGame.class.getSimpleName();
	// 16:9
	public static int LOGICAL_WIDTH = 1280;
	public static int LOGICAL_HEIGHT = 680;
	// 4:3
	// public static int LOGICAL_WIDTH = 256;
	// public static int LOGICAL_HEIGHT = 192;
	private Screen nextScreen;

	@Override
	public void create() {
		Gdx.app.log(LOG_TAG, "create");
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
		if (nextScreen != null) {
			super.setScreen(nextScreen);
			nextScreen = null;
		}
	}

	@Override
	public void setScreen (Screen screen) {
		Gdx.app.log(LOG_TAG, "setScreen");
		nextScreen = screen;
	}

	@Override
	public void dispose() {
		super.dispose();
		Gdx.app.log(LOG_TAG, "dispose");
	}

}

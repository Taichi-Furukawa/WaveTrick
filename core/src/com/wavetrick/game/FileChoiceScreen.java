package com.wavetrick.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

import java.io.File;
import java.io.FileNotFoundException;


public class FileChoiceScreen extends MyScreenAdapter {
    private static final String LOG_TAG = MainMenuScreen.class.getSimpleName();

    public static int LOGICAL_WIDTH = WavetrickGame.LOGICAL_WIDTH;
    public static int LOGICAL_HEIGHT = WavetrickGame.LOGICAL_HEIGHT;
    OrthographicCamera uiCamera;
    Viewport viewport;
    SpriteBatch batch;
    Vector3 touchPoint;

    Texture title_img;
    Texture bgm1_img;
    Texture bgm2_img;
    Texture bgm3_img;
    Texture bgm4_img;
    Sprite title;
    Sprite bgm1;
    Sprite bgm2;
    Sprite bgm3;
    Sprite bgm4;
    Music bgm;


    public FileChoiceScreen(WavetrickGame game,Music bgm) {//コンストラクタ
        super(game);
        Gdx.app.log(LOG_TAG, "constractor");

        this.bgm = bgm;

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, WavetrickGame.LOGICAL_WIDTH, WavetrickGame.LOGICAL_HEIGHT);
        viewport = new FitViewport(WavetrickGame.LOGICAL_WIDTH, WavetrickGame.LOGICAL_HEIGHT, uiCamera);
        batch = new SpriteBatch();
        touchPoint = new Vector3();

        bgm1_img = new Texture(Gdx.files.internal("menu_assets/zerogravity.png"));
        bgm2_img = new Texture(Gdx.files.internal("menu_assets/mindthegap.png"));
        bgm3_img = new Texture(Gdx.files.internal("menu_assets/caramelmacchiato.png"));
        bgm4_img = new Texture(Gdx.files.internal("menu_assets/sweatshop.png"));

        bgm1 = new Sprite(bgm1_img);
        bgm2 = new Sprite(bgm2_img);
        bgm3 = new Sprite(bgm3_img);
        bgm4 = new Sprite(bgm4_img);
        bgm1.setPosition((WavetrickGame.LOGICAL_WIDTH/2)-(bgm1.getWidth()/2),700);
        bgm2.setPosition((WavetrickGame.LOGICAL_WIDTH/2)-(bgm1.getWidth()/2),500);
        bgm3.setPosition((WavetrickGame.LOGICAL_WIDTH/2)-(bgm1.getWidth()/2),300);
        bgm4.setPosition((WavetrickGame.LOGICAL_WIDTH/2)-(bgm1.getWidth()/2),100);
        Gdx.app.log(LOG_TAG, "constractor exit");

    }

    public void update(float delta) {//毎フレームごと，render->update 処理系
        if (Gdx.input.justTouched()) {
            viewport.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Rectangle bgm1Bounds = bgm1.getBoundingRectangle();
            Rectangle bgm2Bounds = bgm2.getBoundingRectangle();
            Rectangle bgm3Bounds = bgm3.getBoundingRectangle();
            Rectangle bgm4Bounds = bgm4.getBoundingRectangle();
            if (bgm1Bounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.log(LOG_TAG, "voice mode start!");
                game.setScreen(new MusicmodeGameScreen(game,"","zerogravity"));
                return;
            }

            if (bgm2Bounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.log(LOG_TAG, "voice mode start!");
                game.setScreen(new MusicmodeGameScreen(game,"","mindthegap"));
                return;
            }

            if (bgm3Bounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.log(LOG_TAG, "voice mode start!");
                game.setScreen(new MusicmodeGameScreen(game,"","caramelmacchiato"));
                return;
            }

            if (bgm4Bounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.log(LOG_TAG, "voice mode start!");
                game.setScreen(new MusicmodeGameScreen(game,"","sweatshop"));
                return;
            }
        }
    }


    public void draw () {//毎フレームごと，render->draw描画系
        GL20 gl = Gdx.gl;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        bgm1.draw(batch);
        bgm2.draw(batch);
        bgm3.draw(batch);
        bgm4.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {//可変のウィンドウサイズを定義
        Gdx.app.log(LOG_TAG, "resize");
        viewport.update(width, height);
    }

    @Override
    public void render(float delta) {//毎フレームごとに呼ばれる．二つの関数に受け渡してるだけ
        update(delta);
        draw();
    }

    @Override
    public void hide() {//このScreenが閉じた時に呼ばれる
        Gdx.app.log(LOG_TAG, "hide");
        dispose();
    }

    @Override
    public void dispose() {//このScreenが破棄された時に呼ばれる
        Gdx.app.log(LOG_TAG, "dispose");
        //素材は全部破棄しとけ！！
        batch.dispose();
        bgm.dispose();
    }

}

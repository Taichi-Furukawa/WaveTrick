package com.wavetrick.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;



public class MainMenuScreen extends MyScreenAdapter {
    private static final String LOG_TAG = MainMenuScreen.class.getSimpleName();
    OrthographicCamera uiCamera;
    Viewport viewport;
    SpriteBatch batch;
    Vector3 touchPoint;
    Texture title_img;
    Texture start_img;
    Texture back_img;
    Sprite start;
    Sprite title;
    Sprite back;
    float alpha;


    public MainMenuScreen(WavetrickGame game) {//コンストラクタ
        super(game);

        Gdx.app.log(LOG_TAG, "constractor");
        title_img = new Texture(Gdx.files.internal("menu_assets/Funky_Tony_logo.png"));
        start_img = new Texture(Gdx.files.internal("menu_assets/game_start-1.png"));
        back_img = new Texture(Gdx.files.internal("menu_assets/building2.png"));

        title = new Sprite(title_img);
        start = new Sprite(start_img);
        back = new Sprite(back_img);
        title.setScale((float)1.5);
        back.setScale((float)1.5);


        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, WavetrickGame.LOGICAL_WIDTH, WavetrickGame.LOGICAL_HEIGHT);
        viewport = new FitViewport(WavetrickGame.LOGICAL_WIDTH, WavetrickGame.LOGICAL_HEIGHT, uiCamera);
        batch = new SpriteBatch();
        touchPoint = new Vector3();

        title.setPosition((WavetrickGame.LOGICAL_WIDTH/2)-(title_img.getWidth()/2),WavetrickGame.LOGICAL_HEIGHT/2);
        start.setPosition((WavetrickGame.LOGICAL_WIDTH/2)-(start_img.getWidth()/2),30);
        back.setPosition((WavetrickGame.LOGICAL_WIDTH / 2) - (back_img.getWidth() / 2), 80);
        back.setAlpha(0.2f);
        alpha = 0;
        Gdx.app.log(LOG_TAG, "constractor exit");

    }

    public void update(float delta) {//毎フレームごと，render->update 処理系
        alpha += 0.05f;
        start.setAlpha(0.5f - 0.3f * (float) Math.sin(alpha));
        if (Gdx.input.justTouched()) {
            viewport.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Rectangle playBounds = start.getBoundingRectangle();
            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.log(LOG_TAG, "game start!");
                game.setScreen(new GameScreen(game));
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
        back.draw(batch);
        title.draw(batch);
        start.draw(batch);


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
        title_img.dispose();
        start_img.dispose();
        back_img.dispose();
        batch.dispose();

    }

}

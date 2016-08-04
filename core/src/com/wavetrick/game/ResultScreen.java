package com.wavetrick.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class ResultScreen extends MyScreenAdapter {
    private static final String LOG_TAG = ResultScreen.class.getSimpleName();
    OrthographicCamera uiCamera;
    Viewport viewport;
    SpriteBatch batch;

    Vector3 touchPoint;

    Texture title_img;
    Texture return_img;

    Animation title;
    Sprite return_button;

    BitmapFont scoreFont;

    int stateTime = 0;
    int len =0;
    int tp = 0;
    float alpha;



    public ResultScreen(WavetrickGame game,int length,int tony_point) {//コンストラクタ
        super(game);
        Gdx.app.log(LOG_TAG, "constractor");

        len = length;
        tp = tony_point;
        if(tp == 0){
            tp=1;
        }

        title_img = new Texture(Gdx.files.internal("menu_assets/result_animation.png"));
        return_img = new Texture(Gdx.files.internal("menu_assets/return_to_menu.png"));

        return_button = new Sprite(return_img);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, WavetrickGame.LOGICAL_WIDTH, WavetrickGame.LOGICAL_HEIGHT);
        viewport = new FitViewport(WavetrickGame.LOGICAL_WIDTH, WavetrickGame.LOGICAL_HEIGHT, uiCamera);
        batch = new SpriteBatch();
        touchPoint = new Vector3();

        return_button.setPosition((WavetrickGame.LOGICAL_WIDTH/2)-(return_button.getWidth()/2),100);
        alpha = 0;

        TextureRegion[] split = new TextureRegion(title_img).split(690, 125)[0];
        title = new Animation(0.5f, split[0], split[1], split[2], split[3]);

        scoreFont = new BitmapFont(Gdx.files.internal("font/hanmaru.fnt"));
        scoreFont.getData().setScale(2f, 2f);

        Gdx.app.log(LOG_TAG, "constractor exit");

    }

    public void update(float delta) {//毎フレームごと，render->update 処理系
        alpha += 0.05f;
        stateTime+=delta;
        return_button.setAlpha(0.5f - 0.3f * (float) Math.sin(alpha));
        if (Gdx.input.justTouched()) {
            viewport.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Rectangle voicemodeBounds = return_button.getBoundingRectangle();
            if (voicemodeBounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.log(LOG_TAG, "back to the menu");
                game.setScreen(new MainMenuScreen(game));
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
        scoreFont.setColor(Color.BLACK);
        scoreFont.draw(batch, "" + (len * tp) + " tony point!", (WavetrickGame.LOGICAL_WIDTH / 2)-200, 550);
        return_button.draw(batch);
        batch.draw(title.getKeyFrame(stateTime, false), (WavetrickGame.LOGICAL_WIDTH / 2) - (690 / 2), 600);
        if (title.isAnimationFinished(stateTime)){
            stateTime = 0;
        }
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
        return_img.dispose();
        batch.dispose();

    }

}

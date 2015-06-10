package com.wavetrick.game;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import com.badlogic.gdx.Input;


/**
 * Created by furukawa on 15/05/13.
 */
public class GameScreen extends MyScreenAdapter {

    public static final String LOG_TAG = GameScreen.class.getSimpleName();

    // 16:9
    public static int LOGICAL_WIDTH = WavetrickGame.LOGICAL_WIDTH;
    public static int LOGICAL_HEIGHT = WavetrickGame.LOGICAL_HEIGHT;

    // ゲームの状態
    enum GameState {
        PLAY,
        PAUSE,
        GAMEOVER,
    }

    //スプライト座業系
    public static int FLOOR_DEFAULT_HEIGHT = -100;
    public static int TONY_DEFAULT_HEIGHT = 240;
    public static int TONY_DEFAULT_WIDTH = 100;
    public static int TONY_SIZE_WIDTH = 150;
    public static int TONY_SIZE_HEIGHT = 200;


    // ゲームカメラとビュポート
    private OrthographicCamera camera;
    private Viewport viewport;

    // UIカメラとビュポート
    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    //animation
    private Animation stand;
    private Animation run;
    private Animation ride_on;
    private Animation riding;
    private Animation push_kick;
    private Animation jump;
    private Animation trick;
    private float stateTime = 0;

    private Animation anim;

    //Texture
    private Texture bg1Img;
    private Texture bg2Img;
    private Texture flImg;
    private Texture push_Img;
    private Texture stand_Img;
    private Texture ride_Img;

    //SpriteBatch
    private SpriteBatch batch;

    //Sprite
    private Sprite bg1_1;
    private Sprite bg1_2;
    private Sprite floor;

    private boolean isloop=true;

    //SpriteState
    private int state = 0;
    private int speed = 0;


    public GameScreen(WavetrickGame game) {
        super(game);
        Gdx.app.log(LOG_TAG, "constractor");
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, LOGICAL_WIDTH, LOGICAL_HEIGHT);
        uiViewport = new FitViewport(LOGICAL_WIDTH, LOGICAL_HEIGHT, uiCamera);

        //画像ファイルを読む
        bg1Img = new Texture(Gdx.files.internal("game_assets/Background.jpg"));
        bg2Img = new Texture(Gdx.files.internal("game_assets/Background.jpg"));

        flImg = new Texture(Gdx.files.internal("game_assets/yuka.png"));
        push_Img = new Texture(Gdx.files.internal("game_assets/animations/push_animation.png"));
        stand_Img = new Texture(Gdx.files.internal("game_assets/animations/tony_stand.png"));
        ride_Img = new Texture(Gdx.files.internal("game_assets/animations/tony_ride.png"));

        //背景と床の定義
        bg1Img.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat); // #1
        bg2Img.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat); // #1
        flImg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

        bg1_1 = new Sprite(bg1Img, 0, 200, LOGICAL_WIDTH, LOGICAL_HEIGHT); // #2
        bg1_2 = new Sprite(bg2Img, bg2Img.getWidth(), 200, LOGICAL_WIDTH, LOGICAL_HEIGHT); // #2
        floor = new Sprite(flImg, 0, FLOOR_DEFAULT_HEIGHT, LOGICAL_WIDTH, LOGICAL_HEIGHT);


        //アニメーション定義
        TextureRegion[] split = new TextureRegion(push_Img).split(200, 250)[0];
        push_kick = new Animation(0.075f, split[0], split[1], split[2], split[3], split[3], split[2], split[1]);

        split = new TextureRegion(stand_Img).split(200, 250)[0];
        stand = new Animation(1.0f, split[0]);

        split = new TextureRegion(ride_Img).split(200, 250)[0];
        riding = new Animation(1.0f, split[0]);


        batch = new SpriteBatch();
        // ログ情報取得
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(LOG_TAG, "resize");
        //viewport.update(width, height);
        uiViewport.update(width, height);
    }

    public void update(float delta) {//毎フレームごと，render->update 処理系
        stateTime += delta;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && state!=1) {
            state = 1;
            if (speed < 9) {
                speed+=3;
            }
            stateTime = 0;
        }
    }


    private Animation currentAnim() {
        Animation anim = null;
        if (state == 0) {//stand
            anim = stand;

        } else if(state == 1) {//pushing
            anim = push_kick;
        } else if(state == 2) {//riding
            anim = riding;
        }
        return anim;
    }

    private void move_background(int speed){
        bg1_1.setPosition(bg1_1.getX()-speed,bg1_1.getY());
        bg1_2.setPosition(bg1_2.getX() - speed, bg1_2.getY());


        if(bg1_1.getX()<=-1&&isloop==true){
            isloop=false;
            Gdx.app.log(LOG_TAG, "helloween!");
            bg1_2.setPosition(LOGICAL_WIDTH, bg1_1.getY());
        }
        if(bg1_2.getX()<=-1&&isloop==false){
            isloop=true;
            Gdx.app.log(LOG_TAG, "hello!");
            bg1_1.setPosition(LOGICAL_WIDTH,bg1_1.getY());
        }
    }


    private void draw() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);

        boolean loop = false;

        move_background(speed);

        batch.begin();
        bg1_1.draw(batch);
        bg1_2.draw(batch);
        floor.draw(batch);
        anim = currentAnim();
        batch.draw(anim.getKeyFrame(stateTime,loop),TONY_DEFAULT_WIDTH,TONY_DEFAULT_HEIGHT,150,200);

        if(state==1&&anim.isAnimationFinished(stateTime)){
            state = 2;
        }

        batch.end();
    }



    @Override
    public void render (float deltaTime) {
        update(deltaTime);
        draw();
    }

    @Override
    public void hide() {
        Gdx.app.log(LOG_TAG, "hide");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log(LOG_TAG, "GameScreen#dispose()");
        batch.dispose();
        bg1Img.dispose();
        bg2Img.dispose();
        flImg.dispose();
        push_Img.dispose();
        stand_Img.dispose();
    }
}

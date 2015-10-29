package com.wavetrick.game;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


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
    public static int FLOOR_DEFAULT_HEIGHT = -750;
    public static int TONY_DEFAULT_HEIGHT = 250;
    public static int TONY_DEFAULT_WIDTH = 150;
    public static int TONY_SIZE_WIDTH = 100;
    public static int TONY_SIZE_HEIGHT = 150;
    public static int TONY_JUMP_HEIGHT = 250;
    public static int EACH_SPEED = 6;
    public static int MAX_SPEED = EACH_SPEED*3;

    public int tony_dynamic_x = TONY_DEFAULT_WIDTH;
    public int tony_dynamic_y = TONY_DEFAULT_HEIGHT;

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
    private  Animation ready_to_jump;
    private float stateTime = 0;

    private Animation anim;

    //Texture
    private Texture fl1Img;
    private Texture push_Img;
    private Texture stand_Img;
    private Texture ride_Img;
    private Texture jump_ready_Img;
    private Texture jump_Img;

    //SpriteBatch
    private SpriteBatch batch;
    private SpriteBatch uiBatch;

    //UISprite
    private int runDistance=0;
    BitmapFont goneLength;

    //Sprite
    private BackgroundSprite bg_sky;
    private BackgroundSprite bg_behind;
    private BackgroundSprite bg_middle;
    private BackgroundSprite bg_front;

    private boolean isTopOfJump = true;

    //SpriteState
    private int state = 0;
    private int speed = 0;

    //map_data
    ArrayList<Vector2> stage = new ArrayList<Vector2>();
    ArrayList<Sprite> floors = new ArrayList<Sprite>();

    //audioThread
    RecodingThread recoThr;

    public GameScreen(WavetrickGame game) {
        super(game);
        Gdx.app.log(LOG_TAG, "constractor");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, LOGICAL_WIDTH, LOGICAL_HEIGHT);
        viewport = new FitViewport(LOGICAL_WIDTH, LOGICAL_HEIGHT, camera);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false,LOGICAL_WIDTH,LOGICAL_HEIGHT);
        uiViewport = new FitViewport(LOGICAL_WIDTH,LOGICAL_HEIGHT,uiCamera);

        //画像ファイルを読む
        fl1Img = new Texture(Gdx.files.internal("game_assets/Ground_pro.png"));

        push_Img = new Texture(Gdx.files.internal("game_assets/animations/push_animation.png"));
        stand_Img = new Texture(Gdx.files.internal("game_assets/animations/tony_stand.png"));
        ride_Img = new Texture(Gdx.files.internal("game_assets/animations/tony_ride.png"));
        jump_ready_Img = new Texture(Gdx.files.internal("game_assets/animations/tony_jump_ready.png"));
        jump_Img = new Texture(Gdx.files.internal("game_assets/animations/tony_jump_animation.png"));

        //背景と床の定義
        bg_sky = new BackgroundSprite("game_assets/BackGround_sky.png",LOGICAL_WIDTH,LOGICAL_HEIGHT);
        bg_behind = new BackgroundSprite("game_assets/BackGround_behind.png",LOGICAL_WIDTH,LOGICAL_HEIGHT);
        bg_middle = new BackgroundSprite("game_assets/BackGround_middle.png",LOGICAL_WIDTH,LOGICAL_HEIGHT);
        bg_front = new BackgroundSprite("game_assets/BackGround_front.png",LOGICAL_WIDTH,LOGICAL_HEIGHT);

        //fl1Img.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

        //アニメーション定義
        TextureRegion[] split = new TextureRegion(push_Img).split(200, 230)[0];
        push_kick = new Animation(0.075f, split[0], split[1], split[2], split[3], split[3], split[2], split[1]);

        split = new TextureRegion(stand_Img).split(200, 230)[0];
        stand = new Animation(1.0f, split[0]);

        split = new TextureRegion(ride_Img).split(200, 230)[0];
        riding = new Animation(1.0f, split[0]);

        split = new TextureRegion(jump_ready_Img).split(200, 230)[0];
        ready_to_jump = new Animation(1.0f, split[0]);

        split = new TextureRegion(jump_Img).split(200, 230)[0];
        jump = new Animation(0.25f, split[0], split[1], split[2]);

        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();
        goneLength = new BitmapFont();
        goneLength.getData().setScale(5f,5f);
        // ログ情報取得
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        recoThr = new RecodingThread();
        recoThr.start();

        for (int i=0;i<15;i++){
            stage.add(new Vector2(i*fl1Img.getWidth(),FLOOR_DEFAULT_HEIGHT));
            floors.add(new Sprite(fl1Img));
        }

    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(LOG_TAG, "resize");
        //viewport.update(width, height);
        viewport.update(width, height);
    }
    private void sub_speed(int les){
        if(speed>0){
            if(speed-les<=0){
                speed = 0;
                state = 0;
            }else {
                speed -= les;
            }
        }

    }

    class StageCreation extends TimerTask{
        public void run(){
            int creationHeight = 0;

            if(stage.get(stage.size()-1).y+fl1Img.getHeight()+TONY_JUMP_HEIGHT<RecodingThread.creation){
                creationHeight = (int)(stage.get(stage.size()-1).y+fl1Img.getHeight()+TONY_JUMP_HEIGHT-TONY_SIZE_HEIGHT);

            }else if((Math.abs(stage.get(stage.size()-1).y+fl1Img.getHeight()-RecodingThread.creation)<TONY_SIZE_HEIGHT/5)){
                creationHeight = (int)(stage.get(stage.size()-1).y+fl1Img.getHeight());

            }else{
                creationHeight=RecodingThread.creation;
            }


            stage.add(new Vector2(stage.get(stage.size()-1).x+fl1Img.getWidth(),creationHeight-fl1Img.getHeight()));
            stage.add(new Vector2(stage.get(stage.size()-1).x+fl1Img.getWidth(),creationHeight-fl1Img.getHeight()));
            stage.add(new Vector2(stage.get(stage.size()-1).x+fl1Img.getWidth(),creationHeight-fl1Img.getHeight()));
            floors.add(new Sprite(fl1Img));
            floors.add(new Sprite(fl1Img));
            floors.add(new Sprite(fl1Img));

            floors_updates();
        }
    }

    private void following_camera(){
        camera.setToOrtho(false, LOGICAL_WIDTH, LOGICAL_HEIGHT);
        camera.position.x = (tony_dynamic_x-TONY_DEFAULT_WIDTH)+LOGICAL_WIDTH/2;
        camera.position.y = tony_dynamic_y+(TONY_SIZE_HEIGHT/2);
    }

    public void draw_floors(){
        for(int i=0;i<floors.size();i++){
            floors.get(i).draw(batch);
        }
    }

    public void floors_updates(){
        try {
            for (int i = 0; i < stage.size(); i++) {
                floors.get(i).setPosition(stage.get(i).x, stage.get(i).y);
            }
        }catch (java.lang.IndexOutOfBoundsException e){
            System.out.println("Exeption!!!!");
        }

        if(stage.get(0).x+fl1Img.getWidth()<-fl1Img.getWidth()){
            stage.remove(0);
            floors.remove(0);
            runDistance++;
        }

    }

    public void drawing_stage(){
        for(int i=0;i<stage.size();i++){
            Vector2 vec = stage.get(i);
            vec.x -= speed;
        }
        floors_updates();
        draw_floors();
    }

    private Sprite under_the_point(int x){
        for(int i=0;i<stage.size();i++){
            if(stage.get(i).x<x && x<stage.get(i).x+fl1Img.getWidth()){
                return floors.get(i);
            }
        }
        return new Sprite(floors.get(3));
    }

    private void tony_jump(){
        check_collision();
        if (isTopOfJump) {
            tony_dynamic_y += 15;
        }else {
            tony_dynamic_y -= 12;
        }
        if(tony_dynamic_y >= (under_the_point(tony_dynamic_x).getY()+fl1Img.getHeight()+TONY_JUMP_HEIGHT)){
            isTopOfJump = false;
        }

        if (tony_dynamic_y <= under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight()){
            tony_dynamic_y = (int)under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight();
            state = 2;
            isTopOfJump = true;
            sub_speed(EACH_SPEED-1);
        }
    }

    private void tony_fall(){
        check_collision();
        tony_dynamic_y -= 12;

        if (tony_dynamic_y <= under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight()){
            tony_dynamic_y = (int)under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight();
            state = 2;
            isTopOfJump = true;
            sub_speed(EACH_SPEED-1);
        }
    }
    private void game_over(){
        game.setScreen(new GameScreen(game));
    }
    private void check_collision(){
        Sprite underSprite = under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH);
        if((tony_dynamic_x+TONY_SIZE_WIDTH)>underSprite.getX() && (tony_dynamic_x+TONY_SIZE_WIDTH)<underSprite.getX()+underSprite.getWidth()
                && tony_dynamic_y < underSprite.getY()+underSprite.getHeight()){
            game_over();
        }
    }

    public void update(float delta) {//毎フレームごと，render->update 処理
        stateTime += delta;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && state!=1 && state!=3 && state!=4) {//keyInputで操対応
            if(state == 0){
                StageCreation stageTask = new StageCreation();
                Timer timer = new Timer();
                timer.schedule(stageTask,1000,500);
            }
            state = 1;
            if (speed < MAX_SPEED) {
                speed+=EACH_SPEED;
            }
            stateTime = 0;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && state !=4 && state !=0){
            state = 3;
        }else if(!Gdx.input.isKeyPressed(Input.Keys.SPACE) && state == 3){
            state = 4;
            stateTime = 0;
        }
        if (state == 4){
            tony_jump();
        }
        if(tony_dynamic_y > under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight() && state != 4){
            state = 5;
            tony_fall();
        }
        check_collision();
    }


    private Animation currentAnim() {
        Animation anim = null;
        if (state == 0) {//stand
            anim = stand;
        } else if(state == 1) { //pushing
            anim = push_kick;
        } else if(state == 2) { //riding
            anim = riding;
        } else if(state == 3) { //ready to jump
            anim = ready_to_jump;
        } else if(state == 4 || state == 5) { //jumping or falling
            anim = jump;
        }
        return anim;
    }


    private void draw() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        following_camera();
        camera.update();
        uiCamera.update();
        batch.setProjectionMatrix(camera.combined);
        uiBatch.setProjectionMatrix(uiCamera.combined);

        boolean loop = false;
        bg_sky.move_background(speed / 4, tony_dynamic_y);
        bg_behind.move_background(speed / 3, tony_dynamic_y);
        bg_middle.move_background(speed / 2, tony_dynamic_y);
        bg_front.move_background(speed, tony_dynamic_y);
        batch.begin();
        bg_sky.drawing(batch);
        bg_behind.drawing(batch);
        bg_middle.drawing(batch);
        bg_front.drawing(batch);

        anim = currentAnim();
        drawing_stage();
        batch.draw(anim.getKeyFrame(stateTime, loop), tony_dynamic_x, tony_dynamic_y, TONY_SIZE_WIDTH, TONY_SIZE_HEIGHT);

        if(state==1&&anim.isAnimationFinished(stateTime)){
            state = 2;
        }
        batch.end();
        uiBatch.begin();
        goneLength.draw(uiBatch, "" + runDistance + " m", 50, LOGICAL_HEIGHT - 50);
        uiBatch.end();
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
        uiBatch.dispose();
        bg_sky.dispose();
        bg_behind.dispose();
        bg_middle.dispose();
        bg_front.dispose();
        fl1Img.dispose();
        push_Img.dispose();
        stand_Img.dispose();
    }
}
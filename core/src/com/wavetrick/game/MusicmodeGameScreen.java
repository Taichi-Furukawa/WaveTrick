package com.wavetrick.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.*;

import java.io.*;
import java.util.ArrayList;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


/**
 * Created by furukawa on 15/05/13.
 */

public class MusicmodeGameScreen extends MyScreenAdapter {

    public static final String LOG_TAG = GameScreen.class.getSimpleName();

    // 16:9
    public static int LOGICAL_WIDTH = WavetrickGame.LOGICAL_WIDTH;
    public static int LOGICAL_HEIGHT = WavetrickGame.LOGICAL_HEIGHT;

    //gamemode
    public static String gamemode;
    //スプライト座業系
    public static int FLOOR_DEFAULT_HEIGHT = -750;
    public static int TONY_DEFAULT_HEIGHT = 250;
    public static int TONY_DEFAULT_WIDTH = 150;
    public static int TONY_SIZE_WIDTH = 100;
    public static int TONY_SIZE_HEIGHT = 150;
    public static int TONY_JUMP_HEIGHT = 350;
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
    private Texture trick_Img;

    //SpriteBatch
    private SpriteBatch batch;
    private SpriteBatch uiBatch;

    //UISprite
    private int runDistance=0;
    private int score = 0;
    BitmapFont goneLength;
    BitmapFont scoreFont;

    //Sprite
    private BackgroundSprite bg_sky;
    private BackgroundSprite bg_behind;
    private BackgroundSprite bg_middle;
    private BackgroundSprite bg_front;

    private boolean isTopOfJump = true;
    private boolean isTriked = false;

    //SpriteState
    private int state = 0;
    private int speed = 0;

    //map_data
    ArrayList<Vector2> stage = new ArrayList<Vector2>();
    ArrayList<Sprite> floors = new ArrayList<Sprite>();

    //Trick_AnimationValue
    private int justDown=0;

    //sound
    Sound kick_sound;
    Sound landing_sound;
    Sound plaining_sound;
    Sound jump_sound;
    //bgm
    private Music bgm;


    public static InputStream in_stream;
    public static FFT4g fft;

    public MusicmodeGameScreen(WavetrickGame game,String mode,String musicfile) {
        super(game);
        Gdx.app.log(LOG_TAG, "constractor");

        //ゲームモード
        gamemode = mode;

        //カメラとビューポートの定義
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
        trick_Img = new Texture(Gdx.files.internal("game_assets/animations/tony_flip.png"));

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

        split = new TextureRegion(jump_Img).split(200, 250)[0];
        jump = new Animation(0.25f, split[0], split[1], split[2]);

        split = new TextureRegion(trick_Img).split(200, 250)[0];
        trick = new Animation(0.1f, split[0], split[1], split[2],split[3],split[4],split[5]);


        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();
        //U表示系
        goneLength = new BitmapFont(Gdx.files.internal("font/hanmaru.fnt"));//走行距離
        goneLength.getData().setScale(2f, 2f);

        scoreFont = new BitmapFont(Gdx.files.internal("font/hanmaru.fnt"));
        scoreFont.getData().setScale(2f, 2f);
        // ログ情報取得
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        for (int i=0;i<15;i++){
            stage.add(new Vector2(i*fl1Img.getWidth(),FLOOR_DEFAULT_HEIGHT));
            floors.add(new Sprite(fl1Img));
        }

        //sound
        kick_sound = Gdx.audio.newSound(Gdx.files.internal("sound_assets/kick_sample.ogg"));
        landing_sound = Gdx.audio.newSound(Gdx.files.internal("sound_assets/Landing_sample.ogg"));
        plaining_sound = Gdx.audio.newSound(Gdx.files.internal("sound_assets/Plaining_sample.ogg"));
        jump_sound = Gdx.audio.newSound(Gdx.files.internal("sound_assets/Jump_sample.ogg"));

        kick_sound.setVolume(1,0.1f);
        landing_sound.setVolume(1,0.1f);
        plaining_sound.setVolume(1,0.1f);
        kick_sound.setVolume(1,0.1f);


        //musicmode stage creation initialize
        in_stream = Gdx.files.internal("music/" + musicfile + ".ogg").read();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(in_stream);
        int read;
        byte[] buff = new byte[1024];
        try {
            while ((read = in.read(buff)) > 0)
            {
                out.write(buff, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] audios = out.toByteArray();//stage data
        byte[][] audiodata = new byte[(audios.length/2048)+1][2048];
        int cnt = 0;
        int index = 0;
        for (int i=0;i<audios.length;i++){
            if(cnt == 2048){
                cnt=0;
                index++;
                //System.out.println(index);
            }
            audiodata[index][cnt] = audios[i];
            cnt++;

        }

        int creation = 0;
        int max = -100;
        int indexof = 0;
        int count = 0;
        for(int k=0;k<audiodata.length;k++){

            double[] doubledata = new double[audiodata[k].length];
            for (int i =0;i<audiodata[k].length;i++){
                doubledata[i] = (double)audiodata[k][i];
            }
            fft = new FFT4g(doubledata.length);
            fft.rdft(1, doubledata);
            Integer[] stageData = new Integer[doubledata.length];
            max = -100;
            indexof = 0;
            count = 0;
            for(int i=0;i<doubledata.length-2;i+=2){
                stageData[count] = (int)sqrt((pow(doubledata[i], 2) + pow(doubledata[i + 1], 2)));
                if(stageData[count]>max){
                    max = stageData[count];
                    indexof = count;
                }
                count++;
            }
            creation = indexof;
            int creationHeight = 0;
            if(creation-(stage.get(stage.size()-1).y+fl1Img.getHeight())>TONY_JUMP_HEIGHT){
                creationHeight = ((int)(stage.get(stage.size()-1).y+fl1Img.getHeight())+(TONY_JUMP_HEIGHT-TONY_SIZE_HEIGHT));

            }else if((Math.abs(stage.get(stage.size()-1).y+fl1Img.getHeight()-RecodingThread.creation)<TONY_SIZE_HEIGHT)){
                creationHeight = (int)(stage.get(stage.size()-1).y+fl1Img.getHeight());

            }else{
                creationHeight=creation;
            }
            stage.add(new Vector2(stage.get(stage.size()-1).x+fl1Img.getWidth(),creationHeight-fl1Img.getHeight()));
            stage.add(new Vector2(stage.get(stage.size()-1).x+fl1Img.getWidth(),creationHeight-fl1Img.getHeight()));
            stage.add(new Vector2(stage.get(stage.size()-1).x+fl1Img.getWidth(),creationHeight-fl1Img.getHeight()));
            stage.add(new Vector2(stage.get(stage.size()-1).x+fl1Img.getWidth(),creationHeight-fl1Img.getHeight()));
            floors.add(new Sprite(fl1Img));
            floors.add(new Sprite(fl1Img));
            floors.add(new Sprite(fl1Img));
            floors.add(new Sprite(fl1Img));

            floors_updates();
        }
        bgm = Gdx.audio.newMusic(Gdx.files.internal("music/" + musicfile + ".ogg"));
        bgm.setLooping(true);
        bgm.setVolume(2.0f);
        bgm.play();
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
    private void plus_speed(int add){
        speed+=add;
        if(speed>MAX_SPEED){
            speed=MAX_SPEED;
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
            tony_dynamic_y += 20;
        }else {
            tony_dynamic_y -= 20;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && isTopOfJump==true){
            isTriked = true;
            state = 6;
        }


        if(tony_dynamic_y >= (under_the_point(tony_dynamic_x).getY()+fl1Img.getHeight()+TONY_JUMP_HEIGHT)){
            isTopOfJump = false;
        }

        if (tony_dynamic_y <= under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight()){
            tony_dynamic_y = (int)under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight();
            state = 2;
            isTopOfJump = true;
            if (justDown==0) {
                Gdx.app.log(LOG_TAG, "zero");
                sub_speed(EACH_SPEED - 1);
            }
            if (justDown==2) {
                Gdx.app.log(LOG_TAG, "two");
                plus_speed(1);
            }
            if (justDown==3) {
                Gdx.app.log(LOG_TAG, "three");
                plus_speed(2);
            }
            justDown = 0;
            isTriked = false;
            landing_sound.play();
        }
    }

    private void tony_fall(){
        check_collision();
        tony_dynamic_y -= 20;

        if (tony_dynamic_y <= under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight()){
            tony_dynamic_y = (int)under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight();
            state = 2;
            if (justDown==0) {
                Gdx.app.log(LOG_TAG, "zero");
                sub_speed(EACH_SPEED - 1);
            }
            if (justDown==2) {
                Gdx.app.log(LOG_TAG, "two");
                plus_speed(1);
            }
            if (justDown==3) {
                Gdx.app.log(LOG_TAG, "three");
                plus_speed(2);
            }
            justDown = 0;
            isTopOfJump = true;
            isTriked = false;
            landing_sound.play();
        }
    }
    private void game_over(){
        game.setScreen(new ResultScreen(game,runDistance,score));
    }
    private void check_collision(){
        Sprite underSprite = under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH);
        if((tony_dynamic_x+TONY_SIZE_WIDTH)>underSprite.getX() && (tony_dynamic_x+TONY_SIZE_WIDTH)<underSprite.getX()+underSprite.getWidth()
                && tony_dynamic_y < underSprite.getY()+underSprite.getHeight()){
            game_over();
        }
    }

    public void update(float delta) {//毎フレームごと，render->update 処理
        if (state == 4 || state == 5 ||state == 0 || state == 6){
            plaining_sound.pause();
        }else{
            plaining_sound.resume();
        }

        stateTime += delta;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && state!=1 && state!=3 && state!=4 && state!=6) {//keyInputで操対応
            if(state == 0){
                plaining_sound.loop();
            }
            state = 1;
            plus_speed(EACH_SPEED);
            kick_sound.play();
            stateTime = 0;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && state !=4 && state !=0 && state!=6){
            state = 3;
        }else if(!Gdx.input.isKeyPressed(Input.Keys.SPACE) && state == 3){
            jump_sound.play();
            state = 4;
            stateTime = 0;
        }
        if (state == 4 || state == 6){
            tony_jump();
        }
        if(tony_dynamic_y > under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight() && state != 4 && state != 6){
            state = 5;
            tony_fall();
        }
        if((state == 4 || state == 5 || state == 6) && Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && justDown==0){
            int betweenLength = (int)(tony_dynamic_y - (under_the_point(tony_dynamic_x+TONY_SIZE_WIDTH).getY()+fl1Img.getHeight()));
            if (betweenLength <= TONY_SIZE_HEIGHT+(TONY_SIZE_HEIGHT/2)){
                justDown = 1;
                if(betweenLength<=TONY_SIZE_HEIGHT){
                    justDown = 2;
                    if(betweenLength<=TONY_SIZE_HEIGHT/2){
                        justDown = 3;
                    }
                }
            }
            if(isTriked==true){
                score += justDown*2;
            }else{
                score +=justDown;
            }
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
        } else if (state == 6){ //tricking
            anim = trick;

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
        scoreFont.draw(uiBatch,""+score+"TP",LOGICAL_WIDTH-200,100);
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
        plaining_sound.dispose();
        kick_sound.dispose();
        jump_sound.dispose();
        landing_sound.dispose();
        bgm.dispose();
    }
}
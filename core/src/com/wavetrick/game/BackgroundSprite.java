package com.wavetrick.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by furukawa on 15/07/04.
 */
public class BackgroundSprite {
    private Texture bg1_img;
    private Texture bg2_img;

    public Sprite bg1;
    public Sprite bg2;

    public Boolean isloop = true;

    public BackgroundSprite(String background,int logical_width,int logical_height){
        bg1_img = new Texture(Gdx.files.internal(background));
        bg2_img = new Texture(Gdx.files.internal(background));

        bg1_img.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.MirroredRepeat);
        bg2_img.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.MirroredRepeat);

        bg1 = new Sprite(bg1_img, 0, 0, bg1_img.getWidth(), bg1_img.getHeight()); // #2
        bg2 = new Sprite(bg2_img, bg2_img.getWidth(), 0, bg2_img.getWidth(), bg2_img.getHeight()); // #2
    }

    public void drawing(SpriteBatch batch){
        bg1.draw(batch);
        bg2.draw(batch);
    }

    public void jump_up() {

    }

    public void move_background(int speed,int tony_y){

        bg1.setPosition(bg1.getX() - speed,tony_y-bg1.getHeight()/4);
        bg2.setPosition(bg2.getX() - speed, tony_y-bg2.getHeight()/4);

        if(bg1.getX()<=-1&&isloop==true){
            isloop=false;
            bg2.setPosition(bg1_img.getWidth()-5, bg1.getY());
        }
        if(bg2.getX()<=-1&&isloop==false){
            isloop=true;
            bg1.setPosition(bg1_img.getWidth()-5,bg1.getY());
        }
    }

    public void dispose(){
        bg1_img.dispose();
        bg2_img.dispose();
    }

}

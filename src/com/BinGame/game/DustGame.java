package com.BinGame.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * @author Milan Topalovic
 *
 */
public class DustGame extends Game {

	public static final String TITLE = "Papper Toss";
	public static final float    V_HEIGHT = 640;
	public static final float    V_WIDTH  = 480;
	
	@Override
	public void create () {
		setScreen(new DustBinGame());
	}
	
	@Override
	public void resize(int width , int height){
		super.resize(width , height);
	}
	
	@Override
	public void render(){
		super.render();
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
}

package com.BinGame.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * @author Milan Topalovic
 *
 */
public class DustBinGame implements Screen{
	
	/*Libgdx dependency classes*/
	private SpriteBatch batch;
	private Skin        skin;
	private Stage       stage;
	
	/*Game related fields*/
	private int         pointCounter;
	private Texture     crushedPaper, binEmpty, binFull;
	private Label       pointLabel , windLabelR , windLabelL;
	private Texture     background;
	private BitmapFont  bmf;
	private Sound       coinSound;
	
	private static final int   BIN_RADIUS = 50; //Radius of the bin
	private static final int   PAPER_RADIUS = 20;  //Radius of the crushed paper
	private float scaleY;
	private float scaleX;
	private float startingX;
	private float startingY;
	private float endingpaperY;
	private float endingpaperX;
	
	/*Animation related stuff*/
	private static final float POINT_ANIMATION = 1.2f;
	private static final float PAPER_ANIMATION = 0.7f;
	private float              currentTimePoint;
	private float              currentTimePaper;
	private boolean            showPointAnimation;
	private boolean            showPaperAnimation;
	private boolean            isDragging;
	private boolean            showFullBin;
	private boolean            playSound;
	private int                pointAnimationMode;
	private int                windBlowRight;
	private int                windBlowLeft;
	
	@Override
	public void show() {
		stage = new Stage(new StretchViewport(DustGame.V_WIDTH, DustGame.V_HEIGHT));
		batch = new SpriteBatch();
		skin  = new Skin(Gdx.files.internal("mySkin/myPack.json"));
		
		Gdx.input.setInputProcessor(stage);
	
		setUpTable();		//Sets top table for point counter
		loadAssets();		//Load all assets for game 
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.draw(background , 0 , 0 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
		drawBin();
		drawPaper(delta);
		drawPoints(delta);
		batch.end();
		
		batch.begin();
		stage.act(delta);
		stage.draw();
		batch.end();
	}
	
	
	private void drawPoints(float delta){
		if(showPointAnimation){
			if(currentTimePoint >= POINT_ANIMATION)
				showPointAnimation = false;	
			currentTimePoint += delta;
			if(pointAnimationMode == 1){
				float widht  = bmf.getBounds("Nice shot +1").width;
				float height = bmf.getBounds("Nice shot +1").height; 
				bmf.setColor(1,1,1,currentTimePoint / POINT_ANIMATION);
				bmf.setScale(scaleX , scaleY);
				bmf.draw(batch, "Nice shot +1", Gdx.graphics.getWidth() / 2 - widht / 2 , Gdx.graphics.getHeight() / 2 + BIN_RADIUS * scaleY + height / 2 + 40 * Interpolation.linear.apply(currentTimePoint) / POINT_ANIMATION);
			}
			else{
				float widht  = bmf.getBounds("Miss").width;
				float height = bmf.getBounds("Miss").height;
				bmf.setColor(1, 1, 1, currentTimePoint / POINT_ANIMATION);
				bmf.setScale(scaleX, scaleY);
				bmf.draw(batch, "Miss", Gdx.graphics.getWidth() / 2 - widht / 2 , Gdx.graphics.getHeight() / 2 + BIN_RADIUS * scaleY + height / 2 + 40 * Interpolation.linear.apply(currentTimePoint) / POINT_ANIMATION);
			}
		}
	}
	
	private void drawPaper(float delta){
		if(showPaperAnimation){
			if(currentTimePaper <= PAPER_ANIMATION){
			currentTimePaper += delta;
			float x = (Gdx.graphics.getWidth() / 2 -  scaleY * PAPER_RADIUS) + (endingpaperX  - (Gdx.graphics.getWidth() / 2 -  scaleY * PAPER_RADIUS)) * Interpolation.linear.apply(currentTimePaper / PAPER_ANIMATION);
			float y = (30 * scaleY) + (endingpaperY - (30 * scaleY)) * Interpolation.linear.apply(currentTimePaper / PAPER_ANIMATION);
			float width =  2 * PAPER_RADIUS * scaleY;
			float height = 2 * PAPER_RADIUS * scaleY;
			batch.draw(crushedPaper, x, y, width / 2, height / 2, width, height, 1 , 1, 360 - 360 * Interpolation.linear.apply(currentTimePaper / PAPER_ANIMATION), 0 , 0 , crushedPaper.getWidth() , crushedPaper.getHeight() , false, false);
			return;
			}else{
				if(pointCounter == 1)
					showFullBin = true;
				showPaperAnimation = false;
				if(pointCounter > 4) applyWindBlow();
				if(playSound){
					playSound = false;
					coinSound.play(0.2f);
				}
			}
		}
		float x = Gdx.graphics.getWidth() / 2 - scaleY * PAPER_RADIUS; //TODO:set position when paper is trown
		float y = 30 * scaleY;    //TODO:set position when paper is trown
		float width  = 2 * PAPER_RADIUS * scaleY;
		float height = 2 * PAPER_RADIUS * scaleY;
		batch.draw(crushedPaper , x , y , width , height);
	}
	
	private void drawBin(){
		float x =      Gdx.graphics.getWidth() / 2 - BIN_RADIUS * scaleY;
		float y = 	   Gdx.graphics.getHeight() / 2 - BIN_RADIUS * scaleY;
		float width =  2 * BIN_RADIUS * scaleY;
		float height = 2 * BIN_RADIUS * scaleY;
		if(pointCounter == 0 || !showFullBin){
			batch.draw(binEmpty , x , y , width , height);
		}else{
			batch.draw(binFull  , x , y , width , height);
		}
	}
	
	private void applyWindBlow(){
			Random r = new Random();
			if(r.nextInt(2) == 1){
				//apply right blow for next shot
				windBlowLeft = 0;
				windLabelL.setText("Wind blow left: 0");
				windBlowRight = r.nextInt(pointCounter) + 14;
				windLabelR.setText("Wind blow right: " + windBlowRight);
				windBlowRight *= -1;
			}else{
				//apply left blow for next shot
				windBlowLeft = 0;
				windLabelR.setText("Wind blow right: 0");
				windBlowLeft = r.nextInt(pointCounter) + 14;
				windLabelL.setText("Wind blow left: " + windBlowLeft);
			}
	}
	
	private void setUpTable(){
		Table table = new Table(skin);
		pointLabel  = new Label("Points: 99",skin);
		windLabelR  = new Label("Wind blow left: 0" , skin);
		windLabelL  = new Label("Wind blow right: 0" , skin);
		windLabelL.setFontScale(0.5f);
		windLabelR.setFontScale(0.5f);
		table.setSize(DustGame.V_WIDTH, 80);
		table.align(Align.topLeft);
		table.setPosition(0 , DustGame.V_HEIGHT - 80);
		table.add(pointLabel).size(pointLabel.getWidth(), pointLabel.getHeight()).align(Align.topLeft);
		table.add(new Label("",skin)).size(DustGame.V_WIDTH - windLabelR.getWidth() - 55, windLabelL.getHeight());
		table.add(windLabelR).maxSize(windLabelL.getWidth(),windLabelL.getHeight()).align(Align.right);
		table.row();
		table.add(new Label("",skin));
		table.add(new Label("",skin));
		table.add(windLabelL).maxSize(windLabelR.getWidth(),windLabelR.getHeight()).right();
		pointLabel.setText("Points: 0");
		stage.addActor(table);
	}
	
	private void loadAssets(){
		Random r = new Random();
		//int backgroundToLoad = r.nextInt(4) + 1; //TODO:Add diffrent backgrounds
		background   = new Texture(Gdx.files.internal("pictures/binbackground2.png"));
		crushedPaper = new Texture(Gdx.files.internal("pictures/crushedpaper.png"));
		binEmpty     = new Texture(Gdx.files.internal("pictures/recyclebinempty.png"));
		binFull      = new Texture(Gdx.files.internal("pictures/recyclebinfull.png"));
		bmf          = new BitmapFont(Gdx.files.internal("mySkin/mydefaultfont.fnt"), Gdx.files.internal("mySkin/mydefaultfont.png"), false);
		coinSound    = Gdx.audio.newSound(Gdx.files.internal("sounds/coin_sound.mp3"));
		stage.addListener(new MyListener());
		currentTimePoint   = 0;
		currentTimePaper   = 0;
		pointCounter       = 0;
		showPointAnimation = false;
		showPaperAnimation = false;
		isDragging         = false;
		showFullBin        = false;
		playSound          = false;
		windBlowLeft       = 0;
		windBlowRight      = 0;
		scaleX = Gdx.graphics.getWidth() / DustGame.V_WIDTH;
		scaleY = Gdx.graphics.getHeight() / DustGame.V_HEIGHT;
	}
	
	/*Input detector (TOUCH,KEY)*/
	class MyListener extends InputListener{
		
		
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			float screenX = (Gdx.graphics.getWidth() / DustGame.V_WIDTH) * x;
			float screenY = (Gdx.graphics.getHeight() / DustGame.V_HEIGHT) * y;
			//Gdx.app.log("Y", startingY + "  screenY: " + screenY);
			//Gdx.app.log("X", startingX + "  screenX: " + screenX);
			if(isDragging){
				if((screenY - startingY) <= 10) return;
				float newPointX = startingX - (startingX - screenX) + windBlowLeft + windBlowRight;
				float halfWidth = Gdx.graphics.getWidth() / 2;
				if(newPointX < (halfWidth + (BIN_RADIUS * scaleY)) && newPointX > (halfWidth - (BIN_RADIUS * scaleY))){
					if((screenY - startingY) >= 100 && (screenY - startingY) <= 100 + (2 * BIN_RADIUS * scaleY)){
						showPaperAnimation = true;
						float trasholdX = halfWidth + (BIN_RADIUS * scaleY) - 2 * PAPER_RADIUS * scaleY;
						endingpaperX = newPointX > trasholdX ? halfWidth + (BIN_RADIUS * scaleY) - 2 * PAPER_RADIUS * scaleY : newPointX;
						endingpaperY = Math.abs(Gdx.graphics.getHeight() / 2 + scaleY * BIN_RADIUS) - PAPER_RADIUS * scaleY;
						currentTimePaper = 0f;
						showPointAnimation = true;
						playSound          = true;
						currentTimePoint = 0f;
						pointCounter++;
						pointAnimationMode = 1;
						pointLabel.setText("Points: " + pointCounter);
						isDragging = false;
						return;
					}
				}
				endingpaperY = startingY + (screenY - startingY);
				endingpaperX = newPointX;
				pointAnimationMode = 0;
				currentTimePoint = 0f;
				currentTimePaper = 0f;
				showPaperAnimation = true;
				showPointAnimation = true;
				isDragging = false;
			}
		}
		
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if(showPaperAnimation) return true;
			startingX = (Gdx.graphics.getWidth() / DustGame.V_WIDTH) * x;
			startingY = (Gdx.graphics.getHeight() / DustGame.V_HEIGHT) * y;
			return true;
		}
		
		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			if(showPaperAnimation)return;
			isDragging = true;
		}
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		scaleX = (Gdx.graphics.getWidth() / DustGame.V_WIDTH);
		scaleY = (Gdx.graphics.getHeight() / DustGame.V_HEIGHT);
	}
	
	@Override
	public void pause() {
		
	}
	
	@Override
	public void resume() {
	
		
	}
	
	@Override
	public void hide() {
	
		
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		coinSound.dispose();
		skin.dispose();
		bmf.dispose();
	}
}
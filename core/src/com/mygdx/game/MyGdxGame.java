package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import utils.Pair;

// Gdx.input.getAccelerometerY();
// Gdx.graphics.getFramesPerSecond()

public class MyGdxGame extends InputAdapter implements ApplicationListener{
	
	final int FIELD_WIDTH = 16;
	final int ROWS = 1;
	final int CELL_WIDTH = 1000;
	final int TUBE_HEIGHT_IN_BLOCKS = 6;
	final int BLOCKS_PER_CHANGE_DELTA_Y = 5;
	final float CELL_LENGTH = 10;
	final float UNIT_OF_DELTA_Y = CELL_LENGTH / CELL_WIDTH;
	final float[] startPos = {150f, 0f, -50f};
	double[][] changes;
	
	int score = 0;
	float deltay = UNIT_OF_DELTA_Y;
	float y = 0;
	float[] pos = {10f, -9f, 0f};
	
	
	public PerspectiveCamera cam;
	public CameraInputController camController; //it will be deleted
	public ModelBuilder builder;
	public ModelBatch modelBatch;
	public Environment environment;
	public Label showScore;
	public MeshPartBuilder meshBuilder;
	public Node node;
	
	public Material tubeMaterial;;
	public Material barrierMaterial;
	public Material tetrahedronMaterial;
	
	public Model spike;
	public Model block;
	public Model tetrahedron;
	public Model tubeBlock;
	
	public ModelInstance[][] tube;
	public ModelInstance[][] gamefield;
	

	 @Override
	 public void create() {
		 
	  changes = new double[FIELD_WIDTH][3];
	  for (int i = 0; i < 3; i++){
		  changes[0][i] = 0;
	  }
	  for (int i = 1; i < FIELD_WIDTH; i++){
		  changes[i][0] = changes[i - 1][0] + CELL_LENGTH * Math.cos(changes[i - 1][2]);
		  changes[i][1] = changes[i - 1][1] + CELL_LENGTH * Math.sin(changes[i - 1][2]);
		  changes[i][2] = changes[i - 1][2] + 180 - 180 * (FIELD_WIDTH - 2) / FIELD_WIDTH;
	  }
	  
	  
	  cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	  cam.position.set(startPos[0], startPos[1], startPos[2]);
	  cam.lookAt(0, 0, 0);
	  cam.near = 1f;
	  cam.far = 300f;
	  cam.update();
	  //it will be deleted
      camController = new CameraInputController(cam);
      Gdx.input.setInputProcessor(camController);
      
      builder = new ModelBuilder();
      
	  modelBatch = new ModelBatch();
	  
	  environment = new Environment();
	  environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
	  environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 10f, 10f, 20f));
	  
	  showScore = new Label(" ", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
      
	  tubeMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN));
	  barrierMaterial = new Material(ColorAttribute.createDiffuse(Color.BLUE));
	  tetrahedronMaterial = new Material(ColorAttribute.createDiffuse(Color.GOLDENROD));
	  
	  spike = builder.createCone(CELL_LENGTH, CELL_LENGTH, CELL_LENGTH, 4, barrierMaterial, 
			  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
	  block = builder.createBox(CELL_LENGTH, CELL_LENGTH, CELL_LENGTH, barrierMaterial, 
			  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
	  tetrahedron = builder.createCone(CELL_LENGTH, CELL_LENGTH, CELL_LENGTH, 4, tetrahedronMaterial,
			  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
	  tubeBlock = builder.createBox(CELL_LENGTH, CELL_LENGTH, CELL_LENGTH, tubeMaterial, 
			  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
	  
	  gamefield = new ModelInstance[ROWS + 1][FIELD_WIDTH];
	  tube = new ModelInstance[ROWS + 1][FIELD_WIDTH];
	  for (int i = 0; i < ROWS + 1; i++){
		  for (int j = 0; j < FIELD_WIDTH; j++){
			  tube[i][j] = new ModelInstance(tubeBlock, (float)changes[j][0], CELL_LENGTH * i, (float)changes[j][1]);
			  tube[i][j].transform.setToRotation(Vector3.X, -1 * (float) changes[j][2]);
		  }
	  }
	 }
	 
	 
	 @Override
	 public void render() {
	  /*pos[0] -= deltay;
	  y += deltay;
	  if (y >= 1000){
		  score++;
		  if (score % BLOCKS_PER_CHANGE_DELTA_Y == 0){
			  deltay += UNIT_OF_DELTA_Y;
		  }
		  y = 0;
		  changeGameField();
	  }*/
		 
	  Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	  Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	  //it will be deleted
	  camController.update();
	  
	  StringBuilder stringBuilder = new StringBuilder();
	  stringBuilder.append(Gdx.input.getAccelerometerY() + "");
	  showScore.setText(stringBuilder);
	  
	  cam.position.set(pos[0], pos[1], pos[2]);
	  cam.update();
	  
	  modelBatch.begin(cam);
	  for (int i = 0; i < ROWS + 1; i++){
		  for (int j = 0; j < FIELD_WIDTH; j++){
			  if (tube[i][j] != null){
				  modelBatch.render(tube[i][j], environment);
			  }
			  if (gamefield[i][j] != null){
				  modelBatch.render(gamefield[i][j], environment);
			  }
		  }
	  }
	  modelBatch.end();
	 }
	 
	 
	 void changeGameField(){
		for (int i = 0; i < ROWS; i++){
			for (int j = 0; j < FIELD_WIDTH; j++){
				gamefield[i][j] = gamefield[i + 1][j];
				tube[i][j].transform.setToTranslation(0f, CELL_LENGTH, 0f);
			}
		}
		for (int i = 0; i < FIELD_WIDTH / 2; i++){
			Pair<Model, Model> pair = generateNewPair();
			if (pair.first != null){
				gamefield[ROWS][i] = new ModelInstance(pair.first,
						(float)changes[i][0], score, (float)changes[i][1]);
				gamefield[ROWS][i].transform.setToRotation(Vector3.X, (float)changes[i][2]);
			} else{
				gamefield[ROWS][i] = null;
			}
			if (pair.second != null){
				gamefield[ROWS][FIELD_WIDTH - i - 1] = new ModelInstance(pair.second, 
						(float)changes[FIELD_WIDTH - i - 1][0], score,
						(float)changes[FIELD_WIDTH - i - 1][1]);
				
			} else{
				gamefield[ROWS][FIELD_WIDTH - i - 1] = null;
			}
			tube[ROWS][i].transform.setToTranslation(0f, CELL_LENGTH, 0f);
			tube[ROWS][FIELD_WIDTH - i - 1].transform.setToTranslation(0f, CELL_LENGTH, 0f);
		}
	 }
	 
	 Pair<Model, Model> generateNewPair(){
		ArrayList<Pair<Model, Model>> possibilities = new ArrayList<Pair<Model, Model>>();
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				int maximum = TUBE_HEIGHT_IN_BLOCKS - 1;
				if (j != 0){
					maximum--;
				}
				Pair<Model, Integer> pair = unHash(i, maximum);
				Model first = pair.first;
				maximum -= pair.second;
				Model second = unHash(j, maximum).first;
				possibilities.add(new Pair<Model, Model>(first, second));
			}
		}
		int index = (int)(Math.random() * possibilities.size());
		return possibilities.get(index);
	 }
	 
	 
	 Pair<Model, Integer> unHash(int hash, int maximum){
		 switch(hash){
		 case 0:
			 // there is returned emptyness
			 return new Pair<Model, Integer>(null, 0);
		 case 1:
			 // there is returned a spike
			 return new Pair<Model, Integer>(spike, 0);
		 case 2:
			 //there will be returned a tower of blocks
			 int height = (int)(Math.random() * maximum) + 1;
			 builder.begin();
			 for (int i = 1; i <= height; i++){
				 node = builder.node(i + "", block);
				 node.translation.set(0f, CELL_LENGTH * (height - i), 0f);
			 }
			 Model tower = builder.end();
			 return new Pair<Model, Integer>(tower, height);
		 }
		 return new Pair<Model, Integer>(null, 0);
	 }
	 
	 
	 @Override
	 public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	  return false;
	 }
	 
	 
	 @Override
	 public void dispose() {
	 }
	 

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}

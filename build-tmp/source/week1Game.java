import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class week1Game extends PApplet {

 /* @pjs font="Anonymous Pro Minus.ttf"; */


PImage seekerImage, wandererImage, blackHoleImage,mouse;
ParticleSystem ps;
Keyboard keyboard;
EntityManager entityManager;
Player player;
EnemySpawner enemySpawner;
Hud hud;
boolean mute = false;
boolean showStart = true;
boolean paused = false;
Palettes palettes;
int[] colors = {0xffF2B774, 0xffFB6084, 0xffFB0029,0xffA197C2,0xff597A74, 0xff6D0D00, 0xffC371F3, 0xff1A3419,0xff878282, 0xffE0E0E0};

// used for advancing to the next level;

SoundManager soundManager;

public void setup() {
	size(720,512, P2D);
  colorMode(HSB);	
  player = new Player();
  imageMode(CENTER);
    rectMode(CENTER);


  soundManager = new SoundManager(this);
  soundManager.addSound("mame_fire");
  soundManager.addSound("mame_explode1");
  ps = new ParticleSystem();
  enemySpawner = new EnemySpawner();
  entityManager = new EntityManager();
  keyboard = new Keyboard();
  hud = new Hud();
  frameRate(60);
  noSmooth();  
  //  hint(ENABLE_OPENGL_4X_SMOOTH);
}

public void draw() {
  background(0);
  if(showStart){
    hud.startScreen();
    if(mousePressed){
      showStart = false;
      paused  = !paused;
    }
  }
  player.display();
  entityManager.display();
  
  if(paused){
      if(enemySpawner.endGame){
        ps.enemyExplode(width/2,height/2,150,colors[(int)random(colors.length)]);
        hud.endGame();
      } else{
        hud.update();
      }


      
      player.update();
      enemySpawner.update();

      entityManager.update();
     // entityManager.display();
      ps.run();    
  } else{
    text("paused", width/2, height - 200);
  }


  scanLinePostProcess();
 // println(killCount);
// console.log(frameRate);
}

// from Andor Salga
public void scanLinePostProcess(){
  pushStyle();
  
  stroke(16, 128);
  strokeWeight(1);
  
  for(int i = 0; i < height; i += 2 ){
    line(0, i, width, i);
  }
  
  for(int i = 0; i < width; i += 2 ){
   // line(i, 0, i, height);
  }

  popStyle();
}


public boolean withinBoundingBox(PVector point, PVector box_center, PVector box_size) {
  // Test to see if point is within AABB defined by a center and size
  if (((box_center.x - ((float)box_size.x / 2)) < point.x) && (point.x < (box_center.x + ((float)box_size.x / 2)))) {
    if (((box_center.y - ((float)box_size.y / 2)) < point.y) && (point.y < (box_center.y + ((float)box_size.y / 2)))) {
      return true;
    }
  }
  return false;
}
// from asteriods
public PVector randomVector(){
  PVector pvec = new PVector(random(-1,1), random(-1,1), 0);
  pvec.normalize();
  return pvec;
}

public PVector randomVector(float range){
  PVector pvec = new PVector(random(-range,range), random(-range,range), 0);
  pvec.normalize();
  return pvec;
}

public void mousePressed(){
      
}

public void keyPressed(){
  if(key == 'm' || key == 'M'){
    mute = !mute;
    soundManager.setMute(mute);
  }
  if(key == ' '){
    showStart = false;
    paused  = !paused;
  }
  keyboard.pressKey(key, keyCode);
}

public void keyReleased(){
  keyboard.releaseKey(key, keyCode);
}
class Bullet extends Entity
{
	// not sure if the best way to do this
	boolean hitWall;
	int c;
	Bullet(PVector pos, PVector vel, int _c){
		hitWall = false;
		
		position = pos.get();
		velocity = vel.get();
		velocity.limit(10);
		radius = 6;
		c = _c;
	}

	public void display(){
		fill(c);
		rect(position.x, position.y,radius,radius);
	}

	public void update(){
		position.add(velocity);
		if(position.x <0 || position.x > width || position.y < 0 || position.y > height){
			ps.bulletExplode(position.x, position.y);
			hitWall = true;
			this.isExpired = true;
		}
	//	if(velocity.magSq() > 0) orientation = velocity.heading();
	}

	public boolean hasHitWall(){
		return hitWall;
	}

}//end of Bullet class
class Enemy extends Entity{

	// add flags for behaviors
	boolean canOverLap = true;

	// controls movement 
	MoveBehavior moveBehavior;
	// controls rotation 
	TurnBehavior turnBehavior;
	// controls reaction to level boundaries
	// maybe put this in entity ? 
	BoundsBehavior boundsBehavior;

	int timeUntilStart = 60;

	Enemy(PImage i, PVector pos){
		position = pos;
		velocity = new PVector();
		img = i;
	}		

	Enemy(PVector pos){
		position = pos;
		velocity = new PVector();
	}		

	Enemy(PImage i){
		this(i, new PVector(width/2, random(height/4)));
		velocity = new PVector();
	}		

	public void update(){
		if(timeUntilStart <= 0){
			tintAlpha = 255;
			nextPosTurn();
			if(moveBehavior != null) preformMove();
			if(turnBehavior != null) preformTurn();
			if(boundsBehavior != null) updateBounds();
		}else{
			timeUntilStart --;
			tintAlpha += 1- timeUntilStart/60;
		}
	}

	// override
	public void display(){
		if(!isExpired){
			noStroke();
			fill(fillColor);
			rect(position.x, position.y, radius,radius);
		}
	}

	public void preformMove(){
		//maybe should pass in a entity instead of just the position and velocity
		moveBehavior.move(position, velocity);				
	}

	public void updateBounds(){
		boundsBehavior.update(this);	
	}

	// default turn.based on next position
	public void nextPosTurn(){
		// PVector target = PVector.sub(PVector.add(position, velocity), position);
		// orientation = target.heading2D();	
	}

	public void preformTurn(){
		turnBehavior.turn(orientation);
	}

	public void setTurnBehavior(TurnBehavior tb){
		turnBehavior = tb;
	}

	public void setMoveBehavior(MoveBehavior mb){
		moveBehavior = mb;
	}

	public void setBoundsBehavior(BoundsBehavior b){
		boundsBehavior = b;
	}

	public void wasShot(){
		isExpired = true;	
	}

	// this take too long check online
	public boolean isCollided(PVector pos){
		if(dist(pos.x, pos.y, this.position.x, this.position.y) < radius){
			return true;
		}
		return false;
	}

	// replace with NOC vehicle seration code
	
    

}// end of enemy class

interface MoveBehavior {
	// might require refactoring
	public void move(PVector position, PVector velocity);
}
interface TurnBehavior{
	// updates orientation
	// void setOrientation(float orientation);
	public void turn(float orientation);
}
interface BoundsBehavior{
	// mods position 
	public void update(Entity e);
}
class EnemySpawner{
	float spawnChance = 15;
	float spawnChanceLimit = 35;

	float seekerSpawnChance = 20;
	float wandererSpawnChance = 20;
	int currentLevel =1;
	boolean endGame = false;

	public void update(){
		if(entityManager.enemyCount() < 30){
			{
				playLevel(currentLevel);
			}
				
		}
	
	}

	public void playLevel(int currentLevel){
		switch(currentLevel){
	    	case 1:
	    			entityManager.add( new WandererEnemy());
	      		break;
	      	case 2:
	    			addStriaghtMove();	
	      		break;
	      	case 3:
	    			entityManager.add( new SeekerEnemy());
	      		break;
	      	case 4:
	    			addPointMover();	
	      		break;
	      	case 5:
	    			addPulseSeeker();
	      		break;
	      	case 6:
	    			addCircleMove();	
	      		break;
	      	case 7:
	    			addRandomWarpEnemy();	
	      		break;
	      	case 8:
	      			player.nextLevelKills = 70;
	    			entityManager.add( new WandererEnemy());
	    			addStriaghtMove();	
	      		break;
	      	case 9:
	    			addStriaghtMove();
	    			addPointMover();
	      		break;
	      	case 10:
	    			addPulseSeeker();
	    			if(random(1.0f) < .55f) addCircleMove();
	      		break;
	      	case 11:
	    			if(random(1.0f) < .55f) addRandomWarpEnemy();
	    			if(random(1.0f) < .55f) addStriaghtMove();	
	    			addPointMover();
	      		break;
	      	case 12:
	    			addRandomWarpEnemy();	
	      		break;
	     	default :
	     		endGame = true;
	     	break;	
	    }
	}

	public void addStriaghtMove(){
			Enemy b = new BasicEnemy(new StraightMove(1.0f));
			b.velocity = randomVector(TWO_PI);
			float rVelScale = random(1,4);
			b.velocity.mult(rVelScale);
			b.radius = rVelScale * 5;
			b.fillColor = colors[6];
			b.setBoundsBehavior( new BounceEdges(new PVector(width, height)));
			entityManager.add(b);
	}

	public void addRandomWarpEnemy(){
		Enemy b1 = new RandomWarpEnemy(500, random(30, 10));
		b1.radius = 20;
		b1.fillColor = colors[4];
		//b1.setBoundsBehavior(new WarpEdges(new PVector(width,height),1));
		b1.setBoundsBehavior(new BounceEdges(new PVector(width, height)));
		entityManager.add(b1);
	}

	public void addCircleMove(){
		float amp = random(2,10);
		//CircleMove(float _freq, float amp){
		Enemy b1 = new BasicEnemy(new CircleMove(random(0.08f,0.15f), amp));
		b1.radius = 3*amp;
		b1.fillColor = colors[8];
		b1.setBoundsBehavior(new WarpEdges(new PVector(width,height),b1.radius + 2));
		//b1.setBoundsBehavior(new BounceEdges(new PVector(width, height)));
		entityManager.add(b1);
	}

	public void addPulseSeeker(){
		float moveTime = random(200, 2000);
		Entity b = new PulseSeeker(moveTime,random(20,100));
		float bRadius= map(moveTime, 200,2000, 5, 20);
		b.radius = bRadius;
		b.fillColor = 255;
		entityManager.add(b );
	}

	public void addPointMover(){
			float speed = random(3,9);
			Enemy b1 = new BasicEnemy(new PointMove(random(1000,2000), speed));
			b1.radius = 100/speed;
			b1.setBoundsBehavior(new WarpEdges(new PVector(width,height),1));
			b1.fillColor = 0xff91B8FF;
			//b1.setBoundsBehavior(new BounceEdges(new PVector(width, height)));
			entityManager.add(b1);
	}

}//
abstract class Entity {
	PImage img;

	float tintAlpha = 20;
	float tintColor = 255;

	int fillColor = 255;

	PVector position, velocity;
	float orientation;

	float radius = 10;

	boolean isExpired = false;

	public PVector getSize() { return img == null ? new PVector(0,0) : new PVector(img.width,img.height); }

	public abstract void update();

	public void display(){

	}

	// fix
	public boolean colidesWith(PVector pos){
    //	return withinBoundingBox(pos, this.position, new PVector(this.radius, this.radius));
    	return withinBoundingBox(this.position, pos, new PVector(radius, radius));
  	}

}
class EntityManager{
	//TODO make lists static and reinitilize objects on death 
	ArrayList<Entity> entities = new ArrayList<Entity>();
	ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();

	boolean isUpdating;
	// this might be over kill 
	ArrayList<Entity> addedEntities = new ArrayList<Entity>();

	public int count(){
		return entities.size();
	}
	
	public int enemyCount(){
		return enemies.size();
	}

	public void killAllEnemies(){
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if(e.isExpired != true){
				if(e instanceof Enemy){
				e.isExpired = true;	
				}
			}
		}
	}

	public void add(Entity entity){
		if(!isUpdating){
			addEntity(entity);
		}else{
			addedEntities.add(entity);
		}
	}

	public void addEntity(Entity entity){
		entities.add(entity);
		//adding to the bullet list doesn't work for some reason
		// is a problem with instance of 
		if(entity instanceof Bullet){
			bullets.add((Bullet)entity);
		} 
		if(entity instanceof Enemy){
			enemies.add((Enemy)entity);
		}
	}

	public void update(){
		isUpdating = true;
		for (Entity e : entities) {
			e.update();
		}

		isUpdating = false;

		for (Entity e : addedEntities) {
			// use entity manager add NOT entities.add
			add(e);
		}

		addedEntities.clear();

		for(int i = entities.size() -1; i >= 0; i--){
			Entity e = entities.get(i);
			if(e.isExpired){
				entities.remove(e);
				if(e instanceof Bullet){
					bullets.remove((Bullet)e);	
				}
				if(e instanceof Enemy){
					enemies.remove((Enemy)e);
					ps.enemyExplode(e.position.x, e.position.y, 100, e.fillColor);
				}
			}
		}
		handleCollisions();

	}
	//seperates the enemies
	public void handleCollisions(){
		for(Enemy e: enemies){
			if(e.isCollided(player.position)){
				e.isExpired = true;
				  soundManager.playSound("mame_explode1");
				PVector heading = PVector.sub(e.velocity,player.velocity);
				ps.hitPlayerExplode(player.position.x, player.position.y, heading);
				player.lives--;
				break;
			}
			bulletCollision(e);
		}
	}

	public void bulletCollision(Enemy e){
		for(int i =0;i < bullets.size(); i++){
			Bullet b = bullets.get(i);
				if(e.isCollided(b.position)){
					e.isExpired = true;
					player.killCount+=1;
					hud.addPoints(2);
			}
		}
	}

	public void display(){
		for (Entity e : entities) {
			e.display();
		}
	}
}//
class Hud{
	int hudWidth, hudHeight;
	int fontSize= 20;

	int multiplierExpiryTime =2000;
	int maxMultiplier = 25;

	int lives; 
	int score;
	int multiplier;
	int currentLevel = 1;
	float multiplierActivationTime;
	int scoreForExtraLife;

	PFont guiFont;
	String livesText;
	String scoreText;
	String multiplierText;
	String currentLevelText;

	String gameOverText;

	Hud(){
		hudWidth = width;
		hudHeight = height;
		//setup text()
		reset();	
		gameOverText = "Game Over";
		guiFont = createFont("Anonymous Pro Minus", fontSize);
		textFont(guiFont);
	}



	public void update(){
		if(multiplier > 1){
			if(millis() - multiplierActivationTime > multiplierExpiryTime){
				multiplier = 1;
				multiplierActivationTime = millis();
				
			}
		}
		updateHUD();
	}

	public void startScreen(){
		text("Week 1 Game: \n press space or click to start", width/2, height/2 - 200);
	}

	public void updateHUD(){
		fill(255);
		// change for js 
		livesText = "Lives: " + player.lives;
		//text(frameRate, 40,40);
		text(livesText, 30, hudHeight - 30, 0);
		scoreText= "Score: " +score; 
		text(scoreText, hudWidth - 150, hudHeight - 30, 0);

		multiplierText = "Multiplier: " + multiplier;
		text(multiplierText, hudWidth - 150, hudHeight - 80, 0);
		currentLevelText ="Level :" + enemySpawner.currentLevel;
		text(currentLevelText, hudWidth - 150, hudHeight - 100, 0);
	}
	public void addPoints(int basePoints){
		score += basePoints * multiplier;
		if(score >= scoreForExtraLife){
			scoreForExtraLife +=2000;
			player.lives++;
		}	
		increaseMultiplier();
        updateHUD();
	}

	public void increaseMultiplier(){
		multiplierActivationTime = millis();
		if(multiplier < maxMultiplier){
			multiplier++;	
		}
	}
	public void reset() {
		score = 0;
		multiplier =1;
		lives = 4;

		multiplierActivationTime = millis();
		scoreForExtraLife = 2000;
		updateHUD();	
	}

	public void endGame(){
		fill(255);
		textSize(32);
		text("YOU WIN ", width/2, height/2);
		scoreText = "YourScore: " + score;
		text(scoreText, 0, 50,0);
	}

}
class Keyboard {
  Boolean holdingUp,holdingRight,holdingLeft,holdingDown,holdingZ,
  holdingW,holdingA,holdingS,holdingD,holdingM;
  
  Keyboard() {
    holdingUp=holdingRight=holdingLeft=holdingDown=holdingZ=holdingW=holdingA=holdingS=holdingD=holdingM=false;
  }

  public void pressKey(int key,int keyCode) {
    if (keyCode == UP) {
      holdingUp = true;
    }
    if (keyCode == LEFT) {
      holdingLeft = true;
    }
    if (keyCode == RIGHT) {
      holdingRight = true;
    }
    if (keyCode == DOWN) {
      holdingDown = true;
    }
    if (key == 'z' || key == 'Z') {
      holdingZ = true;      
    }
    if (key == 'w' || key == 'W') {
      holdingW = true;      
    }
    if (key == 'a' || key == 'A') {
      holdingA = true;      
    }
    if (key == 's' || key == 'S') {
      holdingS = true;      
    }
    if (key == 'd' || key == 'D') {
      holdingD = true;      
    }
    if (key == 'm' || key == 'M') {
      holdingM = true;      
    }
   
    /* // reminder: for keys with letters, check "key"
       // instead of "keyCode" !
    if (key == 'r') {
      // reset program?
    }
    if (key == ' ') {
      holdingSpace = true;
    }*/
  }
  public void releaseKey(int key,int keyCode) {
    if(key == 'z') {
      holdingZ = false;
    }
    if (keyCode == UP) {
      holdingUp = false;
    }
    if (keyCode == LEFT) {
      holdingLeft = false;
    }
    if (keyCode == RIGHT) {
      holdingRight = false;
    }
    if (keyCode == DOWN) {
      holdingDown = false;
    }
    if (key == 'z' || key == 'Z') {
      holdingZ = false;      
    }
    if (key == 'w' || key == 'W') {
      holdingW = false;      
    }
    if (key == 'a' || key == 'A') {
      holdingA = false;      
    }
    if (key == 's' || key == 'S') {
      holdingS = false;      
    }
    if (key == 'd' || key == 'D') {
      holdingD = false;      
    }
    if (key == 'm' || key == 'M') {
      holdingD = false;      
    }
  }
}
class Palettes {
	ArrayList<int[]> colors; // array of colors per index of arraylist
	int currentPalette = 0;

	public Palettes() {
       colors = new ArrayList<int[]>();
    colors.add(colorsFromList(0xffF2B774, 0xffFB6084, 0xffFB0029, 0xff391E21, 0xff56528E, 0xffA197C2));
    colors.add(colorsFromList(0xff597A74, 0xff6D0D00, 0xffA5BB3C, 0xff3E514C, 0xffDF212F, 0xff1A3419));
    colors.add(colorsFromList(0xffD7E1F0, 0xff263165, 0xff56B3A9, 0xff4674C1, 0xff2F565C, 0xffCCC5BA));
    colors.add(colorsFromList(0xffEFF0D8, 0xffEFF0D8, 0xffEFF0D8, 0xffEFF0D8, 0xff0A0831, 0xffA7A6C3));
    colors.add(colorsFromList(0xff313031, 0xff313031, 0xffD2FFDA, 0xff47464C, 0xff7195A8, 0xffA09CAB));
    colors.add(colorsFromList(0xffAFF470, 0xff4B4747, 0xffFC6F44, 0xff45B57E, 0xffF10A36, 0xffF2D863));
    colors.add(colorsFromList(0xffEDEEBE, 0xffEC7A0B, 0xff776573, 0xffEECC63, 0xffD45800, 0xffD2CAD1));
    colors.add(colorsFromList(0xff2C575C, 0xff202751, 0xffA952B8, 0xff16262E, 0xff5A1984, 0xffE6DDC0));
    colors.add(colorsFromList(0xff474447, 0xff605A5F, 0xffCCC8C9, 0xff1C171B, 0xff878282, 0xffE0E0E0));
    colors.add(colorsFromList(0xffA8E3E3, 0xff315D60, 0xff6C5064, 0xff4DB3AC, 0xff1B1F1F, 0xffC98F6C));
	}

	public void nextColor() {
		currentPalette = (currentPalette < colors.size() -1 ? currentPalette + 1 : 0);
		//add 1 to collor Palette until its the size fo the array used to add diffrent color to everyparticle
	}

	//adds colors to the color array
	public int[] colorsFromList(int c1, int c2, int c3, int c4, int c5, int c6) {
    int[] colorList = {
      c1, c2, c3, c4, c5, c6
    };
    return colorList;
  }
}
// Simple Particle System
//From based off the nature of code
class Particle {

  PVector loc;
  PVector vel;
  PVector acc;
  float lifespan = 355;
  float offset = .5f;
  int c = color(random(122,222), 125,255);
  float strokeSize = random(.1f,2.1f)*2;

	  Particle(float x, float y) {
      acc = new PVector(0, 0);
      vel = new PVector(random(-1,1),random(-1,1));
      loc = new PVector(x, y);
      lifespan = 300;
      offset = random(1, 4);

	  }
   
  public void run() {
    update();
    render();
  }

  public void applyForce(PVector f) {
    acc.add(f);
  }

  // Method to update location
  public void update() {

  	 if (loc.x < 0) {vel.x= abs(vel.x);
        } else if (loc.x > width) {vel.x = -abs(vel.x);}
        if (loc.z < 0) {vel.y = abs(vel.y);
        } else if (loc.y > height) {vel.y = -abs(vel.y);}
       if(loc.y < 0) {vel.y = abs(vel.y);}

       // denormalized floats cause significant performance issues
    if (abs(vel.x) + abs(vel.y) < 0.00000000001f){
      vel.mult(0);
    } 
        
    vel.add(acc);
    loc.add(vel);
    acc.mult(0);
    lifespan -= 2.0f;
  }

 // Method to display
   public void render() {
    if(lifespan > 0){
      float a = 255;
        // fade out when lifespan is less than 50
        if(lifespan < 80) {
          a = map(lifespan, 0, 80, 0, 255);
        }
        offset = map(lifespan, 0, 255, 0, 2);
        
        fill(c,a);
        rect(loc.x, loc.y, 3 + offset,3 + offset);
    }
  }

  public void die(){
      lifespan = 0;
  }

  // Is the particle still useful?
  public boolean isDead() {
    if (lifespan <= 0.0f) {
      return true;
    } 
    else {
      return false;
    }
  }
}// 
// A class to describe a group of Particles
// An ArrayList is used to manage the list of Particles 

class ParticleSystem {


  ArrayList<Particle> particles;   
  int c1 = color(204, 153, 0);
 

  ParticleSystem() {
    particles = new ArrayList(); 
  }
  public void run() {
      for (int i = particles.size()-1; i >= 0; i--) {
          Particle p = particles.get(i);
          p.run();
          if (p.isDead()) {
            particles.remove(i);
          }
      }
  }

  public void addParticle(float x, float y) {
    particles.add(new Particle(x,y));
  }

  public void applyForce(PVector f) {
    for (int i = 0; i < particles.size(); ++i) {
      Particle p = particles.get(i);
      p.applyForce(f);
    }
  }

  //(float x, float y, PVector _vel,float lifespan, float size, color _c)
  public void enemyExplode(float x, float y, float lifespan, int fillColor){
     for(int i = 0; i < 10; i++){
     //   Particle p = new Particle(x,y,explodeVec,c, random(0.3,0.6));
     int c = color(fillColor);
        Particle p = new Particle(x,y);
        p.lifespan = lifespan;
        p.vel.mult(random(6,8));
        p.c = fillColor;
        particles.add(p);
      } 
  }

  public void hitPlayerExplode(float x, float y, PVector heading){
    // heading is the enemies velocity on death
    for (int i = 0; i < 40; i++) {
        Particle p = new Particle(x,y);
        p.lifespan = 250;
        p.c = 255;
        //p.vel = heading;
        p.vel = PVector.add(randomVector(.2f), heading);
        //p.vel.mult(random(.3,.4));
        //p.vel.limit(20);
        particles.add(p);       
    } 
  }

  public void bulletExplode(float x, float y){
      for(int i = 0; i < 5; i++){
        // color the particles white 25% of the time
        int c = random(1.0f) > .25f ? color(random(120,150), 255,255): 255;
        Particle p = new Particle(x,y);
        p.lifespan = 250;
        p.c = c;
        particles.add(p);
      }
  }

  // A method to test if the particle system still has particles
  public boolean dead() {
    if (particles.isEmpty()) {
      return true;
    } 
    else {
      return false;
    }
  }
  
}
class Player extends Entity {

  float buffer = 5;
  PVector mousePoint = new PVector(2,2);
  float shootTime = 0;
  float fireInterval = 100;
  float lastFire;
  int killCount = 0;
  int frameUntilRespawn = 0;

  int nextLevelKills = 50;

  PVector shot_vel;
  float speed = .4f;

  int lives = 10;

  Player() {
    position = new PVector(width/2, height/2);
    velocity = new PVector(1, 0);
    radius = 10;
    shot_vel = new PVector();
    fillColor = 255;
  }

  public boolean isDead() {
    return frameUntilRespawn > 0;
  }

  public void kill() {
    frameUntilRespawn=120;
  }

  // override
  public void update() {
    if(position.x > width+ buffer) position.x = -buffer;
    else if(position.x <     - buffer) position.x = width + buffer;
    if(position.y > height + buffer) position.y = -buffer;
    else if(position.y <     - buffer) position.y = height+ buffer;

    if(killCount >= nextLevelKills){
      killCount =0;
      entityManager.killAllEnemies();
      enemySpawner.currentLevel++;
    }
    // shot_vel is vector pointing towards mouse postion
  
    if (!isDead()) {
      display();
    }

      velocity.limit(5);
     position.add(velocity);
     inputCheck();
    if (millis() - lastFire > fireInterval) {
      if (mousePressed) {
          int shotFill =color(255);
           if (millis() - shootTime > 300){
            soundManager.playSound("mame_fire");
            shootTime = millis();
            shotFill =color(random(120,190), 200,255);
          }
          shot_vel = PVector.sub(new PVector(mouseX, mouseY), position);
          Bullet b2 = new Bullet(position, shot_vel, shotFill);

          entityManager.add(b2);
          lastFire = millis();
      } 
    
    }
  }

  //@override
  public void display() {
    fill(fillColor);
    rect(position.x, position.y, radius, radius);
  }

  public void inputCheck() {
    //arrows input check
    if (keyboard.holdingLeft) {
      velocity.x -= speed;
    } 
    else if ( keyboard.holdingRight ) {
      velocity.x += speed;
    }
    if (keyboard.holdingUp) {
      velocity.y -= speed;
    }
    else if (keyboard.holdingDown) {
      velocity.y += speed;
    }
    // wasd input check
    if (keyboard.holdingA) {
      velocity.x -= speed;
    } 
    else if ( keyboard.holdingD ) {
      velocity.x += speed;
    }
    if (keyboard.holdingW) {
      velocity.y -= speed;
    }
    else if (keyboard.holdingS) {
      velocity.y += speed;
    }
  }
}// end of player class

class BasicEnemy extends Enemy{
	BasicEnemy(MoveBehavior m){
		super(seekerImage, new PVector(random(width), random(height)));
		moveBehavior = m;
	}	

}//end of Basic enemy class

class SeekerEnemy extends Enemy {
	SeekerEnemy(){
		super(seekerImage, new PVector(random(width), random(height)));
		moveBehavior = new SeekMovement(1.5f);
		radius = 15;
		fillColor = 0xffffeeaa;
	}
}// end of SeekerEnemy

class PulseSeeker extends Enemy {
	// time of the pluse.
	float moveTime = 0;
	float lastMove = 0;
	float moveTimeOffset = 0;
	// should remove movetime offset set. its kinda of dedundant 
	PulseSeeker(float _moveTime, float _moveTimeOffset){
		super(seekerImage, new PVector(random(width), random(height)));
		moveBehavior = new SeekMovement(50);
		this.moveTime = _moveTime;
		this.moveTimeOffset = _moveTimeOffset;
	}

	//@override
	public void preformMove(){
		if(millis() - lastMove - moveTimeOffset > moveTime){
			fillColor= 0xffffffff;
			moveBehavior.move(position, velocity);				
			//moveBehavior.move(position, PVector.mult(velocity,10));				
			lastMove = millis();
		}else{
			fillColor= 0xffF2B774;
		}
	}
}//end of Pulse seeker class

class RandomWarpEnemy extends Enemy{
	float moveTime = 0;
	float lastMove = 0;
	float moveTimeOffset = 0;

	RandomWarpEnemy(float _moveTime, float _moveTimeOffset){
		super(seekerImage, new PVector(random(width), random(height)));
		moveBehavior = new RandomMove();
		this.moveTime = _moveTime;
		this.moveTimeOffset = _moveTimeOffset;
	}

	public void preformMove(){

		if(millis() - lastMove - moveTimeOffset > moveTime){
			position = new PVector(random(width), random(height));
			//moveBehavior.move(position, PVector.mult(velocity,10));				
			lastMove = millis();
		}else if(millis() - lastMove - moveTimeOffset > moveTime + 20){
			fill(255);
		}
		moveBehavior.move(position, velocity);				

	}
}// end of RandomWarpEnemy class

class WandererEnemy extends Enemy {
	WandererEnemy(){
		// image , position
		super(wandererImage, new PVector(random(width), random(height)));
		float range = random(PI, TWO_PI);
		moveBehavior = new RandomMove(random(300, 600), range);
		boundsBehavior = new WarpEdges(new PVector(width, height), radius);
		radius = range * 3;
		fillColor = 0xff718BF3;
	}

	public void display(){
		if(!isExpired){
			noStroke();
			fill(fillColor);
			ellipse(position.x, position.y, radius,radius);
		}
	}
}// end of Wanderer class

class StraightMove implements MoveBehavior {
	float direction;
	StraightMove(float startDirection){
		direction = startDirection;

	}

	public void move(PVector position, PVector velocity) {
		position.add(velocity);
	}

}// end of StraightMove class

class RandomMove implements MoveBehavior{
	float moveTime = 300;
	float lastMove = 0;
	float direction = 0;

	float directionRange = TWO_PI;
	RandomMove(){}

	RandomMove(float moveTime){
		this.moveTime = moveTime;
	}

	RandomMove(float moveTime, float directionRange){
		this.moveTime = moveTime;
		this.directionRange = directionRange;	
	}

	//@override
	public void move(PVector position, PVector velocity){
		if(millis() - lastMove > moveTime){
			direction = random(-directionRange,directionRange);
			direction += random(-0.1f, 0.1f);
			lastMove = millis();
		}	
		PVector target = new PVector(cos(direction), sin(direction));
		velocity.add(target); 
		velocity.limit(3.5f);
		position.add(velocity);
	}
}//end of Random Move

class SeekMovement implements MoveBehavior {
	float topLimit = 0;
	SeekMovement(float _topLimit){
		topLimit = _topLimit;
	}
	//@override
	public void move(PVector position, PVector velocity){
		PVector target = PVector.sub(player.position, position);
		if(target.mag() > 10) target.mult(.2f);
		velocity.add(target); 
		velocity.limit(topLimit);
		position.add(velocity);
	}
}// end of seekMovement class

class CircleMove implements MoveBehavior{
	float theta = -TWO_PI;;
	// right variable names?
	float amp = 0;
	float freq = 0;

	CircleMove(float _freq,float _amp){
		amp = _amp;
		freq = _freq;
	}

	public void move(PVector position, PVector velocity){
		//theta = theta < TWO_PI ? theta+=freq : -TWO_PI;
		theta+=freq;
		//float o1 = map(theta,-TWO_PI, 1000, 0, amp);
		float o1 = amp;
		float x = o1*cos(theta);
		float y = o1*sin(theta);
		// float x = cos(theta);
		// float y = sin(theta);		
		PVector target = new PVector(x,y);
		// position.x +=x;
		// position.y +=y;
		velocity.add(target); 
		velocity.limit(amp);
		position.add(velocity);	
		velocity.mult(0);
	} 

}// end of CircleMove class

//updates only x or y velocity randomly 
class PointMove implements MoveBehavior{

	float moveTime = 2000;
	float lastMove = 0;
	float speed = 0;
	PVector target = new PVector(0,0);

	PointMove(float _moveTime, float _speed){
		moveTime = _moveTime;
		speed = _speed;	
	}

	//@override
	public void move(PVector position, PVector velocity){
		if(millis() - lastMove > moveTime){
			lastMove = millis();
			float rPoint = random(-speed,speed);
			target = random(1.0f) < .5f ? new PVector(rPoint,0) : new PVector(0,rPoint);
		}
		velocity.set(target); 
		velocity.limit(50);
		position.add(velocity);
	}
}// end of PointMove

class WarpEdges implements BoundsBehavior {
	PVector bounds; // bounds for the box normally width and height
	float buffer;// offset for bounds so position goes off screen before warping

	WarpEdges(PVector bounds, float buffer){
		this.bounds = bounds;
		this.buffer = buffer;
	}
		
	//@override
	public void update(Entity e){
		if(e.position == null) return;
		if(e.position.x > bounds.x + buffer) e.position.x = -buffer;
		else if(e.position.x <     - buffer) e.position.x = bounds.x + buffer;
		if(e.position.y > bounds.y + buffer) e.position.y = -buffer;
		else if(e.position.y <     - buffer) e.position.y = bounds.y + buffer;
	}	
}// end of WarpEdges class

class BounceEdges implements BoundsBehavior {
	PVector bounds; // bounds for the box normally width and height
	float buffer;// offset for bounds so position goes off screen before warping

		BounceEdges(PVector bounds){
			this.bounds = bounds;
			buffer = 20;
		}



	public void update(Entity e){
		if(e.position == null) return;
		if(e.position.x > bounds.x + buffer) e.velocity.x *= -1;
		else if(e.position.x <      0 + buffer) e.velocity.x *= -1;
		if(e.position.y > bounds.y + buffer) e.velocity.y *= -1;
		else if(e.position.y <    0 + buffer) e.velocity.y *= -1;
	}
}//end of bounce edges class
class SoundManager{
	boolean muted = false;
	PApplet sketch;
	Minim minim;

	ArrayList<PlayerQueue> queuedSounds;
	ArrayList <String> queuedSoundNames;

	private class PlayerQueue{
		// holds refeence to minim player
		private	ArrayList <AudioPlayer> players;
		private String path;

		public PlayerQueue(String audioPath){
			path = audioPath;
			//println(audioPath);
			players = new ArrayList<AudioPlayer>();
			// load minim player in
			appendPlayer();
		}
		
		private void appendPlayer(){
      		AudioPlayer player = minim.loadFile(path);
      		players.add(player);
    	}

		public void close(){
			for (int i = 0; i < players.size(); ++i) {
				players.get(i).close();	
			}
		}

		public void play(){
			int freePlayerIndex = -1;
			for (int i = 0; i < players.size(); ++i) {
				// set index at the last player thats not playing
				if(players.get(i).isPlaying() == false){
					freePlayerIndex = i ;
					break;
				}	
			}

			// set index at last shot in the list
			if(freePlayerIndex == -1){
				appendPlayer();
				freePlayerIndex = players.size()-1;
			}

			players.get(freePlayerIndex).play();
			// set player play back to the beginning
			players.get(freePlayerIndex).rewind();
		}

		public void setMute(boolean m){
		      for(int i = 0; i < players.size(); i++){
		        if(m){
		          players.get(i).mute();
		        }
		        else{
		          players.get(i).unmute(); 
		        }
		      }
    	}
	} // end of player queue class	

	SoundManager(PApplet _sketch){
		sketch =  _sketch;
		// we pass this to Minim so that it can load files from the data directory
		minim = new Minim(_sketch);
		//console.log(minim);
		queuedSounds = new ArrayList<PlayerQueue>();
		queuedSoundNames = new ArrayList<String>();
	}

	public void setMute(boolean isMuted){
		muted = isMuted;

		for (int i = 0; i < queuedSounds.size(); ++i) {
			queuedSounds.get(i).setMute(muted);	
		}
	}

	public boolean isMute(){
		return muted;
	}

	public void mute(){
		// empty funtion to corespond to the javascript function 
		// change later
	}

	public void unmute(){
		// empty funtion to corespond to the javascript function 
		// change later
	}

	public void addSound(String soundName){
		queuedSounds.add(new PlayerQueue("audio/" + soundName + ".wav"));
		queuedSoundNames.add(soundName);	
	}

	public void playSound(String soundName){
		// don't play is muted
		if(muted){
			return;
		}	

		int index = -1;
		// search sound based on string name
		for (int i = 0; i < queuedSoundNames.size(); i++) {
			if(soundName.equals(queuedSoundNames.get(i))){
				index = i;
				break;
			}	
		}
		// if the sound is found play to sound
		if(index != -1){
			queuedSounds.get(index).play();
		}
	}

	public void stop(){
		// closes all the players and minim
		for (int i = 0; i < queuedSounds.size(); ++i) {
			queuedSounds.get(i).close();	
		}
		minim.stop();
	}

}// end of sound manager class
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "week1Game" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

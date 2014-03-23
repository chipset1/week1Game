 /* @pjs font="Anonymous Pro Minus.ttf"; */
import ddf.minim.*;

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
color[] colors = {#F2B774, #FB6084, #FB0029,#A197C2,#597A74, #6D0D00, #C371F3, #1A3419,#878282, #E0E0E0};

// used for advancing to the next level;

SoundManager soundManager;

void setup() {
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

void draw() {
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
void scanLinePostProcess(){
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


boolean withinBoundingBox(PVector point, PVector box_center, PVector box_size) {
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

void mousePressed(){
      
}

void keyPressed(){
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

void keyReleased(){
  keyboard.releaseKey(key, keyCode);
}

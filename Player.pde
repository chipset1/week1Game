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
  float speed = .4;

  int lives = 10;

  Player() {
    position = new PVector(width/2, height/2);
    velocity = new PVector(1, 0);
    radius = 10;
    shot_vel = new PVector();
    fillColor = 255;
  }

  boolean isDead() {
    return frameUntilRespawn > 0;
  }

  void kill() {
    frameUntilRespawn=120;
  }

  // override
  void update() {
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
          color shotFill =color(255);
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
  void display() {
    fill(fillColor);
    rect(position.x, position.y, radius, radius);
  }

  void inputCheck() {
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


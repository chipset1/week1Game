// A class to describe a group of Particles
// An ArrayList is used to manage the list of Particles 

class ParticleSystem {


  ArrayList<Particle> particles;   
  color c1 = color(204, 153, 0);
 

  ParticleSystem() {
    particles = new ArrayList(); 
  }
  void run() {
      for (int i = particles.size()-1; i >= 0; i--) {
          Particle p = particles.get(i);
          p.run();
          if (p.isDead()) {
            particles.remove(i);
          }
      }
  }

  void addParticle(float x, float y) {
    particles.add(new Particle(x,y));
  }

  void applyForce(PVector f) {
    for (int i = 0; i < particles.size(); ++i) {
      Particle p = particles.get(i);
      p.applyForce(f);
    }
  }

  //(float x, float y, PVector _vel,float lifespan, float size, color _c)
  void enemyExplode(float x, float y, float lifespan, int fillColor){
     for(int i = 0; i < 10; i++){
     //   Particle p = new Particle(x,y,explodeVec,c, random(0.3,0.6));
     color c = color(fillColor);
        Particle p = new Particle(x,y);
        p.lifespan = lifespan;
        p.vel.mult(random(6,8));
        p.c = fillColor;
        particles.add(p);
      } 
  }

  void hitPlayerExplode(float x, float y, PVector heading){
    // heading is the enemies velocity on death
    for (int i = 0; i < 40; i++) {
        Particle p = new Particle(x,y);
        p.lifespan = 250;
        p.c = 255;
        //p.vel = heading;
        p.vel = PVector.add(randomVector(.2), heading);
        //p.vel.mult(random(.3,.4));
        //p.vel.limit(20);
        particles.add(p);       
    } 
  }

  void bulletExplode(float x, float y){
      for(int i = 0; i < 5; i++){
        // color the particles white 25% of the time
        color c = random(1.0) > .25 ? color(random(120,150), 255,255): 255;
        Particle p = new Particle(x,y);
        p.lifespan = 250;
        p.c = c;
        particles.add(p);
      }
  }

  // A method to test if the particle system still has particles
  boolean dead() {
    if (particles.isEmpty()) {
      return true;
    } 
    else {
      return false;
    }
  }
  
}

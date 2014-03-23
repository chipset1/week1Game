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

	abstract void update();

	void display(){

	}

	// fix
	boolean colidesWith(PVector pos){
    //	return withinBoundingBox(pos, this.position, new PVector(this.radius, this.radius));
    	return withinBoundingBox(this.position, pos, new PVector(radius, radius));
  	}

}

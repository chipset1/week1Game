class Bullet extends Entity
{
	// not sure if the best way to do this
	boolean hitWall;
	color c;
	Bullet(PVector pos, PVector vel, color _c){
		hitWall = false;
		
		position = pos.get();
		velocity = vel.get();
		velocity.limit(10);
		radius = 6;
		c = _c;
	}

	void display(){
		fill(c);
		rect(position.x, position.y,radius,radius);
	}

	void update(){
		position.add(velocity);
		if(position.x <0 || position.x > width || position.y < 0 || position.y > height){
			ps.bulletExplode(position.x, position.y);
			hitWall = true;
			this.isExpired = true;
		}
	//	if(velocity.magSq() > 0) orientation = velocity.heading();
	}

	boolean hasHitWall(){
		return hitWall;
	}

}//end of Bullet class

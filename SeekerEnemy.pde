class BasicEnemy extends Enemy{
	BasicEnemy(MoveBehavior m){
		super(seekerImage, new PVector(random(width), random(height)));
		moveBehavior = m;
	}	

}//end of Basic enemy class

class SeekerEnemy extends Enemy {
	SeekerEnemy(){
		super(seekerImage, new PVector(random(width), random(height)));
		moveBehavior = new SeekMovement(1.5);
		radius = 15;
		fillColor = #ffeeaa;
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
	void preformMove(){
		if(millis() - lastMove - moveTimeOffset > moveTime){
			fillColor= #ffffff;
			moveBehavior.move(position, velocity);				
			//moveBehavior.move(position, PVector.mult(velocity,10));				
			lastMove = millis();
		}else{
			fillColor= #F2B774;
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

	void preformMove(){

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
		fillColor = #718BF3;
	}

	void display(){
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

	void move(PVector position, PVector velocity) {
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
	void move(PVector position, PVector velocity){
		if(millis() - lastMove > moveTime){
			direction = random(-directionRange,directionRange);
			direction += random(-0.1, 0.1);
			lastMove = millis();
		}	
		PVector target = new PVector(cos(direction), sin(direction));
		velocity.add(target); 
		velocity.limit(3.5);
		position.add(velocity);
	}
}//end of Random Move

class SeekMovement implements MoveBehavior {
	float topLimit = 0;
	SeekMovement(float _topLimit){
		topLimit = _topLimit;
	}
	//@override
	void move(PVector position, PVector velocity){
		PVector target = PVector.sub(player.position, position);
		if(target.mag() > 10) target.mult(.2);
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

	void move(PVector position, PVector velocity){
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
	void move(PVector position, PVector velocity){
		if(millis() - lastMove > moveTime){
			lastMove = millis();
			float rPoint = random(-speed,speed);
			target = random(1.0) < .5 ? new PVector(rPoint,0) : new PVector(0,rPoint);
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
	void update(Entity e){
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



	void update(Entity e){
		if(e.position == null) return;
		if(e.position.x > bounds.x + buffer) e.velocity.x *= -1;
		else if(e.position.x <      0 + buffer) e.velocity.x *= -1;
		if(e.position.y > bounds.y + buffer) e.velocity.y *= -1;
		else if(e.position.y <    0 + buffer) e.velocity.y *= -1;
	}
}//end of bounce edges class

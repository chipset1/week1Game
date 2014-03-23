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

	void update(){
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
	void display(){
		if(!isExpired){
			noStroke();
			fill(fillColor);
			rect(position.x, position.y, radius,radius);
		}
	}

	void preformMove(){
		//maybe should pass in a entity instead of just the position and velocity
		moveBehavior.move(position, velocity);				
	}

	void updateBounds(){
		boundsBehavior.update(this);	
	}

	// default turn.based on next position
	void nextPosTurn(){
		// PVector target = PVector.sub(PVector.add(position, velocity), position);
		// orientation = target.heading2D();	
	}

	void preformTurn(){
		turnBehavior.turn(orientation);
	}

	void setTurnBehavior(TurnBehavior tb){
		turnBehavior = tb;
	}

	void setMoveBehavior(MoveBehavior mb){
		moveBehavior = mb;
	}

	void setBoundsBehavior(BoundsBehavior b){
		boundsBehavior = b;
	}

	void wasShot(){
		isExpired = true;	
	}

	// this take too long check online
	boolean isCollided(PVector pos){
		if(dist(pos.x, pos.y, this.position.x, this.position.y) < radius){
			return true;
		}
		return false;
	}

	// replace with NOC vehicle seration code
	
    

}// end of enemy class

interface MoveBehavior {
	// might require refactoring
	void move(PVector position, PVector velocity);
}
interface TurnBehavior{
	// updates orientation
	// void setOrientation(float orientation);
	void turn(float orientation);
}
interface BoundsBehavior{
	// mods position 
	void update(Entity e);
}

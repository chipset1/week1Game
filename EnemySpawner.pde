class EnemySpawner{
	float spawnChance = 15;
	float spawnChanceLimit = 35;

	float seekerSpawnChance = 20;
	float wandererSpawnChance = 20;
	int currentLevel =1;
	boolean endGame = false;

	void update(){
		if(entityManager.enemyCount() < 30){
			{
				playLevel(currentLevel);
			}
				
		}
	
	}

	void playLevel(int currentLevel){
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
	      			player.nextLevelKills = 07;
	    			entityManager.add( new WandererEnemy());
	    			addStriaghtMove();	
	      		break;
	      	case 9:
	    			addStriaghtMove();
	    			addPointMover();
	      		break;
	      	case 10:
	    			addPulseSeeker();
	    			if(random(1.0) < .55) addCircleMove();
	      		break;
	      	case 11:
	    			if(random(1.0) < .55) addRandomWarpEnemy();
	    			if(random(1.0) < .55) addStriaghtMove();	
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

	void addStriaghtMove(){
			Enemy b = new BasicEnemy(new StraightMove(1.0));
			b.velocity = randomVector(TWO_PI);
			float rVelScale = random(1,4);
			b.velocity.mult(rVelScale);
			b.radius = rVelScale * 5;
			b.fillColor = colors[6];
			b.setBoundsBehavior( new BounceEdges(new PVector(width, height)));
			entityManager.add(b);
	}

	void addRandomWarpEnemy(){
		Enemy b1 = new RandomWarpEnemy(500, random(30, 10));
		b1.radius = 20;
		b1.fillColor = colors[4];
		//b1.setBoundsBehavior(new WarpEdges(new PVector(width,height),1));
		b1.setBoundsBehavior(new BounceEdges(new PVector(width, height)));
		entityManager.add(b1);
	}

	void addCircleMove(){
		float amp = random(2,10);
		//CircleMove(float _freq, float amp){
		Enemy b1 = new BasicEnemy(new CircleMove(random(0.08,0.15), amp));
		b1.radius = 3*amp;
		b1.fillColor = colors[8];
		b1.setBoundsBehavior(new WarpEdges(new PVector(width,height),b1.radius + 2));
		//b1.setBoundsBehavior(new BounceEdges(new PVector(width, height)));
		entityManager.add(b1);
	}

	void addPulseSeeker(){
		float moveTime = random(200, 2000);
		Entity b = new PulseSeeker(moveTime,random(20,100));
		float bRadius= map(moveTime, 200,2000, 5, 20);
		b.radius = bRadius;
		b.fillColor = 255;
		entityManager.add(b );
	}

	void addPointMover(){
			float speed = random(3,9);
			Enemy b1 = new BasicEnemy(new PointMove(random(1000,2000), speed));
			b1.radius = 100/speed;
			b1.setBoundsBehavior(new WarpEdges(new PVector(width,height),1));
			b1.fillColor = #91B8FF;
			//b1.setBoundsBehavior(new BounceEdges(new PVector(width, height)));
			entityManager.add(b1);
	}

}//

class EntityManager{
	//TODO make lists static and reinitilize objects on death 
	ArrayList<Entity> entities = new ArrayList<Entity>();
	ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();

	boolean isUpdating;
	// this might be over kill 
	ArrayList<Entity> addedEntities = new ArrayList<Entity>();

	int count(){
		return entities.size();
	}
	
	int enemyCount(){
		return enemies.size();
	}

	void killAllEnemies(){
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if(e.isExpired != true){
				if(e instanceof Enemy){
				e.isExpired = true;	
				}
			}
		}
	}

	void add(Entity entity){
		if(!isUpdating){
			addEntity(entity);
		}else{
			addedEntities.add(entity);
		}
	}

	void addEntity(Entity entity){
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

	void update(){
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
	void handleCollisions(){
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

	void bulletCollision(Enemy e){
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

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



	void update(){
		if(multiplier > 1){
			if(millis() - multiplierActivationTime > multiplierExpiryTime){
				multiplier = 1;
				multiplierActivationTime = millis();
				
			}
		}
		updateHUD();
	}

	void startScreen(){
		text("Week 1 Game: \n press space or click to start", width/2, height/2 - 200);
	}

	void updateHUD(){
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
	void addPoints(int basePoints){
		score += basePoints * multiplier;
		if(score >= scoreForExtraLife){
			scoreForExtraLife +=2000;
			player.lives++;
		}	
		increaseMultiplier();
        updateHUD();
	}

	void increaseMultiplier(){
		multiplierActivationTime = millis();
		if(multiplier < maxMultiplier){
			multiplier++;	
		}
	}
	void reset() {
		score = 0;
		multiplier =1;
		lives = 4;

		multiplierActivationTime = millis();
		scoreForExtraLife = 2000;
		updateHUD();	
	}

	void endGame(){
		fill(255);
		textSize(32);
		text("YOU WIN ", width/2, height/2);
		scoreText = "YourScore: " + score;
		text(scoreText, 0, 50,0);
	}

}
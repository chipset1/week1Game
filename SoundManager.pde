class SoundManager{
	boolean muted = false;
	PApplet sketch;
	Minim minim;

	ArrayList<PlayerQueue> queuedSounds;
	ArrayList <String> queuedSoundNames;

	private class PlayerQueue{
		// holds refeence to minim player
		private	ArrayList <AudioPlayer> players;
		private String path;

		public PlayerQueue(String audioPath){
			path = audioPath;
			//println(audioPath);
			players = new ArrayList<AudioPlayer>();
			// load minim player in
			appendPlayer();
		}
		
		private void appendPlayer(){
      		AudioPlayer player = minim.loadFile(path);
      		players.add(player);
    	}

		public void close(){
			for (int i = 0; i < players.size(); ++i) {
				players.get(i).close();	
			}
		}

		public void play(){
			int freePlayerIndex = -1;
			for (int i = 0; i < players.size(); ++i) {
				// set index at the last player thats not playing
				if(players.get(i).isPlaying() == false){
					freePlayerIndex = i ;
					break;
				}	
			}

			// set index at last shot in the list
			if(freePlayerIndex == -1){
				appendPlayer();
				freePlayerIndex = players.size()-1;
			}

			players.get(freePlayerIndex).play();
			// set player play back to the beginning
			players.get(freePlayerIndex).rewind();
		}

		public void setMute(boolean m){
		      for(int i = 0; i < players.size(); i++){
		        if(m){
		          players.get(i).mute();
		        }
		        else{
		          players.get(i).unmute(); 
		        }
		      }
    	}
	} // end of player queue class	

	SoundManager(PApplet _sketch){
		sketch =  _sketch;
		// we pass this to Minim so that it can load files from the data directory
		minim = new Minim(_sketch);
		//console.log(minim);
		queuedSounds = new ArrayList<PlayerQueue>();
		queuedSoundNames = new ArrayList<String>();
	}

	void setMute(boolean isMuted){
		muted = isMuted;

		for (int i = 0; i < queuedSounds.size(); ++i) {
			queuedSounds.get(i).setMute(muted);	
		}
	}

	boolean isMute(){
		return muted;
	}

	void mute(){
		// empty funtion to corespond to the javascript function 
		// change later
	}

	void unmute(){
		// empty funtion to corespond to the javascript function 
		// change later
	}

	void addSound(String soundName){
		queuedSounds.add(new PlayerQueue("audio/" + soundName + ".wav"));
		queuedSoundNames.add(soundName);	
	}

	void playSound(String soundName){
		// don't play is muted
		if(muted){
			return;
		}	

		int index = -1;
		// search sound based on string name
		for (int i = 0; i < queuedSoundNames.size(); i++) {
			if(soundName.equals(queuedSoundNames.get(i))){
				index = i;
				break;
			}	
		}
		// if the sound is found play to sound
		if(index != -1){
			queuedSounds.get(index).play();
		}
	}

	void stop(){
		// closes all the players and minim
		for (int i = 0; i < queuedSounds.size(); ++i) {
			queuedSounds.get(i).close();	
		}
		minim.stop();
	}

}// end of sound manager class
class Keyboard {
  Boolean holdingUp,holdingRight,holdingLeft,holdingDown,holdingZ,
  holdingW,holdingA,holdingS,holdingD,holdingM;
  
  Keyboard() {
    holdingUp=holdingRight=holdingLeft=holdingDown=holdingZ=holdingW=holdingA=holdingS=holdingD=holdingM=false;
  }

  void pressKey(int key,int keyCode) {
    if (keyCode == UP) {
      holdingUp = true;
    }
    if (keyCode == LEFT) {
      holdingLeft = true;
    }
    if (keyCode == RIGHT) {
      holdingRight = true;
    }
    if (keyCode == DOWN) {
      holdingDown = true;
    }
    if (key == 'z' || key == 'Z') {
      holdingZ = true;      
    }
    if (key == 'w' || key == 'W') {
      holdingW = true;      
    }
    if (key == 'a' || key == 'A') {
      holdingA = true;      
    }
    if (key == 's' || key == 'S') {
      holdingS = true;      
    }
    if (key == 'd' || key == 'D') {
      holdingD = true;      
    }
    if (key == 'm' || key == 'M') {
      holdingM = true;      
    }
   
    /* // reminder: for keys with letters, check "key"
       // instead of "keyCode" !
    if (key == 'r') {
      // reset program?
    }
    if (key == ' ') {
      holdingSpace = true;
    }*/
  }
  void releaseKey(int key,int keyCode) {
    if(key == 'z') {
      holdingZ = false;
    }
    if (keyCode == UP) {
      holdingUp = false;
    }
    if (keyCode == LEFT) {
      holdingLeft = false;
    }
    if (keyCode == RIGHT) {
      holdingRight = false;
    }
    if (keyCode == DOWN) {
      holdingDown = false;
    }
    if (key == 'z' || key == 'Z') {
      holdingZ = false;      
    }
    if (key == 'w' || key == 'W') {
      holdingW = false;      
    }
    if (key == 'a' || key == 'A') {
      holdingA = false;      
    }
    if (key == 's' || key == 'S') {
      holdingS = false;      
    }
    if (key == 'd' || key == 'D') {
      holdingD = false;      
    }
    if (key == 'm' || key == 'M') {
      holdingD = false;      
    }
  }
}

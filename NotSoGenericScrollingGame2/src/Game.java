//SOURCE OF IMAGES: 
// for get.gif: https://www.mariowiki.com/Gallery:Coin
// for avoid.gif: https://gifs.alphacoders.com/gifs/by_tag/17885
// for user.gif: https://giphy.com/stickers/jump-related-sprays-OmxPCq8ATFXig
import java.util.Scanner; //for scanning in user input through console.
public class Game
{
  private Grid grid;
  private int userRow;
  private int msElapsed;
  private int timesGet;
  private int timesAvoid;
  private int speed = 300; //something that I did! | units: milliseconds
  private int level = 0; //my implementation
  private int winningScore = 100; //my implementation (for generalization)... has to be divisible by 10.
  //default is 10 levels.
  private int[] prevScore = new int[100]; //my implementation... winningScore/10 because that's
  //the number needed to win the game.
  private int speedDecrement = 50; //my implementation (for easy medium and hard levels)
  //default is 50 milliseconds, easy difficulty
  int nRows = 5; //default
  int nCols = 10; //default
  public Game()
  {
	initSetup();
    grid = new Grid(nRows, nCols); //Completely changeable.
    userRow = 0;
    msElapsed = 0;
    timesGet = 0;
    timesAvoid = 0;
    updateTitle();
    grid.setImage(new Location(userRow, 0), "user.gif");
  }
  
  public void play()
  {
    while (!isGameOver())
    {
      grid.pause(100);
      handleKeyPress();
      if (msElapsed % speed == 0)
      {
        scrollLeft();
        populateRightEdge();
      }
      updateTitle();
      if (!prevReached()) {
         updateSpeed();
      }
      msElapsed += 100;
    }
  }
  public boolean prevReached() { //checking to see if we've reached this score before
  //so we don't redundantly increment levels
	  int sc = getScore();
	  for (int i=0; i<winningScore/10; i++) {
		  if (prevScore[i] == 1) { //this means we've been here before... check updateSpeed() for details
			  if ((i+1)*10 == sc) { //10, 20, 30, 40... 10n
				  return true;
			  }
		  }
	  }
	  return false;
  }
  public void initSetup() { //my implementation... to change the "settings" of the game.
	  Scanner scan = new Scanner(System.in);
	  System.out.println("I have some setup that you get to choose.");
	  System.out.println("First of all, do you want to play the easy (e), medium (m), or hard (h) level?");
	  char difficulty = scan.next().charAt(0);
	  if (difficulty == 'e') {
		  speedDecrement = 50; //milliseconds
	  }
	  else if (difficulty == 'm') {
		  speedDecrement = 75; //milliseconds
	  }
	  else if (difficulty == 'h') {
		  speedDecrement = 100; //milliseconds
	  }
	  else {
		  System.out.println("That's not a valid level. Please enter the letter of the level you want to play.");
		  System.out.println();
		  initSetup();
	  }
	  System.out.println("Great! Thank you.");
	  System.out.println("Okay, next thing: what is the score that you want to reach in order to win? (Enter a number that's a multiple of 10");
	  winningScore = scan.nextInt();
	  if (winningScore % 10 != 0) {
		  System.out.println("That's not divisible by 10. Please try again.");
		  System.out.println();
		  initSetup();
	  }
	  System.out.println("Finally, what nxn grid size do you want? Enter two line-separated numbers. (Rows, then columns)");
	  nRows = scan.nextInt();
	  nCols = scan.nextInt();
	  if (nCols <= 1 && nRows <= 1) {
		  System.out.println("You can't have an " + nRows + " by " + nCols + " game board. Please try again with a bigger number");
		  System.out.println();
		  initSetup();
	  }
	  System.out.println("Awesome! Let's get you started with your game. Toggle to the tab, you have 5 seconds to do so before the game starts!");
	  grid.pause(5000);
  }
  public void handleKeyPress()
  {
	  grid.setImage(new Location(userRow, 0), "");
	  int key = grid.checkLastKeyPressed();
	  //System.out.println(grid.getNumRows());
	  if (key == 38 && userRow > 0) { // if the up arrow is pressed and we aren't already at the top row.
		  userRow--;
	  }
	  else if (key == 40 && userRow < grid.getNumRows()-1) { // if the down arrow is pressed and we aren't already at the bottom row.
		  userRow++;
	  }
	  if (userRow != grid.getNumRows()-1) {
		  handleCollision(new Location(userRow+1, 0)); //if the user isn't already on the bottom, check if anything is going to collide from below.
	  }
	  else if (userRow != 0) {
		  handleCollision(new Location(userRow-1, 0)); //if the use isn't already on the top, check if anything is going to collide from above.
	  }
	  grid.setImage(new Location(userRow, 0), "user.gif"); //after we're done with handling possible collisions, we can set the use new image.
  }
  
  public void populateRightEdge()
  {
	  for(int i=0; i<grid.getNumRows(); i++) {
		  int binary = (int) (Math.random()*2 + 1);
		  int randCol = (int) (Math.random() * grid.getNumCols());
		  if (binary == 1) {
			  if (randCol % 7 == 1) {
				  grid.setImage(new Location(i, grid.getNumCols()-1), "avoid.gif");
			  }
			  
			  /*
			  else if (randCol % 3 == 2) {
				  grid.setImage(new Location(i, grid.getNumCols()-1), "avoid.gif");
			  }
			  */
			  else {
				  grid.setImage(new Location(i, grid.getNumCols()-1), null);
			  }
		  }
		  else {
			  if (randCol % 7 == 2) {
				  grid.setImage(new Location(i, grid.getNumCols()-1), "get.gif");
			  }
			  else {
				  grid.setImage(new Location(i, grid.getNumCols()-1), null);
			  }
		  }
		  
	  }
	  //this is a completely random algorithm that I made up :)
  }
  
  public void scrollLeft()
  {
	  for (int i=0; i<grid.getNumRows(); i++) {
		  helper(i);
	  }
  }
  public void helper(int row) { //passed in as a 0-based index.
	  for (int i=0; i<grid.getNumCols(); i++) {
		  if (i==0 && row!=userRow) {
			  grid.setImage(new Location(row, i), null);
		  }
		  else if (i==1 && row==userRow) {
			handleCollision(new Location(row, 1)); //check to see if the user is about to hit into something.
			// If it will, we should already take care of it.
		  }
		  else if (i != 0) { //the case of i==0 and row==userRow shouldn't even happen ever.
			  grid.setImage(new Location(row, i-1), grid.getImage(new Location(row, i)));
		  }
	  }
  }
  public void handleCollision(Location loc) //CHECK <--- this should work now...
  {///*
	  if (loc.getCol() == 1 && loc.getRow() == userRow) {
		  
		  if (grid.getImage(loc) == null) {
			  
		  }
		  else if (grid.getImage(loc).equals("get.gif")) {
			  timesGet++;
			  grid.setImage(loc, null);
			 // System.out.println("reached here");
			  
		  }
		  else if (grid.getImage(loc).equals("avoid.gif")) {
			  timesAvoid++;
			  grid.setImage(loc, null);
		  }
	  }
	  else if (loc.getCol() == 0 && (loc.getRow() == userRow-1 || loc.getRow() == userRow+1)) {
		  if (grid.getImage(loc) == null) {
			  
		  }
		  else if (grid.getImage(loc).equals("get.gif")) {
			  timesGet++;
			  grid.setImage(loc, null);
			  
		  }
		  else if (grid.getImage(loc).equals("avoid.gif")) {
			  timesAvoid++;
			  grid.setImage(loc, null);
		  }
	  }
//*/
  }
  
  public int getScore()
  {
	int score = timesGet - timesAvoid;
    return score;
  }
  
  public int getLevel()
  {
    return level;
  }
  
  public void updateSpeed() { //my implementation.
	 if (getScore() % 10 == 0 && getScore()!=0 && speed != 100 && getScore() != winningScore) {
		speed-=speedDecrement;
		//System.out.println("reached the score being " + getScore() + " old score: " + prevScore[0] + 
		//		prevScore[1] + prevScore[2] + prevScore[3]);
		level++;
		grid.setTitle("LEVEL UP! | " + "Level: " + level);
		grid.pause(1000);
		int index = (getScore() / 10) - 1;
		prevScore[index]++;
	 }
  }
  public void updateTitle()
  {
    grid.setTitle("Anika's Awesome Scrolling Game  | Your level: " + level + " |  Your score: " + getScore());
  }
  
  public boolean isGameOver()
  {
	if (getScore() >= winningScore) {
		for (int i=0; i<grid.getNumRows(); i++) {
			for (int j=0; j<grid.getNumCols(); j++) {
				grid.setImage(new Location(i, j), "");
			}
		}
		//just reset all the things in the grid to nothing
		grid.setTitle("YOU WIN! Your score reached " + winningScore);
		//signify that the game is over
		return true;
	}
	else { 
		return false; 
	}
	/*
	if (getScore() % 10 == 0 && getScore() != 0 && getScore() != prevScore && level < 10) {
		level++;
		speed-=10;
		grid.setTitle("LEVEL UP! " + "Level: "  + level + "Your score: " + getScore());
		//signify that we've leveled up
		return false;
	}
	if (level == 10) {
		for (int i=0; i<grid.getNumRows(); i++) {
			for (int j=0; j<grid.getNumCols(); j++) {
				grid.setImage(new Location(i, j), "");
			}
		}
		//just reset all the things in the grid to nothing
		grid.setTitle("GAME OVER!!!  " + "Your score: " + getScore());
		//signify that the game is over
		return true;
	}
	else {
		return false;
	}
	*/
	//the above is a failed attempt to try to implement levels into the game.
  }
  
  public static void test()
  {
    Game game = new Game();
    game.play();
  }
  
  public static void main(String[] args)
  {
    test();
  }
}
package examples.mazeRace;

import tools.CoOrds;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Maze {

  public Maze(GridFrame frame, int dimX, int dimY, int seed, int noRooms){
    rand_ = new Random(seed);
    frame_ = frame;
    maze_ = new boolean[dimX][dimY];
    for(int i = 0; i < maze_.length; i++){
      for(int j = 0; j < maze_[i].length; j++){
        maze_[i][j]=true;
      }
    }
    tempMaze_ = new int[dimX][dimY];
    index_ = 0;
    // make middle room
    int width = makeOdd(randBetween(3, 7));
    int height = makeOdd(randBetween(3, 7));
    newRoom(makeOdd(dimX/2-width/2), makeOdd(dimY/2-height/2), width, height);
    // make four corner rooms
    width = makeOdd(randBetween(3, 7));
    height = makeOdd(randBetween(3, 7));
    newRoom(1 ,1, width, height);
    width = makeOdd(randBetween(3, 7));
    height = makeOdd(randBetween(3, 7));
    newRoom(dimX-width-1, 1,width, height);
    width = makeOdd(randBetween(3, 7));
    height = makeOdd(randBetween(3, 7));
    newRoom(1, dimY-height-1, width, height);
    width = makeOdd(randBetween(3, 7));
    height = makeOdd(randBetween(3, 7));
    newRoom(dimX-width-1, dimY-height-1, width, height);
    for(int i = 5; i<noRooms; i++){
      width = makeOdd(randBetween(3, 7));
      height = makeOdd(randBetween(3, 7));
      int x = makeOdd(randBetween(0, dimX - width - 1));
      int y = makeOdd(randBetween(0, dimY - height - 1));
      newRoom(x, y, width, height);
    }
    for(int i = 1; i<dimX; i+=2){
      for(int j = 1; j<dimY; j+=2){
        if(wallAt(i,j)){
          newCorridor(i, j);
        }
      }
    }
    connectUp();
    quashDeadEnds();
    frame_.redraw(true);
  }

  public boolean wallAt(int x, int y){
    return (x < 0 || x >= maze_.length || y < 0 || y >= maze_[0].length) || maze_[x][y];
  }

  private boolean wallAt2(int x, int y){
    return !(x < 0 || x >= maze_.length || y < 0 || y >= maze_[0].length) && maze_[x][y];
  }

  private int randBetween(int min, int max){
    double val =rand_.nextDouble() * (max - min);
    int val2 = (int) Math.round(val);
    return val2 + min;
  }

  private int makeOdd(int val){
    if(val % 2 == 0){
      return val+1;
    }
    else{
      return val;
    }
  }

  private void newCorridor(int x, int y){
    index_++;
    Random randCol = new Random(index_);
    Color colour = new Color(randCol.nextFloat(), randCol.nextFloat(), randCol.nextFloat());
    frame_.setColour(colour, x,y);
    maze_[x][y] = false;
    tempMaze_[x][y]=index_;
    ArrayList<CoOrds> stack = new ArrayList<CoOrds>();
    stack.add(new CoOrds(x, y));
    while(!stack.isEmpty()){
      CoOrds loc = stack.get(0);
      ArrayList<CoOrds> options = new ArrayList<CoOrds>();
      if(wallAt2(loc.x - 2, loc.y)){
        options.add(new CoOrds(loc.x - 2, loc.y));
      }
      if(wallAt2(loc.x + 2, loc.y)){
        options.add(new CoOrds(loc.x + 2, loc.y));
      }
      if(wallAt2(loc.x, loc.y - 2)){
        options.add(new CoOrds(loc.x, loc.y - 2));
      }
      if(wallAt2(loc.x, loc.y + 2)){
        options.add(new CoOrds(loc.x, loc.y + 2));
      }
      if(!options.isEmpty()) {
        int ran = randBetween(0, options.size() - 1);
        CoOrds newLoc = options.remove(ran);
        int x0 = loc.x;
        int y0 = loc.y;
        int x2 = newLoc.x;
        int y2 = newLoc.y;
        int x1 = x0 + (x2 - x0) / 2;
        int y1 = y0 + (y2 - y0) / 2;
        frame_.setColour(colour, x1, y1);
        maze_[x1][y1] = false;
        tempMaze_[x1][y1] = index_;
        frame_.setColour(colour, x2, y2);
        maze_[x2][y2] = false;
        tempMaze_[x2][y2] = index_;
        stack.add(0, newLoc);
        frame_.redraw(false);
      }
      if (options.isEmpty()) {
        stack.remove(loc);
      }
    }
  }

  private void newRoom(int x, int y, int width, int height) {
    boolean successful = true;
    int x2 = x + width;
    int y2 = y + height;
    for (int i = x - 1; i < x2 + 1 && successful; i++) {
      for (int j = y - 1; j < y2 + 1 && successful; j++) {
        successful = wallAt(i, j);
      }
    }
    if (successful) {
      index_++;
      Random randCol = new Random(index_);
      Color colour = new Color(randCol.nextFloat(), randCol.nextFloat(), randCol.nextFloat());
      for (int i = x; i < x2; i++) {
        for (int j = y; j < y2; j++) {
          frame_.setColour(colour, i, j);
          maze_[i][j] = false;
          tempMaze_[i][j] = index_;
        }
      }
      frame_.redraw(false);
    }
  }

  private void connectUp() {
    ArrayList<Integer> indices = new ArrayList<Integer>();
    for (int i = 0; i < index_; i++) {
      indices.add(i + 1);
    }
    int chosen = indices.remove(randBetween(0, indices.size() - 1));
    for (int i = 0; i < maze_.length; i++) {
      for (int j = 0; j < maze_[0].length; j++) {
        if (tempMaze_[i][j] == chosen) {
          frame_.setColour(Color.white, i, j);
        }
      }
    }
    while (!indices.isEmpty()) {
      frame_.redraw(false);
      ArrayList<int[]> options = new ArrayList<int[]>();
      for (int i = 0; i < maze_.length - 1; i++) {
        for (int j = 2; j < maze_[0].length - 2; j += 2) {
          if (tempMaze_[i][j - 1] != tempMaze_[i][j + 1] && tempMaze_[i][j - 1] != 0 && tempMaze_[i][j + 1] != 0) {
            if (tempMaze_[i][j - 1] == chosen) {
              options.add(new int[]{i, j, tempMaze_[i][j + 1]});
            } else if (tempMaze_[i][j + 1] == chosen) {
              options.add(new int[]{i, j, tempMaze_[i][j - 1]});
            }
          }
        }
      }
      for (int i = 2; i < maze_.length - 2; i += 2) {
        for (int j = 0; j < maze_[0].length - 1; j++) {
          if (tempMaze_[i - 1][j] != tempMaze_[i + 1][j] && tempMaze_[i - 1][j] != 0 && tempMaze_[i + 1][j] != 0) {
            if (tempMaze_[i - 1][j] == chosen) {
              options.add(new int[]{i, j, tempMaze_[i + 1][j]});
            } else if (tempMaze_[i + 1][j] == chosen) {
              options.add(new int[]{i, j, tempMaze_[i - 1][j]});
            }
          }
        }
      }
      if (!options.isEmpty()) {
        int[] ran = options.remove(randBetween(0, options.size() - 1));
        maze_[ran[0]][ran[1]] = false;
        tempMaze_[ran[0]][ran[1]] = ran[2];
        for (int[] option : options) {
          if (rand_.nextFloat() < 0.05 && option[2] == ran[2]) {
            maze_[option[0]][option[1]] = false;
            tempMaze_[option[0]][option[1]] = ran[2];
          }
        }
        for (int i = 0; i < maze_.length; i++) {
          for (int j = 0; j < maze_[0].length; j++) {
            if (tempMaze_[i][j] == ran[2]) {
              frame_.setColour(Color.white, i, j);
            } else if (tempMaze_[i][j] == chosen) {
              tempMaze_[i][j] = ran[2];
            }
          }
        }
        chosen = ran[2];
        indices.remove(indices.indexOf(chosen));
      }
      frame_.redraw(false);
    }
    frame_.redraw(false);
  }

  public void quashDeadEnds(){
    ArrayList<ArrayList<CoOrds>> deadEnds = new ArrayList<ArrayList<CoOrds>>();
    for (int x = 1; x < maze_.length - 1; x++) {
      for (int y = 1; y < maze_[x].length - 1; y++) {
        if (!wallAt(x, y)) {
          ArrayList<CoOrds> adjacents = new ArrayList<CoOrds>();
          adjacents.add(new CoOrds(x, y));
          if (!wallAt(x - 1, y)) {
            adjacents.add(new CoOrds(x - 1, y));
          }
          if (!wallAt(x + 1, y)) {
            adjacents.add(new CoOrds(x + 1, y));
          }
          if (!wallAt(x, y - 1)) {
            adjacents.add(new CoOrds(x, y - 1));
          }
          if (!wallAt(x, y + 1)) {
            adjacents.add(new CoOrds(x, y + 1));
          }
          if (adjacents.size() == 2) {
            deadEnds.add(adjacents);
          }
        }
      }
    }
    while(!deadEnds.isEmpty()){
      ArrayList<CoOrds> deadend = deadEnds.remove(0);
      if(rand_.nextFloat() > 0.01){
        int x1 = deadend.get(0).x;
        int y1 = deadend.get(0).y;
        int x2 = deadend.get(1).x;
        int y2 = deadend.get(1).y;
        maze_[x1][y1] = true;
        frame_.setColour(Color.black, x1, y1);
        frame_.redraw(false);
        ArrayList<CoOrds> adjacents = new ArrayList<CoOrds>();
        adjacents.add(new CoOrds(x2, y2));
        if (!wallAt(x2 - 1, y2)) {
          adjacents.add(new CoOrds(x2 - 1, y2));
        }
        if (!wallAt(x2 + 1, y2)) {
          adjacents.add(new CoOrds(x2 + 1, y2));
        }
        if (!wallAt(x2, y2 - 1)) {
          adjacents.add(new CoOrds(x2, y2 - 1));
        }
        if (!wallAt(x2, y2 + 1)) {
          adjacents.add(new CoOrds(x2, y2 + 1));
        }
        if (adjacents.size() == 2) {
          deadEnds.add(0, adjacents);
        }
      }
    }
  }

  private GridFrame frame_;
  private boolean[][] maze_;
  private int[][] tempMaze_;
  private int index_;
  private Random rand_;
}

package examples.mazeRace;

import common.Cardinal;
import common.Coord;
import tools.RandomTool;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Maze {

  public Maze(int dimX, int dimY, int seed, int noRooms, boolean slowGenerate){
    noPlayers_ = 0;
    players_ = new Coord[4];
    for(int i = 0; i<players_.length; i++){
      players_[i] = new Coord(0, 0);
    }
    rand_ = new RandomTool(seed);
    dim_ = new Coord(makeOdd(dimX), makeOdd(dimY));
    frame_ = new GridFrame(dim_.x, dim_.y, Color.black);
    frame_.makeVisible(slowGenerate);
    maze_ = new boolean[dim_.x][dim_.y];
    for(int i = 0; i < dim_.x; i++){
      for(int j = 0; j < dim_.y; j++){
        maze_[i][j]=true;
      }
    }
    tempMaze_ = new int[dim_.x][dim_.y];
    index_ = 0;
    // make middle room
    int width = makeOdd(rand_.between(3, 7));
    int height = makeOdd(rand_.between(3, 7));
    newRoom(makeOdd(dimX/2-width/2), makeOdd(dimY/2-height/2), width, height);
    // make four corner rooms
    width = makeOdd(rand_.between(3, 7));
    height = makeOdd(rand_.between(3, 7));
    newRoom(1 ,1, width, height);
    width = makeOdd(rand_.between(3, 7));
    height = makeOdd(rand_.between(3, 7));
    newRoom(dimX-width-1, 1,width, height);
    width = makeOdd(rand_.between(3, 7));
    height = makeOdd(rand_.between(3, 7));
    newRoom(1, dimY-height-1, width, height);
    width = makeOdd(rand_.between(3, 7));
    height = makeOdd(rand_.between(3, 7));
    newRoom(dimX-width-1, dimY-height-1, width, height);
    for(int i = 5; i<noRooms; i++){
      width = makeOdd(rand_.between(3, 7));
      height = makeOdd(rand_.between(3, 7));
      int x = makeOdd(rand_.between(0, dimX - width - 1));
      int y = makeOdd(rand_.between(0, dimY - height - 1));
      newRoom(x, y, width, height);
    }
    for(int i = 1; i< dim_.x; i+=2){
      for(int j = 1; j< dim_.y; j+=2){
        if(wallAt(i,j)){
          newCorridor(i, j);
        }
      }
    }
    connectUp();
    quashDeadEnds();
    frame_.setColour(Color.orange, new Coord(dim_.x/2, dim_.y/2));
    frame_.redraw();
  }


  public void setPlayer(){
    frame_.makeVisible(true);
    switch (noPlayers_){
      case 0:{
        players_[0] = new Coord(1, 1);
        frame_.setColour(colours[0], players_[0]);
        break;
      }
      case 1:{
        players_[1] = new Coord(1, dim_.y - 2);
        frame_.setColour(colours[1], players_[1]);
        break;
      }
      case 2:{
        players_[2] = new Coord(dim_.x - 2, 1);
        frame_.setColour(colours[2], players_[2]);
        break;
      }
      case 3:{
        players_[3] = new Coord(dim_.x - 2, dim_.y - 2);
        frame_.setColour(colours[3], players_[3]);
        break;
      }
      default:{
        return;
      }
    }
    noPlayers_++;
  }

  public void killPlayer(int player){
    frame_.setColour(Color.white, playerLoc(player));
    players_[player] = new Coord(0,0);
    frame_.redraw();
  }

  public boolean hasWon(int player){
    return playerLoc(player).equals(new Coord(dim_.x/2, dim_.y/2));
  }

  public boolean canMove(int player, Cardinal direction){
    Coord loc = players_[player];
    switch (direction) {
      case NORTH:{
        return !wallAt(loc.x, loc.y-1) && !playerAt(loc.x, loc.y-1);
      }
      case EAST:{
        return !wallAt(loc.x-1, loc.y) && !playerAt(loc.x-1, loc.y);
      }
      case SOUTH:{
        return !wallAt(loc.x, loc.y+1) && !playerAt(loc.x, loc.y+1);
      }
      case WEST: {
        return !wallAt(loc.x + 1, loc.y) && !playerAt(loc.x + 1, loc.y);
      }
    }
    return false;
  }

  public void move(int player, Cardinal direction){
    Coord loc = players_[player];
    frame_.setColour(Color.white, loc);
    switch (direction) {
      case NORTH:{
        players_[player] = new Coord(loc.x, loc.y-1);
        break;
      }
      case EAST:{
        players_[player] = new Coord(loc.x-1, loc.y);
        break;
      }
      case SOUTH:{
        players_[player] = new Coord(loc.x, loc.y+1);
        break;
      }
      case WEST:{
        players_[player] = new Coord(loc.x+1, loc.y);
        break;
      }
    }
    frame_.setColour(colours[player], players_[player]);
    frame_.redraw();
  }

  public boolean wallAt(int x, int y){
    return (x < 0 || x >= maze_.length || y < 0 || y >= maze_[0].length) || maze_[x][y];
  }

  public boolean playerAt(int x, int y){
    boolean result = false;
    for(int i = 0; i<players_.length && !result; i++){
      result = playerLoc(i).equals(new Coord(x, y));
    }
    return result;
  }

  public Coord playerLoc(int player){
    return players_[player];
  }

  private boolean wallAt2(int x, int y){
    return !(x < 0 || x >= maze_.length || y < 0 || y >= maze_[0].length) && maze_[x][y];
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
    Color colour = new RandomTool(index_).color();
    frame_.setColour(colour, new Coord(x,y));
    maze_[x][y] = false;
    tempMaze_[x][y]=index_;
    ArrayList<Coord> stack = new ArrayList<Coord>();
    stack.add(new Coord(x, y));
    while(!stack.isEmpty()){
      Coord loc = stack.get(0);
      ArrayList<Coord> options = new ArrayList<Coord>();
      if(wallAt2(loc.x - 2, loc.y)){
        options.add(new Coord(loc.x - 2, loc.y));
      }
      if(wallAt2(loc.x + 2, loc.y)){
        options.add(new Coord(loc.x + 2, loc.y));
      }
      if(wallAt2(loc.x, loc.y - 2)){
        options.add(new Coord(loc.x, loc.y - 2));
      }
      if(wallAt2(loc.x, loc.y + 2)){
        options.add(new Coord(loc.x, loc.y + 2));
      }
      if(!options.isEmpty()) {
        int ran = rand_.between(0, options.size());
        Coord newLoc = options.remove(ran);
        int x0 = loc.x;
        int y0 = loc.y;
        int x2 = newLoc.x;
        int y2 = newLoc.y;
        int x1 = x0 + (x2 - x0) / 2;
        int y1 = y0 + (y2 - y0) / 2;
        frame_.setColour(colour, new Coord(x1, y1));
        maze_[x1][y1] = false;
        tempMaze_[x1][y1] = index_;
        frame_.setColour(colour, newLoc);
        maze_[x2][y2] = false;
        tempMaze_[x2][y2] = index_;
        stack.add(0, newLoc);
        frame_.redraw();
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
          frame_.setColour(colour, new Coord(i, j));
          maze_[i][j] = false;
          tempMaze_[i][j] = index_;
        }
      }
      frame_.redraw();
    }
  }

  private void connectUp() {
    ArrayList<Integer> indices = new ArrayList<Integer>();
    for (int i = 0; i < index_; i++) {
      indices.add(i + 1);
    }
    int chosen = indices.remove(rand_.between(0, indices.size()));
    for (int i = 0; i < dim_.x; i++) {
      for (int j = 0; j < dim_.y; j++) {
        if (tempMaze_[i][j] == chosen) {
          frame_.setColour(Color.white, new Coord(i, j));
        }
      }
    }
    while (!indices.isEmpty()) {
      frame_.redraw();
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
        int[] ran = options.remove(rand_.between(0, options.size()));
        maze_[ran[0]][ran[1]] = false;
        tempMaze_[ran[0]][ran[1]] = ran[2];
        for (int[] option : options) {
          if (rand_.decide(0.05) && option[2] == ran[2]) {
            maze_[option[0]][option[1]] = false;
            tempMaze_[option[0]][option[1]] = ran[2];
          }
        }
        for (int i = 0; i < maze_.length; i++) {
          for (int j = 0; j < maze_[0].length; j++) {
            if (tempMaze_[i][j] == ran[2]) {
              frame_.setColour(Color.white, new Coord(i, j));
            } else if (tempMaze_[i][j] == chosen) {
              tempMaze_[i][j] = ran[2];
            }
          }
        }
        chosen = ran[2];
        indices.remove(indices.indexOf(chosen));
      }
      frame_.redraw();
    }
    frame_.redraw();
  }

  public void quashDeadEnds(){
    ArrayList<ArrayList<Coord>> deadEnds = new ArrayList<ArrayList<Coord>>();
    for (int x = 1; x < dim_.x - 1; x++) {
      for (int y = 1; y < dim_.y - 1; y++) {
        if (!wallAt(x, y)) {
          ArrayList<Coord> adjacents = new ArrayList<Coord>();
          adjacents.add(new Coord(x, y));
          if (!wallAt(x - 1, y)) {
            adjacents.add(new Coord(x - 1, y));
          }
          if (!wallAt(x + 1, y)) {
            adjacents.add(new Coord(x + 1, y));
          }
          if (!wallAt(x, y - 1)) {
            adjacents.add(new Coord(x, y - 1));
          }
          if (!wallAt(x, y + 1)) {
            adjacents.add(new Coord(x, y + 1));
          }
          if (adjacents.size() == 2) {
            deadEnds.add(adjacents);
          }
        }
      }
    }
    while(!deadEnds.isEmpty()){
      ArrayList<Coord> deadend = deadEnds.remove(0);
      if(rand_.decide(0.99)){
        int x1 = deadend.get(0).x;
        int y1 = deadend.get(0).y;
        int x2 = deadend.get(1).x;
        int y2 = deadend.get(1).y;
        maze_[x1][y1] = true;
        frame_.setColour(Color.black, deadend.get(0));
        frame_.redraw();
        ArrayList<Coord> adjacents = new ArrayList<Coord>();
        adjacents.add(new Coord(x2, y2));
        if (!wallAt(x2 - 1, y2)) {
          adjacents.add(new Coord(x2 - 1, y2));
        }
        if (!wallAt(x2 + 1, y2)) {
          adjacents.add(new Coord(x2 + 1, y2));
        }
        if (!wallAt(x2, y2 - 1)) {
          adjacents.add(new Coord(x2, y2 - 1));
        }
        if (!wallAt(x2, y2 + 1)) {
          adjacents.add(new Coord(x2, y2 + 1));
        }
        if (adjacents.size() == 2) {
          deadEnds.add(0, adjacents);
        }
      }
    }
  }

  private int noPlayers_;
  private Coord[] players_;
  private GridFrame frame_;
  private Coord dim_;
  private boolean[][] maze_;
  private int[][] tempMaze_;
  private int index_;
  private RandomTool rand_;
  private Color[] colours = new Color[]{ Color.red, Color.blue, Color.green, Color.magenta};
}

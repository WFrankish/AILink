package examples.mazeChase;

import common.Cardinal;
import common.Coord;
import common.Grid;
import examples.mazeRace.GridFrame;
import examples.mazeChase.ChaseState.*;
import tools.RandomTool;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Maze {

  public Maze(int dimX, int dimY, int seed, int noRooms, boolean slowGenerate){
    noPlayers_ = 0;
    players_ = new Coord[3];
    for(int i = 0; i<players_.length; i++){
      players_[i] = new Coord(0, 0);
    }
    rand_ = new RandomTool(seed);
    dim_ = new Coord(makeOdd(dimX), makeOdd(dimY));
    frame_ = new GridFrame(dim_.x, dim_.y, Color.black);
    frame_.makeVisible(slowGenerate);
    maze_ = new Grid<Thing>(dim_, Thing.WALL, Thing.WALL);
    tempMaze_ = new Grid<Integer>(dim_, 0, 0);
    index_ = 0;
    // make middle room
    int width = makeOdd(rand_.between(3, 7));
    int height = makeOdd(rand_.between(3, 7));
    newRoom(makeOdd(dim_.x/2-width/2), makeOdd(dim_.y/2-height/2), width, height);
    // make two corner rooms
    width = makeOdd(rand_.between(3, 7));
    height = makeOdd(rand_.between(3, 7));
    newRoom(1 ,1, width, height);
    width = makeOdd(rand_.between(3, 7));
    height = makeOdd(rand_.between(3, 7));
    newRoom(dim_.x-width-1, dim_.y-height-1, width, height);
    for(int i = 3; i<noRooms; i++){
      width = makeOdd(rand_.between(3, 7));
      height = makeOdd(rand_.between(3, 7));
      int x = makeOdd(rand_.between(0, dim_.x - width - 1));
      int y = makeOdd(rand_.between(0, dim_.y - height - 1));
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
        players_[1] = new Coord(dim_.x - 2, dim_.y - 2);
        frame_.setColour(colours[1], players_[1]);
        break;
      }
      case 2:{
        players_[2] = new Coord(dim_.x/2, dim_.y/2);
        frame_.setColour(colours[2], players_[2]);
        break;
      }
      default:{
        return;
      }
    }
    noPlayers_++;
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
    return maze_.get(x, y)==Thing.WALL;
  }

  public boolean playerAt(int x, int y){
    boolean result = false;
    for(int i = 0; i<players_.length && !result; i++){
      result = playerLoc(i).equals(new Coord(x, y));
    }
    return result;
  }

  public Grid<ChaseState.Thing> cloneMap(){
    Grid<ChaseState.Thing> out = new Grid<ChaseState.Thing>(maze_, ChaseState.Thing.WALL);
    out.set(players_[0], ChaseState.Thing.CHASER1);
    out.set(players_[1], ChaseState.Thing.CHASER2);
    out.set(players_[2], ChaseState.Thing.RUNNER);
    return out;
  }

  public Thing whatsAt(int x, int y){
    return maze_.get(x, y);
  }

  public Coord playerLoc(int player){
    return players_[player];
  }

  private boolean wallAt2(int x, int y){
    return !(x < 0 || x >= dim_.x || y < 0 || y >= dim_.y) && maze_.get(x, y) == Thing.WALL;
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
    Coord loc = new Coord(x, y);
    Color colour = new RandomTool(index_).color();
    frame_.setColour(colour, loc);
    maze_.set(loc, Thing.NOTHING);
    tempMaze_.set(loc, index_);
    ArrayList<Coord> stack = new ArrayList<Coord>();
    stack.add(loc);
    while(!stack.isEmpty()){
      loc = stack.get(0);
      ArrayList<Coord> options = new ArrayList<Coord>();
      if(wallAt2(loc.x - 2, loc.y)){
        options.add(loc.add(-2, 0));
      }
      if(wallAt2(loc.x + 2, loc.y)){
        options.add(loc.add(2, 0));
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
        int mx = loc.x + (newLoc.x - loc.x) / 2;
        int my = loc.y + (newLoc.y - loc.y) / 2;
        frame_.setColour(colour, new Coord(mx, my));
        maze_.set(mx, my, Thing.NOTHING);
        tempMaze_.set(mx, my, index_);
        frame_.setColour(colour, newLoc);
        maze_.set(newLoc, Thing.NOTHING);
        tempMaze_.set(newLoc, index_);
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
          maze_.set(i, j, Thing.NOTHING);
          tempMaze_.set(i, j, index_);
        }
      }
      frame_.redraw();
    }
  }

  private void connectUp() {
    ArrayList<Integer> indices = new ArrayList<Integer>();
    for (int x = 0; x < index_; x++) {
      indices.add(x + 1);
    }
    int chosen = indices.remove(rand_.between(0, indices.size()));
    for (int x = 0; x < dim_.x; x++) {
      for (int y = 0; y < dim_.y; y++) {
        if (tempMaze_.get(x, y) == chosen) {
          frame_.setColour(Color.white, new Coord(x, y));
        }
      }
    }
    while (!indices.isEmpty()) {
      frame_.redraw();
      ArrayList<int[]> options = new ArrayList<int[]>();
      for (int x = 0; x < dim_.x - 1; x++) {
        for (int y = 2; y < dim_.y - 2; y += 2) {
          if (!tempMaze_.get(x, y-1).equals(tempMaze_.get(x, y+1)) && tempMaze_.get(x, y-1) != 0 && tempMaze_.get(x, y+1) != 0) {
            if (tempMaze_.get(x, y-1) == chosen) {
              options.add(new int[]{x, y, tempMaze_.get(x, y+1)});
            } else if (tempMaze_.get(x, y+1) == chosen) {
              options.add(new int[]{x, y, tempMaze_.get(x, y-1)});
            }
          }
        }
      }
      for (int x = 2; x < dim_.x - 2; x += 2) {
        for (int y = 0; y < dim_.y - 1; y++) {
          if (!tempMaze_.get(x-1, y).equals(tempMaze_.get(x+1, y)) && tempMaze_.get(x-1 ,y) != 0 && tempMaze_.get(x+1, y) != 0) {
            if (tempMaze_.get(x-1, y) == chosen) {
              options.add(new int[]{x, y, tempMaze_.get(x+1, y)});
            } else if (tempMaze_.get(x+1, y) == chosen) {
              options.add(new int[]{x, y, tempMaze_.get(x-1, y)});
            }
          }
        }
      }
      if (!options.isEmpty()) {
        int[] ran = options.remove(rand_.between(0, options.size()));
        Coord dimRan = new Coord(ran[0], ran[1]);
        maze_.set(dimRan, Thing.NOTHING);
        tempMaze_.set(dimRan, ran[2]);
        for (int[] option : options) {
          if (rand_.decide(0.1) && option[2] == ran[2]) {
            Coord dimOpt = new Coord(option[0], option[1]);
            maze_.set(dimOpt, Thing.NOTHING);
            tempMaze_.set(dimOpt, ran[2]);
          }
        }
        for (int x = 0; x < dim_.x; x++) {
          for (int y = 0; y < dim_.y; y++) {
            if (tempMaze_.get(x, y) == ran[2]) {
              frame_.setColour(Color.white, new Coord(x, y));
            } else if (tempMaze_.get(x, y) == chosen) {
              tempMaze_.set(x, y, ran[2]);
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
      if(rand_.decide(0.95)){
        Coord dim1 = deadend.get(0);
        Coord dim2 = deadend.get(1);
        maze_.set(dim1, Thing.WALL);
        frame_.setColour(Color.black, dim1);
        frame_.redraw();
        ArrayList<Coord> adjacents = new ArrayList<Coord>();
        adjacents.add(dim2);
        if (maze_.get(dim2.apply(Cardinal.NORTH))!=Thing.WALL) {
          adjacents.add(dim2.apply(Cardinal.NORTH));
        }
        if (maze_.get(dim2.apply(Cardinal.EAST))!=Thing.WALL) {
          adjacents.add(dim2.apply(Cardinal.EAST));
        }
        if (maze_.get(dim2.apply(Cardinal.SOUTH))!=Thing.WALL) {
          adjacents.add(dim2.apply(Cardinal.SOUTH));
        }
        if (maze_.get(dim2.apply(Cardinal.WEST))!=Thing.WALL) {
          adjacents.add(dim2.apply(Cardinal.WEST));
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
  private Grid<Thing> maze_;
  private Grid<Integer> tempMaze_;
  private int index_;
  private RandomTool rand_;
  private Color[] colours = new Color[]{ Color.green, Color.blue, Color.red};
}

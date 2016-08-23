package examples.mazeRace;

import common.*;
import socketInterface.SocketAgentInterface;
import interfaces.Action;
import interfaces.Agent;
import interfaces.AgentInterface;
import interfaces.State;
import tools.ParseTools;
import tools.RandomTool;

import java.awt.*;
import java.util.ArrayList;

public class MazeBot implements Agent {

  public static void main(String[] args) {
    MazeBot instance = new MazeBot(args);
    AgentInterface connection = new SocketAgentInterface(
        instance,
        new MazeStateMaster());
    connection.run();
  }

  public MazeBot(String[] args){
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
    showScreen_ = (ParseTools.find(args, "-v") > -1);
    int seed = ParseTools.findVal(args, "-r", 0);
    rand_ = new RandomTool(seed);
    plannedMoves_ = new ArrayList<MovementAction>();
  }

  @Override
  public boolean checkGame(String ident) {
    return ident.equals("Maze Race");
  }

  @Override
  public void perceiveState(State update) {
    if(update instanceof MazeState.Dimension) {
      // received the initial state telling us the size of the maze
      if (dim_ == null) {
        // construct internal state
        MazeState.Dimension dim = (MazeState.Dimension) update;
        dim_ = new Coord(dim.getX(), dim.getY());
        frame_ = new GridFrame(dim_.x, dim_.y, Color.gray);
        knownMaze_ = new Grid<Integer>(dim_, 0, -1);
        setGoal(dim_.x / 2, dim_.y / 2);
        // draw the borders
        for (int y = 0; y < dim_.y; y++) {
          frame_.setColour(Color.black, new Coord(0, y));
          frame_.setColour(Color.black, new Coord(dim_.x - 1, y));
          knownMaze_.set(0, y, -1);
          knownMaze_.set(dim_.x - 1, y, -1);
        }
        for (int x = 0; x < dim_.x; x++) {
          frame_.setColour(Color.black, new Coord(x, 0));
          frame_.setColour(Color.black, new Coord(x, dim_.y - 1));
          knownMaze_.set(x, 0, -1);
          knownMaze_.set(x, dim_.y - 1, -1);
        }
        frame_.makeVisible(showScreen_);
        frame_.redraw();
      }
    }
    else if(update instanceof MazeState.Sight){
      // we've been told what we can see
      MazeState.Sight sight = (MazeState.Sight) update;
      // find our possible moves for the next action
      allowedMoves_ = new ArrayList<MovementAction>();
      if(sight.distanceNorth() > 1){
        allowedMoves_.add(MovementAction.North);
      }
      if(sight.distanceEast() > 1){
        allowedMoves_.add(MovementAction.East);
      }
      if(sight.distanceSouth() > 1){
        allowedMoves_.add(MovementAction.South);
      }
      if(sight.distanceWest() > 1){
        allowedMoves_.add(MovementAction.West);
      }
      if(x_ == 0 | y_ == 0){
        // we don't know our initial position, but we always start in a corner
        for(Action move : allowedMoves_){
          switch (((MovementAction) move).getDirection()){
            case NORTH:{
              y_ = dim_.y -2;
              break;
            }
            case EAST:{
              x_ = dim_.x - 2;
              break;
            }
            case SOUTH:{
              y_ = 1;
              break;
            }
            case WEST:{
              x_ = 1;
            }
          }
        }
        frame_.setColour(Color.blue, new Coord(x_, y_));
        knownMaze_.set(x_, y_, 1);
      }
      // process what we can see
      for(int d = 0; d<sight.distanceNorth(); d++){
        setFloor(x_, y_-d);
        setInteresting(x_-1, y_-d);
        setInteresting(x_+1, y_-d);
      }
      if(sight.isNorthOpponent()){
        setEnemy(x_, y_-sight.distanceNorth());
      }else{
        setWall(x_, y_-sight.distanceNorth());
      }
      for(int d = 0; d<sight.distanceEast(); d++){
        setFloor(x_-d, y_);
        setInteresting(x_ - d, y_ - 1);
        setInteresting(x_-d, y_+1);
      }
      if(sight.isEastOpponent()){
        setEnemy(x_-sight.distanceEast(), y_);
      }else{
        setWall(x_-sight.distanceEast(), y_);
      }
      for(int d = 0; d<sight.distanceSouth(); d++){
        setFloor(x_, y_+d);
        setInteresting(x_-1, y_+d);
        setInteresting(x_+1, y_+d);
      }
      if(sight.isSouthOpponent()){
        setEnemy(x_, y_+sight.distanceSouth());
      }else{
        setWall(x_, y_+sight.distanceSouth());
      }
      for(int d = 0; d<sight.distanceWest(); d++){
        setFloor(x_+d, y_);
        setInteresting(x_+d, y_-1);
        setInteresting(x_+d, y_+1);
      }
      if(sight.isWestOpponent()) {
        setEnemy(x_+sight.distanceWest(), y_);
      }else{
        setWall(x_ + sight.distanceWest(), y_);
      }
    }
    else{
      // we've been told who the winner is
      MazeState.Winner winner = (MazeState.Winner) update;
      System.out.println(winner.toString() + "!");
    }
  }

  @Override
  public Action decide() {
    // check if currently have plans
    if(plannedMoves_.size() == 0){
      // we have no plans so make some
      int bestDistance = Integer.MAX_VALUE;
      // find the best square of interest (marked with a 3)
      for(int x = 0; x<dim_.x; x++){
        for(int y = 0; y<dim_.y; y++){
          if(knownMaze_.get(x, y) == 3){
            ArrayList<MovementAction> path = pathFind(x, y);
            if(path != null) {
              int distance;
              if (x == goalX_ && y == goalY_) {
                distance = 0;
              } else {
                distance = dim_.x * roughDistance(x, y, goalX_, goalY_) + path.size();
              }
              if (distance < bestDistance) {
                bestDistance = distance;
                plannedMoves_ = path;
                targetX_ = x;
                targetY_ = y;
              }
            }
          }
        }
      }
      frame_.setColour(Color.magenta, new Coord(targetX_, targetY_));
      debug(false, "Now targeting square "+targetX_+", "+targetY_);
    }
    MovementAction nextMove = plannedMoves_.get(0);
    if(panicDuration_ > 0 || allowedMoves_.indexOf(nextMove) < 0){
      if(panicDuration_ > 0) {
        // we're moving randomly hoping a blocking opponent will move;
        panicDuration_--;
      } else {
        // we can't move in the direction we want, move randomly for some turns
        panicDuration_ = rand_.between(0, 10);
      }
      int i = rand_.between(0, allowedMoves_.size());
      nextMove = allowedMoves_.get(i);
      // add opposite of random move onto the stack
      switch (nextMove.getDirection()){
        case NORTH:{
          plannedMoves_.add(0, MovementAction.South);
          break;
        }
        case EAST:{
          plannedMoves_.add(0, MovementAction.West);
          break;
        }
        case SOUTH:{
          plannedMoves_.add(0, MovementAction.North);
          break;
        }
        case WEST:{
          plannedMoves_.add(0, MovementAction.East);
          break;
        }
      }
    } else {
      // pick out next move
      nextMove = plannedMoves_.remove(0);
    }
    // draw effects of next move
    frame_.setColour(Color.white, new Coord(x_, y_));
    switch (nextMove.getDirection()){
      case NORTH:{
        y_--;
        break;
      }
      case EAST:{
        x_--;
        break;
      }
      case SOUTH:{
        y_++;
        break;
      }
      case WEST:{
        x_++;
      }
    }
    frame_.setColour(Color.blue, new Coord(x_, y_));
    frame_.redraw();
    return nextMove;
  }

  @Override
  public String identity() {
    return "MazeBot";
  }

  @Override
  public void message(Object obj) {
    System.out.println("Message from game: " + obj);
  }

  @Override
  public void debug(boolean isMajor, Object obj) {
    if( showDebug_ && (showMinor_ || isMajor)){
      System.out.println("Debug: " + obj);
    }
  }

  @Override
  public void error(Object obj) {
    System.out.println("Error: " + obj);
  }

  //  0 : undiscovered
  // -1 : wall
  //  1 : floor
  //  2 : opponent
  //  3 : reachable square
  //  4 : goal

  // draw square as a wall
  private void setWall(int x, int y){
    if(x == targetX_ && y == targetY_){
      // if our current target is in sight, we can make a new target
      plannedMoves_.clear();
    }
    frame_.setColour(Color.black, new Coord(x,y));
    knownMaze_.set(x, y, -1);
  }

  private void setFloor(int x, int y){
    if(x == targetX_ && y == targetY_  && (x != goalX_  || y != goalY_)){
      // if our current target is in sight, we can make a new target
      plannedMoves_.clear();
    }
    // don't overwrite the goal
    if(x != goalX_  || y != goalY_){
      frame_.setColour(Color.white, new Coord(x, y));
      knownMaze_.set(x, y, 1);
    } else {
      knownMaze_.set(x, y, 3);
    }
  }

  private void setEnemy(int x, int y){
    if(x == targetX_ && y == targetY_){
      // if our current target is in sight, we can make a new target
      plannedMoves_.clear();
    }
    // don't check for goal, since if an enemy is on the goal its over anyway
    frame_.setColour(Color.red, new Coord(x,y));
    knownMaze_.set(x, y, 2);
  }

  private void setInteresting(int x, int y){
    // don't overwrite floor, walls, can overwrite goal
    if(knownMaze_.get(x, y) == 0 || knownMaze_.get(x, y) > 1){
      if(x == goalX_ && y == goalY_) {
        frame_.setColour(Color.orange, new Coord(x,y));
      } else {
        frame_.setColour(Color.green, new Coord(x, y));
      }
      knownMaze_.set(x, y, 3);
    }
  }

  private void setGoal(int x, int y){
    goalX_ = x;
    goalY_ = y;
    frame_.setColour(Color.orange, new Coord(x,y));
    knownMaze_.set(x, y, 4);
  }

  // distance between two squares
  private int roughDistance(int x1, int y1, int x2, int y2){
    return Math.abs(y2-y1) + Math.abs(x2 - x1);
  }

  // find the shortest path and path length to a square
  // only called on reachable squares
  private ArrayList<MovementAction> pathFind(int targetX, int targetY){
    Grid<Integer> heuristic = new Grid<Integer>(dim_, 0, Integer.MAX_VALUE);
    Grid<Boolean> found = new Grid<Boolean>(dim_, false, true);
    for(int x = 0; x < dim_.x; x++){
      for(int y = 0; y < dim_.y; y++){
        // heuristic is grid-wise distance
        heuristic.set(x, y, roughDistance(x, y, targetX, targetY));
        // declare walls and squares not known to be reachable found
        found.set(x, y,(knownMaze_.get(x, y) != 1 && knownMaze_.get(x, y) != 2 && (x != targetX || y != targetY)));
      }
    }
    Coord goal = new Coord(targetX, targetY);
    KeyedQueue<Tuple<ArrayList<MovementAction>, Coord>> search = new KeyedQueue<Tuple<ArrayList<MovementAction>, Coord>>();
    search.add(heuristic.get(x_, y_), new Tuple<ArrayList<MovementAction>, Coord>(new ArrayList<MovementAction>(), new Coord(x_, y_)));
    while(!search.isEmpty()){
      Tuple<ArrayList<MovementAction>, Coord> next = search.pop();
      Coord loc = next.snd;
      found.set(loc, true);
      ArrayList<MovementAction> path = next.fst;
      if(loc.equals(goal)){
        return next.fst;
      } else {
        Coord north = loc.apply(Cardinal.NORTH);
        Coord east = loc.apply(Cardinal.EAST);
        Coord south = loc.apply(Cardinal.SOUTH);
        Coord west = loc.apply(Cardinal.WEST);
        if(!found.get(north)){
          ArrayList<MovementAction> newPath = (ArrayList<MovementAction>) path.clone();
          newPath.add(MovementAction.North);
          int k = newPath.size() + roughDistance(north.x, north.y, targetX, targetY);
          search.add(k, new Tuple<ArrayList<MovementAction>, Coord>(newPath, north));
        }
        if(!found.get(east)){
          ArrayList<MovementAction> newPath = (ArrayList<MovementAction>) path.clone();
          newPath.add(MovementAction.East);
          int k = newPath.size() + roughDistance(east.x, east.y, targetX, targetY);
          search.add(k, new Tuple<ArrayList<MovementAction>, Coord>(newPath, east));
        }
        if(!found.get(south)){
          ArrayList<MovementAction> newPath = (ArrayList<MovementAction>) path.clone();
          newPath.add(MovementAction.South);
          int k = newPath.size() + roughDistance(south.x, south.y, targetX, targetY);
          search.add(k, new Tuple<ArrayList<MovementAction>, Coord>(newPath, south));
        }
        if(!found.get(west)){
          ArrayList<MovementAction> newPath = (ArrayList<MovementAction>) path.clone();
          newPath.add(MovementAction.West);
          int k = newPath.size() + roughDistance(west.x, west.y, targetX, targetY);
          search.add(k, new Tuple<ArrayList<MovementAction>, Coord>(newPath, west));
        }
      }
    }
    // things may seem unreachable as a result of being behind the goal
    return null;
  }

  private int x_;
  private int y_;
  private int goalX_;
  private int goalY_;
  private Coord dim_;
  private GridFrame frame_;
  private Grid<Integer> knownMaze_;
  private boolean showMinor_;
  private boolean showDebug_;
  private boolean showScreen_;
  private ArrayList<MovementAction> allowedMoves_;
  private int panicDuration_;
  private int targetX_;
  private int targetY_;
  private ArrayList<MovementAction> plannedMoves_;
  private RandomTool rand_;
}

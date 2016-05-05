package examples.mazeRace;

import common.Coord;
import socketInterface.SocketAgentInterface;
import templates.Action;
import templates.Agent;
import templates.AgentInterface;
import templates.State;
import tools.ParseTools;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MazeBot implements Agent {

  public static void main(String[] args) {
    boolean showMinor = ParseTools.find(args, "-d") > -1;
    MazeBot instance = new MazeBot(showMinor);
    try {
      String url = "localhost";
      System.out.println("Enter port number of host:");
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      int port = Integer.parseInt(stdIn.readLine());
      AgentInterface connection = new SocketAgentInterface(url,
          port,
          instance,
          new MazeStateMaster(),
          new MovementActionMaster());
      connection.run();
    } catch (Exception e) {
      instance.error(e);
    }
  }

  public MazeBot(boolean showMinor){
    showMinor_ = showMinor;
  }

  @Override
  public void initialState(State debrief) {
    MazeState.Dimension dim = (MazeState.Dimension) debrief;
    dim_ = new Coord(dim.getX(), dim.getY());
    frame_ = new GridFrame(dim_.x, dim_.y, Color.gray);
    knownMaze_ = new int[dim_.x][dim_.y];
    setGoal(dim_.x/2, dim_.y/2);
    for(int y = 0; y < dim_.y; y++){
      frame_.setColour(Color.black, new Coord(0, y));
      frame_.setColour(Color.black, new Coord(dim_.x-1, y));
      knownMaze_[0][y] = -1;
      knownMaze_[dim_.x-1][y] = -1;
    }
    for(int x = 0; x < dim_.x; x++){
      frame_.setColour(Color.black, new Coord(x, 0));
      frame_.setColour(Color.black, new Coord(x, dim_.y-1));
      knownMaze_[x][0] = -1;
      knownMaze_[x][dim_.y-1] = -1;
    }
    frame_.makeVisible(true);
    frame_.redraw();
  }

  @Override
  public void updateState(State update) {
    // never occurs
  }

  @Override
  public Action decide(Action[] actions, State state) {
    MazeState.Sight sight = (MazeState.Sight) state;
    if(x_ == 0 | y_ == 0){
      // always start in corner
      for(Action move : actions){
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
      knownMaze_[x_][y_] = 1;
    }
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
    int i =  (int) Math.round(Math.random() * (actions.length-1));
    MovementAction move = (MovementAction) actions[i];
    switch (move.getDirection()){
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
    return actions[i];
  }

  @Override
  public String identity() {
    return "MazeBot";
  }

  @Override
  public void debug(boolean isMajor, Object obj) {
    if(showMinor_ || isMajor){
      System.out.println("Debug: " + obj);
    }
  }

  @Override
  public void error(Object obj) {
    System.out.println("Error: " + obj);
  }

  @Override
  public void end() {

  }

  private void setWall(int x, int y){
    frame_.setColour(Color.black, new Coord(x,y));
    knownMaze_[x][y] = -1;
  }

  private void setFloor(int x, int y){
    if(knownMaze_[x][y]!=4) {
      frame_.setColour(Color.white, new Coord(x, y));
      knownMaze_[x][y] = 1;
    }
  }

  private void setEnemy(int x, int y){
    frame_.setColour(Color.red, new Coord(x,y));
    knownMaze_[x][y] = 2;
  }

  private void setInteresting(int x, int y){
    if(knownMaze_[x][y] == 0 || knownMaze_[x][y] == 2){
      frame_.setColour(Color.green, new Coord(x,y));
      knownMaze_[x][y] = 3;
    }
  }

  private void setGoal(int x, int y){
    frame_.setColour(Color.orange, new Coord(x,y));
    knownMaze_[x][y] = 4;
  }

  private int roughDistance(int x1, int y1, int x2, int y2){
    return (y2-y1) + (x2-x1);
  }

  private MovementAction[] pathfind (int x, int y){
    return null;
  }

  private int x_;
  private int y_;
  private Coord dim_;
  private GridFrame frame_;
  private int[][] knownMaze_;
  private boolean showMinor_;

}

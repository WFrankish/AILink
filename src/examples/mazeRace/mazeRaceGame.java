package examples.mazeRace;

import common.Cardinal;
import common.Coord;
import socketInterface.SocketGameInterface;
import templates.Action;
import templates.Game;
import templates.GameInterface;
import templates.State;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class mazeRaceGame implements Game {

  public static void main(String[] args){
    boolean showMinor = (ParseTools.find(args, "-d") > -1);
    int x = ParseTools.findVal(args, "-x", 50);
    int y = ParseTools.findVal(args, "-y", 50);
    int players = ParseTools.clamp(ParseTools.findVal(args, "-p", 4), 1, 4);
    int noRooms = ParseTools.findVal(args, "-r", 50);
    int seed = ParseTools.findVal(args, "-s", (int) Math.round(Math.random()*255));
    new mazeRaceGame(showMinor, x, y, players).run(noRooms, seed);
  }

  public mazeRaceGame(boolean showMinor, int x, int y, int maxPlayers){
    gameInterface_ = new SocketGameInterface(this, new MovementActionMaster());
    showMinor_ = showMinor;
    if(x % 2 == 0){
      dimX_ = x+1;
    } else {

      dimX_ = x;
    }
    if(y % 2 == 0){
      dimY_ = y+1;
    } else {
      dimY_ = y;
    }
    maxPlayers_ = maxPlayers;
    agentNames_ = new String[maxPlayers_];
  }

  public void run(int noRooms, int seed){
    Maze maze = new Maze(dimX_, dimY_, seed, noRooms);
    while(players_ < maxPlayers_){
      gameInterface_.requestAgent();
    }
    for(int i = 0; i < players_; i++){
      maze.setPlayer();
    }
    int winner = -1;
    while(winner < 0){
      for(int i = 0; i < players_; i++){
        ArrayList<MovementAction> actions = new ArrayList<MovementAction>();
        Coord loc = maze.playerLoc(i);
        int nDist = 1;
        while(!maze.wallAt(loc.x, loc.y-nDist) && !maze.playerAt(loc.x, loc.y-nDist)){
          nDist++;
        }
        boolean nOp = maze.playerAt(loc.x, loc.y-nDist);
        if(nDist > 1){
          actions.add(MovementAction.North);
        }
        int eDist = 1;
        while(!maze.wallAt(loc.x-eDist, loc.y) && !maze.playerAt(loc.x-eDist, loc.y)){
          eDist++;
        }
        boolean eOp = maze.playerAt(loc.x-eDist, loc.y);
        if(eDist > 1){
          actions.add(MovementAction.East);
        }
        int sDist = 1;
        while(!maze.wallAt(loc.x, loc.y+sDist) && !maze.playerAt(loc.x, loc.y+sDist)){
          sDist++;
        }
        boolean sOp = maze.playerAt(loc.x, loc.y+sDist);
        if(sDist > 1){
          actions.add(MovementAction.South);
        }
        int wDist = 1;
        while(!maze.wallAt(loc.x+wDist, loc.y) && !maze.playerAt(loc.x+wDist, loc.y)){
          wDist++;
        }
        boolean wOp = maze.playerAt(loc.x+wDist, loc.y);
        if(wDist > 1){
          actions.add(MovementAction.West);
        }
        if(!actions.isEmpty()){
          MazeState.Sight state = new MazeState.Sight(nOp, nDist, eOp, eDist, sOp, sDist, wOp, wDist);
          MovementAction[] actionsA = actions.toArray(new MovementAction[1]);
          Action result = gameInterface_.requestAction(i, state, actionsA);
          Cardinal movement = ((MovementAction) result).getDirection();
          if(maze.canMove(i, movement)){
            maze.move(i, movement);
          }
          else {
            maze.killPlayer(i);
          }
          if(maze.hasWon(i)){
            winner = i;
          }
        }
      }
    }
    for(int i = 0; i<players_; i++){
      gameInterface_.updateState(i, new MazeState.Winner(agentNames_[winner]));
    }
  }

  @Override
  public State registerAgent(String agent, int agentNo) {
    message(agent + "wants to play.");
    while(true){
      message("Enter y to accept, n to reject.");
      try {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String temp = stdIn.readLine();
        if (temp.length() > 0) {
          char c = temp.charAt(0);
          if (c == 'y' || c == 'Y') {
            agentNames_[players_] = agent;
            players_++;
            return new MazeState.Dimension(dimX_, dimY_);
          } else if (c == 'n' || c == 'N') {
            return null;
          }
        }
      } catch (Exception e) {
        error(e);
        return null;
      }
    }
  }

  @Override
  public String identity() {
    return "Maze Race";
  }

  @Override
  public void message(Object obj) {
    System.out.println("Message for Game Master: "+obj);
  }

  @Override
  public void debug(boolean isMajor, Object obj) {
    if(isMajor || showMinor_){
      System.out.println("Debug: "+obj);
    }
  }

  @Override
  public void error(Object obj) {
    System.out.println("Error: "+obj);
  }

  @Override
  public void end() {

  }

  private String[] agentNames_;
  private int dimX_;
  private int dimY_;
  private int players_;
  private int maxPlayers_;
  private GameInterface gameInterface_;
  private boolean showMinor_;
}

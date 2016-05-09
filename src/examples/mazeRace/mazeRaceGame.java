package examples.mazeRace;

import common.Cardinal;
import common.Coord;
import common.Tuple;
import socketInterface.SocketGameInterface;
import interfaces.Action;
import interfaces.Game;
import interfaces.GameInterface;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class mazeRaceGame implements Game {

  public static void main(String[] args){
    new mazeRaceGame(args).run();
  }

  public mazeRaceGame(String[] args){
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
    slowGenerate_ = (ParseTools.find(args, "-g") > -1);
    noRooms_ = ParseTools.findVal(args, "-r", 50);
    seed_ = ParseTools.findVal(args, "-s", (int) Math.round(Math.random()*255));
    interface_ = new SocketGameInterface(this, new MovementActionMaster());
    int x = ParseTools.findVal(args, "-x", 50);
    int y = ParseTools.findVal(args, "-y", 50);
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
    maxPlayers_ = ParseTools.clamp(ParseTools.findVal(args, "-p", 4), 1, 4);
    agentNames_ = new String[maxPlayers_];
    agentIDs_ = new int[maxPlayers_];
  }

  public void run(){
    Maze maze = new Maze(dimX_, dimY_, seed_, noRooms_, slowGenerate_);
    int noPlayers = 0;
    debug(true, "Seeking " + maxPlayers_ + " players.");
    while(noPlayers < maxPlayers_){
      Tuple<Integer, String> agentInfo = interface_.findAgent();
      int agentNo = agentInfo.fst;
      String agent = agentInfo.snd;
      if(acceptAgent(agent)) {
        maze.setPlayer();
        agentIDs_[noPlayers] = agentNo;
        agentNames_[noPlayers] = agent;
        noPlayers++;
        debug(true, "Added agent " + agent);
        debug(false, "Now the number of agents is: " + noPlayers);
      } else {
        debug(true, "Rejecting agent " + agent);
        interface_.terminateAgent(agentNo, "Rejected.");
      }
    }
    debug(true, "Sending initial state.");
    for(int agentID : agentIDs_){
      interface_.sendState(agentID, new MazeState.Dimension(dimX_, dimY_));
    }
    debug(true, "Beginning game.");
    int winner = -1;
    while(winner < 0){
      for(int player = 0; player < noPlayers; player++){
        debug(false, "Seeking actions for player " + player);
        ArrayList<MovementAction> actions = new ArrayList<MovementAction>();
        Coord loc = maze.playerLoc(player);
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
          debug(true, "Next to move is player " + player);
          MazeState.Sight state = new MazeState.Sight(nOp, nDist, eOp, eDist, sOp, sDist, wOp, wDist);
          MovementAction[] actionsA = actions.toArray(new MovementAction[1]);
          Action result = interface_.requestAction(agentIDs_[player], state, actionsA);
          Cardinal movement = ((MovementAction) result).getDirection();
          if(maze.canMove(player, movement)){
            maze.move(player, movement);
          }
          else {
            maze.killPlayer(player);
            interface_.terminateAgent(agentIDs_[player], "Illegal Move attempted.");
          }
          if(maze.hasWon(player)){
            winner = player;
            for(int j = 0; j < noPlayers; j++){
              interface_.sendState(agentIDs_[j], new MazeState.Winner(agentNames_[player]));
              interface_.terminateAgent(agentIDs_[j], "Game Over.");
            }
          }
        }
      }
    }
    for(int i = 0; i<noPlayers; i++){
      interface_.sendState(agentIDs_[i], new MazeState.Winner(agentNames_[winner]));
    }
  }

  private boolean acceptAgent(String agent) {
    message(agent + "wants to play.");
    while(true){
      message("Enter y to accept, n to reject.");
      try {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String temp = stdIn.readLine();
        if (temp.length() > 0) {
          char c = temp.charAt(0);
          if (c == 'y' || c == 'Y') {
            return true;
          }
          else if (c == 'n' || c == 'N'){
            return false;
          }
        }
      } catch (Exception e) {
        error(e);
        return false;
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
    if(showDebug_ && (isMajor || showMinor_)){
      System.out.println("Debug: "+obj);
    }
  }

  @Override
  public void error(Object obj) {
    System.out.println("Error: "+obj);
  }

  private String[] agentNames_;
  private int[] agentIDs_;
  private int dimX_;
  private int dimY_;
  private int maxPlayers_;
  private GameInterface interface_;
  private boolean showDebug_;
  private boolean showMinor_;
  private int seed_;
  private int noRooms_;
  private boolean slowGenerate_;
}

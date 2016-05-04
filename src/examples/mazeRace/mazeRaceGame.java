package examples.mazeRace;

import socketInterface.SocketGameInterface;
import templates.Action;
import templates.Game;
import templates.GameInterface;
import templates.State;
import tools.ParseTools;

import java.awt.*;
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
    GridFrame frame = new GridFrame(dimX_, dimY_);
    Maze maze = new Maze(frame, dimX_, dimY_, seed, noRooms);
    while(players_ < maxPlayers_){
      gameInterface_.requestAgent();
    }
    MazeState.Dimension[] playerLocs = new MazeState.Dimension[players_];
    if(players_ > 0){
      playerLocs[0] = new MazeState.Dimension(1,1);
      frame.setColour(colours[0], 1, 1);
    }
    if(players_ > 1){
      playerLocs[1] = new MazeState.Dimension(1, dimY_ -2);
      frame.setColour(colours[1], 1, dimY_ - 2);
    }
    if(players_ > 2){
      playerLocs[2] = new MazeState.Dimension(dimX_ -2,1);
      frame.setColour(colours[2], dimX_ - 2, 1);
    }
    if(players_ > 3){
      playerLocs[3] = new MazeState.Dimension(dimX_ -2, dimY_ -2);
      frame.setColour(colours[3], dimX_ - 2, dimY_ - 2);
    }
    frame.setColour(Color.orange, dimX_ / 2, dimY_ / 2);
    frame.redraw(true);
    int winner = -1;
    frame.setIsFast(false);
    while(winner < 0){
      for(int i = 0; i < players_; i++){
        ArrayList<MovementAction> actions = new ArrayList<MovementAction>();
        int x = playerLocs[i].getX();
        int y = playerLocs[i].getY();
        int nDist = 1;
        boolean nOp = false;
        while(!maze.wallAt(x, y-nDist) && !nOp){
          if(playersHere(x, y-nDist, playerLocs)){
            nOp = true;
          }
          else{
            nDist++;
          }
        }
        if(nDist > 1){
          actions.add(MovementAction.North);
        }
        int eDist = 1;
        boolean eOp = false;
        while(!maze.wallAt(x-eDist, y) && !eOp){
          if(playersHere(x-eDist, y, playerLocs)){
            eOp = true;
          }
          else{
            eDist++;
          }
        }
        if(eDist > 1){
          actions.add(MovementAction.East);
        }
        int sDist = 1;
        boolean sOp = false;
        while(!maze.wallAt(x, y+sDist) && !sOp){
          if(playersHere(x, y+sDist, playerLocs)){
            sOp = true;
          }
          else{
            sDist++;
          }
        }
        if(sDist > 1){
          actions.add(MovementAction.South);
        }
        int wDist = 1;
        boolean wOp = false;
        while(!maze.wallAt(x+wDist, y) && !wOp){
          if(playersHere(x+wDist, y, playerLocs)){
            wOp = true;
          }
          else{
            wDist++;
          }
        }
        if(wDist > 1) {
          actions.add(MovementAction.West);
        }
        if(!actions.isEmpty()){
          MazeState.Sight state = new MazeState.Sight(nOp, nDist, eOp, eDist, sOp, sDist, wOp, wDist);
          MovementAction[] actionsA = actions.toArray(new MovementAction[0]);
          Action result = gameInterface_.requestAction(i, state, actionsA);
          MovementAction movement = (MovementAction) result;
          int nx = playerLocs[i].getX();
          int ny = playerLocs[i].getY();
          if(movement.equals(MovementAction.North)){
            ny--;
          }
          else if(movement.equals(MovementAction.East)){
            nx--;
          }
          else if(movement.equals(MovementAction.South)){
            ny++;
          }
          else{
            nx++;
          }
          playerLocs[i] = new MazeState.Dimension(nx, ny);
          frame.setColour(Color.white, x, y);
          frame.setColour(colours[i], nx, ny);
          frame.redraw(false);
          if(nx == dimX_ / 2 && ny == dimY_ / 2){
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

  private boolean playersHere(int x, int y, MazeState.Dimension[] locs){
    for(int i = 0; i<locs.length; i++){
      MazeState.Dimension dim = locs[i];
      if(dim.getX()== x && dim.getY()==y){
        return true;
      }
    }
    return false;
  }

  private String[] agentNames_;
  private int dimX_;
  private int dimY_;
  private int players_;
  private int maxPlayers_;
  private GameInterface gameInterface_;
  private boolean showMinor_;
  private Color[] colours = new Color[]{ Color.red, Color.blue, Color.green, Color.magenta};
}

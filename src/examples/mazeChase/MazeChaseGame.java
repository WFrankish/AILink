package examples.mazeChase;


import common.Cardinal;
import common.Coord;
import common.Grid;
import common.Tuple;
import interfaces.Action;
import interfaces.Game;
import interfaces.GameInterface;
import socketInterface.SocketGameInterface;
import tools.ParseTools;
import examples.mazeChase.ChaseState.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

public class MazeChaseGame implements Game {

  public static void main(String[] args){
    new MazeChaseGame(args).run();
  }

  public MazeChaseGame(String[] args){
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
    agentIDs_ = new int[3];
    agentNames_ = new String[3];
    interface_ = new SocketGameInterface(this, new ChaseActionMaster());
    int x = ParseTools.findVal(args, "-x", 30);
    int y = ParseTools.findVal(args, "-y", 30);
    int seed = ParseTools.findVal(args, "-s", (int) Math.round(Math.random()*255));
    int rooms = ParseTools.findVal(args, "-r", 500);
    maze_ = new Maze(x, y, seed, rooms, false);
  }

  @Override
  public String identity() {
    return "Maze Chase Game";
  }

  @Override
  public void message(Object obj) {
    System.out.println("Message for Game Master: " + obj);
  }

  @Override
  public void debug(boolean isMajor, Object obj) {
    if(showDebug_ && (showMinor_ || isMajor)){
      System.out.println("Debug: " + obj);
    }
  }

  @Override
  public void error(Object obj) {
    System.out.println("Error: " + obj);
  }


  public void run(){
    int noChasers = 0;
    boolean runnerFound = false;
    debug(true, "Seeking 3 players.");
    while(noChasers < 2 || !runnerFound){
      Tuple<Integer, String> agentInfo = interface_.findAgent();
      int agentID = agentInfo.fst;
      String agent = agentInfo.snd;
      int response = acceptAgent(agent, noChasers < 2, !runnerFound);
      switch (response){
        case 1:{
          maze_.setPlayer();
          agentIDs_[noChasers] = agentID;
          agentNames_[noChasers] = agent;
          noChasers++;
          debug(true, "Added agent " + agent + " as chaser no. " + noChasers);
          break;
        }
        case 2:{
          maze_.setPlayer();
          agentIDs_[2] = agentID;
          agentNames_[2] = agent;
          runnerFound = true;
          debug(true, "Added agent " + agent + " as runner");
          break;
        }
        default:{
          debug(true, "Rejecting agent " + agent);
          interface_.terminateAgent(agentID, "Rejected.");
        }
      }
    }
    debug(true, "Sending initial state.");
    interface_.sendState(agentIDs_[0], new You(Thing.CHASER1));
    interface_.sendState(agentIDs_[1], new You(Thing.CHASER2));
    interface_.sendState(agentIDs_[2], new You(Thing.RUNNER));
    debug(true, "Beginning game.");
    int runnerHealth = 10;
    int turns = 0;
    boolean failed = false;
    main:
    while (runnerHealth > 0){
      for(int p = 0; p<3; p++){
        debug(true, "Next is player " + p);
        if(p < 2){
          Visible chaserVis = getChaserVision();
          interface_.sendState(agentIDs_[p], chaserVis);
          Action chosen = interface_.requestAction(agentIDs_[p]);
          if(chosen instanceof ChaseAction) {
            ChaseAction action = (ChaseAction) chosen;
            if (action.action == ChaseAction.ActionType.MOVE) {
              if (maze_.canMove(p, action.direction)) {
                maze_.move(p, action.direction);
              }
            } else {
              Coord shooter = maze_.playerLoc(p);
              Coord target = maze_.playerLoc(2);
              boolean hit = false;
              if(shooter.x == target.x){
                hit = true;
                int from = Math.min(shooter.x, target.x) + 1;
                int to = Math.max(shooter.x, target.x);
                for(int i = from; i < to; i++){
                  if(maze_.whatsAt(i, shooter.y) != Thing.NOTHING){
                    hit = false;
                    break;
                  }
                }
              }
              else if(shooter.y == target.y){
                hit = true;
                int from = Math.min(shooter.y, target.y) + 1;
                int to = Math.max(shooter.y, target.y);
                for(int i = from; i < to; i++){
                  if(maze_.whatsAt(shooter.x, i) != Thing.NOTHING){
                    hit = false;
                    break;
                  }
                }
              }
              if(hit){
                runnerHealth--;
              }
            }
          } else {
            debug(true, "Action not valid");
            failed = true;
            interface_.terminateAgent(agentIDs_[p], "Invalid Action");
            break main;
          }
        } else {
          Visible runnerVis = new Visible(maze_.cloneMap());
          interface_.sendState(agentIDs_[p], runnerVis);
          Action chosen = interface_.requestAction(agentIDs_[p]);
          if(chosen instanceof ChaseAction){
            ChaseAction action = (ChaseAction) chosen;
            if(action.action == ChaseAction.ActionType.MOVE){
              if(maze_.canMove(p, action.direction)){
                maze_.move(p, action.direction);
              }
            }
          } else {
            debug(true, "Action not valid");
            failed = true;
            interface_.terminateAgent(agentIDs_[p], "Invalid Action");
            break main;
          }
        }
      }
      turns++;
    }
    if(failed){
      debug(true, "Game has gone wrong.");
      for(int p = 0; p < 3; p++){
        interface_.terminateAgent(p, "An agent has attempted an invalid action");
      }
    } else {
      debug(true, "Game is over, releasing results.");
      TimeLasted result = new TimeLasted(turns);
      for(int p = 0; p < 3; p++){
        interface_.sendState(p, result);
      }
    }

    interface_.end();
  }

  public int acceptAgent(String agent, boolean needChaser, boolean needRunner){
    message(agent + " wants to play");
    try {
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      if (needChaser && needRunner) {
        message("Enter C for chaser or R for runner, enter nothing to reject");
        String response = stdIn.readLine();
        if(response.length() == 0) {
          return 0;
        } else {
          char c = response.charAt(0);
          if(c == 'C' || c == 'c'){
            return 1;
          } else if(c == 'R' | c == 'r'){
            return 2;
          } else {
            return 0;
          }
        }
      } else if (needChaser) {
        message("Enter C for chaser, enter nothing to reject");
        String response = stdIn.readLine();
        if(response.length() == 0) {
          return 0;
        } else {
          char c = response.charAt(0);
          if(c == 'C' || c == 'c'){
            return 1;
          } else {
            return 0;
          }
        }
      } else if (needRunner) {
        message("Enter R for runner, enter nothing to reject");
        String response = stdIn.readLine();
        if(response.length() == 0) {
          return 0;
        } else {
          char c = response.charAt(0);
          if(c == 'R' | c == 'r'){
            return 2;
          } else {
            return 0;
          }
        }
      } else {
        error("Game is full.");
        return 0;
      }
    }
    catch (Exception e) {
      error(e);
      return 0;
    }
  }

  private Visible getChaserVision(){
    Grid<Thing> maze = maze_.cloneMap();
    Grid<Thing> visible = new Grid<Thing>(maze.width(), maze.height(), Thing.UNKNOWN, Thing.WALL);
    Coord one = maze_.playerLoc(0);
    Coord two = maze_.playerLoc(1);
    // temporarily set player locations as empty
    visible.set(one, Thing.NOTHING);
    visible.set(two, Thing.NOTHING);
    Queue<Coord> queue = new LinkedList<Coord>();
    // Set immediate lines of sight
    for(Cardinal dir : Cardinal.values()){
      Coord next = one.apply(dir);
      while(maze.get(next) == Thing.NOTHING){
        queue.add(next);
        visible.set(next, maze.get(next));
        next = next.apply(dir);
      }
      visible.set(next, maze.get(next));
      next = two.apply(dir);
      while(maze.get(next) == Thing.NOTHING){
        queue.add(next);
        visible.set(next, maze.get(next));
        next = next.apply(dir);
      }
      visible.set(next, maze.get(next));
    }
    // If two adjacent (inc diagonals) squares are visible and empty, you can see it.
    // Queue should cause squares checked to fan out in a diamond
    while(!queue.isEmpty()){
      Coord cur = queue.remove();
      int emptyAdjacent = 0;
      if(visible.get(new Coord(cur.x-1, cur.y)) == Thing.NOTHING){
        emptyAdjacent++;
      }
      if(visible.get(new Coord(cur.x-1, cur.y-1)) == Thing.NOTHING){
        emptyAdjacent++;
      }
      if(visible.get(new Coord(cur.x, cur.y-1)) == Thing.NOTHING){
        emptyAdjacent++;
      }
      if(visible.get(new Coord(cur.x+1, cur.y-1)) == Thing.NOTHING){
        emptyAdjacent++;
      }
      if(visible.get(new Coord(cur.x+1, cur.y)) == Thing.NOTHING){
        emptyAdjacent++;
      }
      if(visible.get(new Coord(cur.x+1, cur.y+1)) == Thing.NOTHING){
        emptyAdjacent++;
      }
      if(visible.get(new Coord(cur.x, cur.y+1)) == Thing.NOTHING){
        emptyAdjacent++;
      }
      if(visible.get(new Coord(cur.x-1, cur.y+1)) == Thing.NOTHING){
        emptyAdjacent++;
      }
      if(emptyAdjacent > 1){
        visible.set(cur, maze.get(cur));
      }
      // Add adjacent (not inc diagonals) squares to queue;
      if(visible.get(cur) == Thing.NOTHING){
        for(Cardinal dir : Cardinal.values()) {
          Coord adj = cur.apply(dir);
          if (visible.get(adj) == Thing.UNKNOWN) {
            queue.add(cur.apply(dir));
          }
        }
      }
    }
    visible.set(one, Thing.CHASER1);
    visible.set(two, Thing.CHASER2);

    return new Visible(visible);
  }

  private GameInterface interface_;
  private boolean showMinor_;
  private boolean showDebug_;
  private int[] agentIDs_;
  private String[] agentNames_;
  private Maze maze_;
}

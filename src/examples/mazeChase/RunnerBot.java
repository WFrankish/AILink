package examples.mazeChase;

import common.*;
import examples.mazeRace.GridFrame;
import interfaces.Action;
import interfaces.Agent;
import interfaces.AgentInterface;
import interfaces.State;
import socketInterface.SocketAgentInterface;
import tools.ParseTools;
import tools.RandomTool;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class RunnerBot implements Agent {

  public static void main(String[] args) {
    RunnerBot instance = new RunnerBot(args);
    AgentInterface connection = new SocketAgentInterface(
        instance,
        new ChaseStateMaster());
    connection.run();
  }

  public RunnerBot(String[] args){
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
    showDeadEndsMap_ = (ParseTools.find(args, "-v") > -1);
    int seed = ParseTools.findVal(args, "-r", 0);
    allowedMoves_ = new ArrayList<ChaseAction>();
  }

  @Override
  public void perceiveState(State update) {
    if(update instanceof ChaseState.You){
      assert(((ChaseState.You) update).you == ChaseState.Thing.RUNNER);
    }
    else if(update instanceof ChaseState.TimeLasted){
      System.out.println(update.toReadable());
    }
    else if(update instanceof ChaseState.Visible) {
      maze_ = ((ChaseState.Visible) update).getVisible();
      if (noExits_ == null) {
        // first time seeing the map
        // find players
        int found = 0;
        search:
        for(int x = 0; x<maze_.width(); x++){
          for(int y = 0; y<maze_.height(); y++){
            ChaseState.Thing thing =  maze_.get(x, y);
            if(thing == ChaseState.Thing.RUNNER){
              us_ = new Coord(x, y);
              found++;
            }
            else if(thing == ChaseState.Thing.CHASER1){
              them1_ = new Coord(x, y);
              found++;
            }
            else if(thing == ChaseState.Thing.CHASER2){
              them2_ = new Coord(x, y);
              found++;
            }
            if(found > 2){
              break search;
            }
          }
        }
        // make map showing movement options in corridors, room location
        // seek to find dead ends so the agent knows to never enter them
        Stack<Coord> ones = new Stack<Coord>();
        ArrayList<Coord> fives = new ArrayList<Coord>();
        noExits_ = new Grid<Integer>(maze_.width(), maze_.height(), -1, 0);
        // first pass - mark rooms as 5, otherwise mark squares as the directions they can move in
        for (int x = 0; x < noExits_.width(); x++) {
          for (int y = 0; y < noExits_.height(); y++) {
            if(maze_.get(x, y) == ChaseState.Thing.WALL){
              noExits_.set(x, y, 0);
            } else {
              Coord loc = new Coord(x, y);
              boolean N = maze_.get(loc.add(0, -1)) != ChaseState.Thing.WALL;
              boolean NE = maze_.get(loc.add(-1, -1)) != ChaseState.Thing.WALL;
              boolean E = maze_.get(loc.add(-1, 0)) != ChaseState.Thing.WALL;
              boolean SE = maze_.get(loc.add(-1, 1)) != ChaseState.Thing.WALL;
              boolean S = maze_.get(loc.add(0, 1)) != ChaseState.Thing.WALL;
              boolean SW = maze_.get(loc.add(1, 1)) != ChaseState.Thing.WALL;
              boolean W = maze_.get(loc.add(1, 0)) != ChaseState.Thing.WALL;
              boolean NW = maze_.get(loc.add(1, -1)) != ChaseState.Thing.WALL;
              // a room square is any square that can get to a diagonal with more than set of two moves
              if((N && NE && E) || (E && SE && S) || (S && SW && W) || (W && NW && N)){
                noExits_.set(loc, 5);
                fives.add(loc);
              } else {
                int count = 0;
                if(N){ count++; }
                if(E){ count++; }
                if(S){ count++; }
                if(W){ count++; }
                if(count == 1){
                   //This defines a room-square as any square which is part of a 2*2 block of floor
                  noExits_.set(loc, 2);
                  ones.add(loc);
                } else {
                  noExits_.set(loc, count);
                }
              }
            }

          }
        }

        // process further
        while(!fives.isEmpty()) {

          // fill out obviously deadend rooms
          // fill out deadend corridors
          // see if deadend corridors reveal more candidate deadend rooms

          // find rooms with only one exit
          // fives contains all room squares, rather then all rooms
          // but once we start exploring a room, all its room squares will be removed from fives
          while (!fives.isEmpty()) {
            Stack<Coord> toCheck = new Stack<Coord>();
            ArrayList<Coord> forLater = new ArrayList<Coord>();
            toCheck.push(fives.get(0));
            int twos = 0;
            // will never be called while still null
            Coord exitIfOnly = null;
            while (!toCheck.isEmpty()) {
              Coord cur = toCheck.pop();
              fives.remove(cur);
              // spread out to adjacent room squares, looking for exits
              for (Cardinal dir : Cardinal.values()) {
                Coord next = cur.apply(dir);
                if (noExits_.get(next) == 2 || noExits_.get(next) == 3) {
                  twos++;
                  exitIfOnly = next;
                } else if (noExits_.get(next) == 5 && !forLater.contains(next)) {
                  // part of the room, continue exploring it
                  toCheck.push(next);
                  forLater.add(next);
                }
              }
            }
            if (twos == 1) {
              // for easier loop logic, set this exit square to have 2 options for now
              noExits_.set(exitIfOnly, 2);
              ones.add(exitIfOnly);
              // mark dead end rooms as such
              for(Coord coord : forLater){
                noExits_.set(coord, 1);
              }
            }
          }
        }


        // fill out deadEnd corridors
        // if a corridor branches out into two dead end corridors, this will still catch them
        while(!ones.isEmpty()){
          Coord cur = ones.pop();
          split:
          while (noExits_.get(cur) == 2){
            // while we're still going along a linear corridor
            noExits_.set(cur, 1);
            // look for next point in the corridor;
            for(Cardinal dir : Cardinal.values()){
              Coord next = cur.apply(dir);
              if(noExits_.get(next) == 2){
                cur = next;
                break;
              }
              else if(noExits_.get(next) == 5){
                // found a new room, will need to reprocess that room
                fives.add(next);
                break split;
              }
              else if(noExits_.get(next) > 1){
                // found a branch, take away one movement option
                noExits_.set(next, noExits_.get(next)-1);
                break;
              }
            }
          }
        }

        if(showDeadEndsMap_) {
          GridFrame frame = new GridFrame(noExits_.width(), noExits_.height(), Color.black);
          Color[] colours = new Color[]{
              Color.black, Color.red, Color.blue, Color.green, Color.yellow, Color.white
          };
          for (int y = 0; y < noExits_.height(); y++) {
            for (int x = 0; x < noExits_.width(); x++) {
              int no = noExits_.get(x, y);
              frame.setColour(colours[no], new Coord(x, y));
            }
          }
          frame.makeVisible(true);
          frame.redraw();
        }
      } else {
        // find moving entities knowing they can have only moved one square
        if(maze_.get(us_)!= ChaseState.Thing.RUNNER){
          for(Cardinal dir : Cardinal.values()){
            Coord next = us_.apply(dir);
            if(maze_.get(next) == ChaseState.Thing.RUNNER){
              us_ = next;
              break;
            }
          }
        }
        if(maze_.get(them1_)!= ChaseState.Thing.CHASER1){
          for(Cardinal dir : Cardinal.values()){
            Coord next = them1_.apply(dir);
            if(maze_.get(next) == ChaseState.Thing.CHASER1){
              them1_ = next;
              break;
            }
          }
        }
        if(maze_.get(them2_)!= ChaseState.Thing.CHASER2){
          for(Cardinal dir : Cardinal.values()){
            Coord next = them2_.apply(dir);
            if(maze_.get(next) == ChaseState.Thing.CHASER2){
              them2_ = next;
              break;
            }
          }
        }

      }

      // find allowed moves
      allowedMoves_ = new ArrayList<ChaseAction>();
      for(Cardinal dir : Cardinal.values()){
        if(maze_.get(us_.apply(dir))== ChaseState.Thing.NOTHING){
          allowedMoves_.add(new ChaseAction(ChaseAction.ActionType.MOVE, dir));
        }
      }

      // recreate proximity map
      proximity_ = new Grid<Integer>(maze_.width(), maze_.height(), -1, -2);
      ArrayList<Tuple<Coord, Integer>> queue = new ArrayList<Tuple<Coord, Integer>>();
      queue.add(new Tuple<Coord, Integer>(them1_, 0));
      queue.add(new Tuple<Coord, Integer>(them2_, 0));
      while(!queue.isEmpty()){
        Tuple<Coord, Integer> loc = queue.remove(0);
        if(proximity_.get(loc.fst) == -1) {
          proximity_.set(loc.fst, loc.snd);
          for (Cardinal dir : Cardinal.values()) {
            Coord next = loc.fst.apply(dir);
            if (maze_.get(next) == ChaseState.Thing.NOTHING) {
              queue.add(new Tuple<Coord, Integer>(next, loc.snd + 1));
            }
          }
        }
      }

    }
  }

  @Override
  public Action decide() {

    // find furthest point from chasers, weighted to avoid unsafe spots
    Coord best = new Coord(0, 0);
    int distance = 0;
    for(int x = 0; x < maze_.width(); x++){
      for(int y =0; y < maze_.height(); y++){
        if(maze_.get(x, y)!= ChaseState.Thing.WALL){
          int current = proximity_.get(x, y) * noExits_.get(x, y);
          if(current > distance){
            distance = current;
            best = new Coord(x, y);
          }
        }
      }
    }

    KeyedQueue<Tuple<ArrayList<Cardinal>, Coord>> search = new KeyedQueue<Tuple<ArrayList<Cardinal>, Coord>>();
    search.add(0, new Tuple<ArrayList<Cardinal>, Coord>(new ArrayList<Cardinal>(), us_));
    Grid<Boolean> found = new Grid<Boolean>(maze_.width(), maze_.height(), false, true);
    while(!search.isEmpty()){
      Tuple<ArrayList<Cardinal>, Coord> next = search.pop();
      found.set(next.snd, true);
      if(next.snd.equals(best)){
        if(next.fst.size()==0){
          // returning shoot as the runner acts as choosing to not move
          return new ChaseAction(ChaseAction.ActionType.SHOOT, Cardinal.NORTH);
        } else {
          return new ChaseAction(ChaseAction.ActionType.MOVE, next.fst.get(0));
        }
      } else {
        for(Cardinal dir : Cardinal.values()){
          Coord loc = next.snd.apply(dir);
          if(maze_.get(loc) == ChaseState.Thing.NOTHING && !found.get(loc)){
            int k = next.fst.size() + Math.abs(best.x - loc.x) + Math.abs(best.y - loc.y);
            ArrayList<Cardinal> newPath = (ArrayList<Cardinal>) next.fst.clone();
            newPath.add(dir);
            search.add(k, new Tuple<ArrayList<Cardinal>, Coord>(newPath, loc));
          }
        }
      }

    }
    return new ChaseAction(ChaseAction.ActionType.MOVE, Cardinal.NORTH);
  }

  @Override
  public String identity() {
    return "Runner Bot";
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


  @Override
  public boolean checkGame(String ident) {
    return ident.equals("Maze Chase Game");
  }

  private boolean showMinor_;
  private boolean showDebug_;
  private boolean showDeadEndsMap_;
  private Coord us_;
  private Coord them1_;
  private Coord them2_;
  private ArrayList<ChaseAction> allowedMoves_;
  private Grid<ChaseState.Thing> maze_;
  private Grid<Integer> noExits_;
  private Grid<Integer> proximity_;
 }

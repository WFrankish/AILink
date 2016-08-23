package examples.mazeChase;

import common.*;
import examples.mazeRace.GridFrame;
import interfaces.Action;
import interfaces.Agent;
import interfaces.AgentInterface;
import interfaces.State;
import socketInterface.SocketAgentInterface;
import tools.ParseTools;

import java.util.ArrayList;
import java.util.Stack;

public class ChaserBot implements Agent {

  public static void main(String[] args) {
    ChaserBot instance = new ChaserBot(args);
    AgentInterface connection = new SocketAgentInterface(
        instance,
        new ChaseStateMaster());
    connection.run();
  }

  public ChaserBot(String[] args){
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
    plannedMoves_ = new ArrayList<Cardinal>();
  }

  @Override
  public void perceiveState(State update) {
    if(update instanceof ChaseState.You){
      me_ = ((ChaseState.You) update).you;
      if(me_ != ChaseState.Thing.CHASER1 && me_ != ChaseState.Thing.CHASER2){
        assert false;
      }
    }
    else if(update instanceof ChaseState.TimeLasted){
      System.out.println(update.toString());
    }
    else if(update instanceof ChaseState.Visible){
      Grid<ChaseState.Thing> visible =((ChaseState.Visible) update).getVisible();
      if(maze_ == null){
        maze_ = new Grid<ChaseState.Thing>(visible.width(), visible.height(), ChaseState.Thing.UNKNOWN, ChaseState.Thing.WALL);
        canSee_ = new Grid<Boolean>(maze_.width(), maze_.height(), false, false);
        runnerSpace_ = new Grid<Boolean>(maze_.width(), maze_.height(), false, false);
        runnerSpace_.set(maze_.width()/2, maze_.height()/2, true);
        targets_ = new ArrayList<Coord>();
      }
      // expand runnerSpace by one square in each direction
      Stack<Coord> runnerLocs = new Stack<Coord>();
      for(int x = 0; x < runnerSpace_.width(); x++){
        for(int y = 0; y < runnerSpace_.height(); y++){
          if(runnerSpace_.get(x, y)){
            runnerLocs.add(new Coord(x, y));
          }
        }
      }
      for(Coord loc : runnerLocs){
        for(Cardinal dir : Cardinal.values()){
          Coord newLoc = loc.apply(dir);
          if(maze_.get(newLoc)!= ChaseState.Thing.WALL){
            runnerSpace_.set(newLoc, true);
          }
        }
      }

      // process visible state
      runnerLoc_ = null;
      for(int x = 0; x < maze_.width(); x++){
        for(int y = 0; y < maze_.height(); y++){
          switch ((visible.get(x, y))){
            case WALL:{
              maze_.set(x,y, ChaseState.Thing.WALL);
              runnerSpace_.set(x, y, false);
              canSee_.set(x, y, true);
              targets_.remove(new Coord(x, y));
              break;
            }
            case NOTHING:{
              maze_.set(x, y, ChaseState.Thing.NOTHING);
              runnerSpace_.set(x, y, false);
              canSee_.set(x, y, true);
              targets_.remove(new Coord(x, y));
              break;
            }
            case CHASER1:{
              if(me_ == ChaseState.Thing.CHASER1){
                myLoc_ = new Coord(x, y);
              } else {
                allyLoc_ = new Coord(x, y);
              }
              runnerSpace_.set(x, y, false);
              canSee_.set(x, y, true);
              targets_.remove(new Coord(x, y));
              break;
            }
            case CHASER2:{
              if(me_ == ChaseState.Thing.CHASER2){
                myLoc_ = new Coord(x, y);
              } else {
                allyLoc_ = new Coord(x, y);
              }
              targets_.remove(new Coord(x, y));
              runnerSpace_.set(x, y, false);
              canSee_.set(x, y, true);
              break;
            }
            case RUNNER:{
              runnerSpace_ = new Grid<Boolean>(maze_.width(), maze_.height(), false, false);
              runnerSpace_.set(x, y, true);
              canSee_.set(x, y, true);
              targets_.clear();
              targets_.add(new Coord(x, y));
              plannedMoves_.clear();
              runnerLoc_ = new Coord(x, y);
              break;
            }
            case UNKNOWN:{
              canSee_.set(x, y, false);
              if(runnerLoc_ == null && maze_.get(x, y) == ChaseState.Thing.UNKNOWN){
                for(Cardinal dir : Cardinal.values()){
                  Coord next = new Coord(x, y).apply(dir);
                  if(maze_.get(next) == ChaseState.Thing.NOTHING || visible.get(next) == ChaseState.Thing.NOTHING){
                    targets_.add(new Coord(x, y));
                    break;
                  }
                }
              }
              break;
            }
          }
        }
      }

      /*
      System.out.println(canSee_.toReadable());
      System.out.println(runnerSpace_.toReadable());
      System.out.println(visible.toReadable());
      System.out.println(maze_.toReadable());
      */
      /*
      if(frame_ == null) {
        frame_ = new GridFrame(runnerSpace_.width(), runnerSpace_.height(), Color.black);
      }
      for(int x = 0; x<runnerSpace_.width(); x++){
        for(int y = 0; y<runnerSpace_.height(); y++){
          if(targets_.contains(new Coord(x, y))){
            frame_.setColour(Color.green, new Coord(x, y));
          }
          else if(maze_.get(x, y) == ChaseState.Thing.UNKNOWN) {
            frame_.setColour(Color.red, new Coord(x, y));
          } else {
            frame_.setColour(Color.black, new Coord(x, y));
          }
        }
      }
      frame_.makeVisible(true);
      frame_.redraw();
      */

    }
  }

  @Override
  public Action decide() {
    if(runnerLoc_ != null && (runnerLoc_.x == myLoc_.x  || runnerLoc_.y == myLoc_.y)) {
      if (runnerLoc_.y < myLoc_.y) {
        return new ChaseAction(ChaseAction.ActionType.SHOOT, Cardinal.NORTH);
      } else if (runnerLoc_.y > myLoc_.y) {
        return new ChaseAction(ChaseAction.ActionType.SHOOT, Cardinal.SOUTH);
      } else if (runnerLoc_.x < myLoc_.x) {
        return new ChaseAction(ChaseAction.ActionType.SHOOT, Cardinal.EAST);
      } else if (runnerLoc_.x > myLoc_.x) {
        return new ChaseAction(ChaseAction.ActionType.SHOOT, Cardinal.WEST);
      }
    }
    if(plannedMoves_.size() > 0){
      Cardinal dir = plannedMoves_.remove(0);
      if(maze_.get(myLoc_.apply(dir)) == ChaseState.Thing.NOTHING){
        return new ChaseAction(ChaseAction.ActionType.MOVE, dir);
      }
    }
    // else replan our moves
    int bestDistance = Integer.MAX_VALUE;
    Coord best = null;
    for(Coord target : targets_){
      Tuple<Integer, ArrayList<Cardinal>> path = pathFind(target);
      if(path.fst < bestDistance){
        bestDistance = path.fst;
        plannedMoves_ = path.snd;
        best = target;
      }
    }

    //no unknown places to explore, find best place runner might be;
    if(bestDistance == Integer.MAX_VALUE) {
      targets_.clear();
      for (int x = 0; x < runnerSpace_.width(); x++) {
        for (int y = 0; y < runnerSpace_.height(); y++) {
          if (runnerSpace_.get(x, y)) {
            targets_.add(new Coord(x, y));
          }
        }
      }

      // try again
      for (Coord target : targets_) {
        Tuple<Integer, ArrayList<Cardinal>> path = pathFind(target);
        if (path.fst < bestDistance) {
          bestDistance = path.fst;
          plannedMoves_ = path.snd;
          best = target;
        }
      }
    }

    targets_.remove(best);

    if(plannedMoves_.size() == 0){
      ArrayList<Cardinal> allowed = new ArrayList<Cardinal>();
      for(Cardinal dir : Cardinal.values()){
        if(maze_.get(myLoc_.apply(dir)) == ChaseState.Thing.NOTHING){
          allowed.add(dir);
        }
      }
      int rand = (int) (Math.random() * allowed.size()-1);
      Cardinal dir = allowed.get(rand);
      return  new ChaseAction(ChaseAction.ActionType.MOVE, dir);
    } else {
      return new ChaseAction(ChaseAction.ActionType.MOVE, plannedMoves_.remove(0));
    }
  }


  @Override
  public String identity() {
    return "Chaser Bot";
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

  private Tuple<Integer, ArrayList<Cardinal>>pathFind(Coord goal){
    KeyedQueue<Tuple<ArrayList<Cardinal>, Coord>> search = new KeyedQueue<Tuple<ArrayList<Cardinal>, Coord>>();
    search.add(0, new Tuple<ArrayList<Cardinal>, Coord>(new ArrayList<Cardinal>(), myLoc_));
    Grid<Boolean> found = new Grid<Boolean>(maze_.width(), maze_.height(), false, true);
    while(!search.isEmpty()) {
      Tuple<ArrayList<Cardinal>, Coord> next = search.pop();
      found.set(next.snd, true);
      if (next.snd.equals(goal)) {
        return new Tuple<Integer, ArrayList<Cardinal>>(next.fst.size(), next.fst);
      } else {
        for (Cardinal dir : Cardinal.values()) {
          Coord loc = next.snd.apply(dir);
          if ((maze_.get(loc) == ChaseState.Thing.NOTHING && !found.get(loc)) || loc.equals(goal)) {
            int k = next.fst.size() + Math.abs(goal.x - loc.x) + Math.abs(goal.y - loc.y);
            ArrayList<Cardinal> newPath = (ArrayList<Cardinal>) next.fst.clone();
            newPath.add(dir);
            search.add(k, new Tuple<ArrayList<Cardinal>, Coord>(newPath, loc));
          }
        }
      }
    }
    return new Tuple<Integer, ArrayList<Cardinal>>(Integer.MAX_VALUE, new ArrayList<Cardinal>());
  }

  private boolean showMinor_;
  private boolean showDebug_;
  private ArrayList<Cardinal> plannedMoves_;
  private ArrayList<Coord> targets_;
  private Grid<ChaseState.Thing> maze_;
  private Grid<Boolean> canSee_;
  private Grid<Boolean> runnerSpace_;
  private Coord myLoc_;
  private Coord allyLoc_;
  private Coord runnerLoc_;
  private ChaseState.Thing me_;
  private GridFrame frame_;
}

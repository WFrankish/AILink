package examples.mazeChase;

import common.Coord;
import common.Grid;
import interfaces.State;

public class ChaseState {

  public static class You implements State{

    public You(Thing y){
      switch (y){
        case CHASER1: case CHASER2: case RUNNER:{
          you = y;
          break;
        }
        default:{
          you = Thing.UNKNOWN;
        }
      }
    }

    @Override
    public String toString() {
      return 'Y'+you.toString();
    }

    @Override
    public String toReadable() {
      return you.toReadable();
    }

    public Thing you;

  }

  public static class TimeLasted implements State{

    public TimeLasted(int time){
      time_ = time;
    }

    @Override
    public String toString() {
      return "W"+time_;
    }

    @Override
    public String toReadable() {
      return time_ + " turns before runner defeated";
    }

    private int time_;

  }

  public static class Hit implements State{

    @Override
    public String toString() {
      return "H";
    }

    @Override
    public String toReadable() {
      return "You've been hit!";
    }

  }

  public static class Visible implements State{

    public Visible(Grid<Thing> visible){
      state_ = visible;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
      result.append('V');
      for(int y = 0; y<state_.height(); y++){
        for(int x = 0; x<state_.width(); x++){
          result.append(state_.get(x, y).toString());
        }
        result.append('_');
      }
      return result.toString();
    }

    @Override
    public String toReadable() {
      StringBuilder result = new StringBuilder();
      for(int y = 0; y<state_.height(); y++){
        for(int x = 0; x<state_.width(); x++){
          switch (state_.get(x, y)){
            case WALL:{
              result.append('#');
              break;
            }
            case NOTHING:{
              result.append(' ');
              break;
            }
            case RUNNER:{
              result.append('r');
              break;
            }
            case CHASER1:{
              result.append('1');
              break;
            }
            case CHASER2:{
              result.append('2');
              break;
            }
            default:{
              result.append('?');
            }
          }
        }
        result.append('\n');
      }
      return result.toString();
    }

    public Grid<Thing> getVisible(){
      return state_;
    }

    private Grid<Thing> state_;

  }

  public enum Thing {
    WALL, NOTHING, CHASER1, CHASER2, RUNNER, UNKNOWN;

    @Override
    public String toString() {
      switch (this){
        case WALL:{
          return "W";
        }
        case NOTHING:{
          return "N";
        }
        case CHASER1:{
          return "1";
        }
        case CHASER2:{
          return "2";
        }
        case RUNNER:{
          return "R";
        }
        default:{
          return "?";
        }
      }
    }

    public String toReadable(){
      switch (this) {
        case WALL: {
          return "A Wall";
        }
        case NOTHING: {
          return "Nothing";
        }
        case CHASER1: {
          return "Chaser No 1";
        }
        case CHASER2: {
          return "Chaser No 2";
        }
        case RUNNER: {
          return "The Runner";
        }
        default: {
          return "Unknown";
        }
      }
    }

    public static Thing parse(char c){
      switch (c){
        case 'W':{
          return WALL;
        }
        case 'N':{
          return NOTHING;
        }
        case '1':{
          return CHASER1;
        }
        case '2':{
          return CHASER2;
        }
        case 'R':{
          return RUNNER;
        }
        default:{
          return UNKNOWN;
        }
      }
    }

  }

}

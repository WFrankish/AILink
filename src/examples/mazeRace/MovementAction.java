package examples.mazeRace;

import common.Cardinal;
import interfaces.Action;

public class MovementAction implements Action {

  public static MovementAction North = new MovementAction(Cardinal.NORTH);

  public static MovementAction East = new MovementAction(Cardinal.EAST);

  public static MovementAction South = new MovementAction(Cardinal.SOUTH);

  public static MovementAction West = new MovementAction(Cardinal.WEST);

  public Cardinal getDirection(){
    return  direction_;
  }

  @Override
  public String encode() {
    return toString().substring(0, 1);
  }

  @Override
  public String toString() {
    switch ( direction_){
      case NORTH: {
        return "North";
      }
      case WEST: {
        return "West";
      }
      case SOUTH: {
        return "South";
      }
      default: {
        return "East";
      }
    }
  }

  private MovementAction(Cardinal dir){
    direction_ = dir;
  }

  private Cardinal direction_;
}

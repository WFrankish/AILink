package examples.mazeChase;

import common.Cardinal;
import interfaces.Action;

public class ChaseAction implements Action{

  public ChaseAction(ActionType act, Cardinal dir){
    action = act;
    direction = dir;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    if(action == ActionType.MOVE){
      result.append('M');
    } else {
      result.append('S');
    }
    result.append(direction.toString());
    return result.toString();
  }

  @Override
  public String toReadable() {
    StringBuilder result = new StringBuilder();
    if(action == ActionType.MOVE){
      result.append("Move to the ");
    } else {
      result.append("Shoot to the ");
    }
    result.append(direction.toReadable());
    return result.toString();
  }

  public Cardinal direction;
  public ActionType action;

  public enum ActionType {
    MOVE, SHOOT
  }


}

package examples.mazeChase;

import common.Cardinal;
import interfaces.Action;
import interfaces.ActionMaster;

public class ChaseActionMaster implements ActionMaster {

  @Override
  public Action parseAction(String input) {
    if(input.length() != 2){
      return null;
    } else {
      ChaseAction.ActionType action;
      switch (input.charAt(0)){
        case 'S':{
          action = ChaseAction.ActionType.SHOOT;
          break;
        }
        case 'M':{
          action = ChaseAction.ActionType.MOVE;
          break;
        }
        default:{
          return null;
        }
      }
      Cardinal direction = Cardinal.parse(input.charAt(1));
      return new ChaseAction(action, direction);
    }
  }

}

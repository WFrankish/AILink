package examples.mazeRace;


import interfaces.Action;
import interfaces.ActionMaster;

import java.util.ArrayList;

public class MovementActionMaster implements ActionMaster {

  @Override
  public String actionsToString(Action[] actions) {
    StringBuilder result = new StringBuilder();
    for(Action action : actions){
      result.append(action.toString());
    }
    return result.toString();
  }

  @Override
  public String actionsToReadable(Action[] actions) {
    StringBuilder result = new StringBuilder();
    for(int i = 0; i < actions.length; i++){
      result.append(actions[i].toReadable());
      if(i < actions.length-1){
        result.append(", ");
      }
    }
    return result.toString();
  }

  @Override
  public Action parseAction(String input) {
    char c = input.charAt(0);
    switch (c){
      case 'N':{
        return MovementAction.North;
      }
      case 'E':{
        return MovementAction.East;
      }
      case 'S':{
        return MovementAction.South;
      }
      case 'W':{
        return MovementAction.West;
      }
      default:{
        return null;
      }
    }
  }

  @Override
  public Action[] parseActions(String input) {
    ArrayList<Action> actions = new ArrayList<Action>();
    for(int i = 0; i<input.length(); i++){
      actions.add(parseAction(input.substring(i, i+1)));
    }
    return actions.toArray(new Action[0]);
  }

}

package examples.mazeRace;


import interfaces.Action;
import interfaces.ActionMaster;

import java.util.ArrayList;

public class MovementActionMaster implements ActionMaster {

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

}

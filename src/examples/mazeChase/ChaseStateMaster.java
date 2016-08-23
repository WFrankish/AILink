package examples.mazeChase;

import common.Grid;
import interfaces.State;
import interfaces.StateMaster;

public class ChaseStateMaster implements StateMaster {

  @Override
  public State decode(String input) {
    if(input.length() == 0){
      return null;
    } else {
      char header = input.charAt(0);
      String content = (input.length() > 1)  ? input.substring(1) : "";
      switch (header){
        case 'Y':{
          char c = (content.length() > 0) ? content.charAt(0) : null;
          return new ChaseState.You(ChaseState.Thing.decode(c));
        }
        case 'H':{
          return new ChaseState.Hit();
        }
        case 'W':{
          return new ChaseState.TimeLasted(Integer.parseInt(content));
        }
        case 'V':{
          String[] groups = content.split("_");
          int width = groups[0].length();
          int height = groups.length;
          Grid<ChaseState.Thing> output = new Grid<ChaseState.Thing>(width, height, ChaseState.Thing.UNKNOWN, ChaseState.Thing.WALL);
          for(int y = 0; y<height; y++){
            for(int x = 0; x<width; x++){
              ChaseState.Thing thing = ChaseState.Thing.decode(groups[y].charAt(x));
              output.set(x, y, thing);
            }
          }
          return new ChaseState.Visible(output);
        }
        default:{
          return null;
        }
      }
    }
  }
}

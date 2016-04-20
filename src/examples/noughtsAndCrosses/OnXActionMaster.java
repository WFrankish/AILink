package examples.noughtsAndCrosses;

import templates.Action;
import templates.ActionMaster;

public class OnXActionMaster implements ActionMaster {

  @Override
  public Action[] parseActions(String input) {
    int length = input.length() / 2;
    Action[] res = new Action[length];
    for(int i = 0; i<length; i++){
      int x = Integer.parseInt(input.substring(2*i, 2*i + 1));
      int y = Integer.parseInt(input.substring(2*i + 1, 2*i + 2));
      res[i] = new OnXAction(x, y);
    }
    return res;
  }

  @Override
  public String actionsToString(Action[] actions) {
    StringBuilder builder = new StringBuilder();
    for( Action action : actions){
      builder.append(action.toString());
    }
    return builder.toString();
  }

  @Override
  public Action parseAction(String input) {
    int x = Integer.parseInt(input.substring(0,1));
    int y = Integer.parseInt(input.substring(1,2));
    return new OnXAction(x, y);
  }

}

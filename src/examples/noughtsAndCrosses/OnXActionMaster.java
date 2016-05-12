package examples.noughtsAndCrosses;

import interfaces.Action;
import interfaces.ActionMaster;

/**
 * OnX Actions are a pair of digits from 0-2
 */
public class OnXActionMaster implements ActionMaster {

  @Override
  public Action parseAction(String input) {
    // string format is xy for digits 0-2 only
    int x = Integer.parseInt(input.substring(0,1));
    int y = Integer.parseInt(input.substring(1,2));
    return new OnXAction(x, y);
  }

}

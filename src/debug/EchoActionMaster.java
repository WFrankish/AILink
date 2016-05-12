package debug;

import interfaces.Action;
import interfaces.ActionMaster;

/**
 * EchoActions are already strings, so this doesn't need to do anything.
 */
public class EchoActionMaster implements ActionMaster {

  @Override
  public Action parseAction(String input) {
    return new EchoAction(input);
  }

}

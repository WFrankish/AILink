package templates;

/**
 * A definition for all actions that may become available as part of a Game.
 */
public interface ActionMaster {
  /**
   * Convert a list of Actions from String format.
   * @param input a String representing a list of Actions
   * @return a list of Actions
   */
  Action[] parseActions(String input);

  /**
   * Convert a single Action from String format.
   * @param input a String representing a single Action
   * @return an Action
   */
  Action parseAction(String input);
}

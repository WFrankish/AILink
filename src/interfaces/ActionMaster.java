package interfaces;

/**
 * A definition for all actions that may become available as part of a Game.
 */
public interface ActionMaster {
  /**
   * Convert a list of Actions to String format.
   * @param actions a list of Actions
   * @return a String representing a list of Actions
   */
  String actionsToString(Action[] actions);

  /**
   * Convert a list of Actions to String format.
   * @param actions a list of Actions
   * @return a Human Readable String representing a list of Actions
   */
  String actionsToReadable(Action[] actions);

  /**
   * Convert a single Action from String format.
   * Must be the inverse of toString.
   * @param input a String representing a single Action
   * @return an Action
   */
  Action parseAction(String input);

  /**
   * Convert a list of Actions from String format.
   * parseActions(actionsToString(actions)) should return some permutation of actions.
   * @param input a String representing a list of Actions
   * @return a list of Actions
   */
  Action[] parseActions(String input);

}

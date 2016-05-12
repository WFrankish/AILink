package interfaces;

/**
 * A definition for all actions that may become available as part of a Game.
 */
public interface ActionMaster {
  /**
   * Convert a single Action from String format.
   * Must be the inverse of toString.
   * @param input a String representing a single Action
   * @return an Action
   */
  Action parseAction(String input);

}

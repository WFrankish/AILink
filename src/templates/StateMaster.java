package templates;

/**
 * A definition for visible State for a Game.
 */
public interface StateMaster {
  /**
   * Given the state information as a string, convert it to the state class.
   * @param input templates.State information as String
   * @return templates.State information as State
   */
  State parseString(String input);

}

package templates;

/**
 * State information for an AI problem. This is not necessarily all of the information held about the state, only that
 * which is communicated to the agent.
 */
public interface State {
  /**
   * Given the state information as a string, convert it to the state class.
   * @param input templates.State information as String
   * @return templates.State information as State
   */
  State parseString(String input);

  /**
   * Convert the current state information to string format.
   * @return state information as String.
   */
  String toString();
}

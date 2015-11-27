package templates;

/**
 * A Game for an AILink program.
 */
public interface Game {

  /**
   * Given a String naming the Agent and a unique number for the agent, register the Agent and return its initial state.
   * @param agent an arbitrary String that is the agents chosen name.
   * @param agentNo
   * @return
   */
  State registerAgent(String agent, int agentNo);

  /**
   * Give the String that identifies your Game. Mostly for human use, so uniqueness is helpful but not mandatory.
   * Should return the same value every time.
   * @return your identity as String
   */
  String identity();

  /**
   * React to debug information from the interface.
   * Suggestion is to print to console or ignore it.
   * @param str debug information as String
   */
  void debug(String str);

  /**
   * React to error information from the interface.
   * Suggestion is to print to console.
   * @param str error information as String
   */
  void error(String str);
}

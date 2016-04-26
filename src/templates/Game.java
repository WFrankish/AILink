package templates;

/**
 * A Game for an AILink program.
 */
public interface Game {

  /**
   * Given a String naming the Agent and a unique number for the agent, register the Agent and return its initial state.
   * @param agent an arbitrary String that is the agents chosen name.
   * @param agentNo a unique integer representing the agent, chosen by the GameInterface.
   * @return Initial state for that agent (null if we reject the agent)
   */
  State registerAgent(String agent, int agentNo);

  /**
   * Give the String that identifies your Game. Mostly for human use, so uniqueness is helpful but not mandatory.
   * Should return the same value every time.
   * @return your identity as String
   */
  String identity();

  /** Transmit important information to human game master
   * Do not ignore.
   * @param obj information as any Object (therefore it has a toString method)
   */
  void message(Object obj);

  /**
   * React to debug information from the interface.
   * Suggestion is to print to console or ignore it.
   * @param isMajor importance level of debug information.
   * @param obj debug information as any Object (therefore it has a toString method)
   */
  void debug(boolean isMajor, Object obj);

  /**
   * React to error information from the interface.
   * Suggestion is to print to console.
   * @param obj error information as any Object (therefore it has a toString method.
   */
  void error(Object obj);

  /**
   * Called when the GameInterface has died.
   * Does not have to end your Game.
   */
  void end();
}

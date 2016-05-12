package interfaces;

/**
 * An Agent for an AILink Program
 */
public interface Agent {

  /**
   * Check with the Agent (or the Agent's human master) whether it wants to play this game.
   * @param ident The name of the game;
   * @return whether the agent accepts
   */
  boolean checkGame(String ident);

  /**
   * Given the current visible state and available actions, decide on an action to perform.
   * @return an action from the available actions
   */
  Action decide();

  /**
   * Note a change in the visible state
   * This is helpful if there is more than one agent, so state can change without this agent performing actions.
   * For example, you may want to send state updates each time so you can visualise what is happening outside of your
   * turn.
   * @param update a change in the visible state
   */
  void perceiveState(State update);

  /**
   * Give the String that identifies your Agent. Mostly for human use, so uniqueness is helpful but not mandatory.
   * Should return the same value every time.
   * @return your identity as String
   */
  String identity();

  /**
   * Transmit error messages from the game itself.
   * Suggestion is to print to console or ignore it.
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
   * @param obj error information as any Object (therefore it has a toString method)
   */
  void error(Object obj);

}

package templates;

/**
 * An Agent for an AILink Program
 */
public interface Agent {
  /**
   * Given the current visible state and available actions, decide on an action to perform.
   * @param actions available actions
   * @param state current visible state
   * @return an action from the available actions
   */
  Action decide(Action[] actions, State state);

  /**
   * Note a change in the visible state
   * This is helpful if there is more than one agent, so state can change without this agent performing actions.
   * For example, you may want to send state updates each time so you can visualise what is happening outside of your
   * turn.
   * @param update a change in the visible state
   */
  void updateState(State update);

  /**
   * Not any important initial state, for example what team you are on, what specific scenario this is, what class you
   * are playing, etc.
   * @param debrief important initial state
   */
  void initialState(State debrief);

  /**
   * Give the String that identifies your Agent. Mostly for human use, so uniqueness is helpful but not mandatory.
   * Should return the same value every time.
   * @return your identity as String
   */
  String identity();

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

  /**
   * Called when the AgentInterface has died and/or the Game is over.
   * Does not have to end your Agent.
   */
  void end();
}

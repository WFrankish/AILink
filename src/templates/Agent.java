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
   * These changes will be given again when decide is called, so this is only if you want to be able to visualise what
   * is happening outside of your turn.
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
   * @param str debug information as String
   */
  void debug(String str);

  /**
   * React to error information from the interface.
   * Suggestion is to print to console.
   * @param str error information as String
   */
  void error(String str);

  /**
   * Called when the AgentInterface has died and/or the Game is over.
   * Does not have to end your Agent.
   */
  void end();
}

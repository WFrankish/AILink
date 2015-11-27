package templates;

/**
 * The interface for a Game connecting it to all of the external Agents participating.
 */
public interface GameInterface {
  void requestAgent();
  Action requestAction(int agentID, State state, Action[] actions);
  void end();
}
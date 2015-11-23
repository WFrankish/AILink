package templates;

/**
 * The interface for a Game connecting it to all of the external Agents participating.
 */
public interface GameInterface {
  String requestAgent();
  Action requestAction(String agentID, State state, Action[] actions);
}
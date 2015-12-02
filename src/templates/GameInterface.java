package templates;

/**
 * The interface for a Game connecting it to all of the external Agents participating.
 */
public interface GameInterface {
  /**
   * request that we attempt to set up communication with an agent
   */
  void requestAgent();

  /**
   * request an action from an agent, giving it a state and some actions to choose from
   * @param agentID the agent to request from
   * @param state the state that the agent can see
   * @param actions the actions that the agent can choose from
   * @return the action that the agent chose
   */
  Action requestAction(int agentID, State state, Action[] actions);

  /**
   * offer an update in state to an agent
   * @param agentID the agent to update
   * @param state the state the agent can see
   */
  void updateState(int agentID, State state);

  /**
   * terminate communication with an agent
   * @param agentID
   */
  void terminateAgent(int agentID);

  void end();
}
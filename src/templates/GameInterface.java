package templates;

import common.Tuple;

/**
 * The interface for a Game connecting it to all of the external Agents participating.
 */
public interface GameInterface {

  /**
   * request the interface searches for an agent
   * @return A tuple of the agent's found unique id and the agent's arbitrary name.
   */
  Tuple<Integer, String> findAgent();

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
   * @param agentID the agent to terminate
   * @param msg a message to pass to the agent
   */
  void terminateAgent(int agentID, String msg);

  void end();
}
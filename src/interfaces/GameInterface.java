package interfaces;

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
   * @return the action that the agent chose
   */
  Action requestAction(int agentID);

  /**
   * offer an update in state to an agent
   * @param agentID the agent to update
   * @param state the state the agent can see
   */
  void sendState(int agentID, State state);

  /**
   * terminate communication with an agent
   * @param agentID the agent to terminate
   * @param msg a message to pass to the agent
   */
  void terminateAgent(int agentID, String msg);

  void end();
}
package interfaces;

/**
 * Visible State for a Game. This is not necessarily all of the information held about the state by the game,
 * only that which is to be communicated to the agent.
 */
public interface State {
  /**
   * Convert the State into String format. All information about this State should be recoverable from the String.
   * @return State as String
   */
  String toString();

  /**
   * Convert the State into Human Readable format.
   * @return State as Human Readable String
   */
  String toReadable();
}

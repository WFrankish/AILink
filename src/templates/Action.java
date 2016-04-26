package templates;

/**
 * An Action in the Game.
 */
public interface Action {
  /**
   * Convert the Action into String format. All information about the action should be recoverable from the String.
   * @return Action as String
   */
  String toString();

  /**
   * Convert the Action into String format. This should be Human Readable.
   * @return Action as Human Readable String
   */
  String toReadable();

}
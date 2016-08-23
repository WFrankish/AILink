package interfaces;

/**
 * An Action in the Game.
 */
public interface Action {
  /**
   * Convert the Action into String format. All information about the action should be recoverable from the String.
   * @return Action as String
   */
  String encode();

}
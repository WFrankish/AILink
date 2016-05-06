package interfaces;

/**
 * A definition for visible State for a Game.
 */
public interface StateMaster {
  /**
   * Given the state information as a string, convert it to the state class.
   * @param input interfaces.State information as String
   * @return interfaces.State information as State
   */
  State parseString(String input);

}

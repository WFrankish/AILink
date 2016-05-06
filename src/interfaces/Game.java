package interfaces;

/**
 * A Game for an AILink program.
 */
public interface Game {

  /**
   * Give the String that identifies your Game. Mostly for human use, so uniqueness is helpful but not mandatory.
   * Should return the same value every time.
   * @return your identity as String
   */
  String identity();

  /**
   * Transmit important information to human game master
   * Do not ignore.
   * @param obj information as any Object (therefore it has a toString method)
   */
  void message(Object obj);

  /**
   * React to debug information from the interface.
   * Suggestion is to print to console or ignore it.
   * @param isMajor importance level of debug information.
   * @param obj debug information as any Object (therefore it has a toString method)
   */
  void debug(boolean isMajor, Object obj);

  /**
   * React to error information from the interface.
   * Suggestion is to print to console.
   * @param obj error information as any Object (therefore it has a toString method.
   */
  void error(Object obj);

}

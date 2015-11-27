package templates;

/**
 * The interface for an Agent, connecting it to a Game.
 */
public interface AgentInterface {
  /**
   * Run the interface, taking control of its associated agent.
   */
  void run();

  /**
   * End the run of the interface, and notify the agent we've ended.
   */
  void end();
}

package templates;

/**
 * The interface for an Agent, connecting it to a Game.
 */
public interface AgentInterface {
  void registerAgent (Agent agent);
  void run();
}

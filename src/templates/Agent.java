package templates;

public interface Agent {
  Action decide(Action[] actions, State state);
}

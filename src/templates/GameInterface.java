package templates;

import templates.Action;

public interface GameInterface {
  String requestAgents();
  Action requestAction(String agentID, State state, Action[] actions);
}
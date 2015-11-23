package socketInterface;

import templates.Action;
import templates.GameInterface;
import templates.State;

public class SocketGameInterface implements GameInterface {
  public SocketGameInterface(int port){
    signUpPort_ = port;
  }

  @Override
  public String requestAgents() {
    return null;
  }

  @Override
  public Action requestAction(String agentID, State state, Action[] actions) {
    return null;
  }

  private int signUpPort_;
}

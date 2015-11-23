package socketInterface;

import templates.*;

public class SocketGameInterface implements GameInterface {
  public SocketGameInterface(int port, Game game, StateMaster stateMaster, ActionMaster actionMaster){
    signUpPort_ = port;
    game_ = game;
    stateMaster_ = stateMaster;
    actionMaster_ = actionMaster;
  }

  @Override
  public String requestAgent() {
    return null;
  }

  @Override
  public Action requestAction(String agentID, State state, Action[] actions) {
    return null;
  }

  private int signUpPort_;
  private Game game_;
  private StateMaster stateMaster_;
  private ActionMaster actionMaster_;

}

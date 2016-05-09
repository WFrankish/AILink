package examples.noughtsAndCrosses;

import socketInterface.SocketAgentInterface;
import interfaces.Action;
import interfaces.Agent;
import interfaces.AgentInterface;
import interfaces.State;
import tools.ParseTools;

public class OnXRandomBot implements Agent{

  public static void main(String[] args) {
    OnXRandomBot instance = new OnXRandomBot(args);
    AgentInterface connection = new SocketAgentInterface(
        instance,
        new OnXStateMaster(),
        new OnXActionMaster());
    connection.run();
  }

  /**
   * instantiation method
   * @param args -d for show debug, -dm for show all debug
   */
  public OnXRandomBot(String[] args){
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
  }

  @Override
  public boolean checkGame(String ident) {
    return ident.equals("Noughts and Crosses");
  }

  @Override
  public Action decide(Action[] actions, State state) {
    OnXState.Grid grid = (OnXState.Grid) state;
    System.out.println(grid.toReadable());
    // no ai whatsoever, pick at random
    double random = (Math.random() * actions.length);
    return actions[(int) random];
  }

  @Override
  public void perceiveState(State update) {
    // game calls update to announce the side you are playing at the start,
    // announce the winner or show the grid at the end.
    if(update instanceof OnXState.Player){
      // should only happen once
      OnXState.Player state = (OnXState.Player) update;
      me_ = state.getMe();
    }
    else if(update instanceof OnXState.Grid){
      OnXState.Grid grid = (OnXState.Grid) update;
      System.out.println("Final result: ");
      System.out.println(grid.toReadable());
    }
    else {
      OnXState.Winner state = (OnXState.Winner) update;
      System.out.println(state.toReadable());
      Token winner = state.getWinner();
      if (winner.equals(me_)) {
        System.out.println("I won!");
      } else if ( winner != Token.BLANK ) {
        System.out.println("Opponent won.");
      } else {
        System.out.println("No winner");
      }
    }
  }

  @Override
  public String identity() {
    return "Noughts and Crosses Random Bot";
  }

  @Override
  public void debug(boolean isMajor, Object obj) {
    if(showDebug_ && (isMajor || showMinor_)) {
      String message = "Debug for " + identity() + ": " + obj;
      System.out.println(message);
    }
  }

  @Override
  public void error(Object obj) {
    String message = "Error for "+identity()+": "+obj;
    System.out.println(message);
  }

  // levels of debug to show;
  private boolean showDebug_;
  private boolean showMinor_;

  // who I am playing as
  private Token me_;
}

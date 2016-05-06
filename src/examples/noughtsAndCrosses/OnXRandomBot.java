package examples.noughtsAndCrosses;

import socketInterface.SocketAgentInterface;
import interfaces.Action;
import interfaces.Agent;
import interfaces.AgentInterface;
import interfaces.State;
import tools.ParseTools;

public class OnXRandomBot implements Agent{

  public static void main(String[] args) {
    boolean showMinor = (args.length > 0 && ParseTools.parseTruth(args[0]));
    OnXRandomBot instance = new OnXRandomBot(showMinor);
    AgentInterface connection = new SocketAgentInterface(
        instance,
        new OnXStateMaster(),
        new OnXActionMaster());
    connection.run();
  }

  public OnXRandomBot(boolean showMinor){
    showMinor_ = showMinor;
  }

  @Override
  public boolean checkGame(String ident) {
    return ident.equals("Noughts and Crosses");
  }

  @Override
  public Action decide(Action[] actions, State state) {
    OnXState.Grid grid = (OnXState.Grid) state;
    System.out.println(grid.toReadable());
    double random = (Math.random() * actions.length);
    return actions[(int) random];
  }

  @Override
  public void updateState(State update) {
    if(update instanceof OnXState.Player){
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
    if(isMajor || showMinor_) {
      String message = "Debug for " + identity() + ": " + obj;
      System.out.println(message);
    }
  }

  @Override
  public void error(Object obj) {
    String message = "Error for "+identity()+": "+obj;
    System.out.println(message);
  }

  private boolean showMinor_;
  private Token me_;
}

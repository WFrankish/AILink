package examples.noughtsAndCrosses;

import socketInterface.SocketAgentInterface;
import interfaces.Action;
import interfaces.Agent;
import interfaces.AgentInterface;
import interfaces.State;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OnXUserControlled implements Agent {
  public static void main(String[] args) {
    boolean showMinor = ParseTools.find(args, "-d") > -1;
    OnXUserControlled instance = new OnXUserControlled(showMinor);
    AgentInterface connection = new SocketAgentInterface(
        instance,
        new OnXStateMaster(),
        new OnXActionMaster());
    connection.run();
  }

  public OnXUserControlled(boolean showMinor){
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
    try {
      while(true) {
        System.out.println("Choose a grid-space, available: ");
        StringBuilder builder = new StringBuilder();
        int i;
        for( i = 0; i<actions.length-1; i++){
          builder.append(actions[i].toString());
          builder.append(", ");
        }
        if(i<actions.length){
          builder.append(actions[i].toString());
        }
        System.out.println(builder.toString());
        System.out.println();
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String temp = stdIn.readLine();
        if (temp.length() == 2) {
          int x = Integer.parseInt(temp.substring(0, 1));
          int y = Integer.parseInt(temp.substring(1, 2));
          return new OnXAction(x, y);
        }
      }
    }
    catch (Exception e){
      error(e);
      return null;
    }
  }

  @Override
  public void perceiveState(State update) {
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
        System.out.println("You won!");
      } else if ( winner != Token.BLANK ) {
        System.out.println("Opponent won.");
      } else {
        System.out.println("No winner");
      }
    }
  }

  @Override
  public String identity() {
    return "Noughts and Crosses Human Player";
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

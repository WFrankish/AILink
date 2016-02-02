package examples.noughtsAndCrosses;

import socketInterface.SocketAgentInterface;
import templates.Action;
import templates.Agent;
import templates.AgentInterface;
import templates.State;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OnXRandomBot implements Agent{

  public static void main(String[] args) {
    OnXRandomBot instance = new OnXRandomBot();
    try {
      String url = "localhost";
      System.out.println("Enter port number of host:");
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      int port = Integer.parseInt(stdIn.readLine());
      AgentInterface connection = new SocketAgentInterface(url,
          port,
          instance,
          new OnXStateMaster(),
          new OnXActionMaster());
      connection.run();
    } catch (Exception e) {
      instance.error(e);
    }
  }

  @Override
  public Action decide(Action[] actions, State state) {
    OnXState state2 = (OnXState) state;
    System.out.println(state2.gridToNiceString());
    double random = (Math.random() * actions.length);
    return actions[(int) random];
  }

  @Override
  public void initialState(State debrief) {
    OnXState state = (OnXState) debrief;
    me_ = state.getMe();
  }

  @Override
  public void updateState(State update) {
    OnXState state = (OnXState) update;
    System.out.println(state.gridToNiceString());
    Token winner = state.getWinner();
    if(winner.equals(me_)){
      System.out.println("I won!");
    }
    else if(winner.isNought() || winner.isCross()){
      System.out.println("Opponent won.");
    }
    else{
      System.out.println("No winner");
    }
  }

  @Override
  public String identity() {
    return "Noughts and Crosses Random Bot";
  }

  @Override
  public void debug(Object o1) {
    if(debug_) {
      String message = "Debug for " + identity() + ": " + o1;
      System.out.println(message);
    }
  }

  @Override
  public void debug(Object o1, Object o2) {
    if(debug_) {
      String message = "Debug for " + identity() + ": " + o1 + o2;
      System.out.println(message);
    }
  }

  @Override
  public void debug(Object o1, Object o2, Object o3) {
    if(debug_) {
      String message = "Debug for " + identity() + ": " + o1 + o2 + o3;
      System.out.println(message);
    }
  }

  @Override
  public void debug(Object o1, Object o2, Object o3, Object o4) {
    if (debug_) {
      String message = "Debug for " + identity() + ": " + o1 + o2 + o3 + o4;
      System.out.println(message);
    }
  }

  @Override
  public void error(Object obj) {
    String message = "Error for "+identity()+": "+obj;
    System.out.println(message);
  }

  @Override
  public void end() {
    System.out.println("Game over.");
  }

  private boolean debug_ = false;
  private Token me_;
}

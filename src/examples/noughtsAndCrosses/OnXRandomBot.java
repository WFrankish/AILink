package examples.noughtsAndCrosses;

import socketInterface.SocketAgentInterface;
import templates.Action;
import templates.Agent;
import templates.AgentInterface;
import templates.State;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OnXRandomBot implements Agent{

  public static void main(String[] args) {
    boolean showMinor = (args.length > 0 && ParseTools.parseTruth(args[0]));
    OnXRandomBot instance = new OnXRandomBot(showMinor);
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

  public OnXRandomBot(boolean showMinor){
    showMinor_ = showMinor;
  }

  @Override
  public Action decide(Action[] actions, State state) {
    OnXState state2 = (OnXState) state;
    System.out.println(state2.toReadable());
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
    System.out.println(state.toReadable());
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

  @Override
  public void end() {
    System.out.println("Game over.");
  }

  private boolean showMinor_;
  private Token me_;
}

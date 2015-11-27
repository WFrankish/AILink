package debug;

import socketInterface.SocketGameInterface;
import templates.*;

import java.util.ArrayList;

/**
 * A test implementation of an AILink program which is actually just a really convoluted echo server when given a
 * working implementation of AgentInterface and GameInterface.
 */
public class Echo {

  public static void main(String[] args){
    new Echo().new EchoGame().run();
  }

  private class EchoGame implements Game{

    public EchoGame(){
      gameInterface_ = new SocketGameInterface(this, new EchoActionMaster());
    }

    public void run(){
      gameInterface_.requestAgent();
      EchoActionMaster actionMaster = new EchoActionMaster();
      EchoStateMaster stateMaster = new EchoStateMaster();
      String transcript = "";
      while(true){
        Action action = gameInterface_.requestAction(agents_.get(0), stateMaster.parseString(transcript), actionMaster.parseActions("abcdefghijklmnopqrtstuvxyz"));
        transcript += action.toString();
        transcript += "\n";
      }
    }

    @Override
    public State registerAgent(String agent, int agentNo) {
      agents_.add(agentNo);
      debug("Added agent "+agentNo);
      debug("Now the number of agents is: "+agents_.size());
      return new EchoState("Welcome");
    }

    @Override
    public String identity() {
      return "Echo";
    }

    @Override
    public void debug(String str) {
      System.out.println("Debug for " + identity() + ": " + str);
    }

    @Override
    public void error(String str) {
      System.out.println("Error for " + identity() + ": " + str);
    }

    private GameInterface gameInterface_;
    private ArrayList<Integer> agents_ = new ArrayList<Integer>();
  }

  public static class EchoActionMaster implements ActionMaster {
    @Override
    public Action[] parseActions(String input) {
      Action[] out = new Action[input.length()];
      for (int i = 0; i < input.length(); i++) {
        out[i] = new EchoAction(input.substring(i, i + 1));
      }
      return out;
    }

    @Override
    public String actionsToString(Action[] actions) {
      StringBuilder builder = new StringBuilder();
      for(Action a : actions){
        builder.append(a.toString());
      }
      return builder.toString();
    }

    @Override
    public Action parseAction(String input) {
      return new EchoAction(input);
    }
  }

  /**
   * An EchoAction is a string.
   */
  public static class EchoAction implements Action{
    public EchoAction(String str){
      msg_ = str;
    }

    @Override
    public String toString() {
      return msg_;
    }

    private String msg_;
  }

  public static class EchoStateMaster implements StateMaster{
    @Override
    public State parseString(String input) {
      return new EchoState(input);
    }
  }

  /**
   * The EchoState is all previous messages.
   */
  public static class EchoState implements State{
    public EchoState(String str){
      transcript_ = str;
    }

    @Override
    public String toString() {
      return transcript_;
    }

    private String transcript_;
  }
}

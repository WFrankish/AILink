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
      // Request two agents
      gameInterface_.requestAgent();
      gameInterface_.requestAgent();
      EchoActionMaster actionMaster = new EchoActionMaster();
      EchoStateMaster stateMaster = new EchoStateMaster();
      String transcript = "";
      Boolean cont = true;
      int agent = 0;
      while(cont){
        // The state is our full transcript of messages
        State state = stateMaster.parseString(transcript);
        // The available actions is a character set
        Action[] actions = actionMaster.parseActions("abcdefghijklmnopqrtstuvxyz\\");
        // We get an action (a message) from the agent
        // TODO: Check for illegal actions
        Action action = gameInterface_.requestAction(agents_.get(agent), state, actions);
        String actionS = action.toString();
        // If the message is empty, we shut down.
        cont = !actionS.equals("");
        // Add the message to the transcript
        transcript += actionS;
        transcript += "\n";
        // Switch agents
        agent = 1 - agent;
      }
      gameInterface_.end();
    }

    @Override
    public State registerAgent(String agent, int agentNo) {
      // Nothing fancy, just debug that we've added agents, and give them a welcome message.
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
    public void debug(Object o1) {
      String message = "Debug for "+identity()+": "+o1;
      System.out.println(message);
    }

    @Override
    public void debug(Object o1, Object o2) {
      String message = "Debug for "+identity()+": "+o1+o2;
      System.out.println(message);
    }

    @Override
    public void debug(Object o1, Object o2, Object o3) {
      String message = "Debug for "+identity()+": "+o1+o2+o3;
      System.out.println(message);
    }

    @Override
    public void debug(Object o1, Object o2, Object o3, Object o4) {
      String message = "Debug for "+identity()+": "+o1+o2+o3+o4;
      System.out.println(message);
    }

    @Override
    public void error(Object obj) {
      String message = "Error for "+identity()+": "+obj;
      System.out.println(message);
    }

    @Override
    public void end() {

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

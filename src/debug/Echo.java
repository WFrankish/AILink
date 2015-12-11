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
    EchoGame game = new Echo().new EchoGame();
    try {
      int noAgents = Integer.parseInt(args[0]);
      game.run(noAgents);
    }
    catch(Exception e){
      game.error("Bad arguments: Please input a number representing the number of agents.");
    }
  }

  private class EchoGame implements Game{

    public EchoGame(){
      gameInterface_ = new SocketGameInterface(this, new EchoActionMaster());
    }

    public void run(int noAgents){
      // Request two agents
      while (agents_.size() < noAgents){
        gameInterface_.requestAgent();
      }
      EchoActionMaster actionMaster = new EchoActionMaster();
      EchoStateMaster stateMaster = new EchoStateMaster();
      StringBuilder transcript = new StringBuilder("");
      // If agents offer forbidden actions we cull them.
      int remainingAgents = noAgents;
      boolean[] agentsAlive = new boolean[noAgents];
      for(int i = 0; i<agentsAlive.length; i++){
       agentsAlive[i]=true;
      }
      int agent = 0;
      while(remainingAgents > 0){
        if(agentsAlive[agent]) {
          // The state is our full transcript of messages
          State state = stateMaster.parseString(transcript.toString());
          // The available actions is a character set
          Action[] actions = actionMaster.parseActions("abcdefghijklmnopqrtstuvxyz\\");
          // We get an action (a message) from the agent
          Action response = gameInterface_.requestAction(agents_.get(agent), state, actions);
          String actionStr = response.toString();
          // Check they have only used allowed characters.
          String allowedChars = actionMaster.actionsToString(actions);
          for (int i = 0; i < actionStr.length() && agentsAlive[agent]; i++) {
            String c = actionStr.substring(i, i + 1);
            if (!allowedChars.contains(c)) {
              System.out.println("Agent "+agent+" has tried to perform an illegal action.");
              agentsAlive[agent] = false;
            }
          }
          if (agentsAlive[agent]) {
            // If the message is empty, we shut down.
            boolean stop = actionStr.equals("");
            if (stop) {
              remainingAgents = 0;
            }
            // Add the message to the transcript
            transcript.append(actionStr);
            transcript.append("\n");
          }
          else{
            // If they cheated, kick them.
            gameInterface_.terminateAgent(agent);
            remainingAgents--;
          }
        }
        // Switch agents
        agent++;
        if(agent == noAgents){
          agent = 0;
        }
      }
      gameInterface_.end();
    }

    @Override
    public State registerAgent(String agent, int agentNo) {
      if(actBuggy_) {
        actBuggy_ = false;
        // Nothing fancy, just debug that we've added agents, and give them a welcome message.
        agents_.add(agentNo);
        System.out.println("Added agent " + agent);
        System.out.println("Now the number of agents is: " + agents_.size());
        return new EchoState("Welcome");
      }
      else{
        actBuggy_ = true;
        System.out.println("Rejecting agent " + agent);
        return null;
      }
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

    private boolean actBuggy_ = true;
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

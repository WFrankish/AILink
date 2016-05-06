package debug;

import common.Tuple;
import socketInterface.SocketGameInterface;
import templates.*;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * A test implementation of an AILink program which is actually just a really convoluted echo server when given a
 * working implementation of AgentInterface and GameInterface.
 */
public class Echo {

  public static void main(String[] args){
    boolean showMinor = ParseTools.find(args, "-d") > -1;
    EchoGame game = new Echo().new EchoGame(showMinor);
    int noAgents = ParseTools.findVal(args, "-p", 2);
    game.run(noAgents);
  }

  private class EchoGame implements Game{

    public EchoGame(boolean showMinor){
      showMinor_ = showMinor;
      interface_ = new SocketGameInterface(this, new EchoActionMaster());
    }

    public void run(int noAgents){
      agents_ = new ArrayList<Integer>();
      // Request two agents
      while (agents_.size() < noAgents){
        Tuple<Integer, String> agentInfo = interface_.findAgent();
        int agentNo = agentInfo.fst;
        String agentName = agentInfo.snd;
        message(agentName + "wants to join.");
        while(true){
          message("Enter y to accept, n to reject.");
          try {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String temp = stdIn.readLine();
            if (temp.length() > 0) {
              char c = temp.charAt(0);
              if (c == 'y' || c == 'Y') {
                agents_.add(agentInfo.fst);
                debug(true, "Added agent " + agentName);
                debug(true, "Now the number of agents is: " + agents_.size());
                interface_.updateState(agentInfo.fst, new EchoState("Welcome!"));
                break;
              } else if (c == 'n' || c == 'N') {
                debug(true, "Rejecting agent " + agentName);
                interface_.terminateAgent(agentNo, "Rejected.");
                break;
              }
            }
          } catch (Exception e) {
            error(e);
          }
        }
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
          Action[] actions = actionMaster.parseActions( randomCharSet() );
          // We get an action (a message) from the agent
          Action response = interface_.requestAction(agents_.get(agent), state, actions);
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
            interface_.terminateAgent(agent, "Invalid Action chosen.");
            remainingAgents--;
          }
        }
        // Switch agents
        agent++;
        if(agent == noAgents){
          agent = 0;
        }
      }
      interface_.end();
    }

    @Override
    public String identity() {
      return "Echo";
    }

    @Override
    public void message(Object obj) {
      String message = "Message to Game Master: " + obj;
      System.out.println(message);
    }

    @Override
    public void debug(boolean isMajor, Object obj) {
      if(isMajor ||showMinor_) {
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
    public void interfaceFailed() {

    }

    private String randomCharSet(){
      String allChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ";
      StringBuilder result = new StringBuilder();
      for(int i = 0; i < allChars.length(); i++){
        if(Math.random() > 0.1){
          result.append(allChars.charAt(i));
        }
      }
      return result.toString();
    }

    private GameInterface interface_;
    private ArrayList<Integer> agents_;
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
    public String actionsToReadable(Action[] actions) {
      StringBuilder builder = new StringBuilder();
      for(Action a : actions){
        builder.append(a.toString());
        builder.append(" ");
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

    @Override
    public String toReadable() {
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

    @Override
    public String toReadable() {
      return transcript_;
    }

    private String transcript_;
  }

  private boolean showMinor_;

}

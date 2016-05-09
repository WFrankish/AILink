package debug;

import common.Tuple;
import socketInterface.SocketGameInterface;
import interfaces.*;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A test implementation of an AILink program which is actually just a really convoluted echo server when given a
 * working implementation of AgentInterface and GameInterface.
 */
public class Echo implements Game {

  public static void main(String[] args){
    new Echo(args).run();
  }

  public Echo(String[] args){
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
    maxPlayers_ = ParseTools.findVal(args, "-p", 2);
    agentIDs_ = new int[maxPlayers_];
    agentNames_ = new String[maxPlayers_];
    interface_ = new SocketGameInterface(this, new EchoActionMaster());
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
    if( showDebug_ && (isMajor || showMinor_)) {
      String message = "Debug for " + identity() + ": " + obj;
      System.out.println(message);
    }
  }

  @Override
  public void error(Object obj) {
    String message = "Error for "+identity()+": "+obj;
    System.out.println(message);
  }

  private void run(){
    int players = 0;
    boolean[] agentAlive = new boolean[maxPlayers_];
    debug(true, "Seeking " + maxPlayers_ + " players.");
    while (players < maxPlayers_){
      Tuple<Integer, String> agentInfo = interface_.findAgent();
      int agentNo = agentInfo.fst;
      String agent = agentInfo.snd;
      if(acceptAgent(agent)){
        agentIDs_[players] = agentNo;
        agentNames_[players] = agent;
        agentAlive[players] = true;
        players++;
        debug(true, "Added agent " + agent);
        debug(true, "Now the number of agents is: " + players);
        interface_.sendState(agentInfo.fst, new EchoState("Welcome!"));
      } else {
        debug(true, "Rejecting agent " + agent);
        interface_.terminateAgent(agentNo, "Rejected.");
      }
    }
    debug(true, "Sending initial state.");
    for(int agentID : agentIDs_){
      interface_.sendState(agentID, new EchoState("Welcome!"));
    }
    debug(true, "Beginning game.");
    EchoActionMaster actionMaster = new EchoActionMaster();
    EchoState transcript = new EchoState("");
    int remainingAgents = maxPlayers_;
    while(remainingAgents > 0){
      for(int i = 0; i < maxPlayers_; i++){
        if(agentAlive[i]){
          debug(true, "Next is player " + i);
          Action[] actions = actionMaster.parseActions( randomCharSet() );
          // We get an action (a message) from the agent
          Action chosen = interface_.requestAction(agentIDs_[i], transcript, actions);
          if(chosen != null){
            String message = chosen.toString();
            String allowedChars = actionMaster.actionsToString(actions);
            for (int j = 0; j < message.length() && agentAlive[i]; j++) {
              String c = message.substring(j, j + 1);
              if (!allowedChars.contains(c)) {
                System.out.println("Agent "+i+" has tried to perform an illegal action.");
                interface_.terminateAgent(i, "Invalid Action chosen.");
                agentAlive[i] = false;
                remainingAgents--;

              }
            }
            if (agentAlive[i]) {
              // If the message is empty, we shut down.
              if(message.equals("")){
                remainingAgents = 0;
                debug(true, "Empty message received, ending program.");
              }
              // Add the message to the transcript
              transcript.append(agentNames_[i]);
              transcript.append(": ");
              transcript.append(message);
              transcript.append("\n");
            }
          } else {
            debug(true, "Received null.");
            agentAlive[i] = false;
            remainingAgents--;
            interface_.terminateAgent(agentIDs_[players], "Received null action.");
          }
        }
      }
    }
    interface_.end();
  }

  private boolean acceptAgent(String agent){
    message(agent + "wants to join.");
    while(true){
      message("Enter y to accept, n to reject.");
      try {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String temp = stdIn.readLine();
        if (temp.length() > 0) {
          char c = temp.charAt(0);
          if (c == 'y' || c == 'Y') {
            return true;
          } else if (c == 'n' || c == 'N') {
            return false;
          }
        }
      } catch (IOException e) {
        error(e);
      }
    }
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
  private int[] agentIDs_;
  private String[] agentNames_;
  private int maxPlayers_;
  private boolean showDebug_;
  private boolean showMinor_;

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
      transcript_ = new StringBuilder(str);
    }

    public void append(String str){
      transcript_.append(str);
    }

    @Override
    public String toString() {
      return transcript_.toString();
    }

    @Override
    public String toReadable() {
      return transcript_.toString();
    }

    private StringBuilder transcript_;
  }

}

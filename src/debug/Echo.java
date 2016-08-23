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
    int noPlayers = 0;
    boolean[] agentAlive = new boolean[maxPlayers_];
    debug(true, "Seeking " + maxPlayers_ + " players.");
    while (noPlayers < maxPlayers_){
      Tuple<Integer, String> agentInfo = interface_.findAgent();
      int agentNo = agentInfo.fst;
      String agent = agentInfo.snd;
      if(acceptAgent(agent)){
        agentIDs_[noPlayers] = agentNo;
        agentNames_[noPlayers] = agent;
        agentAlive[noPlayers] = true;
        noPlayers++;
        debug(true, "Added agent " + agent);
        debug(true, "Now the number of agents is: " + noPlayers);
      } else {
        debug(true, "Rejecting agent " + agent);
        interface_.terminateAgent(agentNo, "Rejected.");
      }
    }
    EchoState.Transcript transcript = new EchoState.Transcript("Echo: Welcome!\n");
    debug(true, "Beginning game.");
    int remainingAgents = maxPlayers_;
    while(remainingAgents > 0){
      char[] allowed = randomCharSet();
      for(int i = 0; i < maxPlayers_; i++){
        if(agentAlive[i]){
          debug(true, "Next is player " + i);
          interface_.sendState(agentIDs_[i], transcript);
          interface_.sendState(agentIDs_[i], new EchoState.AllowedChars(allowed));
          // We get an action (a message) from the agent
          Action chosen = interface_.requestAction(agentIDs_[i]);
          if(chosen != null){
            String message = chosen.encode();
            if(!isValid(allowed, message)) {
              System.out.println("Agent " + i + " has tried to perform an illegal action.");
              interface_.terminateAgent(i, "Invalid Action chosen.");
              agentAlive[i] = false;
              remainingAgents--;
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
            interface_.terminateAgent(agentIDs_[i], "Received null action.");
          }
        }
      }
    }
    interface_.end();
  }

  /**
   * Asks at terminal whether to accept an agent.
   * @param agent name of the agent
   * @return whether agent was accepted
   */
  private boolean acceptAgent(String agent){
    message(agent + " wants to join.");
    // try until a valid answer is received
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

  /**
   * @param allowed characters that are allowed
   * @param message string to check
   * @return message contains only characters that are allowed
   */
  private boolean isValid(char[] allowed, String message){
    boolean valid = true;
    for(int i = 0; i<message.length() && valid; i++){
      boolean found = false;
      for(int j = 0; j<allowed.length && !found; j++){
        found = message.charAt(i) == allowed[j];
      }
      valid = found;
    }
    return valid;
  }

  /**
   * @return a selection of roughly 9/10 of all alphabetical characters plus space
   */
  private char[] randomCharSet(){
    String allChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\\ ";
    StringBuilder result = new StringBuilder();
    for(int i = 0; i < allChars.length(); i++){
      if(Math.random() > 0.1){
        result.append(allChars.charAt(i));
      }
    }
    return result.toString().toCharArray();
  }

  private GameInterface interface_;
  private int[] agentIDs_;
  private String[] agentNames_;
  private int maxPlayers_;
  private boolean showDebug_;
  private boolean showMinor_;

}

package debug;

import socketInterface.SocketAgentInterface;
import interfaces.Action;
import interfaces.Agent;
import interfaces.AgentInterface;
import interfaces.State;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * An Agent for the Echo "game". It prints the previous things that have been said and then asks the user for what to
 * say, ignoring characters not available to it.
 */
public class EchoBot implements Agent {

  /**
   * Runs the EchoBot, connecting to the given url at the given port.
   * @param args -t to show all debug messages
   */
  public static void main(String[] args) {
    EchoBot instance = new EchoBot(args);
    AgentInterface connection = new SocketAgentInterface(instance, new EchoStateMaster());
    connection.run();
    instance.debug(true, "Simulation over.");
  }

  /**
   * Constructor
   * @param args -d for show debug, -dm for show all debug
   */
  public EchoBot(String[] args){
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
  }

  @Override
  public boolean checkGame(String ident) {
    return ident.equals("Echo");
  }

  @Override
  public Action decide() {
    StringBuilder message = new StringBuilder();
    try {
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      String temp = stdIn.readLine();
      for(int i = 0; i<temp.length(); i++){
        char c = temp.charAt(i);
        for(char valid : allowed_){
          if(c == valid){
            message.append(c);
          }
        }
      }
    }
    catch(IOException e){
      error(e);
    }
    return new EchoAction(message.toString());
  }

  @Override
  public void perceiveState(State update) {
    if(update instanceof EchoState.Transcript){
      System.out.println("Message Log:\n" + update.toString());
    }
    else if(update instanceof EchoState.AllowedChars){
      allowed_ = ((EchoState.AllowedChars) update).getAllowed();
    }
  }

  @Override
  public String identity() {
    return "EchoBot";
  }

  @Override
  public void message(Object obj) {
    System.out.println("Message from game: " + obj);
  }

  @Override
  public void debug(boolean isMajor, Object o1) {
    if(showDebug_ && (isMajor || showMinor_)) {
      String message = "Debug for " + identity() + ": " + o1;
      System.out.println(message);
    }
  }

  @Override
  public void error(Object obj) {
    String message = "Error for "+identity()+": "+obj;
    System.out.println(message);
  }

  /**
   * What chars the agent may use.
   */
  private char[] allowed_;

  /**
   * What levels of debug message to show.
   */
  private boolean showDebug_;
  private boolean showMinor_;
}

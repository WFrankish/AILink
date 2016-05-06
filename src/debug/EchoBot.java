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
   * Constructor
   * @param showMinor whether to show minor debug messages
   */
  public EchoBot(boolean showMinor){
    showMinor_ = showMinor;
  }

  /**
   * Runs the EchoBot, connecting to the given url at the given port.
   * @param args -t to show all debug messages
   */
  public static void main(String[] args) {
    boolean showMinor = (args.length > 0 && ParseTools.parseTruth(args[0]));
    while(true) {
      EchoBot instance = new EchoBot(showMinor);
      AgentInterface connection = new SocketAgentInterface(
        instance,
        new Echo.EchoStateMaster(),
        new Echo.EchoActionMaster());
      connection.run();
    }
  }

  @Override
  public boolean checkGame(String ident) {
    return ident.equals("Echo");
  }

  @Override
  public Action decide(Action[] actions, State state) {
    System.out.println("Previous Messages:");
    System.out.print(state.toString());
    System.out.println("Allowed characters:");
    for(Action action : actions){
      System.out.print(action);
    }
    System.out.println();
    StringBuilder message = new StringBuilder();
    try {
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      String temp = stdIn.readLine();
      for(int i = 0; i<temp.length(); i++){
        String c = temp.substring(i, i+1);
        for(Action action : actions){
          if(c.equals(action.toString())){
            message.append(c);
          }
        }
      }
    }
    catch(IOException e){
      error(e);
    }
    return new Echo.EchoAction(message.toString());
  }

  @Override
  public void updateState(State update) {
    System.out.println("Message Received: " + update.toReadable());
  }

  @Override
  public String identity() {
    return "EchoBot";
  }

  @Override
  public void debug(boolean isMajor, Object o1) {
    if(isMajor || showMinor_) {
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
   * Whether to show minor debug messages.
   */
  private boolean showMinor_;
}

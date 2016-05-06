package debug;

import socketInterface.SocketAgentInterface;
import templates.Action;
import templates.Agent;
import templates.AgentInterface;
import templates.State;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * An Agent for the Echo "game". It prints the previous things that have been said and then asks the user for what to
 * say, INCLUDING characters not available to it.
 */
public class BadEchoBot implements Agent {

  /**
   * Constructor
   * @param showMinor whether to show minor debug messages
   */
  public BadEchoBot(boolean showMinor){
    showMinor_ = showMinor;
  }

  /**
   * Runs the BadEchoBot, connecting to the given url at the given port.
   * @param args -t to show all debug messages
   */
  public static void main(String[] args) {
    boolean showMinor = (args.length > 0 && ParseTools.parseTruth(args[0]));
    while(true) {
      BadEchoBot instance = new BadEchoBot(showMinor);
      try {
        String url = "localhost";
        System.out.println("Enter port number of host:");
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        int port = Integer.parseInt(stdIn.readLine());
        AgentInterface connection = new SocketAgentInterface(url,
            port,
            instance,
            new Echo.EchoStateMaster(),
            new Echo.EchoActionMaster());
        connection.run();
      } catch (Exception e) {
        instance.error(e);
      }
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
    StringBuilder message = new StringBuilder();
    try {
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      message.append(stdIn.readLine());
    }
    catch(IOException e){
      error(e);
    }
    return new Echo.EchoAction(message.toString());
  }

  @Override
  public void updateState(State update) {
      System.out.println("Received message: " + update.toReadable());
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


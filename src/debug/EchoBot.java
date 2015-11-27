package debug;

import socketInterface.SocketAgentInterface;
import templates.Action;
import templates.Agent;
import templates.AgentInterface;
import templates.State;

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
   * @param args url port
   */
  public static void main(String[] args) {
    EchoBot instance = new EchoBot();
    if (args.length < 2) {
      instance.error("Bad arguments");
    } else {
      try {
        String url = args[0];
        int port = Integer.parseInt(args[1]);
        AgentInterface connection = new SocketAgentInterface(url,
            port,
            instance,
            new Echo.EchoStateMaster(),
            new Echo.EchoActionMaster());
        connection.run();
      } catch (Error e) {
        instance.error(e.toString());
      }
    }
  }

  @Override
  public Action decide(Action[] actions, State state) {
    System.out.println("Previous Messages ");
    System.out.println(state.toString());
    StringBuilder message = new StringBuilder();
    try {
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      String temp = stdIn.readLine();
      stdIn.close();
      for(int i = 0; i<temp.length(); i++){
        String c = temp.substring(i, i+1);
        for(int j = 0; j<actions.length; j++){
          if(c.equals(actions[j].toString())){
            message.append(c);
          }
        }
      }
    }
    catch(IOException e){
      error(e.toString());
    }
    return new Echo.EchoAction(message.toString());
  }

  @Override
  public void initialState(State debrief) {
    System.out.println("Initial message is: " + debrief.toString());
  }

  @Override
  public void updateState(State update) {
    // Do nothing.
  }

  @Override
  public String identity() {
    return "EchoBot";
  }

  @Override
  public void end() {

  }

  @Override
  public void debug(String str) {
    System.out.println("Debug for " + identity() + ": " + str);
  }

  @Override
  public void error(String str) {
    System.out.println("Error for " + identity() + ": " + str);
  }
}

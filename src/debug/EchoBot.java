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
   * @param args
   */
  public static void main(String[] args) {
    EchoBot instance = new EchoBot();
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

  @Override
  public Action decide(Action[] actions, State state) {
    System.out.println("Previous Messages:");
    System.out.print(state.toString());
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
}

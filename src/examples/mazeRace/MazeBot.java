package examples.mazeRace;

import socketInterface.SocketAgentInterface;
import templates.Action;
import templates.Agent;
import templates.AgentInterface;
import templates.State;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MazeBot implements Agent {

  public static void main(String[] args) {
    MazeBot instance = new MazeBot();
    try {
      String url = "localhost";
      System.out.println("Enter port number of host:");
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      int port = Integer.parseInt(stdIn.readLine());
      AgentInterface connection = new SocketAgentInterface(url,
          port,
          instance,
          new MazeStateMaster(),
          new MovementActionMaster());
      connection.run();
    } catch (Exception e) {
      instance.error(e);
    }
  }

  @Override
  public void initialState(State debrief) {

  }

  @Override
  public void updateState(State update) {

  }

  @Override
  public Action decide(Action[] actions, State state) {
    int i =  (int) Math.round(Math.random() * (actions.length-1));
    return actions[i];
  }

  @Override
  public String identity() {
    return "MazeBot";
  }

  @Override
  public void debug(boolean isMajor, Object obj) {
    System.out.println(obj);
  }

  @Override
  public void error(Object obj) {
    System.out.println(obj);
  }

  @Override
  public void end() {

  }
}

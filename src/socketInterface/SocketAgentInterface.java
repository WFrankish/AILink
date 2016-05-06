package socketInterface;

import interfaces.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketAgentInterface implements AgentInterface {
  /**
   * Constructor
   * @param agent the agent we are communicating for
   * @param stateMaster information on how to parse states for the problem we are communicating for
   * @param actionMaster information on how to parse actions for the problem we are communicating for
   */
  public SocketAgentInterface(Agent agent, StateMaster stateMaster, ActionMaster actionMaster){
    String url = "";
    int port = 0;
    try {
      url = "localhost";
      System.out.println("Enter port number of host:");
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      port = Integer.parseInt(stdIn.readLine());
    } catch (Exception e) {
      agent_.error(e);
    }
    url_ = url;
    signUpPort_ = port;
    agent_ = agent;
    stateMaster_ = stateMaster;
    actionMaster_ = actionMaster;
    ended = false;
  }

  @Override
  public void run(){
    try{
      // Create local socket to communicate actions.
      ServerSocket serverSocket = new ServerSocket(0, 0, InetAddress.getByName("localhost"));
      String address = serverSocket.getInetAddress().toString();
      int port = serverSocket.getLocalPort();
      // Register agent with the game via its registration socket, passing it our socket to communicate on.
      Socket registrationSocket = new Socket(url_, signUpPort_);
      InOut inOut = new InOut(registrationSocket);
      agent_.debug(true, "Signing up for game.");
      inOut.writeLine(agent_.identity());
      inOut.writeLine(address);
      inOut.writeLine(port + "");
      // Receive the initial state information for the game.
      String header = inOut.readLine();
      if(header.equals("ACCEPT")) {
        String gameName = inOut.readLine();
        if(agent_.checkGame(gameName)) {
          agent_.debug(false, "Registered with interface.");
          inOut.writeLine("ACCEPT");
          registrationSocket.close();
        }
        else{
          inOut.writeLine("CLOSE");
          registrationSocket.close();
          return;
        }
      }
      else{
        registrationSocket.close();
        agent_.error("Registration process failed.");
        end();
        return;
      }
      // Play the game.
      Socket socket = serverSocket.accept();
      inOut = new InOut(socket);
      while(!ended && !socket.isClosed()){
        header = inOut.readLine();
        if(header.equals("REQUEST")) {
          // Communication is a request for an action, so we will be passed a state and a list of actions
          State state = stateMaster_.parseString(inOut.readLine());
          agent_.debug(false, "Received State: " + state.toReadable());
          Action[] actions = actionMaster_.parseActions(inOut.readLine());
          agent_.debug(false, "Received Actions: " + actionMaster_.actionsToReadable(actions));
          Action action = agent_.decide(actions, state);
          agent_.debug(false, "Writing action: " + action.toReadable());
          inOut.writeLine(action.toString());
        }
        else if(header.equals("UPDATE")){
          // Communication is a state update, so we wil be passed a state.
          State state = stateMaster_.parseString(inOut.readLine());
          agent_.debug(false, "Received State: " + state.toReadable());
          agent_.updateState(state);
        }
        else if(header.equals("CLOSE")){
          String msg = inOut.readLine();
          agent_.error("Kicked with message from game - " + msg);
          end();
        }
        else{
          // Communication is either to close, or something has gone wrong so we close anyway.
          agent_.error("Bad header - " + header);
          inOut.close();
          end();
        }
      }
      agent_.debug(true, "Game over.");
    }
    catch(IOException e){
      agent_.error(e);
      end();
    }
  }

  public void end(){
    agent_.debug(true, "Interface has ended.");
    ended = true;
  }

  private Agent agent_;
  private String url_;
  private int signUpPort_;
  private StateMaster stateMaster_;
  private ActionMaster actionMaster_;
  private boolean ended;


}

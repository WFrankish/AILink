package socketInterface;

import templates.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketAgentInterface implements AgentInterface {
  /**
   * Constructor
   * @param url the url for the socket we will register with
   * @param port the port for the socket we will register with
   * @param agent the agent we are communicating for
   * @param stateMaster information on how to parse states for the problem we are communicating for
   * @param actionMaster information on how to parse actions for the problem we are communicating for
   */
  public SocketAgentInterface(String url, int port, Agent agent, StateMaster stateMaster, ActionMaster actionMaster){
    url_ = url;
    signUpPort_ = port;
    agent_ = agent;
    stateMaster_ = stateMaster;
    actionMaster_ = actionMaster;
    ended = false;
  }

  public void run(){
    try{
      // Create local socket to communicate actions.
      ServerSocket serverSocket = new ServerSocket(0, 0, InetAddress.getByName("localhost"));
      String address = serverSocket.getInetAddress().toString();
      int port = serverSocket.getLocalPort();
      // Register agent with the game via its registration socket, passing it our socket to communicate on.
      Socket registrationSocket = new Socket(url_, signUpPort_);
      InOut inOut = new InOut(registrationSocket);
      agent_.debug("Signing up for game.");
      inOut.writeLine(agent_.identity());
      inOut.writeLine(address);
      inOut.writeLine(port+"");
      // Receive the initial state information for the game.
      String header = inOut.readLine();
      if(header.equals("ACCEPT")) {
        String debrief = inOut.readLine();
        agent_.debug("Received debrief: ", debrief);
        registrationSocket.close();
        agent_.initialState(stateMaster_.parseString(debrief));
      }
      else{
        registrationSocket.close();
        agent_.error("We were rejected by the game.");
        end();
        return;
      }
      // Play the game.
      Socket socket = serverSocket.accept();
      inOut = new InOut(socket);
      while(!socket.isClosed()){
        header = inOut.readLine();
        if(header.equals("REQUEST")) {
          // Communication is a request for an action, so we will be passed a state and a list of actions
          String stateStr = inOut.readLine();
          agent_.debug("Received State: ", stateStr);
          String actionsStr = inOut.readLine();
          agent_.debug("Received Actions: ", actionsStr);
          State state = stateMaster_.parseString(stateStr);
          Action[] actions = actionMaster_.parseActions(actionsStr);
          Action action = agent_.decide(actions, state);
          agent_.debug("Writing action: ", action);
          inOut.writeLine(action.toString());
        }
        else if(header.equals("UPDATE")){
          // Communication is a state update, so we wil be passed a state.
          String stateStr = inOut.readLine();
          agent_.debug("Received State: ", stateStr);
          State state = stateMaster_.parseString(stateStr);
          agent_.updateState(state);
        }
        else{
          // Communication is either to close, or something has gone wrong so we close anyway.
          inOut.close();
          end();
        }
      }
      agent_.debug("Game over.");
    }
    catch(IOException e){
      agent_.error(e);
      end();
    }
  }

  public void end(){
    if(!ended){
      agent_.debug("Interface has ended.");
      ended = true;
      agent_.end();
    }
  }

  private Agent agent_;
  private String url_;
  private int signUpPort_;
  private StateMaster stateMaster_;
  private ActionMaster actionMaster_;
  private boolean ended;
}

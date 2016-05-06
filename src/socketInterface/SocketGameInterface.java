package socketInterface;

import common.Tuple;
import interfaces.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class SocketGameInterface implements GameInterface {
  /**
   * Constructor
   * @param game the game we will be communicating for
   * @param actionMaster information on how to parse actions for the problem we are communicating for
   */
  public SocketGameInterface(Game game, ActionMaster actionMaster){
    game_ = game;
    actionMaster_ = actionMaster;
    try {
      // construct a registration chanel
      serverSocket_ = new ServerSocket(0, 0, InetAddress.getByName("localhost"));
      signUpPort_ = serverSocket_.getLocalPort();
      game_.message("Sign up address is " + serverSocket_.getInetAddress().toString());
      game_.message("Sign up port is " + signUpPort_);
    }
    catch (IOException e){
      game_.error(e.toString());
    }
  }

  @Override
  public Tuple<Integer, String> findAgent() {
    try {
      while(true) {
        // accept a communication on the registration chanel
        // we are expecting the name of an agent, and the address and port for a new chanel to the agent
        Socket clientSocket = serverSocket_.accept();
        InOut inOut = new InOut(clientSocket);
        String agent = inOut.readLine();
        String agentAddress = inOut.readLine();
        int agentPort = Integer.parseInt(inOut.readLine());
        inOut.writeLine("ACCEPT");
        inOut.writeLine(game_.identity());
        String response = inOut.readLine();
        if(response.equals("ACCEPT")) {
          int agentId = agentCount_;
          agentCount_++;
          game_.debug(true, "Agent " + agent + " is registering.");
          game_.debug(true, "Address is " + agentAddress + " and Port is " + agentPort);
          // construct our new chanel and add it to the list
          Socket agentSocket = new Socket(InetAddress.getByName("localhost"), agentPort);
          agents_.add(agentId, new InOut(agentSocket));
          inOut.close();
          return new Tuple<Integer, String>(agentId, agent);
        }
      }
    }
    catch(IOException e){
      game_.error(e.toString());
      return null;
    }
  }

  @Override
  public Action requestAction(int agentID, State state, Action[] actions) {
    InOut inOut = agents_.get(agentID);
    try {
      // Send the agent a state and a list of actions
      game_.debug(false, "Requesting action from " + agentID);
      inOut.writeLine("REQUEST");
      game_.debug(false, "Sending state: " + state.toReadable());
      inOut.writeLine(state.toString());
      game_.debug(false, "Sending actions: " + actionMaster_.actionsToReadable(actions));
      inOut.writeLine(actionMaster_.actionsToString(actions));
      // Get an action back.
      String action = inOut.readLine();
      game_.debug(false, "Received action: " + action);
      return actionMaster_.parseAction(action);
    }
    catch (IOException e){
      game_.error(e.toString());
      return null;
    }
  }

  @Override
  public void updateState(int agentID, State state) {
    InOut inOut = agents_.get(agentID);
    try {
      // Send an agent a state
      game_.debug(false, "Sending State update to " + agentID);
      inOut.writeLine("UPDATE");
      game_.debug(false, "Sending state: " + state.toReadable());
      inOut.writeLine(state.toString());
      // Expect nothing back, so don't wait.
    }
    catch (IOException e){
      game_.error(e.toString());
    }
  }

  @Override
  public void terminateAgent(int agentID, String msg) {
    InOut inOut = agents_.get(agentID);
    try {
      // Tell the agent the chanel will close.
      game_.debug(true, "Terminating agent: " + agentID);
      inOut.writeLine("CLOSE");
      inOut.writeLine(msg);
      inOut.close();
    }
    catch (IOException e){
      game_.error(e.toString());
    }
  }

  @Override
  public void end() {
    try {
      game_.debug(true, "Game interface has ended.");
      serverSocket_.close();
      for (int i = 0; i<agentCount_; i++) {
        terminateAgent(i, "Simulation over.");
      }
    }
    catch (IOException e){
      game_.error(e.toString());
    }
  }

  ServerSocket serverSocket_;
  int signUpPort_;
  private LinkedList<InOut> agents_ = new LinkedList<InOut>();
  private int agentCount_;
  private Game game_;
  private ActionMaster actionMaster_;

}

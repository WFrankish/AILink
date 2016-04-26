package socketInterface;

import templates.*;

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
      game_.message("sign up address is " + serverSocket_.getInetAddress().toString());
      game_.message("sign up port is " + signUpPort_);
    }
    catch (IOException e){
      game_.error(e.toString());
    }
  }

  @Override
  public void requestAgent() {
    try {
      // accept a communication on the registration chanel
      // we are expecting the name of an agent, and the address and port for a new chanel to the agent
      Socket clientSocket = serverSocket_.accept();
      InOut inOut = new InOut(clientSocket);
      String agent = inOut.readLine();
      game_.debug(true, "Agent " + agent + " is registering.");
      String agentAddress = inOut.readLine();
      int agentPort = Integer.parseInt(inOut.readLine());
      game_.debug(true, "Address is " + agentAddress + " and Port is "+agentPort);
      // tell the game about the agent, so it may construct an initial state for the agent
      State debrief = game_.registerAgent(agent, agents_.size());
      if(debrief==null) {
        // reject the agent
        inOut.writeLine("CLOSE");
      }
      else{
        game_.debug(false, "Delivering debrief: " + debrief.toReadable());
        // send the agent its initial state
        inOut.writeLine("ACCEPT");
        inOut.writeLine(debrief.toString());
        // construct our new chanel and add it to the list
        Socket agentSocket = new Socket(InetAddress.getByName("localhost"), agentPort);
        agents_.add(new InOut(agentSocket));
      }
      inOut.close();
    }
    catch(IOException e){
      game_.error(e.toString());
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
  public void terminateAgent(int agentID) {
    InOut inOut = agents_.get(agentID);
    try {
      // Tell the agent the chanel will close.
      game_.debug(true, "Terminating agent: " + agentID);
      inOut.writeLine("CLOSE");
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
      for (InOut io : agents_) {
        io.writeLine("CLOSE");
        io.close();
      }
    }
    catch (IOException e){
      game_.error(e.toString());
    }
  }

  ServerSocket serverSocket_;
  int signUpPort_;
  private LinkedList<InOut> agents_ = new LinkedList<InOut>();

  private Game game_;
  private ActionMaster actionMaster_;

}

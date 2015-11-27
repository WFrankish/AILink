package socketInterface;

import templates.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class SocketGameInterface implements GameInterface {
  public SocketGameInterface(Game game, ActionMaster actionMaster){
    game_ = game;
    actionMaster_ = actionMaster;
    try {
      serverSocket_ = new ServerSocket(0, 0, InetAddress.getByName("localhost"));
      signUpPort_ = serverSocket_.getLocalPort();
      game_.debug("sign up address is "+ serverSocket_.getInetAddress().toString());
      game_.debug("sign up port is "+signUpPort_);
    }
    catch (IOException e){
      game_.error(e.toString());
    }
  }

  @Override
  public void requestAgent() {
    try {
      Socket clientSocket = serverSocket_.accept();
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      String agent = in.readLine();
      game_.debug("Agent "+agent+" is registering.");
      String agentAddress = in.readLine();
      int agentPort = Integer.parseInt(in.readLine());
      game_.debug("Address is "+agentAddress+" and Port is "+agentPort);
      State debrief = game_.registerAgent(agent, agents_.size());
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
      out.write(debrief.toString()+"\n");
      out.flush();
      Socket agentSocket = new Socket(InetAddress.getByName("localhost"), agentPort);
      agents_.add(agentSocket);
    }
    catch(IOException e){
      game_.error(e.toString());
    }
  }

  @Override
  public Action requestAction(int agentID, State state, Action[] actions) {
    Socket socket = agents_.get(agentID);
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      out.write(state.toString()+"\n");
      out.write(actionMaster_.actionsToString(actions));
      out.flush();
      String action = in.readLine();
      return actionMaster_.parseAction(action);
    }
    catch (IOException e){
      game_.error(e.toString());
      return null;
    }
  }

  @Override
  public void end() {
    try {
      serverSocket_.close();
      for (Socket s : agents_) {
        s.close();
      }
    }
    catch (IOException e){
      game_.error(e.toString());
    }
  }

  ServerSocket serverSocket_;
  int signUpPort_;
  private LinkedList<Socket> agents_ = new LinkedList<Socket>();

  private Game game_;
  private ActionMaster actionMaster_;

}

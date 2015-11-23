package socketInterface;

import templates.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketGameInterface implements GameInterface {
  public SocketGameInterface(int port, Game game, ActionMaster actionMaster){
    signUpPort_ = port;
    game_ = game;
    actionMaster_ = actionMaster;
  }

  @Override
  public void requestAgent() {
    try {
      ServerSocket serverSocket = new ServerSocket(signUpPort_);
      Socket clientSocket = serverSocket.accept();
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      String agent = in.readLine();
      in.close();
      int port = 3;
      State debrief = game_.registerAgent(agent, port);
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
      out.write(port+"\n");
      out.write(debrief.toString()+"\n");
      out.flush();
      out.close();
    }
    catch(IOException e){
      game_.error(e.toString());
    }
  }

  @Override
  public Action requestAction(int agentID, State state, Action[] actions) {
    return null;
  }

  private int signUpPort_;
  private Game game_;
  private ActionMaster actionMaster_;

}

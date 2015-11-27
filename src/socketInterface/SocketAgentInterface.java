package socketInterface;

import templates.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketAgentInterface implements AgentInterface {
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
      ServerSocket serverSocket = new ServerSocket(0, 0, InetAddress.getByName("localhost"));
      String address = serverSocket.getInetAddress().toString();
      int port = serverSocket.getLocalPort();
      // Register agent with the game.
      Socket registrationSocket = new Socket(url_, signUpPort_);
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(registrationSocket.getOutputStream()));
      out.write(agent_.identity()+"\n");
      out.write(address+"\n");
      out.write(port+"\n");
      out.flush();
      //out.close();
      BufferedReader in = new BufferedReader(new InputStreamReader(registrationSocket.getInputStream()));
      String debrief = in.readLine();
      //in.close();
      registrationSocket.close();
      agent_.initialState(stateMaster_.parseString(debrief));
      // Play the game.
      Socket socket = serverSocket.accept();
      out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      while(!socket.isClosed()){
        String stateS = in.readLine();
        String actionsS = in.readLine();
        State state = stateMaster_.parseString(stateS);
        Action[] actions = actionMaster_.parseActions(actionsS);
        Action action = agent_.decide(actions, state);
        out.write(action.toString());
        out.flush();
      }
      in.close();
      out.close();
    }
    catch(IOException e){
      agent_.error(e.toString());
      end();
    }
  }

  public void end(){
    if(!ended){
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

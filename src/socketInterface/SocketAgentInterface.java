package socketInterface;

import templates.ActionMaster;
import templates.Agent;
import templates.AgentInterface;
import templates.StateMaster;

import java.io.*;
import java.net.Socket;

public class SocketAgentInterface implements AgentInterface {
  public SocketAgentInterface(String url, int port, StateMaster stateMaster, ActionMaster actionMaster){
    url_ = url_;
    signUpPort_ = port;
    stateMaster_ = stateMaster;
    actionMaster_ = actionMaster;
  }

  public void registerAgent(Agent agent){
    agent_ = agent;
  }

  public void run(){
    // Register agent with the game.
    try{
      Socket registrationSocket = new Socket(url_, signUpPort_);
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(registrationSocket.getOutputStream()));
      out.write(agent_.identity()+"\n");
      out.flush();
      out.close();
      BufferedReader in = new BufferedReader(new InputStreamReader(registrationSocket.getInputStream()));
    }
    catch(IOException e){
      agent_.error(e.toString());
    }
    // Play the game.
  }

  private Agent agent_;
  private String url_;
  private int signUpPort_;
  private int port_;
  private StateMaster stateMaster_;
  private ActionMaster actionMaster_;
}

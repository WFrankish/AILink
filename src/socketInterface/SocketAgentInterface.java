package socketInterface;

import templates.Agent;
import templates.AgentInterface;

import java.net.Socket;
import java.io.IOException;

public class SocketAgentInterface implements AgentInterface {
  public SocketAgentInterface(String url, int port){
    url_ = url_;
    signUpPort_ = port;
  }

  public void registerAgent(Agent agent){
    agent_ = agent;
  }

  public void run(){
    try{
      port_ = registerSelf();
    }
    catch(IOException e){

    }
  }

  private int registerSelf() throws IOException{
    Socket registrationSocket = new Socket(url_, signUpPort_);
    return 0;
  }

  private Agent agent_;
  private String url_;
  private int signUpPort_;
  private int port_;
}

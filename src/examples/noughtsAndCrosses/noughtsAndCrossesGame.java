package examples.noughtsAndCrosses;

import common.Tuple;
import socketInterface.SocketGameInterface;
import templates.Action;
import templates.Game;
import templates.GameInterface;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class noughtsAndCrossesGame implements Game {

  public static void main(String[] args){
    boolean showMinor = ParseTools.find(args, "-d") > -1;
    while(true) {
      new noughtsAndCrossesGame(showMinor).run();
    }
  }

  public  noughtsAndCrossesGame(boolean showMinor){
    interface_ = new SocketGameInterface(this, new OnXActionMaster());
    showMinor_ = showMinor;
    state_ = new OnXState.Grid();
  }

  public void run(){
    while(players_ < 2){
      Tuple<Integer, String> agentInfo = interface_.findAgent();
      registerAgent(agentInfo);
    }
    boolean done = false;
    boolean crossTurn = true;
    Action[] actions = getActions();
    while(!done && actions.length>0){
      if(crossTurn){
        crossTurn = false;
        Action temp = interface_.requestAction(cross_, state_, actions);
        OnXAction action = (OnXAction) temp;
        if(!validAction(action)){
          done = true;
          setWinner(Token.NOUGHT);
        }
        else{
          done = doAction(action, Token.CROSS);
        }
       }
      else{
        crossTurn = true;
        Action temp = interface_.requestAction(nought_, state_, getActions());
        OnXAction action = (OnXAction) temp;
        if(!validAction(action)){
          done = true;
          setWinner(Token.CROSS);
        }
        else {
          done = doAction(action, Token.NOUGHT);
        }
      }
      actions = getActions();
    }
    interface_.terminateAgent(cross_, "Game Over.");
    interface_.terminateAgent(nought_, "Game Over.");
    interface_.end();
  }

  public void registerAgent(Tuple<Integer, String> agentInfo) {
    int agentNo = agentInfo.fst;
    String agent = agentInfo.snd;
    message(agent + " wants to play.");
    if(players_ == 0) {
      message("Enter X for cross and O for nought, enter nothing to reject");
      try {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String temp = stdIn.readLine();
        if (temp.length() == 0) {
          interface_.terminateAgent(agentNo, "Rejected.");
        } else {
          char c = temp.charAt(0);
          if (c == 'X' || c == 'x') {
            cross_ = agentNo;
            players_++;
            interface_.updateState(agentNo, new OnXState.Player(Token.CROSS));
          } else if (c == 'O' || c == 'o' || c == '0') {
            nought_ = agentNo;
            noughtSet_ = true;
            players_++;
            interface_.updateState(agentNo, new OnXState.Player(Token.NOUGHT));
          } else {
            interface_.terminateAgent(agentNo, "Rejected.");
          }
        }
      } catch (Exception e) {
        error(e);
        interface_.terminateAgent(agentNo, "Issue at registration phase.");
      }
    }
    else{
      if(noughtSet_){
        message("Enter X for cross, enter nothing to reject");
        try {
          BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
          String temp = stdIn.readLine();
          if (temp.length() == 0) {
            interface_.terminateAgent(agentNo, "Rejected.");
          } else {
            char c = temp.charAt(0);
            if (c == 'X' || c == 'x') {
              cross_ = agentNo;
              players_++;
              interface_.updateState(agentNo, new OnXState.Player(Token.CROSS));
            } else {
              interface_.terminateAgent(agentNo, "Rejected.");
            }
          }
        } catch (Exception e) {
          error(e);
          interface_.terminateAgent(agentNo, "Issue at registration phase.");
        }
      }
      else{
        message("Enter O for nought, enter nothing to reject");
        try {
          BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
          String temp = stdIn.readLine();
          if (temp.length() == 0) {
            interface_.terminateAgent(agentNo, "Rejected.");
          } else {
            char c = temp.charAt(0);
            if (c == 'O' || c == 'o' || c == '0') {
              nought_ = agentNo;
              noughtSet_ = true;
              players_++;
              interface_.updateState(agentNo, new OnXState.Player(Token.NOUGHT));
            } else {
              interface_.terminateAgent(agentNo, "Rejected.");
            }
          }
        } catch (Exception e) {
          error(e);
          interface_.terminateAgent(agentNo, "Issue at registration phase.");
        }
      }
    }
  }

  @Override
  public String identity() {
    return "Noughts and Crosses";
  }

  @Override
  public void message(Object obj) {
    String message = "Message to Game Master: " + obj;
    System.out.println(message);
  }

  @Override
  public void debug(boolean isMajor, Object obj) {
    if(isMajor || showMinor_) {
      String message = "Debug for " + identity() + ": " + obj;
      System.out.println(message);
    }
  }

  @Override
  public void error(Object obj) {
    String message = "Error for "+identity()+": "+obj;
    System.out.println(message);
  }

  @Override
  public void interfaceFailed() {

  }

  private Action[] getActions(){
    ArrayList<Action> actions = new ArrayList<Action>();
    for(int y = 0; y < 3; y++){
      for(int x = 0; x < 3; x++){
        Token token = state_.getTokenAt(x, y);
        if(token == Token.BLANK){
          actions.add(new OnXAction(x, y));
        }
      }
    }
    return actions.toArray(new Action[1]);
  }

  private boolean validAction(OnXAction action){
    if(action==null){
      return false;
    }
    int x = action.getX();
    int y = action.getY();
    Token token = state_.getTokenAt(x, y);
    return token == Token.BLANK;
  }

  public boolean doAction(OnXAction action, Token token){
    int x = action.getX();
    int y = action.getY();
    state_.setTokenAt(x, y, token);
    // search for new line of token given
    // try horizontal lines
    boolean line = true;
    boolean full = false;
    for(int nx = 0; nx < 3 && line; nx++){
      line = (token == state_.getTokenAt(nx, y));
    }
    if(!line){
      // no horizontal lines, try vertical
      line = true;
      for(int ny = 0; ny < 3 && line; ny++){
        line = (token == state_.getTokenAt(x, ny));
      }
    }
    if(!line && (x == y) ){
      // try diagonal
      line = true;
      for(int n = 0; n < 3 && line; n++){
        line = (token == state_.getTokenAt(n, n));
      }
    }
    if(!line && (x == 2-y) ){
      // try other diagonal
      line = true;
      for(int n = 0; n < 3 && line; n++){
        line = (token == state_.getTokenAt(2-n, n));
      }
    }
    if(line){
      setWinner(token);
    }
    else{
      full = true;
      for(int i = 0; i<2; i++){
        for(int j = 0; j<2; j++){
          full &= state_.getTokenAt(i, j) != Token.BLANK;
        }
      }
      if(full){
        setWinner(Token.BLANK);
      }
    }
    return line || full;
  }

  private void setWinner(Token token){
    OnXState.Winner winner = new OnXState.Winner(token);
    interface_.updateState(cross_, state_);
    interface_.updateState(cross_, winner);
    interface_.updateState(nought_, state_);
    interface_.updateState(nought_, winner);
  }

  private int nought_;
  private int cross_;
  private boolean noughtSet_ = false;
  private OnXState.Grid state_ = new OnXState.Grid();
  private GameInterface interface_;
  private int players_ = 0;
  private boolean showMinor_;
}

package examples.noughtsAndCrosses;

import common.Tuple;
import socketInterface.SocketGameInterface;
import interfaces.Action;
import interfaces.Game;
import interfaces.GameInterface;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class noughtsAndCrossesGame implements Game {

  public static void main(String[] args){
    while(true) {
      new noughtsAndCrossesGame(args).run();
    }
  }

  public  noughtsAndCrossesGame(String[] args){
    interface_ = new SocketGameInterface(this, new OnXActionMaster());
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
    state_ = new OnXState.Grid();
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
    if( showDebug_ && (isMajor || showMinor_)) {
      String message = "Debug for " + identity() + ": " + obj;
      System.out.println(message);
    }
  }

  @Override
  public void error(Object obj) {
    String message = "Error for "+identity()+": "+obj;
    System.out.println(message);
  }

  private void run(){
    int noPlayers = 0;
    noughtSet_ = false;
    debug(true, "Seeking two players.");
    while(noPlayers < 2){
      Tuple<Integer, String> agentInfo = interface_.findAgent();
      int agentNo = agentInfo.fst;
      String agent = agentInfo.snd;
      Token token = acceptAgent(agent, noPlayers);
      if(token != Token.BLANK){
        if(token == Token.CROSS){
          cross_ = agentNo;
        } else {
          noughtSet_ = true;
          nought_ = agentNo;
        }
        noPlayers++;
        debug(true, "Added agent " + agent);
        debug(false, "Now the number of agents is: " + noPlayers);
        interface_.updateState(agentNo, new OnXState.Player(token));
      } else {
        debug(true, "Rejecting agent " + agent);
        interface_.terminateAgent(agentNo, "Rejected.");
      }
    }
    boolean gameOver = false;
    boolean crossTurn = true;
    Action[] actions = getActions();
    while(!gameOver && actions.length>0){
      if(crossTurn){
        crossTurn = false;
        Action temp = interface_.requestAction(cross_, state_, actions);
        OnXAction action = (OnXAction) temp;
        if(!validAction(action)){
          gameOver = true;
          setWinner(Token.NOUGHT);
        }
        else{
          gameOver = doAction(action, Token.CROSS);
        }
      }
      else{
        crossTurn = true;
        Action temp = interface_.requestAction(nought_, state_, getActions());
        OnXAction action = (OnXAction) temp;
        if(!validAction(action)){
          gameOver = true;
          setWinner(Token.CROSS);
        }
        else {
          gameOver = doAction(action, Token.NOUGHT);
        }
      }
      actions = getActions();
    }
    interface_.end();
  }

  private Token acceptAgent(String agent, int players) {
    message(agent + " wants to play.");
    if(players == 0) {
      message("Enter X for cross and O for nought, enter nothing to reject");
      try {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String temp = stdIn.readLine();
        if (temp.length() == 0) {
          return Token.BLANK;
        } else {
          char c = temp.charAt(0);
          if (c == 'X' || c == 'x') {
            return Token.CROSS;
          } else if (c == 'O' || c == 'o' || c == '0') {
            return Token.NOUGHT;
          } else {
            return Token.BLANK;
          }
        }
      } catch (Exception e) {
        error(e);
        return Token.BLANK;
      }
    }
    else{
      if(noughtSet_){
        message("Enter X for cross, enter nothing to reject");
        try {
          BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
          String temp = stdIn.readLine();
          if (temp.length() == 0) {
            return Token.BLANK;
          } else {
            char c = temp.charAt(0);
            if (c == 'X' || c == 'x') {
              return Token.CROSS;
            } else {
              return Token.BLANK;
            }
          }
        } catch (Exception e) {
          error(e);
          return Token.BLANK;
        }
      }
      else{
        message("Enter O for nought, enter nothing to reject");
        try {
          BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
          String temp = stdIn.readLine();
          if (temp.length() == 0) {
            return Token.BLANK;
          } else {
            char c = temp.charAt(0);
            if (c == 'O' || c == 'o' || c == '0') {
              return Token.NOUGHT;
            } else {
              return Token.BLANK;
            }
          }
        } catch (Exception e) {
          error(e);
          return Token.BLANK;
        }
      }
    }
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
  private boolean noughtSet_;
  private OnXState.Grid state_ = new OnXState.Grid();
  private GameInterface interface_;
  private boolean showDebug_;
  private boolean showMinor_;
}

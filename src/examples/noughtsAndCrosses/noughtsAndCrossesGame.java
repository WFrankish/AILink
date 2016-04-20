package examples.noughtsAndCrosses;

import socketInterface.SocketGameInterface;
import templates.Action;
import templates.Game;
import templates.GameInterface;
import templates.State;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class noughtsAndCrossesGame implements Game {

  public static void main(String[] args){
    new noughtsAndCrossesGame().run();
  }

  public  noughtsAndCrossesGame(){
    gameInterface_ = new SocketGameInterface(this, new OnXActionMaster());
    //debug_ = false;
    for(int x = 0; x < 3; x++){
      for(int y = 0; y < 3; y++){
        grid_[y][x] = Token.blank();
      }
    }
  }

  public void run(){
    while(players_ < 2){
      gameInterface_.requestAgent();
    }
    boolean done = false;
    boolean crossTurn = true;
    Action[] actions = getActions();
    while(!done && actions.length>0){
      if(crossTurn){
        crossTurn = false;
        Action temp = gameInterface_.requestAction(cross_, crosses_, actions);
        OnXAction action = (OnXAction) temp;
        if(!validAction(action)){
          done = true;
          setWinner(Token.nought());
        }
        else{
          done = doAction(action, Token.cross());
        }
       }
      else{
        crossTurn = true;
        Action temp = gameInterface_.requestAction(nought_, noughts_, getActions());
        OnXAction action = (OnXAction) temp;
        if(!validAction(action)){
          done = true;
          setWinner(Token.cross());
        }
        else {
          done = doAction(action, Token.nought());
        }
      }
      System.out.println(crosses_.gridToNiceString());
      actions = getActions();
    }
    gameInterface_.updateState(cross_, crosses_);
    gameInterface_.updateState(nought_, noughts_);
    gameInterface_.terminateAgent(cross_);
    gameInterface_.terminateAgent(nought_);
    gameInterface_.end();
  }

  @Override
  public State registerAgent(String agent, int agentNo) {
    System.out.println(agent + " wants to play.");
    if(players_ == 0) {
      System.out.println("Enter X for cross and O for nought, enter nothing to reject");
      try {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String temp = stdIn.readLine();
        if (temp.length() == 0) {
          return null;
        } else {
          char c = temp.charAt(0);
          if (c == 'X' || c == 'x') {
            cross_ = agentNo;
            players_++;
            return crosses_;
          } else if (c == 'O' || c == 'o' || c == '0') {
            nought_ = agentNo;
            noughtSet_ = true;
            players_++;
            return noughts_;
          } else {
            return null;
          }
        }
      } catch (Exception e) {
        error(e);
        return null;
      }
    }
    else{
      if(noughtSet_){
        System.out.println("Enter X for cross, enter nothing to reject");
        try {
          BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
          String temp = stdIn.readLine();
          if (temp.length() == 0) {
            return null;
          } else {
            char c = temp.charAt(0);
            if (c == 'X' || c == 'x') {
              cross_ = agentNo;
              players_++;
              return crosses_;
            } else {
              return null;
            }
          }
        } catch (Exception e) {
          error(e);
          return null;
        }
      }
      else{
        System.out.println("Enter O for nought, enter nothing to reject");
        try {
          BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
          String temp = stdIn.readLine();
          if (temp.length() == 0) {
            return null;
          } else {
            char c = temp.charAt(0);
            if (c == 'O' || c == 'o' || c == '0') {
              nought_ = agentNo;
              noughtSet_ = true;
              players_++;
              return noughts_;
            } else {
              return null;
            }
          }
        } catch (Exception e) {
          error(e);
          return null;
        }
      }
    }
  }

  @Override
  public String identity() {
    return "Noughts and Crosses";
  }

  @Override
  public void debug(Object o1) {
    if(debug_) {
      String message = "Debug for " + identity() + ": " + o1;
      System.out.println(message);
    }
  }

  @Override
  public void debug(Object o1, Object o2) {
    if(debug_) {
      String message = "Debug for " + identity() + ": " + o1 + o2;
      System.out.println(message);
    }
  }

  @Override
  public void debug(Object o1, Object o2, Object o3) {
    if(debug_) {
      String message = "Debug for " + identity() + ": " + o1 + o2 + o3;
      System.out.println(message);
    }
  }

  @Override
  public void debug(Object o1, Object o2, Object o3, Object o4) {
    if (debug_) {
      String message = "Debug for " + identity() + ": " + o1 + o2 + o3 + o4;
      System.out.println(message);
    }
  }

  @Override
  public void error(Object obj) {
    String message = "Error for "+identity()+": "+obj;
    System.out.println(message);
  }

  @Override
  public void end() {

  }

  private Action[] getActions(){
    ArrayList<Action> actions = new ArrayList<Action>();
    for(int x = 0; x < 3; x++){
      for(int y = 0; y < 3; y++){
        Token token = grid_[y][x];
        if(!token.isCross() && !token.isNought()){
          actions.add(new OnXAction(x, y));
        }
      }
    }
    return actions.toArray(new Action[0]);
  }

  private boolean validAction(OnXAction action){
    if(action==null){
      return false;
    }
    int x = action.getX();
    int y = action.getY();
    Token token = grid_[y][x];
    return !token.isCross() && !token.isNought();
  }

  public boolean doAction(OnXAction action, Token token){
    int x = action.getX();
    int y = action.getY();
    noughts_.setTokenAt(x, y, token);
    crosses_.setTokenAt(x, y, token);
    grid_[y][x] = token;
    // search for new line of token given
    // try horizontal lines
    boolean line = true;
    for(int nx = 0; nx < 3 && line; nx++){
      line = token.equals(grid_[y][nx]);
    }
    if(!line){
      // no horizontal lines, try vertical
      line = true;
      for(int ny = 0; ny < 3 && line; ny++){
        line = token.equals(grid_[ny][x]);
      }
    }
    if(!line && (x == y) ){
      // try diagonal
      line = true;
      for(int n = 0; n < 3 && line; n++){
        line = token.equals(grid_[n][n]);
      }
    }
    if(!line && (x == 2-y) ){
      // try other diagonal
      line = true;
      for(int n = 0; n < 3 && line; n++){
        line = token.equals(grid_[n][2-n]);
      }
    }
    if(line){
      setWinner(token);
    }
    return line;
  }

  private void setWinner(Token token){
    noughts_.setWinner(token);
    crosses_.setWinner(token);
  }

  private int nought_;
  private int cross_;
  private boolean noughtSet_ = false;
  private Token[][] grid_ = new Token[3][3];
  private OnXState noughts_ = new OnXState(Token.nought());
  private OnXState crosses_ = new OnXState(Token.cross());
  private GameInterface gameInterface_;
  private boolean debug_ = true;
  private int players_ = 0;
}

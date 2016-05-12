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

/**
 * A Noughts and Crosses Game for AILink
 */
public class noughtsAndCrossesGame implements Game {

  /**
   * Runs a noughts and crosses game, repeatedly
   * @param args -d for some debug, -dm for all debug
   */
  public static void main(String[] args){
    // when one game is over, open another for new agents
    while(true) {
      new noughtsAndCrossesGame(args).run();
    }
  }

  /**
   * instantiation method
   * @param args -d for show debug, -dm for show all debug
   */
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

  /**
   * The main program loop.
   */
  private void run(){
    int noPlayers = 0;
    boolean noughtSet = false;
    debug(true, "Seeking two players.");
    while(noPlayers < 2){
      Tuple<Integer, String> agentInfo = interface_.findAgent();
      int agentNo = agentInfo.fst;
      String agent = agentInfo.snd;
      // ask human game master how to deal with agent
      Token token = acceptAgent(agent, noPlayers, noughtSet);
      if(token != Token.BLANK){
        // add agent to game
        if(token == Token.CROSS){
          cross_ = agentNo;
        } else {
          noughtSet = true;
          nought_ = agentNo;
        }
        noPlayers++;
        debug(true, "Added agent " + agent);
        debug(false, "Now the number of players is: " + noPlayers);
        debug(true, "Sending initial state for "+token);
        interface_.sendState(agentNo, new OnXState.Player(token));
      } else {
        debug(true, "Rejecting agent " + agent);
        interface_.terminateAgent(agentNo, "Rejected.");
      }
    }
    debug(true, "Start of game.");
    boolean gameOver = false;
    // Cross moves first
    boolean crossTurn = true;
    // Find empty squares
    Action[] actions = getActions();
    while(!gameOver && actions.length>0){
      if(crossTurn){
        debug(true, "Cross's turn.");
        crossTurn = false;
        interface_.sendState(cross_, state_);
        Action chosenAction = interface_.requestAction(cross_);
        OnXAction placeToken = (OnXAction) chosenAction;
        if(!validAction(placeToken)){
          debug(true, "Cross tried an illegal action.");
          gameOver = true;
          setWinner(Token.NOUGHT);
        }
        else{
          gameOver = doAction(placeToken, Token.CROSS);
        }
      }
      else{
        debug(true, "Nought's turn.");
        crossTurn = true;
        interface_.sendState(nought_, state_);
        Action temp = interface_.requestAction(nought_);
        OnXAction action = (OnXAction) temp;
        if(!validAction(action)){
          debug(true, "Nought tried an illegal action.");
          gameOver = true;
          setWinner(Token.CROSS);
        }
        else {
          gameOver = doAction(action, Token.NOUGHT);
        }
      }
      // update remaining grid squares
      actions = getActions();
    }
    // game's over now
    interface_.end();
  }

  /**
   * Ask the human game master how to assign an agent
   * @param agent name of agent
   * @param players amount of players already obtained
   * @param noughtSet whether any one is yet playing nought
   * @return BLANK is rejected, otherwise their token.
   */
  private Token acceptAgent(String agent, int players, boolean noughtSet) {
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
      if(noughtSet){
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


  /**
   * Find remaining squares
   * @return the co-ordinates of the remaining squares.
   */
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

  /**
   * checks if the chosen grid space is free
   * @param action action to query
   * @return whether an action is allowed
   */
  private boolean validAction(OnXAction action){
    if(action==null){
      return false;
    }
    int x = action.getX();
    int y = action.getY();
    return state_.getTokenAt(x, y) == Token.BLANK;
  }

  /**
   * update the internal state with an action
   * @param action the square to place a token
   * @param token the token to place
   * @return whether this ends the game
   */
  private boolean doAction(OnXAction action, Token token){
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
      // check for empty squares
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

  /**
   * announce the winner and show the final grid
   * @param token the winner's token
   */
  private void setWinner(Token token){
    OnXState.Winner winner = new OnXState.Winner(token);
    interface_.sendState(cross_, state_);
    interface_.sendState(cross_, winner);
    interface_.sendState(nought_, state_);
    interface_.sendState(nought_, winner);
  }

  // interface id of the two players
  private int nought_;
  private int cross_;

  // the play grid
  private OnXState.Grid state_ = new OnXState.Grid();

  // the interface component
  private GameInterface interface_;

  // levels of debug to show
  private boolean showDebug_;
  private boolean showMinor_;
}

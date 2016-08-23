package examples.noughtsAndCrosses;

import socketInterface.SocketAgentInterface;
import interfaces.*;
import tools.ParseTools;

import java.util.ArrayList;

public class OnXDynamicBot implements Agent{
  public static void main(String[] args) {
    OnXDynamicBot instance = new OnXDynamicBot(args);
    AgentInterface connection = new SocketAgentInterface(
        instance,
        new OnXStateMaster());
    connection.run();
    instance.debug(true, "Ending Program.");
  }

  /**
   * Constructor.
   * @param args -d for show debug, -dm for show all debug
   */
  public OnXDynamicBot(String args[]){
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
  }

  @Override
  public boolean checkGame(String ident) {
    return ident.equals("Noughts and Crosses");
  }

  @Override
  public Action decide() {
    debug(false, "Deciding action.");
    ArrayList<OnXAction> actions = getActions();
    if(node_==null){
      // If we were cross, node_ would already be instantiated
      node_ = new Node();
      OnXAction crossAction = null;
      // find cross's first action
      search:
      for(int x = 0; x < 3; x++){
        for(int y = 0; y < 3; y++){
          if(grid_.getTokenAt(x, y).equals(Token.CROSS)){
            crossAction = new OnXAction(x, y);
            break search;
          }
        }
      }
      assert(crossAction != null);
      node_.addAction(crossAction);
      node_ = node_.children.get(0);
      // create the game tree starting from cross's action
      node_.populate();
    }
    else{
      // Find the last action (there may not be one)
      for(OnXAction action : node_.remaining_){
        boolean stillThere = actions.contains(action);
        if(!stillThere){
          // found it, move to its branch and break out of for loop
          int index = node_.findAction(action);
          if(index==-1){
            node_.addAction(action);
            index = node_.findAction(action);
          }
          node_ = node_.getNode(index);
          node_.populate();
          break;
        }
      }
    }

    // find the best action
    // if all are bad, just give the first action
    OnXAction bestSoFar = actions.get(0);
    for (OnXAction action : actions){
      int next = node_.findAction(action);
      Token winner = node_.getNode(next).getWinner();
      if(winner == me_){
        // this action lets us force a win, immediately go for it
        node_ = node_.getNode(next);
        return action;
      }
      else if(winner.equals(Token.BLANK)){
        // this action does not let the opponent force a win, use it if nothing better found
        bestSoFar = action;
      }
    }
    node_ = node_.getNode(node_.findAction(bestSoFar));
    return bestSoFar;
  }


  @Override
  public void perceiveState(State update) {
    if(update instanceof OnXState.Player){
      // this state tells us who we are
      OnXState.Player state = (OnXState.Player) update;
      me_ = state.getMe();
      node_ = new Node();
      node_.populate();
      debug(true, "Playing as " + me_);
    }
    else if(update instanceof OnXState.Grid){
      // this state tells us the current grid
      grid_ = (OnXState.Grid) update;
      System.out.println(grid_.toString());
    }
    else if(update instanceof OnXState.Winner) {
      // this state tells us who won
      OnXState.Winner state = (OnXState.Winner) update;
      System.out.println(state.toString());
      Token winner = state.getWinner();
      if (winner.equals(me_)) {
        System.out.println("I won!");
      } else if ( winner != Token.BLANK ) {
        System.out.println("Opponent won.");
      } else {
        System.out.println("No winner");
      }
    } else{
      error("Unexpected state " + update);
    }
  }

  @Override
  public String identity() {
    return "Noughts and Crosses Dynamic Programming Bot";
  }

  @Override
  public void message(Object obj) {
    System.out.println("Message from game: " + obj);
  }

  @Override
  public void debug(boolean isMajor, Object obj) {
    if(showDebug_ && (isMajor || showMinor_)) {
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
   * @return current legal actions
   */
  private ArrayList<OnXAction> getActions(){
    StringBuilder builder = new StringBuilder();
    System.out.println(builder.toString());
    ArrayList<OnXAction> result = new ArrayList<OnXAction>();
    for(int x = 0; x<3; x++){
      for(int y = 0; y<3; y++){
        if(grid_.getTokenAt(x, y)==Token.BLANK){
          OnXAction action = new OnXAction(x, y);
          result.add(action);
          builder.append("(" + action + ") ");
        }
      }
    }
    debug(false, "Allowed actions: " + builder);
    return result;
  }

  // current grid
  private OnXState.Grid grid_;

  // current game plan
  private Node node_;

  // whether to show debug information
  private boolean showMinor_;
  private boolean showDebug_;

  // who I am
  private Token me_;

  /**
   * A tree structure used to plan for the game
   */
  private class Node{

    public Node(){
      // start with all moves remaining
      for(int x = 0; x<3; x++){
        for(int y = 0; y<3; y++){
          remaining_.add(0, new OnXAction(x, y));
        }
      }
      winner_ = Token.BLANK;
      // turn flips each time, so if first turn is cross then "zeroth" turn is nought
      turn_ = Token.NOUGHT;
      grid_ = new OnXState.Grid();
    }

    // add a tree to the children representing what happens on choosing an action
    public void addAction(OnXAction action){
      children.add(new Node(action, remaining_, grid_, turn_.opposite()));
    }

    // gets the winner of this node by induction on its children
    // calling this will fill out most of the tree, but does not fill out alternate options when a win condition is found
    public Token getWinner(){
      if(winner_ != null) {
        // winner already known
        return winner_;
      } else {
        // winner is not yet known
        // if no children have been created, so initialise them
        populate();
        if (turn_ != me_) {
          // Next turn is decided by us
          Token winner = me_.opposite();
          for (Node child : children) {
            Token temp = child.getWinner();
            if (temp == me_) {
              // we can force a win condition
              return me_;
            } else if (temp == Token.BLANK) {
              // there exists a non-lose condition
              winner = Token.BLANK;
            }
            // else leave winner as is
          }
          winner_ = winner;
          return winner;
        } else {
          // Next turn is decided by them
          Token winner = me_;
          for (Node child : children) {
            Token temp = child.getWinner();
            if (temp == me_.opposite()) {
              // we can force a win condition
              return me_.opposite();
            } else if (temp == Token.BLANK) {
              // there exists a non-lose condition
              winner = Token.BLANK;
            }
            // else leave winner as is
          }
          winner_ = winner;
          return winner;
        }
      }
    }

    // finds an action on the current node, returning -1 if not found
    public int findAction(OnXAction action){
      for(int i = 0; i < children.size(); i++){
        if(children.get(i).action_.equals(action)){
          return i;
        }
      }
      return -1;
    }

    public Node getNode(int i){
      return children.get(i);
    }

    // add all possible children
    public void populate(){
      if(children.size() == 0) {
        for (OnXAction remains : remaining_) {
          addAction(remains);
        }
      }
    }

    // construct a node, doing all needed processing
    private Node(OnXAction action, ArrayList<OnXAction> remaining, OnXState.Grid grid, Token token){
      // get the reduced remaining actions
      remaining_ = (ArrayList<OnXAction>) remaining.clone();
      remaining_.remove(action);
      action_ = action;
      // clone grid
      grid_ = new OnXState.Grid();
      for(int x = 0; x<3; x++){
        for(int y = 0; y<3; y++){
          grid_.setTokenAt(x, y, grid.getTokenAt(x, y));
        }
      }
      // add action to grid
      turn_ = token;
      int x = action.getX();
      int y = action.getY();
      grid_.setTokenAt(x, y, token);

      // test for lines
      boolean line = true;
      // try horizontal lines
      for(int nx = 0; nx < 3 && line; nx++){
        line = (token == grid_.getTokenAt(nx, y));
      }
      if(!line){
        // try vertical lines
        line = true;
        for(int ny = 0; ny < 3 && line; ny++){
          line = (token == grid_.getTokenAt(x, ny));
        }
      }
      if(!line && (x == y) ){
        // try diagonal lines
        line = true;
        for(int n = 0; n < 3 && line; n++){
          line = (token == grid_.getTokenAt(n, n));
        }
      }
      if(!line && (x == 2-y) ){
        // try other diagonal lines
        line = true;
        for(int n = 0; n < 3 && line; n++){
          line = (token == grid_.getTokenAt(2-n, n));
        }
      }
      // a line was found, this state represents a win for one player
      if(line){
        winner_ = token;
      }
      // no more spaces are remaining, this state represents a draw
      else if(remaining_.size()==0){
        winner_ = Token.BLANK;
      }
    }

    // list of all child nodes
    private ArrayList<Node> children = new ArrayList<Node>();

    // the grid layout for this node
    private OnXState.Grid grid_;

    // the action that leads to this node
    private OnXAction action_;

    // the actions not yet taken
    private ArrayList<OnXAction> remaining_ = new ArrayList<OnXAction>();

    // the winner
    // lazily evaluated - win if we can force a win from here, draw if we can only force a draw from here, lose otherwise
    private Token winner_;

    // the player who played the action this turn
    private Token turn_;
  }
}

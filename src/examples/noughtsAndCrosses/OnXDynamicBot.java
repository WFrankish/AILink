package examples.noughtsAndCrosses;

import socketInterface.SocketAgentInterface;
import templates.*;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OnXDynamicBot implements Agent{
  public static void main(String[] args) {
    boolean showMinor = ParseTools.find(args, "-d") > -1;
    OnXDynamicBot instance = new OnXDynamicBot(showMinor);
    try {
      String url = "localhost";
      System.out.println("Enter port number of host:");
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      int port = Integer.parseInt(stdIn.readLine());
      AgentInterface connection = new SocketAgentInterface(url,
          port,
          instance,
          new OnXStateMaster(),
          new OnXActionMaster());
      connection.run();
    } catch (Exception e) {
      instance.error(e);
    }
  }

  public OnXDynamicBot(boolean showMinor){
    showMinor_ = showMinor;
  }

  @Override
  public boolean checkGame(String ident) {
    return ident.equals("Noughts and Crosses");
  }

  @Override
  public Action decide(Action[] actions, State state) {
    OnXState.Grid grid = (OnXState.Grid) state;
    System.out.println(grid.toReadable());
    // print actions nicely
    StringBuilder builder = new StringBuilder();
    int i;
    for( i = 0; i<actions.length-1; i++){
      builder.append(actions[i].toString());
      builder.append(", ");
    }
    if(i<actions.length){
      builder.append(actions[i].toString());
    }
    System.out.println(builder.toString());
    // decide
    if(node_==null){
      // We are nought, find where crosses first action was and construct the tree.
      node_ = new Node();
      OnXAction crossAction = new OnXAction(0,0);
      boolean done = false;
      for(int x = 0; x < 3 && !done; x++){
        for(int y = 0; y < 3 && !done; y++){
          if(grid.getTokenAt(x, y).equals(Token.CROSS)){
            crossAction = new OnXAction(x, y);
            done = true;
          }
        }
      }
      node_.addAction(crossAction, Token.CROSS);
      node_ = node_.children.get(0);
      node_.populate();
    }
    else{
      // Find the last action (there may not be one)
      for(OnXAction remains : node_.remaining_){
        boolean stillThere = false;
        OnXAction missing = remains;
        for(Action action: actions){
          OnXAction action2 = (OnXAction) action;
          stillThere = action2.equals(remains);
          if(stillThere){
            // this isn't the last action, break out of for loop
            break;
          }
          missing = remains;
        }
        if(!stillThere){
          // found it, move to its branch and break out of for loop
          int index = node_.getAction(missing);
          if(index==-1){
            node_.addAction(missing, opposite_);
            index = node_.getAction(missing);
          }
          node_ = node_.getNode(index);
          node_.populate();
          break;
        }
      }
    }
    OnXAction bestSoFar = (OnXAction) actions[0];
    for (Action action : actions){
      OnXAction action2 = (OnXAction) action;
      int next = node_.getAction(action2);
      Token winner = node_.getNode(next).getWinner();
      if(winner == me_){
        node_ = node_.getNode(next);
        return action;
      }
      else if(winner.equals(Token.BLANK)){
        bestSoFar = action2;
      }
    }
    node_ = node_.getNode(node_.getAction(bestSoFar));
    return bestSoFar;
  }


  @Override
  public void updateState(State update) {
    if(update instanceof OnXState.Player){
      OnXState.Player state = (OnXState.Player) update;
      me_ = state.getMe();
      if(me_ == Token.CROSS){
        opposite_ = Token.NOUGHT;
        node_ = new Node();
        node_.populate();
      } else {
        opposite_ = Token.CROSS;
      }
    }
    else if(update instanceof OnXState.Grid){
      OnXState.Grid grid = (OnXState.Grid) update;
      System.out.println("Final result: ");
      System.out.println(grid.toReadable());
    }
    else {
      OnXState.Winner state = (OnXState.Winner) update;
      System.out.println(state.toReadable());
      Token winner = state.getWinner();
      if (winner.equals(me_)) {
        System.out.println("I won!");
      } else if ( winner != Token.BLANK ) {
        System.out.println("Opponent won.");
      } else {
        System.out.println("No winner");
      }
    }
  }

  @Override
  public String identity() {
    return "Noughts and Crosses Dynamic Programming Bot";
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

  private Node node_;
  private boolean showMinor_;
  private Token me_;
  private Token opposite_;

  private class Node{

    public Node(){
      for(int x = 0; x<3; x++){
        for(int y = 0; y<3; y++){
          remaining_.add(0, new OnXAction(x, y));
        }
      }
      winner_ = Token.BLANK;
      turn_ = Token.NOUGHT;
      grid_ = new OnXState.Grid();
    }

    public void addAction(OnXAction action, Token turn){
      children.add(new Node(action, remaining_, grid_, turn));
    }

    public Token getWinner(){
      if(winner_ == null) {
        if (children.size() == 0) {
          populate();
        }
        if(turn_ != me_){
          // Next turn is decided by us
          Token winner = opposite_;
          for(Node child : children){
            Token temp = child.getWinner();
            if(temp == me_){
              // we can force a win condition
              return me_;
            }
            else if(temp == Token.BLANK){
              // there exists a non-lose condition
              winner = Token.BLANK;
            }
          }
          winner_ = winner;
          return winner;
        }
        else {
          // Next turn is decided by them
          Token winner = me_;
          for(Node child : children){
            Token temp = child.getWinner();
            if(temp == opposite_){
              // we can force a win condition
              return opposite_;
            }
            else if(temp == Token.BLANK){
              // there exists a non-lose condition
              winner = Token.BLANK;
            }
          }
          winner_ = winner;
          return winner;
        }
      }
      else{
        return winner_;
      }
    }

    public int getAction(OnXAction action){
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

    public void populate(){
      for (OnXAction remains : remaining_) {
        switch (turn_){
          case NOUGHT:{
            addAction(remains, Token.CROSS);
            break;
          }
          case CROSS:{
            addAction(remains, Token.NOUGHT);
            break;
          }
          case BLANK:{
            // do nothing
          }
        }
      }
    }

    private Node(OnXAction action, ArrayList<OnXAction> remaining, OnXState.Grid grid, Token token){
      remaining_ = (ArrayList<OnXAction>) remaining.clone();
      remaining_.remove(action);
      action_ = action;
      grid_ = new OnXState.Grid();
      for(int x = 0; x<3; x++){
        for(int y = 0; y<3; y++){
          grid_.setTokenAt(x, y, grid.getTokenAt(x, y));
        }
      }
      turn_ = token;
      int x = action.getX();
      int y = action.getY();
      grid_.setTokenAt(x, y, token);
      boolean line = true;
      for(int nx = 0; nx < 3 && line; nx++){
        line = (token == grid_.getTokenAt(nx, y));
      }
      if(!line){
        // no horizontal lines, try vertical
        line = true;
        for(int ny = 0; ny < 3 && line; ny++){
          line = (token == grid_.getTokenAt(x, ny));
        }
      }
      if(!line && (x == y) ){
        // try diagonal
        line = true;
        for(int n = 0; n < 3 && line; n++){
          line = (token == grid_.getTokenAt(n, n));
        }
      }
      if(!line && (x == 2-y) ){
        // try other diagonal
        line = true;
        for(int n = 0; n < 3 && line; n++){
          line = (token == grid_.getTokenAt(2-n, n));
        }
      }
      if(line){
        winner_ = token;
      }
      else if(remaining_.size()==0){
        winner_ = Token.BLANK;
      }
    }

    private ArrayList<Node> children = new ArrayList<Node>();
    private OnXState.Grid grid_;
    private OnXAction action_;
    private ArrayList<OnXAction> remaining_ = new ArrayList<OnXAction>();
    private Token winner_;
    private Token turn_;
  }
}

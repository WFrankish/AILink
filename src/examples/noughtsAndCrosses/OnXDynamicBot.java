package examples.noughtsAndCrosses;

import socketInterface.SocketAgentInterface;
import templates.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OnXDynamicBot implements Agent{
  public static void main(String[] args) {
    OnXDynamicBot instance = new OnXDynamicBot();
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

  @Override
  public Action decide(Action[] actions, State state) {
    OnXState state2 = (OnXState) state;
    System.out.println(state2.gridToNiceString());
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
          if(((OnXState) state).getTokenAt(x, y).equals(Token.cross())){
            crossAction = new OnXAction(x, y);
            done = true;
          }
        }
      }
      node_.addAction(crossAction, Token.cross());
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
            node_.addAction(missing, me_.opposite());
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
      if(winner.equals(me_)){
        node_ = node_.getNode(next);
        return action;
      }
      else if(winner.equals(Token.blank())){
        bestSoFar = action2;
      }
    }
    node_ = node_.getNode(node_.getAction(bestSoFar));
    return bestSoFar;
  }

  @Override
  public void initialState(State debrief) {
    OnXState state = (OnXState) debrief;
    me_ = state.getMe();
    System.out.println("I am "+me_);
    if(me_.isCross()){
      node_ = new Node();
      node_.populate();
    }
  }

  @Override
  public void updateState(State update) {
    OnXState state = (OnXState) update;
    System.out.println(state.gridToNiceString());
    Token winner = state.getWinner();
    if(winner.equals(me_)){
      System.out.println("I won!");
    }
    else if(winner.isNought() || winner.isCross()){
      System.out.println("Opponent won.");
    }
    else{
      System.out.println("No winner");
    }
  }

  @Override
  public String identity() {
    return "Noughts and Crosses Dynamic Programming Bot";
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
    System.out.println("Game over.");
  }

  private Node node_;

  private boolean debug_ = false;
  private Token me_;

  private class Node{

    public Node(){
      for(int x = 0; x<3; x++){
        for(int y = 0; y<3; y++){
          remaining_.add(0, new OnXAction(x, y));
        }
      }
      winner_ = Token.blank();
      turn_ = Token.nought();
      grid_ = new Token[3][3];
      for(int x = 0; x < 3; x++){
        for(int y = 0; y < 3; y++){
          grid_[y][x] = Token.blank();
        }
      }
    }

    public void addAction(OnXAction action, Token turn){
      children.add(new Node(action, remaining_, grid_, turn));
    }

    public Token getWinner(){
      if(winner_ == null) {
        if (children.size() == 0) {
          populate();
        }
        if(!turn_.equals(me_)){
          // Next turn is decided by us
          Token winner = me_.opposite();
          for(Node child : children){
            Token temp = child.getWinner();
            if(temp.equals(me_)){
              // we can force a win condition
              return me_;
            }
            else if(temp.equals(Token.blank())){
              // there exists a non-lose condition
              winner = Token.blank();
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
            if(temp.equals(me_.opposite())){
              // we can force a win condition
              return me_.opposite();
            }
            else if(temp.equals(Token.blank())){
              // there exists a non-lose condition
              winner = Token.blank();
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
        addAction(remains, turn_.opposite());
      }
    }

    private Node(OnXAction action, ArrayList<OnXAction> remaining, Token[][] grid, Token token){
      remaining_ = (ArrayList<OnXAction>) remaining.clone();
      remaining_.remove(action);
      action_ = action;
      for(int x = 0; x<3; x++){
        for(int y = 0; y<3; y++){
          grid_[y][x] = grid[y][x].clone();
        }
      }
      turn_ = token;
      int x = action.getX();
      int y = action.getY();
      grid_[y][x] = token;
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
        winner_ = token;
      }
      else if(remaining_.size()==0){
        winner_ = Token.blank();
      }
    }

    private ArrayList<Node> children = new ArrayList<Node>();
    private Token[][] grid_ = new Token[3][3];
    private OnXAction action_;
    private ArrayList<OnXAction> remaining_ = new ArrayList<OnXAction>();
    private Token winner_;
    private Token turn_;
  }
}

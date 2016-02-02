package examples.noughtsAndCrosses;

import socketInterface.SocketAgentInterface;
import templates.Action;
import templates.Agent;
import templates.AgentInterface;
import templates.State;

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
    if(node_==null){
      System.out.println("populatin'");
      node_ = new Node();
      OnXAction lastAction = new OnXAction(0,0);
      boolean done = false;
      for(int x = 0; x < 3 && !done; x++){
        for(int y = 0; y < 3 && !done; y++){
          if(((OnXState) state).getTokenAt(x, y).equals(Token.blank())){
            lastAction = new OnXAction(x, y);
            done = true;
          }
        }
      }
      node_.addAction(lastAction, Token.nought());
      node_.populate();
    }
    Action bestSoFar = actions[0];
    for (Action action : actions){
      System.out.println("decidin'");
      OnXAction action2 = (OnXAction) action;
      int next = node_.getAction(action2);
      Token winner = node_.getNode(next).getWinner();
      if(winner.equals(me_)){
        node_ = node_.getNode(next);
        return action;
      }
      else if(winner.equals(Token.blank())){
        bestSoFar = action;
      }
    }
    node_ = node_.getNode(node_.getAction((OnXAction) bestSoFar));
    return bestSoFar;
  }

  @Override
  public void initialState(State debrief) {
    OnXState state = (OnXState) debrief;
    me_ = state.getMe();
    if(me_.isCross()){
      node_ = new Node();
      node_.addAction(new OnXAction(0, 0), me_);
      node_.addAction(new OnXAction(0, 1), me_);
      node_.addAction(new OnXAction(0, 2), me_);
      node_.addAction(new OnXAction(1, 0), me_);
      node_.addAction(new OnXAction(1, 1), me_);
      node_.addAction(new OnXAction(1, 2), me_);
      node_.addAction(new OnXAction(2, 0), me_);
      node_.addAction(new OnXAction(2, 1), me_);
      node_.addAction(new OnXAction(2, 2), me_);
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
      winner_ = Token.blank();
      for(int x = 0; x < 3; x++){
        for(int y = 0; y < 3; y++){
          grid_[y][x] = Token.blank();
        }
      }
    }

    public void addAction(OnXAction action, Token turn){
      children.add(new Node(action, grid_, turn));
    }

    public Token getWinner(){
      if(winner_ == null) {
        if (children.size() == 0) {
          return Token.blank();
        } else {
          Token winner = children.get(0).getWinner();
          if(winner.equals(Token.blank())){
            return winner;
          }
          else {
            for (int i = 1; i < children.size(); i++) {
              Token temp = children.get(i).getWinner();
              if(!temp.equals(winner)){
                return Token.blank();
              }
            }
            return winner;
          }
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
      if(winner_== null || turn_ == null){
        for(Node n : children){
          n.populate();
        }
      }
      else if(getWinner().equals(Token.blank())){
        Token next;
        if(turn_.isCross()){
          next = Token.nought();
        }
        else{
          next = Token.cross();
        }
        for(int x = 0; x < 3; x++){
          for(int y = 0; y < 3; y++){
            OnXAction newAction = new OnXAction(x, y);
            if(getAction(newAction)== -1 ){
              addAction(newAction, next);
            }
          }
        }
        for(Node n : children){
          n.populate();
        }
      }
    }

    private Node(OnXAction action, Token[][] grid, Token token){
      action_ = action;
      grid_ = grid;
      turn_ = token;
      int x = action.getX();
      int y = action.getY();
      grid[y][x] = token;
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
    }

    private ArrayList<Node> children = new ArrayList<Node>();
    private Token[][] grid_;
    private OnXAction action_;
    private Token winner_;
    private Token turn_;
  }
}

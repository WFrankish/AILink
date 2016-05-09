package examples.templates;

import common.Tuple;
import interfaces.Action;
import interfaces.Game;
import interfaces.GameInterface;
import interfaces.State;

public class ExampleGame implements Game {

  // Instantiate class, run class
  public static void main(String[] args){
    new ExampleGame(args).run();
  }

  // Parse Args, instantiate private variables.
  public ExampleGame(String[] args){
    // Parse Args
  }

  @Override
  public String identity() {
    return "Example Game";
  }

  @Override
  public void message(Object obj) {
    System.out.println("Message for Game Master: " + obj);
  }

  @Override
  public void debug(boolean isMajor, Object obj) {
    if(showDebug_ && (showMinor_ || isMajor)){
      System.out.println("Debug: " + obj);
    }
  }

  @Override
  public void error(Object obj) {
    System.out.println("Error: " + obj);
  }

  // Run the simulation, called only once.
  private void run(){
    int noPlayers = 0;
    debug(true, "Seeking " + maxPlayers_ + " players.");
    while(noPlayers < maxPlayers_){
      Tuple<Integer, String> agentInfo = interface_.findAgent();
      int agentID = agentInfo.fst;
      String agent = agentInfo.snd;
      if(acceptAgent(agent)){
        agentIDs_[noPlayers] = agentID;
        agentNames_[noPlayers] = agent;
        noPlayers++;
        debug(true, "Added agent " + agent);
        debug(false, "Now the number of agents is: " + noPlayers);
      } else {
        debug(true, "Rejecting agent " + agent);
        interface_.terminateAgent(agentID, "Rejected.");
      }
    }
    debug(true, "Sending initial state.");
    for(int i = 0; i < maxPlayers_; i++){
      State initial = createInitialState(i);
      interface_.sendState(agentIDs_[i], initial);
    }
    debug(true, "Beginning game.");
    boolean gameOver = false;
    while(!gameOver){
      int player = getNextPlayer();
      debug(true, "Next is player " + player);
      State state = getCurrentState(player);
      Action[] actions = getAvailableActions(player);
      Action chosen = interface_.requestAction(agentIDs_[player], state, actions);
      if(chosen != null && validAction(player, chosen)){
        progressState(player, chosen);
      }
      else{
        debug(true, "Action not valid");
        removePlayer(player);
        interface_.terminateAgent(agentIDs_[player], "Illegal Action.");
      }
      gameOver = gameEnded();
    }
    debug(true, "Game has ended, reporting winners.");
    // Send winner to all players
    int winner = getWinner();
    for(int i = 0; i < maxPlayers_; i++){
      reportWinnerTo(i, winner);
    }
    interface_.end();
  }

  // decide whether to accept an agent
  private boolean acceptAgent(String agent){
    return false;
  }

  // create the initial state for an agent
  private State createInitialState(int playerNo){
    return null;
  }

  // find which agent is next in turn
  private int getNextPlayer(){
    return 0;
  }

  // get the current visible state for an agent
  private State getCurrentState(int playerNo){
    return null;
  }

  // get the current available actions for an agent
  private Action[] getAvailableActions(int playerNo){
    return null;
  }

  // decide whether an action is valid
  private boolean validAction(int playerNo, Action action){
    return false;
  }

  // update the internal state based on the last action
  private void progressState(int playerNo, Action action){

  }

  // remove a player from the simulation
  private void removePlayer(int playerNo){

  }

  // decide whether the game has ended
  private boolean gameEnded(){
    return false;
  }

  // find the winner of the game
  private int getWinner(){
    return 0;
  }

  // report the winner to a player agent
  private void reportWinnerTo(int playerNo, int winnerNo){

  }

  // whether to show any debug messages
  private boolean showDebug_;

  // whether to show minor debug messages
  boolean showMinor_;

  // the interface to the agents
  GameInterface interface_;

  // the maximum players for the game
  int maxPlayers_;

  // a mapping of the player number to the ID number used by the interface
  int[] agentIDs_;

  // a mapping of the player number to the agent's name
  String[] agentNames_;

}

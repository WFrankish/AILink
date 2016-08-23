package examples.templates;

import common.Tuple;
import interfaces.Action;
import interfaces.Game;
import interfaces.GameInterface;
import interfaces.State;
import tools.ParseTools;

public class ExampleGame implements Game {

  // Instantiate class, run class
  public static void main(String[] args){
    new ExampleGame(args).run();
  }

  // Parse Args, instantiate private variables.
  public ExampleGame(String[] args){
    showMinor_ = (ParseTools.find(args, "-dm") > -1);
    showDebug_ = showMinor_ || (ParseTools.find(args, "-d") > -1);
    // Parse Any Other Args
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
    for(int p = 0; p < maxPlayers_; p++){
      State initial = createInitialState(p);
      interface_.sendState(agentIDs_[p], initial);
    }
    debug(true, "Beginning game.");
    boolean gameOver = false;
    while(!gameOver){
      int p = getNextPlayer();
      debug(true, "Next is player " + p);
      Action chosen = interface_.requestAction(agentIDs_[p]);
      if(chosen != null && validAction(p, chosen)){
        progressState(p, chosen);
      }
      else{
        debug(true, "Action not valid");
        removePlayer(p);
        interface_.terminateAgent(agentIDs_[p], "Illegal Action.");
      }
      for(int i = 0; i < maxPlayers_; i++){
        if(isAlive(i)){
          State state = getCurrentState(i);
          interface_.sendState(agentIDs_[i], state);
        }
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
  private boolean acceptAgent(String name){
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

  // reports if a player is still alive
  private boolean isAlive(int playerNo){
    return false;
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

package examples.twentyOne;

import socketInterface.SocketGameInterface;
import templates.Game;
import templates.GameInterface;
import templates.State;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class twentyOneGame implements Game {

  public static void main(String[] args){
    boolean showMinor = (ParseTools.find(args, "-m") > -1);
    int maxPlayers = ParseTools.findVal(args, "-p", 1);
    int decks = ParseTools.findVal(args, "-d", 2);
    int rounds = ParseTools.findVal(args, "-r", 5);
    new twentyOneGame(maxPlayers, showMinor, decks, rounds).run();
  }

  public twentyOneGame(int maxPlayers, boolean showMinor, int decks, int rounds){
    gameInterface_ = new SocketGameInterface(this, new tw1ActionMaster());
    showMinor_ = showMinor;
    maxPlayers_ = maxPlayers;
    decks_ = decks;
    rounds_ = rounds;
    debug(false, "Showing all debug messages");
    debug(true, "Expected players: " + maxPlayers_);
  }

  public void run(){
    while(players_ < maxPlayers_){
      gameInterface_.requestAgent();
    }
    addDecks(decks_, false);
    shuffle(false);
    for(int i = 0; i<rounds_; i++){
      round();
    }
  }

  public void round(){

  }

  public void addDecks(int no, boolean announce){

  }

  public void shuffle(boolean announce){

  }

  @Override
  public State registerAgent(String agent, int agentNo) {
    message(agent + "wants to play.");
    while(true){
      message("Enter y to accept, n to reject.");
      try {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String temp = stdIn.readLine();
        if (temp.length() > 0) {
          char c = temp.charAt(0);
          if (c == 'y' || c == 'Y') {
            players_++;
            return new tw1State.Decks(decks_);
          } else if (c == 'n' || c == 'N') {
            return null;
          }
        }
      } catch (Exception e) {
        error(e);
        return null;
      }
    }
  }

  @Override
  public String identity() {
    return "TwentyOne";
  }

  @Override
  public void message(Object obj) {
    System.out.println("Message to Player: " + obj);
  }

  @Override
  public void debug(boolean isMajor, Object obj) {
    if(isMajor || showMinor_){
      System.out.println("Debug: " + obj);
    }
  }

  @Override
  public void error(Object obj) {
    System.out.println("Error: " + obj);
  }

  @Override
  public void end() {

  }

  private GameInterface gameInterface_;
  private int players_;
  private int maxPlayers_;
  private int decks_;
  private int rounds_;
  private boolean showMinor_;
}

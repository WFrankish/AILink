package examples.twentyOne;

import socketInterface.SocketGameInterface;
import templates.Game;
import templates.GameInterface;
import templates.State;
import tools.ParseTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class twentyOneGame implements Game {

  public static void main(String[] args){
    boolean showMinor = (ParseTools.find(args, "-m") > -1);
    int maxPlayers = ParseTools.findVal(args, "-p", 1);
    int decks = ParseTools.findVal(args, "-d", 2);
    int rounds = ParseTools.findVal(args, "-r", 5);
    new twentyOneGame(maxPlayers, showMinor, decks, rounds).run();
  }

  public twentyOneGame(int maxPlayers, boolean showMinor, int noDecks, int noRounds){
    gameInterface_ = new SocketGameInterface(this, new tw1ActionMaster());
    showMinor_ = showMinor;
    maxPlayers_ = maxPlayers;
    noDecks_ = noDecks;
    noRounds_ = noRounds;
    debug(false, "Showing all debug messages");
    debug(true, "Expected players: " + maxPlayers_);
  }

  public void run(){
    while(players_ < maxPlayers_){
      gameInterface_.requestAgent();
    }
    deck_ = new Deck(noDecks_);
    for(int i = 0; i< noRounds_; i++){
      round();
    }
  }

  private void round(){
    ArrayList<Card>[] hands = new ArrayList[maxPlayers_+1];

    deck_.discardAll();
  }

  private Card drawCard(int agentNo){
    Card card = deck_.draw();
    if(agentNo == maxPlayers_){
      tw1State.Deal deal = new tw1State.Deal(false, true, card);
      for(int i = 0; i<maxPlayers_; i++){
        gameInterface_.updateState(i, deal);
      }
    } else{

    }
    return card;
  }

  private int softTotal(ArrayList<Card> hand){
    int result = 0;
    for(Card c : hand){
      switch (c.getRank().charAt(0)){
        case 'A':
          result += 1; break;
        case '2':
          result += 2; break;
        case '3':
          result += 2; break;
        case '4':
          result += 2; break;
        case '5':
          result += 2; break;
        case '6':
          result += 2; break;
        case '7':
          result += 2; break;
        case '8':
          result += 2; break;
        case '9':
          result += 2; break;
        default:
          result += 10;
      }
    }
    return result;
  }

  private void addDecks(int no){
    deck_.addDecks(no);
    tw1State.Decks decks = new tw1State.Decks(no);
    for(int i = 0; i<maxPlayers_; i++){
      gameInterface_.updateState(i, decks);
    }
  }

  private void shuffle(){
    deck_.shuffle();
    tw1State.Shuffle shuffle = new tw1State.Shuffle();
    for(int i = 0; i<maxPlayers_; i++){
      gameInterface_.updateState(i, shuffle);
    }
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
            return new tw1State.Decks(noDecks_);
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
  private int noDecks_;
  private int noRounds_;
  private boolean showMinor_;
  private Deck deck_;
}

package examples.twentyOne;

import templates.State;

public class tw1State {

  public static class Deal implements State {

    public Deal(boolean isYours, boolean isDealers, Card card){
      isYours_ = isYours;
      isDealers_ = isDealers;
      card_ = card;
    }

    @Override
    public String toString() {
      if(isDealers_){
        return "D"+ card_.getRank().charAt(0) + card_.getSuite().charAt(0);
      } else if(isYours_){
        return "Y"+ card_.getRank().charAt(0) + card_.getSuite().charAt(0);
      }
      else{
        return "N"+ card_.getRank().charAt(0) + card_.getSuite().charAt(0);
      }
    }

    @Override
    public String toReadable() {
      if(isDealers_){
        return "Dealer drew a"+ card_.getRank() + " of " + card_.getSuite();
      } else if(isYours_){
        return "You drew a"+ card_.getRank() + " of " + card_.getSuite();
      }
      else{
        return "Someone drew a"+ card_.getRank() + " of " + card_.getSuite();
      }
    }

    public boolean isYours(){
      return  isYours_;
    }

    public boolean isDealers(){
      return isDealers_;
    }

    public Card getCard(){
      return card_;
    }

    private Card card_;
    private boolean isYours_;
    private boolean isDealers_;

  }

  public static class Shuffle implements State{
    @Override
    public String toString() {
      return "S";
    }

    @Override
    public String toReadable() {
      return "The Deck was shuffled.";
    }
  }

  public static class Decks implements State{

    public Decks(int no){
      no_ = no;
    }

    @Override
    public String toString() {
      return "@"+no_;
    }

    @Override
    public String toReadable() {
      return no_ + "decks added to play";
    }

    private int no_;
  }

  public static class Ack implements State{

    @Override
    public String toString() {
      return "";
    }

    @Override
    public String toReadable() {
      return "";
    }
  }

}

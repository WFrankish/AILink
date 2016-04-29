package examples.twentyOne;

public class Card {

  public static Card newHearts(int rank){
    return new Card(0, rank%13);
  }

  public static Card newDiamonds(int rank){
    return new Card(1, rank%13);
  }

  public static Card newClubs(int rank){
    return new Card(2, rank%13);
  }

  public static Card newSpades(int rank){
    return new Card(3, rank%13);
  }

  public String getSuite(){
    switch (suite_) {
      case 0:
        return "Hearts";
      case 1:
        return "Diamonds";
      case 2:
        return "Clubs";
      case 3:
        return "Spades";
      default:
        return "Invalid";
    }
  }

  public String getRank(){
    switch (rank_) {
      case 0:
        return "King";
      case 1:
        return "Ace";
      case 11:
        return "Jack";
      case 12:
        return "Queen";
      default:
        return rank_+"";
    }
  }

  private Card(int suite, int rank){
    suite_ = suite;
    rank_ = rank;
  }

  private int suite_;
  private int rank_;
}

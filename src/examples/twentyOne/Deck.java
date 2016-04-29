package examples.twentyOne;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
  public Deck(int noPacks){
    deck_ = new ArrayList<Card>();
    out_ = new ArrayList<Card>();
    discard_ = new ArrayList<Card>();
    addDecks(noPacks);
  }

  public void addDecks(int noPacks){
    for(int i = 0; i<13; i++){
      for(int j = 0; j<noPacks; j++){
        deck_.add(0, Card.newClubs(i));
        deck_.add(0, Card.newHearts(i));
        deck_.add(0, Card.newDiamonds(i));
        deck_.add(0, Card.newSpades(i));
      }
    }
    shuffle();
  }

  public Card draw(){
    Card card = deck_.remove(0);
    out_.add(0, card);
    return card;
  }

  public void discardAll(){
    while(out_.size()>0){
      Card card = out_.remove(0);
      discard_.add(0, card);
    }
  }

  public void shuffle(){
    while(discard_.size()>0){
      Card card = discard_.remove(0);
      deck_.add(0, card);
    }
    long seed = System.nanoTime();
    Collections.shuffle(deck_, new Random(seed));
  }

  public int remaining(){
    return deck_.size();
  }

  private ArrayList<Card> deck_;
  private ArrayList<Card> out_;
  private ArrayList<Card> discard_;
}

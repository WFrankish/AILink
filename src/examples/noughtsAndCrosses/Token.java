package examples.noughtsAndCrosses;

public enum  Token {
  NOUGHT, CROSS, BLANK;

  public Token opposite(){
    switch (this){
      case NOUGHT:{
        return CROSS;
      }
      case CROSS:{
        return NOUGHT;
      }
      default:{
        return BLANK;
      }
    }
  }
}

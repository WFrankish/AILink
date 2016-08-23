package examples.noughtsAndCrosses;

public enum  Token {
  NOUGHT, CROSS, BLANK;

  public String toString(){
    switch (this){
      case NOUGHT:{
        return "Nought";
      }
      case CROSS:{
        return "Cross";
      }
      default:{
        return "Blank";
      }
    }
  }

  public char encode(){
    switch (this){
      case NOUGHT:{
        return 'O';
      }
      case CROSS:{
        return 'X';
      }
      default:{
        return '?';
      }
    }
  }

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

  public static Token decode(char c){
    switch (c){
      case 'X':{
        return CROSS;
      }
      case 'O':{
        return NOUGHT;
      }
      default:{
        return BLANK;
      }
    }
  }
}

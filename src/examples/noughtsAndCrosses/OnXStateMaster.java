package examples.noughtsAndCrosses;

import interfaces.State;
import interfaces.StateMaster;

public class OnXStateMaster implements StateMaster{

  @Override
  public State parseString(String input){
    char header = input.charAt(0);
    switch (header){
      case 'P':{
        Token t = parseToken(input.charAt(1));
        return new OnXState.Player(t);
      }
      case 'W':{
        Token t = parseToken(input.charAt(1));
        return new OnXState.Winner(t);
      }
      case 'G':{
        OnXState.Grid grid = new OnXState.Grid();
        for(int x = 0; x < 3; x++){
          for(int y = 0; y < 3; y++){
            int pos = 1 + 3*y + x;
            Token token = parseToken(input.charAt(pos));
            grid.setTokenAt(x, y, token);
          }
        }
        return grid;
      }
      default:{
        return null;
      }
    }
  }

  private Token parseToken(char input){
    if(input == 'O'){
      return Token.NOUGHT;
    }
    else if (input == 'X'){
      return Token.CROSS;
    }
    else {
      return Token.BLANK;
    }
  }

}

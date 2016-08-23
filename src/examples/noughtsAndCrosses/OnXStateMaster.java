package examples.noughtsAndCrosses;

import interfaces.State;
import interfaces.StateMaster;

public class OnXStateMaster implements StateMaster{

  @Override
  public State decode(String input){
    char header = input.charAt(0);
    switch (header){
      case 'P':{
        // Player state, remaining encoding is a single token
        Token t = parseToken(input.charAt(1));
        return new OnXState.Player(t);
      }
      case 'W':{
        // Winner state, remaining encoding is a single token
        Token t = parseToken(input.charAt(1));
        return new OnXState.Winner(t);
      }
      case 'G':{
        // Grid state, remaining encoding is the 9 tokens, row by row.
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

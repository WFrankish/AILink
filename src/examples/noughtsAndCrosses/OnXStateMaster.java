package examples.noughtsAndCrosses;

import templates.State;
import templates.StateMaster;

public class OnXStateMaster implements StateMaster{

  @Override
  public State parseString(String input){
    Token me = parseToken(input.charAt(0));
    OnXState res = new OnXState(me);
    Token winner = parseToken(input.charAt(1));
    res.setWinner(winner);
    for(int x = 0; x < 3; x++){
      for(int y = 0; y < 3; y++){
        int pos = 2 + 3*y + x;
        Token token = parseToken(input.charAt(pos));
        res.setTokenAt(x, y, token);
      }
    }
    return res;
  }

  private Token parseToken(char input){
    if(input == 'O'){
      return Token.nought();
    }
    else if (input == 'X'){
      return Token.cross();
    }
    else {
      return Token.blank();
    }
  }

}

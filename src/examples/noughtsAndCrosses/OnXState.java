package examples.noughtsAndCrosses;

import templates.State;

public class OnXState implements State {

  public OnXState(Token me) {
    me_ = me;
    winner_ = Token.blank();
    for(int x = 0; x < 3; x++){
      for(int y = 0; y < 3; y++){
        grid_[y][x] = Token.blank();
      }
    }
  }

  public Token getMe(){
    return me_;
  }

  public Token getWinner(){
    return winner_;
  }

  public void setWinner(Token winner){
    winner_ = winner;
  }

  public Token getTokenAt(int x, int y){
    if(x < 0 || x  >= 3){
      throw new IllegalArgumentException("x must be between 0 and 2");
    }
    if(y < 0 || y  >= 3){
      throw new IllegalArgumentException("y must be between 0 and 2");
    }
    return grid_[y][x];
  }

  public void setTokenAt(int x, int y, Token token){
    if(x < 0 || x  >= 3){
      throw new IllegalArgumentException("x must be between 0 and 2");
    }
    if(y < 0 || y  >= 3){
      throw new IllegalArgumentException("y must be between 0 and 2");
    }
    grid_[y][x] = token;
  }

  private Token me_;

  private Token winner_;

  private Token[][] grid_ = new Token[3][3];

  public String toString(){
    StringBuilder builder = new StringBuilder();
    if(me_.isNought()){
      builder.append("O");
    }
    else if(me_.isCross()){
      builder.append("X");
    }
    else{
      builder.append("?");
    }
    if(winner_.isNought()){
      builder.append("O");
    }
    else if(winner_.isCross()){
      builder.append("X");
    }
    else{
      builder.append("?");
    }
    for(Token[] line : grid_){
      for(Token token : line){
        if(token.isNought()){
          builder.append("O");
        }
        else if(token.isCross()){
          builder.append("X");
        }
        else{
          builder.append("?");
        }
      }
    }
    return builder.toString();
  }

  public String gridToNiceString(){
    StringBuilder builder = new StringBuilder("You are: ");
    if(me_.isNought()){
      builder.append("O\n");
    }
    else if(me_.isCross()){
      builder.append("X\n");
    }
    else{
      builder.append("?\n");
    }
    builder.append("---------\n");
    for(Token[] line : grid_){
      builder.append("| ");
      for(Token token : line){
        if(token.isNought()){
          builder.append("O");
        }
        else if(token.isCross()){
          builder.append("X");
        }
        else{
          builder.append("?");
        }
        builder.append(" ");
      }
      builder.append("|\n");
    }
    builder.append("---------");
    return builder.toString();
  }

}

package examples.noughtsAndCrosses;

import interfaces.State;

public class OnXState{

  public static class Grid implements State{

    public Grid(){
      grid_ = new Token[3][3];
      for(int x = 0; x<3; x++){
        for(int y = 0; y<3; y++){
          grid_[x][y] = Token.BLANK;
        }
      }
    }

    public Token getTokenAt(int x, int y){
      return grid_[x][y];
    }

    public void setTokenAt(int x, int y, Token token){
      grid_[x][y] = token;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder("G");
      for(int y = 0; y<3; y++){
        for(int x = 0; x<3; x++){
          switch (grid_[x][y]){
            case CROSS:{
              result.append('X');
              break;
            }
            case NOUGHT:{
              result.append('O');
              break;
            }
            default:{
              result.append('?');
            }
          }
        }
      }
      return result.toString();
    }

    @Override
    public String toReadable() {
      StringBuilder result = new StringBuilder();
      for(int y = 0; y<3; y++){
        result.append("[ ");
        for(int x = 0; x<3; x++){
          switch (grid_[x][y]){
            case CROSS:{
              result.append("X ");
              break;
            }
            case NOUGHT:{
              result.append("O ");
              break;
            }
            default:{
              result.append("? ");
            }
          }
        }
        result.append("]\n");
      }
      return result.toString();
    }

    private Token[][] grid_;

  }

  public static class Player implements State{

    public Player(Token them){
      me_ = them;
    }

    public Token getMe(){
      return me_;
    }

    @Override
    public String toString() {
      switch (me_){
        case NOUGHT:{
          return "PO";
        }
        case CROSS:{
          return "PX";
        }
        default:{
          return "P?";
        }
      }
    }

    @Override
    public String toReadable() {
      switch (me_){
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

    private Token me_;

  }

  public static class Winner implements State{

    public Winner(Token them){
      winner_ = them;
    }

    public Token getWinner(){
      return winner_;
    }

    @Override
    public String toString() {
      switch (winner_){
        case NOUGHT:{
          return "WO";
        }
        case CROSS:{
          return "WX";
        }
        default:{
          return "W?";
        }
      }
    }

    @Override
    public String toReadable() {
      switch (winner_){
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

    private Token winner_;

  }


}

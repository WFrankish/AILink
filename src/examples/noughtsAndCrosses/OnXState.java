package examples.noughtsAndCrosses;

import interfaces.State;

public class OnXState{

  /**
   * A 3*3 grid of tokens.
   */
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
    public String encode() {
      // G is the header OnX StateMaster expects for Grid State
      StringBuilder result = new StringBuilder("G");
      // show each token in each row sequentially
      for(int y = 0; y<3; y++){
        for(int x = 0; x<3; x++){
          result.append(grid_[x][y].encode());
        }
      }
      return result.toString();
    }

    /**
     * [ X ? X ]
     * [ ? O ? ]
     * [ X ? O ]
     * @return grid formatted as above;
     */
    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
      for(int y = 0; y<3; y++){
        result.append("[ ");
        for(int x = 0; x<3; x++){
          result.append(grid_[x][y].encode());
          result.append(' ');
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
    public String encode() {
      // P is the header OnXStateMaster expects for Player state.
      // Tokens, in this case only one, are encoded as X, O or ?.
      return "P"+me_.encode();
    }

    @Override
    public String toString() {
      return me_.toString();
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
    public String encode() {
      // W is the header OnX State expects for Winner State
      // Otherwise this encodes identically to Player State
      return "W"+winner_.encode();
    }

    @Override
    public String toString() {
      return winner_.toString();
    }

    private Token winner_;

  }

}

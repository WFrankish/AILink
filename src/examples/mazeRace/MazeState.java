package examples.mazeRace;

import templates.State;
import common.Coord;

public class MazeState {

  public static class Sight implements State{

    public Sight(boolean opN, int distN, boolean opE, int distE, boolean opS, int distS, boolean opW, int distW){
      opN_ = opN;
      opE_ = opE;
      opS_ = opS;
      opW_ = opW;
      distN_ = distN;
      distE_ = distE;
      this.distS_ = distS;
      distW_ = distW;
    }

    public int distanceNorth(){
      return distN_;
    }

    public int distanceEast(){
      return distE_;
    }

    public int distanceSouth(){
      return distS_;
    }

    public int distanceWest(){
      return distW_;
    }

    public boolean isNorthOpponent(){
      return opN_;
    }

    public boolean isEastOpponent(){
      return opE_;
    }

    public boolean isSouthOpponent(){
      return opS_;
    }

    public boolean isWestOpponent(){
      return opW_;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder("S");
      if(opN_){
        result.append("o");
      } else {
        result.append("w");
      }
      result.append(distN_);
      if(opE_){
        result.append("o");
      } else {
        result.append("w");
      }
      result.append(distE_);
      if(opS_){
        result.append("o");
      } else {
        result.append("w");
      }
      result.append(distS_);
      if(opW_){
        result.append("o");
      } else {
        result.append("w");
      }
      result.append(distW_);
      return result.toString();
    }

    @Override
    public String toReadable() {
      StringBuilder result = new StringBuilder("There is a");
      if(opN_){
        result.append("n opposing agent ");
      } else {
       result.append(" wall ");
      }
      if(distN_ > 1){
        result.append((distN_-1)+ " paces");
      } else {
        result.append("immediately");
      }
      result.append(" to the North.\n");
      result.append("There is a");
      if(opE_){
        result.append("n opposing agent ");
      } else {
        result.append(" wall ");
      }
      if(distE_ > 1){
        result.append((distE_-1)+ " paces");
      } else {
        result.append("immediately");
      }
      result.append(" to the East.\n");

      result.append("There is a");
      if(opS_){
        result.append("n opposing agent ");
      } else {
        result.append(" wall ");
      }
      if(distS_ > 1){
        result.append((distS_-1)+ " paces");
      } else {
        result.append("immediately");
      }
      result.append(" to the South.\n");
      result.append("There is a");
      if(opW_){
        result.append("n opposing agent ");
      } else {
        result.append(" wall ");
      }
      if(distW_ > 1){
        result.append((distW_-1)+ " paces");
      } else {
        result.append("immediately");
      }
      result.append(" to the West.\n");
      return result.toString();
    }

    private int distN_;
    private boolean opN_;
    private int distE_;
    private boolean opE_;
    private int distS_;
    private boolean opS_;
    private int distW_;
    private boolean opW_;
  }

  public static class Dimension implements State {

    public Dimension(int x, int y) {
      dim_ = new Coord(x, y);
    }

    public int getX() {
      return dim_.x;
    }

    public int getY() {
      return dim_.y;
    }

    @Override
    public String toString() {
      return "D" + dim_.x + "," + dim_.y;
    }

    @Override
    public String toReadable() {
      return "Maze is " + dim_.x +  " wide and " + dim_.y + " long";
    }

    private Coord dim_;
  }

  public static class Winner implements State{

    public Winner(String winner){
      winner_ = winner;
    }

    @Override
    public String toString() {
      return "W" + winner_;
    }

    @Override
    public String toReadable() {
      return "The winner is " + winner_;
    }

    private String winner_;

  }

}

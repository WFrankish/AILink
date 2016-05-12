package common;

public class Coord {

  public Coord(int ix, int iy){
    x = ix;
    y = iy;
  }

  public boolean equals(Object obj){
    boolean result = false;
    if(obj instanceof Coord){
      Coord that = (Coord) obj;
      result = (this.x == that.x && this.y == that.y);
    }

    return result;
  }

  public Coord apply(Cardinal dir){
    switch (dir){
      case NORTH:{
        return new Coord(x, y-1);
      }
      case EAST:{
        return new Coord(x-1, y);
      }
      case SOUTH:{
        return new Coord(x, y+1);
      }
      case WEST:{
        return new Coord(x+1, y);
      }
      default:{
        return null;
      }
    }
  }

  public int x;
  public int y;

}

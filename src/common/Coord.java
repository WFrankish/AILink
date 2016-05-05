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

  public int x;
  public int y;

}

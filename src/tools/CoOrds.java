package tools;

public class CoOrds {

  public CoOrds(int ix, int iy){
    x = ix;
    y = iy;
  }

  public boolean equals(Object obj){
    boolean result = false;
    if(obj instanceof CoOrds){
      CoOrds that = (CoOrds) obj;
      result = (this.x == that.x && this.y == that.y);
    }

    return result;
  }

  public int x;
  public int y;

}

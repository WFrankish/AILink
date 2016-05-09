package examples.noughtsAndCrosses;

import interfaces.Action;

public class OnXAction implements Action {

  public OnXAction(int x, int y){
    // grid of only 3*3
    if(x < 0 || x  >= 3){
      throw new IllegalArgumentException("x must be between 0 and 2");
    }
    if(y < 0 || y  >= 3){
      throw new IllegalArgumentException("y must be between 0 and 2");
    }
    x_ = x;
    y_ = y;
  }

  public String toString(){
    return "" + x_ + y_;
  }

  @Override
  public String toReadable() {
    return "x: " + x_ + ", y: " + y_;
  }

  public int getX(){
    return x_;
  }

  public int getY(){
    return y_;
  }

  public boolean equals(Object o){
    if(o instanceof OnXAction){
      OnXAction that = (OnXAction) o;
      return this.x_ == that.x_ && this.y_ == that.y_;
    }
    else{
      return false;
    }
  }

  private int x_;
  private int y_;
}

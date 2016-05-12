package common;

public class Grid<T> {

  public Grid(int width, int height, T auto, T outOfBounds){
    width_ = width;
    height_ = height;
    grid_ = (T[][]) new Object[width_][height_];
    outOfBounds_ = outOfBounds;
    for(int x = 0; x < width_; x++){
      for(int y = 0; y < height_; y++){
        grid_[x][y] = auto;
      }
    }
  }

  public Grid(Coord dimension, T auto, T outOfBounds){
    width_ = dimension.x;
    height_ = dimension.y;
    grid_ = (T[][]) new Object[width_][height_];
    outOfBounds_ = outOfBounds;
    for(int x = 0; x < width_; x++){
      for(int y = 0; y < height_; y++){
        grid_[x][y] = auto;
      }
    }
  }

  public T get(int x, int y){
    if(x < 0 || x >= width_ || y < 0 || y >= height_){
      return outOfBounds_;
    } else {
      return grid_[x][y];
    }
  }

  public T get(Coord loc){
    return get(loc.x, loc.y);
  }

  public boolean set(int x, int y, T t){
    if(x < 0 || x >= width_ || y < 0 || y >= height_){
      return false;
    } else {
      grid_[x][y] = t;
      return true;
    }
  }

  public boolean set(Coord loc, T t){
    return set(loc.x, loc.y, t);
  }

  public int height(){
    return  height_;
  }

  public int width(){
    return width_;
  }

  private T outOfBounds_;
  private int width_;
  private int height_;
  private T[][] grid_;

}

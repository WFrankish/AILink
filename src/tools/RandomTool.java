package tools;

import java.awt.*;
import java.util.Random;

public class RandomTool {

  public RandomTool(int seed){
    rand_ = new Random(seed);
  }

  /**
   * @param start minimum number (inclusive)
   * @param end maximum number (exclusive)
   * @return a random int in range [start, end)
   */
  public int between(int start, int end){
    return rand_.nextInt(end-start) + start;
  }

  /**
   * @param prob probability between 0 and 1, inclusive
   * @return true with probability given
   */
  public boolean decide(double prob){
    double ran = rand_.nextDouble();
    return prob > ran;
  }

  /**
   * @return a random colour
   */
  public Color nextColour(){
    return new Color(rand_.nextFloat(), rand_.nextFloat(), rand_.nextFloat());
  }

  private Random rand_;
}

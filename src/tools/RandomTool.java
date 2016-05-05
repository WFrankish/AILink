package tools;

import java.awt.*;
import java.util.Random;

public class RandomTool {

  public RandomTool(int seed){
    rand_ = new Random(seed);
  }

  public int between(int start, int end){
    return  (int) (start + (rand_.nextFloat() * (end - start -1)));
  }

  public boolean decide(double prob){
    double ran = rand_.nextDouble();
    return prob >= ran;
  }

  public Color color(){
    return new Color(rand_.nextFloat(), rand_.nextFloat(), rand_.nextFloat());
  }

  private Random rand_;
}
